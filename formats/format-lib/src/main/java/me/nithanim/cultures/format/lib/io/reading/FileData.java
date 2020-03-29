package me.nithanim.cultures.format.lib.io.reading;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface FileData {
  InputStream getInputStream() throws IOException;
}
