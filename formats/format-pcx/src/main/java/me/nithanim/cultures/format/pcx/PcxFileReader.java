package me.nithanim.cultures.format.pcx;

import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;

public class PcxFileReader {
  public PcxFile read(InputStream ist) throws IOException {
    LittleEndianDataInputStream in = new LittleEndianDataInputStream(ist);
    PcxHeader header = readHeader(in);
    byte[] data = readData(in, header);
    tryPaletteByte(in);

    byte[] palette = new byte[256 * 3];
    in.readFully(palette);

    return new PcxFile(header, data, palette);
  }

  private void tryPaletteByte(InputStream in) throws IOException {
    int b = in.read();
    if (b == -1) {
      throw new UnsupportedOperationException("No palette at the end of the pcx file found!");
    } else if (b != 0x0C) {
      throw new UnsupportedOperationException(
          "Byte past the end of the data section is not the start of a palette!");
    }
  }

  private byte[] readData(LittleEndianDataInputStream in, PcxHeader header) throws IOException {
    int lineLength = header.getBytesPerLine() * header.getColorPlanes();
    int width = header.getXMax() - header.getXMin() + 1;
    int height = header.getYMax() - header.getYMin() + 1;

    int linePaddingSize = (lineLength * (8 / header.getBitsPerPixel())) - width;
    if (linePaddingSize > 0) {
      throw new UnsupportedOperationException(
          "Line padding not supported"); // Could be fixed by ignoring padding in loop below
    }

    byte[] data = new byte[height * lineLength];
    int pointer = 0;

    while (pointer < data.length) {
      int raw = in.read();
      if ((raw >>> 6) == 0b11) { // Is RLE?
        int length = raw & 0b00111111;
        int value = in.read();
        for (int i = 0; i < length; i++) {
          data[pointer++] = (byte) value;
        }
      } else {
        data[pointer++] = (byte) raw;
      }
    }
    return data;
  }

  private PcxHeader readHeader(DataInput in) throws IOException {
    byte magic = in.readByte();
    if (magic != 0x0A) {
      throw new IllegalArgumentException("File is not a pcx image!");
    }
    byte version = in.readByte();
    if (version != 5) {
      throw new IllegalArgumentException("Only pcx version 3 supported!");
    }
    byte compression = in.readByte();
    if (compression != 1) {
      throw new IllegalArgumentException("Only RLE compression supported!");
    }
    byte bitsPerPixel = in.readByte();
    if (bitsPerPixel != 8) {
      throw new IllegalArgumentException("Only exactly 8 bits per pixes supported (256 colors)!");
    }
    short xMin = in.readShort();
    short yMin = in.readShort();
    short xMax = in.readShort();
    short yMax = in.readShort();
    short dpiH = in.readShort();
    short dpiV = in.readShort();
    byte[] palette16 = new byte[16 * 3];
    in.readFully(palette16);
    byte reserved = in.readByte();
    byte colorPlanes = in.readByte();
    if (colorPlanes != 1) {
      throw new IllegalArgumentException("Only one color plane supported!");
    }
    short bytesPerLine = in.readShort();
    short paletteType = in.readShort();
    if (paletteType != 0) {
      //throw new IllegalArgumentException("Only palette mode 0 supported, not " + paletteType + "!");
      //Works without the check so we leave it like that...
    }
    short resH = in.readShort();
    short resV = in.readShort();
    byte[] reservedBlock = new byte[54];
    in.readFully(reservedBlock);

    return new PcxHeader(
        magic,
        version,
        compression,
        bitsPerPixel,
        xMin,
        yMin,
        xMax,
        yMax,
        dpiH,
        dpiV,
        palette16,
        reserved,
        colorPlanes,
        bytesPerLine,
        paletteType,
        resH,
        resV,
        reservedBlock);
  }
}
