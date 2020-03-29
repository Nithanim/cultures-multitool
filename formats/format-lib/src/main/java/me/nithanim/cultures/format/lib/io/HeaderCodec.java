package me.nithanim.cultures.format.lib.io;

import java.io.IOException;
import me.nithanim.cultures.format.io.LittleEndianDataInputStream;
import me.nithanim.cultures.format.lib.LibFormat;

public class HeaderCodec {
  public Header unpack(LittleEndianDataInputStream dis, LibFormat format) throws IOException {
    int unknown = dis.readInt();
    int dirCount;
    if (format == LibFormat.CULTURES2) {
      dirCount = dis.readInt();
    } else {
      dirCount = -1;
    }
    int fileCount = dis.readInt();
    return new Header(unknown, dirCount, fileCount);
  }
}
