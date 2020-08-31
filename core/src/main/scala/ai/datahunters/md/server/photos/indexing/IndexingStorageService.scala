package ai.datahunters.md.server.photos.indexing

import java.io.File

import monix.bio.IO

trait IndexingStorageService {
  def saveUploadedFile(indexingJobId: IndexingJobId, file: File): IO[IndexingError, Unit]
}
