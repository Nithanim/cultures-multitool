package me.nithanim.cultures.format.lib.io;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import lombok.experimental.UtilityClass;

@UtilityClass
public class IOUtil {
  public static void fillArray(InputStream src, byte[] dest) throws IOException {
    int filled = 0;
    do {
      int read = src.read(dest, filled, dest.length - filled);
      if (read == -1) {
        throw new EOFException();
      }
      filled += read;
    } while (filled != dest.length);
  }
}
