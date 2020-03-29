package me.nithanim.cultures.format.lib;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;
import me.nithanim.cultures.format.lib.io.DirMeta;
import me.nithanim.cultures.format.lib.io.DirMetaCodec;
import me.nithanim.cultures.format.lib.io.FileMeta;
import me.nithanim.cultures.format.lib.io.FileMetaCodec;
import me.nithanim.cultures.format.lib.io.Header;
import me.nithanim.cultures.format.lib.io.HeaderCodec;
import me.nithanim.cultures.format.io.LittleEndianDataInputStream;

@UtilityClass
public class LibFileUtil {
  public static LibFileInfo read(InputStream in, LibFormat format) throws IOException {
    try {
      LittleEndianDataInputStream dis = new LittleEndianDataInputStream(in);
      Header header = new HeaderCodec().unpack(dis, format);

      List<DirMeta> dirMetas;
      if (format == LibFormat.CULTURES2) {
        DirMetaCodec dirMetaCodec = new DirMetaCodec();

        dirMetas = new ArrayList<>();
        for (int i = 0; i < header.getDirCount(); i++) {
          dirMetas.add(dirMetaCodec.unpack(dis));
        }
      } else {
        dirMetas = null;
      }

      FileMetaCodec fileMetaCodec = new FileMetaCodec();
      List<FileMeta> fileMetas = new ArrayList<>();
      for (int i = 0; i < header.getFileCount(); i++) {
        fileMetas.add(fileMetaCodec.unpack(dis));
      }
      return new LibFileInfo(format, dirMetas, fileMetas);
    } finally{
      in.close();
    }
  }
}
