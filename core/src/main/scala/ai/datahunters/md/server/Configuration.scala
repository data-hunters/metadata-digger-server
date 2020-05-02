package ai.datahunters.md.server

import ai.datahunters.md.server.infrastructure.solr.PhotosSolrRepository

case class Configuration(http: HttpEndpoint.Configuration, solr: PhotosSolrRepository.Config)
