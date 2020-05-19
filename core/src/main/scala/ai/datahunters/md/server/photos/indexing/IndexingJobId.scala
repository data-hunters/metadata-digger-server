package ai.datahunters.md.server.photos.indexing

import java.util.UUID

case class IndexingJobId(id: UUID)

object IndexingJobId {
  implicit class IndexingJobIdOps(val indexingJobId: IndexingJobId) extends AnyVal {
    def buildPath: String = indexingJobId.id.toString
  }
}
