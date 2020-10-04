package ai.datahunters.md.server.photos.search

sealed trait PossibleFilter

object PossibleFilter {
  case class MultipleSelectFilter(field: Field, possibleValues: Map[String, Long]) extends PossibleFilter
}


