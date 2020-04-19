package ai.datahunters.md.server.photos.search

import monix.bio.BIO

trait PhotosRepository {
  def search(request: SearchRequest): BIO[SearchError, SearchResponse]
}
