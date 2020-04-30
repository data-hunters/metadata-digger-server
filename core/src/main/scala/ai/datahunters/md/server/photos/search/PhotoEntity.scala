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
    metaData: Map[String, List[String]])
