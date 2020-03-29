package me.nithanim.cultures.format.pcx;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LittleEndianDataInputStream extends InputStream implements DataInput {
  private final DataInputStream src;
  private final byte[] buffer;

  public LittleEndianDataInputStream(InputStream src) {
    this.src = new DataInputStream(src);
    buffer = new byte[8];
  }

  @Override
  public int available() throws IOException {
    return src.available();
  }

  @Override
  public short readShort() throws IOException {
    src.readFully(buffer, 0, 2);
    return (short) ((buffer[1] & 0xff) << 8 | (buffer[0] & 0xff));
  }

  @Override
  public int readUnsignedShort() throws IOException {
    src.readFully(buffer, 0, 2);
    return ((buffer[1] & 0xff) << 8 | (buffer[0] & 0xff));
  }

  @Override
  public char readChar() throws IOException {
    src.readFully(buffer, 0, 2);
    return (char) ((buffer[1] & 0xff) << 8 | (buffer[0] & 0xff));
  }

  @Override
  public int readInt() throws IOException {
    src.readFully(buffer, 0, 4);
    return (buffer[3]) << 24
        | (buffer[2] & 0xff) << 16
        | (buffer[1] & 0xff) << 8
        | (buffer[0] & 0xff);
  }

  @Override
  public long readLong() throws IOException {
    src.readFully(buffer, 0, 8);
    return (long) (buffer[7]) << 56
        | (long) (buffer[6] & 0xff) << 48
        | (long) (buffer[5] & 0xff) << 40
        | (long) (buffer[4] & 0xff) << 32
        | (long) (buffer[3] & 0xff) << 24
        | (long) (buffer[2] & 0xff) << 16
        | (long) (buffer[1] & 0xff) << 8
        | (long) (buffer[0] & 0xff);
  }

  @Override
  public float readFloat() throws IOException {
    return Float.intBitsToFloat(readInt());
  }

  @Override
  public double readDouble() throws IOException {
    return Double.longBitsToDouble(readLong());
  }

  @Override
  public int read(byte[] b, int off, int len) throws IOException {
    return src.read(b, off, len);
  }

  @Override
  public final void readFully(byte[] b) throws IOException {
    src.readFully(b, 0, b.length);
  }

  @Override
  public void readFully(byte[] b, int off, int len) throws IOException {
    src.readFully(b, off, len);
  }

  @Override
  public int skipBytes(int n) throws IOException {
    return src.skipBytes(n);
  }

  @Override
  public boolean readBoolean() throws IOException {
    return src.readBoolean();
  }

  @Override
  public byte readByte() throws IOException {
    return src.readByte();
  }

  @Override
  public int read() throws IOException {
    return src.read();
  }

  @Override
  public int readUnsignedByte() throws IOException {
    return src.readUnsignedByte();
  }

  @Deprecated
  @Override
  public String readLine() throws IOException {
    return src.readLine();
  }

  @Override
  public String readUTF() throws IOException {
    return src.readUTF();
  }

  @Override
  public void close() throws IOException {
    src.close();
  }
}
