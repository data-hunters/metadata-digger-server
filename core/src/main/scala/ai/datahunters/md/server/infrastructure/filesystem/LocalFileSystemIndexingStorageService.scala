package ai.datahunters.md.server.infrastructure.filesystem

import java.io.{ File, FileInputStream, FileOutputStream }
import java.nio.channels.FileChannel
import java.nio.file.{ Files, Path, Paths }

import ai.datahunters.md.server.photos.indexing.IndexingError.IOError
import ai.datahunters.md.server.photos.indexing.{ IndexingError, IndexingJobId, IndexingStorageService }
import cats.effect.Resource
import monix.bio.{ BIO, Task }

class LocalFileSystemIndexingStorageService(fileRoot: File) extends IndexingStorageService {
  override def saveUploadedFile(indexingJobId: IndexingJobId, file: File): BIO[IndexingError, Unit] = {
    val operation = for {
      path <- BIO(Paths.get(fileRoot.getAbsolutePath, indexingJobId.buildPath, "upload"))
      _ <- BIO(Files.createDirectories(path))
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
      }
  }
}

object LocalFileSystemIndexingStorageService {
  case class Config(rootDirPath: String)

  def prepareRootDirAndBuildStorage(config: Config): Task[LocalFileSystemIndexingStorageService] = {
    val fileRoot = new File(config.rootDirPath)

    for {
      _ <- BIO(Files.createDirectories(fileRoot.toPath))
    } yield new LocalFileSystemIndexingStorageService(fileRoot)
  }

}
