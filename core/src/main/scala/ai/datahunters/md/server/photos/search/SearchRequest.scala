package ai.datahunters.md.server.photos.search

case class SearchRequest(
    textQuery: Option[String],
    facets: Option[Set[Field]],
    page: Option[Int],
    perPage: Option[Int],
    filters: Option[Set[Filter]])
