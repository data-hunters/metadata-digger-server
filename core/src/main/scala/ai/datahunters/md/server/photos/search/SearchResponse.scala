package ai.datahunters.md.server.photos.search

case class SearchResponse(photos: List[PhotoEntity], facets: Map[String, SearchResponse.FacetField], page: Int, total: Int)

object SearchResponse {
  case class FacetField(results: Map[String, Long])
}
