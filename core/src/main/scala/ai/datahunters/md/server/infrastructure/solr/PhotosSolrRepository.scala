package ai.datahunters.md.server.infrastructure.solr

import ai.datahunters.md.server.infrastructure.solr.PhotosSolrRepository._
import ai.datahunters.md.server.photos.search.SearchError._
import ai.datahunters.md.server.photos.search._
import cats.implicits._
import com.github.takezoe.solr.scala._
import com.typesafe.scalalogging.StrictLogging
import monix.bio.IO
import ResponseParser._

class PhotosSolrRepository(config: Config) extends PhotosRepository with StrictLogging {
  val client = new SolrClient(config.solrUrl)

  override def search(request: SearchRequest): IO[SearchError, SearchResponse] = {
    val perPage = request.perPage.getOrElse(DefaultPerPage)
    val page = request.page.getOrElse(0)
    val from = perPage * page

    val baseQuery = client
      .query(request.textQuery.filterNot(_.isEmpty).getOrElse("*:*"))
      .filteredQuery(request.filters.map(_.map(filterToFilterQuery)).getOrElse(Set.empty).toSeq: _*)
      .collection(Collection)
      .rows(perPage)
      .start(from)

    val query = request.facets.fold(baseQuery)(fields => baseQuery.facetFields(fields.map(_.solrFieldName).toSeq: _*))

    IO(query.getResultAsMap())
      .mapError(err => SolrExecutionError(err))
      .flatMap(qr => IO.fromEither(mapResult(request.filters.getOrElse(Set.empty), page)(qr)))
  }

  private def filterToFilterQuery(filter: FilterToBeApplied): String =
    filter match {
      case FilterToBeApplied.MultipleSelectFilter(fieldName, selectedValues) =>
        s"${fieldName.solrFieldName}:${selectedValues.mkString("(", " ", ")")}"
    }

  private def mapResult(selectedFilters: Set[FilterToBeApplied], page: Int)(
      mapQueryResult: MapQueryResult): CanFail[SearchResponse] = {
    for {
      photos <- mapQueryResult.documents.traverse(mapDocument)
      facets <- mapQueryResult.facetFields.toList.traverse { case (k, v) => solrFacetFieldToDomain(k).map(f => f -> v) }
      possibleFilters = buildPossibleFilters(selectedFilters, facets.toMap)
    } yield SearchResponse(
      photos = photos,
      facets = facets.toMap,
      page = page,
      total = mapQueryResult.numFound,
      possibleFilters = possibleFilters)
  }

  private def buildPossibleFilters(
      selectedFilters: Set[FilterToBeApplied],
      facets: Map[Field, Map[String, Long]]): Set[PossibleFilter] = {
    def isValueSelected(field: Field, value: String): Boolean = {
      selectedFilters
        .collectFirst { case FilterToBeApplied.MultipleSelectFilter(`field`, selectedValues) => selectedValues(value) }
        .getOrElse(false)
    }
    facets.map { case (field, values) =>
      val possibleValues = values.map { case (name, count) =>
        PossibleFilter.MultipleSelectFilter.PossibleValue(name, count, isValueSelected(field, name))
      }.toList

      PossibleFilter.MultipleSelectFilter(field, possibleValues)
    }.toSet
  }
}

object PhotosSolrRepository {
  val DefaultPerPage = 20
  val Collection = "metadata_digger"

  case class Config(solrUrl: String)
}
