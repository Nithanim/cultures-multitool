package me.nithanim.cultures.format.lib;

import java.util.List;
import lombok.Value;
import me.nithanim.cultures.format.lib.io.DirMeta;
import me.nithanim.cultures.format.lib.io.FileMeta;

@Value
public class LibFileInfo {
  LibFormat format;
  List<DirMeta> dirMetas;
  List<FileMeta> fileMetas;

  public LibFileInfo(LibFormat format, List<DirMeta> dirMetas, List<FileMeta> fileMetas) {
    this.format = format;
    this.dirMetas = dirMetas;
    this.fileMetas = fileMetas;
  }
}
