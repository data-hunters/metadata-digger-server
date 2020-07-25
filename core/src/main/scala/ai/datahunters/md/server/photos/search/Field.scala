package ai.datahunters.md.server.photos.search

import enumeratum.{CirceEnum, Enum, EnumEntry}

sealed trait Field extends EnumEntry {
  def solrFieldName: String
}

object Field extends Enum[Field] with CirceEnum[Field] {

  case object Labels extends Field {
    override def solrFieldName: String = "labels"
  }

  case object Type extends Field {
    override def solrFieldName: String = "file_type"
  }

  case object TagNames extends Field {
    override def solrFieldName: String = "tag_names"
  }

  case object DirectoryNames extends Field {
    override def solrFieldName: String = "directory_names"
  }

  case object Model extends Field {
    override def solrFieldName: String = "md_exif_ifd0_model"
  }

  override def values: IndexedSeq[Field] = findValues
}

