package ai.datahunters.md.server.photos.indexing

sealed trait IndexingError

object IndexingError {
  case class WrongUploadedFileType(actualType: String) extends IndexingError

  case class IOError(throwable: Throwable) extends IndexingError

}
