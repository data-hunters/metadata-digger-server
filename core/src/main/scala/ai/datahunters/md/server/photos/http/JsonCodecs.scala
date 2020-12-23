package ai.datahunters.md.server.photos.http

import ai.datahunters.md.server.photos.indexing.{ IndexingJobId, StartIndexingResponse }
import ai.datahunters.md.server.photos.search.PhotoEntity.MetaDataEntry
import ai.datahunters.md.server.photos.search._
import enumeratum.{ Circe, Enum, EnumEntry }
import io.circe.Decoder.Result
import io.circe.{ Codec, Decoder, HCursor, Json, KeyDecoder, KeyEncoder }
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.deriveConfiguredCodec
import cats.implicits._
import io.circe.syntax._
import sttp.tapir.Schema

import java.util.UUID

object JsonCodecs {
  implicit val config: Configuration = Configuration.default.withSnakeCaseMemberNames.withDefaults

  implicit val metaDataEntryCodec: Codec[MetaDataEntry] = new Codec[MetaDataEntry] {
    override def apply(a: MetaDataEntry): Json =
      a match {
        case MetaDataEntry.IntEntry(value)    => Json.fromInt(value)
        case MetaDataEntry.FloatEntry(value)  => Json.fromFloatOrNull(value)
        case MetaDataEntry.TextEntry(value)   => Json.fromString(value)
        case MetaDataEntry.TextsEntry(values) => Json.fromValues(values.map(Json.fromString))
      }

    override def apply(c: HCursor): Result[MetaDataEntry] =
      c.as[Int]
        .map(MetaDataEntry.IntEntry)
        .orElse(c.as[Float].map(MetaDataEntry.FloatEntry))
        .orElse(c.as[List[String]].map(MetaDataEntry.TextsEntry))
        .orElse(c.as[String].map(MetaDataEntry.TextEntry))
  }

  implicit val fieldCodec: Codec[Field] = enumCodec[Field]
  implicit val fieldKeyEncoder: KeyEncoder[Field] = (key: Field) => key.entryName
  implicit val fieldKeyDecoder: KeyDecoder[Field] = (key: String) => Field.withNameInsensitiveOption(key)
  implicit def keySchemaTapir[V](implicit ec: Schema[V]): Schema[Map[Field, V]] =
    Schema.schemaForMap(ec).contramap(t => t.map(e => e._1.solrFieldName -> e._2))
  implicit val locationCodec: Codec[PhotoEntity.Location] = deriveConfiguredCodec
  implicit val photoEntityCode: Codec[PhotoEntity] = deriveConfiguredCodec
  implicit val searchErrorCodec: Codec[PhotosEndpointError] = deriveConfiguredCodec
  implicit val toBeAppliedmultipleSelectFilterCodec: Codec[FilterToBeApplied.MultipleSelectFilter] =
    deriveConfiguredCodec

  implicit val filterCodec: Codec[FilterToBeApplied] = new Codec[FilterToBeApplied] {
    override def apply(a: FilterToBeApplied): Json =
      a match {
        case f: FilterToBeApplied.MultipleSelectFilter => f.asJson
      }

    override def apply(c: HCursor): Result[FilterToBeApplied] =
      List[Decoder[FilterToBeApplied]](Decoder[FilterToBeApplied.MultipleSelectFilter].widen)
        .reduceLeft(_ or _)
        .decodeJson(c.value)
  }
  implicit val searchRequestCodec: Codec[SearchRequest] = deriveConfiguredCodec
  implicit val possibleMultpipleSelectFilterValueCode: Codec[PossibleFilter.MultipleSelectFilter.PossibleValue] =
    deriveConfiguredCodec
  implicit val possibleMultipleSelectFilterCodec: Codec[PossibleFilter.MultipleSelectFilter] =
    deriveConfiguredCodec

  implicit val possibleFilterCodec: Codec[PossibleFilter] = new Codec[PossibleFilter] {
    override def apply(c: HCursor): Result[PossibleFilter] =
      List[Decoder[PossibleFilter]](Decoder[PossibleFilter.MultipleSelectFilter].widen)
        .reduceLeft(_ or _)
        .decodeJson(c.value)

    override def apply(a: PossibleFilter): Json = a match {
      case c: PossibleFilter.MultipleSelectFilter => c.asJson
    }
  }
  implicit val searchResponseCodec: Codec[SearchResponse] = deriveConfiguredCodec
  implicit val indexingJobIdCodec: Codec[IndexingJobId] = new Codec[IndexingJobId] {
    override def apply(c: HCursor): Result[IndexingJobId] = c.as[UUID].map(IndexingJobId.apply)

    override def apply(a: IndexingJobId): Json = Json.fromString(a.id.toString)
  }
  implicit val startIndexingResponseCodec: Codec[StartIndexingResponse] = deriveConfiguredCodec

  def enumCodec[E <: EnumEntry: Enum]: Codec[E] = {
    val enum = implicitly[Enum[E]]
    Codec.from(Circe.decodeCaseInsensitive(`enum`), Circe.encoderLowercase(`enum`))
  }
  implicit val dupaCoded: Codec[Dupa] = deriveConfiguredCodec
}
