package ai.datahunters.md.server.photos.search

import monix.bio.IO

trait PhotosRepository {
  def search(request: SearchRequest): IO[SearchError, SearchResponse]
}
