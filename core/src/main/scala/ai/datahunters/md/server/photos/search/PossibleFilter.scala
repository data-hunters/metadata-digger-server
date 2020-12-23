package ai.datahunters.md.server.photos.search

sealed trait PossibleFilter

object PossibleFilter {
  case class MultipleSelectFilter(field: Field, values: List[MultipleSelectFilter.PossibleValue]) extends PossibleFilter

  object MultipleSelectFilter {
    case class PossibleValue(name: String, entryCount: Long, isSelected: Boolean)
  }
}
