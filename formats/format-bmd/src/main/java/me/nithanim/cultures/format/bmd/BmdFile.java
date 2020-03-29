package me.nithanim.cultures.format.bmd;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import lombok.Value;
import me.nithanim.cultures.format.bmd.RawBmdFileReader.BmdFrameInfo;
import me.nithanim.cultures.format.bmd.Test.Bitmap;

@Value
public class BmdFile {
  RawBmdFile rawBmdFile;

  public int getSize() {
    return rawBmdFile.getFrameInfo().size();
  }

  public BufferedImage get(int frame, byte[] palette) throws IOException {
    int type = rawBmdFile.getFrameInfo().get(frame).getType();
    Bitmap bmp;
    switch (type) {
      case 1:
        bmp = extractFrameType1(rawBmdFile, rawBmdFile.getFrameInfo().get(frame), palette);
        break;
      case 2:
        bmp = extractFrameType2(rawBmdFile, rawBmdFile.getFrameInfo().get(frame), palette);
        break;
      default:
        throw new UnsupportedOperationException("Unknown bmp type " + type);
    }
    return convertToImage(bmp);
  }

  private Bitmap extractFrameType1(RawBmdFile bmdFile, BmdFrameInfo frameInfo, byte[] palette) {
    int frameStart = frameInfo.getOff();
    int frameCount = frameInfo.getLen();
    int width = frameInfo.getWidth();

    byte[] frameBuffer = bmdFile.getPixels();
    int frameBufferIndex = bmdFile.getRowInfo().get(frameStart).getOffset();

    Bitmap bmp = new Bitmap(width, frameCount + 1);

    for (int row = 0; row < frameCount; row++) {
      int indent = bmdFile.getRowInfo().get(row + frameStart).getIndent();
      int i = 0;

      while (i < indent) {
        bmp.setPixel(i++, row, 0x00000000);
      }

      for (int head = frameBuffer[frameBufferIndex++];
          head != 0;
          head = frameBuffer[frameBufferIndex++] & 0xFF) {
        if (head < 0x80) {
          for (int j = 0; j < head; j++) {
            bmp.setPixel(i++, row, getFromPalette(palette, frameBuffer[frameBufferIndex++] & 0xFF));
          }
        } else {
          for (int j = 0; j < head - 0x80; j++) {
            bmp.setPixel(i++, row, 0x00000000);
          }
        }
      }

      while (i < width) {
        bmp.setPixel(i++, row, 0x00000000);
      }
    }
    return bmp;
  }

  private Bitmap extractFrameType2(RawBmdFile bmdFile, BmdFrameInfo frameInfo, byte[] palette) {
    int frameStart = frameInfo.getOff();
    int frameCount = frameInfo.getLen();
    int width = frameInfo.getWidth();

    byte[] frameBuffer = bmdFile.getPixels();
    int frameBufferIndex = bmdFile.getRowInfo().get(frameStart).getOffset();

    Bitmap bmp = new Bitmap(width, frameCount + 1);

    for (int row = 0; row < frameCount; row++) {
      int indent = bmdFile.getRowInfo().get(row + frameStart).getIndent();
      int i = 0;

      while (i < indent) {
        bmp.setPixel(i++, row, 0x00000000);
      }

      for (int head = frameBuffer[frameBufferIndex++];
          head != 0;
          head = frameBuffer[frameBufferIndex++] & 0xFF) {
        if (head < 0x80) {
          bmp.setPixel(i++, row, getFromPalette(palette, head));
        } else {
          i += head - 0x80;
        }
      }

      while (i < width) {
        bmp.setPixel(i++, row, 0x00000000);
      }
    }
    return bmp;
  }

  private int getFromPalette(byte[] palette, int idx) {
    int pointer = idx * 3;
    byte r = palette[pointer];
    byte g = palette[pointer + 1];
    byte b = palette[pointer + 2];

    int color = 0xFF000000;
    color |= r << 8 * 2;
    color |= g << 8 * 1;
    color |= b << 8 * 0;
    return color;
  }

  private BufferedImage convertToImage(Bitmap bmp) {
    BufferedImage bu = new BufferedImage(bmp.getW(), bmp.getH(), BufferedImage.TYPE_INT_ARGB);
    WritableRaster r = bu.getRaster();
    for (int x = 0; x < bmp.getW(); x++) {
      for (int y = 0; y < bmp.getH(); y++) {
        int c = bmp.getPixel(x, y);
        bu.setRGB(x, y, c);
      }
    }
    return bu;
  }
}
