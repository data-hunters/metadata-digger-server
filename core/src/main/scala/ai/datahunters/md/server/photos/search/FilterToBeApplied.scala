package ai.datahunters.md.server.photos.search

sealed trait FilterToBeApplied

object FilterToBeApplied {
  case class MultipleSelectFilter(fieldName: Field, selectedValues: Set[String]) extends FilterToBeApplied

}
