package ai.datahunters.md.server.photos.search

sealed trait Filter

object Filter {
  case class MultipleSelectFilter(fieldName: Field, selectedValues: Set[String]) extends Filter

}
