package me.nithanim.cultures.format.lib.io;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import me.nithanim.cultures.format.io.LittleEndianDataInputStream;

public class DirMetaCodec {
  public DirMeta unpack(LittleEndianDataInputStream dis) throws IOException {
    int nameLength = dis.readInt();
    byte[] chars = new byte[nameLength];
    IOUtil.fillArray(dis, chars);
    int level = dis.readInt();
    return new DirMeta(new String(chars, StandardCharsets.ISO_8859_1), level);
  }
}
