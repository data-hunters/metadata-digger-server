package ai.datahunters.md.server.photos.search

import ai.datahunters.md.server.photos.search.PhotoEntity.{ Location, MetaDataEntry }

case class PhotoEntity(
    id: String,
    basePath: String,
    filePath: String,
    fileType: String,
    directoryNames: List[String],
    tagNames: List[String],
    labels: List[String],
    thumbnail: String,
    metaData: Map[String, MetaDataEntry],
    location: Option[Location])

object PhotoEntity {

  sealed trait MetaDataEntry

  object MetaDataEntry {

    case class IntEntry(value: Int) extends MetaDataEntry

    case class FloatEntry(value: Float) extends MetaDataEntry

    case class TextEntry(value: String) extends MetaDataEntry

    case class TextsEntry(value: List[String]) extends MetaDataEntry

  }

  case class Location(latitude: Double, longitude: Double)

}
