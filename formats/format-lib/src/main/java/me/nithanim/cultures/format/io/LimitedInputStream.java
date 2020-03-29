package me.nithanim.cultures.format.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * A stream that only allows reading only a given amount of bytes from the underlying stream.
 */
public class LimitedInputStream extends InputStream {
  private static final int EOF = -1;
  private final InputStream in;
  private final long allowance;

  private long readTotal;

  public LimitedInputStream(InputStream in, long length) {
    this.in = in;
    this.allowance = length;
  }

  @Override
  public int available() throws IOException {
    return Math.min(in.available(), calcRemainingBytes());
  }

  @Override
  public void close() throws IOException {
    in.close();
  }

  @Override
  public synchronized void mark(int readlimit) {
    in.mark(readlimit);
  }

  @Override
  public boolean markSupported() {
    return in.markSupported();
  }

  @Override
  public int read() throws IOException {
    if (isAtEof()) {
      return EOF;
    }
    readTotal++;
    return in.read();
  }

  @Override
  public int read(byte[] b) throws IOException {
    int remaining = calcRemainingBytes();
    if (remaining == 0 && b.length > 0) {
      return EOF;
    }
    int read = read(b, 0, Math.min(b.length, remaining));
    readTotal += read;
    return read;
  }

  @Override
  public int read(byte[] b, int off, int len) throws IOException {
    if (len == 0) {
      return 0;
    } else if (isAtEof()) {
      return -1;
    } else {
      int read = in.read(b, off, Math.min(len, calcRemainingBytes()));
      readTotal += read;
      return read;
    }
  }

  @Override
  public synchronized void reset() throws IOException {
    in.reset();
  }

  @Override
  public long skip(long n) throws IOException {
    long toSkip = Math.min(n, calcRemainingBytes());
    if (toSkip < 0) {
      // In case we go negative and the delegate allows seek back? Who knows but we support that.
      // We allow going back all byte that we already read (our starting point).
      toSkip = Math.max(-readTotal, toSkip);
    }
    long read = in.skip(toSkip);
    readTotal += read;
    return read;
  }

  private boolean isAtEof() {
    return calcRemainingBytes() > 0;
  }

  private int calcRemainingBytes() {
    return (int) (allowance - readTotal);
  }
}
