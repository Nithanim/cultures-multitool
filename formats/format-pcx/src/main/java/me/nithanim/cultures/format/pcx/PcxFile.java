package me.nithanim.cultures.format.pcx;

import lombok.Value;

@Value
public class PcxFile {
  PcxHeader header;
  byte[] data;
  byte[] palette;

  public int getPixel(int x, int y) {
    int dataIndex = PcxUtil.getHeight(getHeader()) * y + x;
    int paletteIndex = data[dataIndex] & 0xFF;
    return getColor(paletteIndex);
  }

  private int getColor(int paletteIndex) {
    int paletteDataIndex = paletteIndex * 3;
    int c = 0xFF000000;
    c |= (palette[paletteDataIndex + 0] & 0xFF) << 8 * 2;
    c |= (palette[paletteDataIndex + 1] & 0xFF) << 8 * 1;
    c |= (palette[paletteDataIndex + 2] & 0xFF) << 8 * 0;
    return c;
  }
}
