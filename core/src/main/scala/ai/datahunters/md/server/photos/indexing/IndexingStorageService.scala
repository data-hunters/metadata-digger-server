package ai.datahunters.md.server.photos.indexing

import java.io.File

import monix.bio.BIO

trait IndexingStorageService {
  def saveUploadedFile(indexingJobId: IndexingJobId, file: File): BIO[IndexingError, Unit]
}
