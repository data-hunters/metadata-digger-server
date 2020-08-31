package ai.datahunters.md.server.infrastructure.filesystem

import java.io.{ File, FileInputStream, FileOutputStream }
import java.nio.channels.FileChannel
import java.nio.file.{ Files, Paths }

import ai.datahunters.md.server.photos.indexing.IndexingError.IOError
import ai.datahunters.md.server.photos.indexing.{ IndexingError, IndexingJobId, IndexingStorageService }
import cats.effect.Resource
import monix.bio.{ IO, Task }

class LocalFileSystemIndexingStorageService(fileRoot: File) extends IndexingStorageService {
  override def saveUploadedFile(indexingJobId: IndexingJobId, file: File): IO[IndexingError, Unit] = {
    val operation = for {
      path <- IO(Paths.get(fileRoot.getAbsolutePath, indexingJobId.buildPath, "upload"))
      _ <- IO(Files.createDirectories(path))
      fileToBeSaved = path.resolve("upload").toFile
      _ <- copyUploadedToDestination(file, fileToBeSaved)
    } yield ()

    operation.mapError(t => IOError(t))
  }

  private def copyUploadedToDestination(source: File, dest: File): Task[Unit] = {
    Resource
      .fromAutoCloseable[Task, FileChannel](Task(new FileInputStream(source).getChannel))
      .parZip(Resource.fromAutoCloseable[Task, FileChannel](Task(new FileOutputStream(dest).getChannel)))
      .use {
        case (sourceChannel, destChannel) =>
          Task(destChannel.transferFrom(sourceChannel, 0, sourceChannel.size))
            .flatMap(checkAmountOfTransferedBytes(sourceChannel.size()))

      }
  }

  private def checkAmountOfTransferedBytes(sourceChannelSize: Long)(bytesTransfered: Long) = {
    if (bytesTransfered != sourceChannelSize)
      IO.raiseError(
        new RuntimeException(
          s"Bytes transferred $bytesTransfered does not match source channel size ${sourceChannelSize}"))
    else Task(())
  }
}

object LocalFileSystemIndexingStorageService {
  case class Config(rootDirPath: String)

  def prepareRootDirAndBuildStorage(config: Config): Task[LocalFileSystemIndexingStorageService] = {
    val fileRoot = new File(config.rootDirPath)

    for {
      _ <- IO(Files.createDirectories(fileRoot.toPath))
    } yield new LocalFileSystemIndexingStorageService(fileRoot)
  }

}
