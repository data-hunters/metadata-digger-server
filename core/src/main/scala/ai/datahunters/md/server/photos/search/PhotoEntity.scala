package ai.datahunters.md.server.photos.search

case class PhotoEntity(
    id: String,
    basePath: String,
    filePath: String,
    fileType: String,
    directoryNames: List[String],
    tagNames: List[String],
    labels: List[String],
    thumbnail: String,
    hashCrc32: Option[String],
    hashMd5: Option[String],
    hashSha1: Option[String],
    hashSha224: Option[String],
    hashSha256: Option[String],
    hashSha284: Option[String],
    hashSha512: Option[String],
//    metaData: Map[String, List[String]]
                      )
