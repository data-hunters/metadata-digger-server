package ai.datahunters.md.server.photos.search

sealed trait SearchError {
  def description: String
}

object SearchError {

  case class SolrDeserializationError[A, B](field: String, expectedType: Class[A], butWas: Class[B])
      extends SearchError {
    override def description: String =
      s"Failed to deserialize solr response, expected field $field to be of type $expectedType, but was $butWas"
  }

  case class SolrMissingField(field: String) extends SearchError {
    override def description: String = s"$field not found in the response"
  }

  case class SolrExecutionError(err: Throwable) extends SearchError {
    override def description: String = err.getMessage
  }

  case class UnexpectedFacetField(field: String) extends SearchError {
    override def description: String = s"Unexpected field when parsing facets: $field"
  }
}
