package ai.datahunters.md.server.photos.search

case class SearchResponse(photos: List[PhotoEntity], facets: Map[String, Map[String, Long]], page: Int, total: Long)
