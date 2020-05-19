package ai.datahunters.md.server.photos.indexing

import java.io.File
import java.util.UUID

import monix.bio.{ BIO, UIO }
import org.apache.tika.Tika
import ai.datahunters.md.server.photos.indexing.IndexingService._
import com.typesafe.scalalogging.StrictLogging

class IndexingService(indexingStorageService: IndexingStorageService) extends StrictLogging {
  private val tika = new Tika()
  def handleUpload(startIndexingRequest: StartIndexingRequest): BIO[IndexingError, StartIndexingResponse] = {
    val indexingId = IndexingJobId(UUID.randomUUID())

    for {
      _ <- UIO(logger.info(s"Starting upload for $indexingId"))
      file <- BIO.fromEither(detectFileType(startIndexingRequest.file))
      _ <- indexingStorageService.saveUploadedFile(indexingId, file)
      _ <- (extractFile(indexingId).flatMap(_ => startSpark(indexingId))).start
    } yield StartIndexingResponse(indexingId)
  }

  private def detectFileType(file: File): Either[IndexingError, File] = {
    val fileType = tika.detect(file)
    if (AcceptedFileTypes.contains(fileType)) Right(file)
    else Left(IndexingError.WrongUploadedFileType(fileType))
  }

  private def extractFile(indexingJobId: IndexingJobId): BIO[IndexingError, Unit] =
    UIO(logger.info(s"Fake extract for $indexingJobId"))

  private def startSpark(indexingJobId: IndexingJobId): BIO[IndexingError, Unit] =
    UIO(logger.info(s"Fake start spark for $indexingJobId"))
}
object IndexingService {

  private val GTAR = "application/x-gtar"
  private val TAR = "application/x-tar"
  private val ZIP = "application/zip"
  private val GZ = "application/gzip"
  private val XZ = "application/x-xz"
  private val BZ2 = "application/x-bzip2"

  val AcceptedFileTypes = Seq(GTAR, TAR, ZIP, GZ, XZ, BZ2)
}
