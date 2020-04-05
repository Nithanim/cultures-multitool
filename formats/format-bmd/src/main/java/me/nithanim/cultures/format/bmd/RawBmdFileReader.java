package me.nithanim.cultures.format.bmd;

import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Value;

public class RawBmdFileReader {
  public RawBmdFile read(InputStream ist) throws IOException {
    LittleEndianDataInputStream in = new LittleEndianDataInputStream(ist);
    BmdHeader header = readBmdHeader(in);
    if (header.getNumFrames() == 0 && header.getNumPixels() == 0 && header.getNumRows() == 0) {
      return new RawBmdFile(header, Collections.emptyList(), new byte[0], Collections.emptyList());
    } else {
      List<BmdFrameInfo> frameInfo = readFramesSection(in);
      byte[] pixels = readPixelsSection(in);
      List<BmdFrameRow> rowInfo = readRowsSection(in);
      return new RawBmdFile(header, frameInfo, pixels, rowInfo);
    }
  }

  private List<BmdFrameRow> readRowsSection(LittleEndianDataInputStream in) throws IOException {
    BmdSectionHeader rowsSectionHeader = readBmdSectionHeader(in);
    List<BmdFrameRow> rowInfo = new ArrayList<>();
    for (int i = 0; i < rowsSectionHeader.getLength() / BmdFrameRow.SIZE; i++) {
      rowInfo.add(readBmdFrameRow(in));
    }
    return rowInfo;
  }

  private byte[] readPixelsSection(LittleEndianDataInputStream in) throws IOException {
    BmdSectionHeader pixelsSectionHeader = readBmdSectionHeader(in);
    return in.readNBytes(pixelsSectionHeader.getLength());
  }

  private List<BmdFrameInfo> readFramesSection(LittleEndianDataInputStream in) throws IOException {
    BmdSectionHeader framesSectionHeader = readBmdSectionHeader(in);
    List<BmdFrameInfo> frameInfo = new ArrayList<>();
    for (int i = 0; i < framesSectionHeader.getLength() / BmdFrameInfo.SIZE; i++) {
      frameInfo.add(readBmdFrameInfo(in));
    }
    return frameInfo;
  }

  private BmdHeader readBmdHeader(DataInput in) throws IOException {
    int magic = in.readInt();
    int zero0 = in.readInt();
    int zero1 = in.readInt();
    int numFrames = in.readInt();
    int numPixels = in.readInt();
    int numRows = in.readInt();
    int unknown0 = in.readInt();
    int unknown1 = in.readInt();
    int zero2 = in.readInt();
    return new BmdHeader(magic, numFrames, numPixels, numRows);
  }

  private BmdSectionHeader readBmdSectionHeader(DataInput in) throws IOException {
    int magic = in.readInt();
    int zero0 = in.readInt();
    int length = in.readInt();
    return new BmdSectionHeader(magic, length);
  }

  private BmdFrameInfo readBmdFrameInfo(DataInput in) throws IOException {
    int type = in.readInt();
    int meta1 = in.readInt();
    int meta2 = in.readInt();
    int width = in.readInt();
    int len = in.readInt();
    int off = in.readInt();
    return new BmdFrameInfo(type, meta1, meta2, width, len, off);
  }

  private BmdFrameRow readBmdFrameRow(DataInput in) throws IOException {
    int data = in.readInt();
    // the lower bits are taken first; so offset with 22 and then the higher order bits are the 10
    // indent
    int offset = data & 0b00111111_11111111_11111111;
    int indent = (data >>> 22) & 0b11_11111111;
    return new BmdFrameRow(offset, indent);
  }

  @Value
  public static class BmdHeader {
    int magic;
    int numFrames;
    int numPixels;
    int numRows;
  }

  @Value
  public static class BmdSectionHeader {
    int magic;
    int length;
  }

  @Value
  public static class BmdFrameInfo {
    public static final int SIZE = 6 * Integer.BYTES;
    public static final int TYPE_NORMAL = 1;
    public static final int TYPE_SHADOW = 2;
    public static final int TYPE_EXTENDED = 4;

    int type;
    int dx;
    int dy;
    int width;
    int len;
    int off;
  }

  @Value
  public static class BmdFrameRow {
    public static final int SIZE = 4;
    int offset; // These are swapped in the docs! First are 22 bits offset and then 10 bits indent!
    int indent;
  }
}
