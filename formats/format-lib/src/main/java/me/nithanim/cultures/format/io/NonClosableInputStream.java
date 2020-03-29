package me.nithanim.cultures.format.io;

import java.io.IOException;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NonClosableInputStream extends InputStream {
  private final InputStream in;

  @Override
  public void close() throws IOException {
    //noop
  }
  @Override
  public int read() throws IOException {
    return in.read();
  }

  @Override
  public int read(byte[] b) throws IOException {
    return in.read(b);
  }

  @Override
  public int read(byte[] b, int off, int len) throws IOException {
    return in.read(b, off, len);
  }

  @Override
  public long skip(long n) throws IOException {
    return in.skip(n);
  }

  @Override
  public int available() throws IOException {
    return in.available();
  }


  @Override
  public void mark(int readlimit) {
    in.mark(readlimit);
  }

  @Override
  public void reset() throws IOException {
    in.reset();
  }

  @Override
  public boolean markSupported() {
    return in.markSupported();
  }
}
