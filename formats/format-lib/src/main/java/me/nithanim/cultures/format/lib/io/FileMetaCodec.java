package me.nithanim.cultures.format.lib.io;

import static me.nithanim.cultures.format.lib.io.IOUtil.fillArray;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import me.nithanim.cultures.format.io.LittleEndianDataInputStream;

public class FileMetaCodec {
  public FileMeta unpack(LittleEndianDataInputStream dis) throws IOException {
    int nameLength = dis.readInt();
    byte[] chars = new byte[nameLength];
    fillArray(dis, chars);
    String name = new String(chars, StandardCharsets.ISO_8859_1);
    int pos = dis.readInt();
    int len = dis.readInt();
    return new FileMeta(name, pos, len);
  }
}
