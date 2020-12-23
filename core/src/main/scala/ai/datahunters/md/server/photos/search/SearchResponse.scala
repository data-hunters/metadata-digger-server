package ai.datahunters.md.server.photos.search

case class SearchResponse(
    photos: List[PhotoEntity],
    facets: Map[Field, Map[String, Long]],
    page: Int,
    total: Long,
    possibleFilters: Set[PossibleFilter])

case class Dupa(map: Map[Field, Map[String, Long]])
