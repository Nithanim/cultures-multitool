package me.nithanim.cultures.format.pcx;

import lombok.Value;

@Value
public class PcxHeader {
  byte magic;
  byte version;
  byte compression;
  byte bitsPerPixel;
  short xMin;
  short yMin;
  short xMax;
  short yMax;
  short dpiH;
  short dpiV;
  byte[] palette;
  byte reserved;
  byte colorPlanes;
  short bytesPerLine;
  short paletteType;
  short resH;
  short resV;
  byte[] reservedBlock;
}
