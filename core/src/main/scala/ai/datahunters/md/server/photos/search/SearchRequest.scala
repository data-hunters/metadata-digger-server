package ai.datahunters.md.server.photos.search

case class SearchRequest(
    textQuery: Option[String],
    facets: Option[Set[String]],
    page: Option[Int],
    perPage: Option[Int])
