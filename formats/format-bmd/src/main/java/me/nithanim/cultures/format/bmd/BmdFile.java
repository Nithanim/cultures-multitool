package me.nithanim.cultures.format.bmd;

import java.awt.image.BufferedImage;
import lombok.Value;
import me.nithanim.cultures.format.bmd.RawBmdFileReader.BmdFrameInfo;
import me.nithanim.cultures.format.bmd.RawBmdFileReader.BmdFrameRow;
import me.nithanim.cultures.format.bmd.Test.Bitmap;

@Value
public class BmdFile {
  RawBmdFile rawBmdFile;

  public int getSize() {
    return rawBmdFile.getFrameInfo().size();
  }

  public BufferedImage getFrame(int frame, byte[] palette) throws BmdDecodeException {
    try {
      BmdFrameInfo frameInfo = rawBmdFile.getFrameInfo().get(frame);
      if (frameInfo.getType() == 0) {
        return null;
      } else {
        Bitmap bmp = extractFrame(rawBmdFile, frameInfo, palette);
        return convertToImage(bmp);
      }
    } catch (Exception ex) {
      throw new BmdDecodeException("Unable to decode frame " + frame, ex);
    }
  }

  // TODO Speedup would be to decode the frame without palette to be able to change it dynamically
  private Bitmap extractFrame(RawBmdFile bmdFile, BmdFrameInfo frameInfo, byte[] palette) {
    int frameType = frameInfo.getType();
    if (frameType != 1 && frameType != 2 && frameType != 4) {
      throw new UnsupportedOperationException("Frame type " + frameType + " is not supported!");
    }
    int frameStart = frameInfo.getOff();
    int frameCount = frameInfo.getLen();
    int width = frameInfo.getWidth();

    byte[] pixels = bmdFile.getPixels();
    int pixelPointer = bmdFile.getRowInfo().get(frameStart).getOffset();

    Bitmap bmp = new Bitmap(width, frameCount + 1);

    for (int row = 0; row < frameCount; row++) {
      BmdFrameRow rowInfo = bmdFile.getRowInfo().get(row + frameStart);
      if (isEmpty(rowInfo)) {
        continue;
      }
      int indent = rowInfo.getIndent();
      int i = indent;

      int pixelBlockLength = pixels[pixelPointer++] & 0xFF;

      while (pixelBlockLength != 0) {
        if (pixelBlockLength < 0x80) {
          for (int z = 0; z < pixelBlockLength; z++) {
            int color, alpha;
            if (frameType == BmdFrameInfo.TYPE_EXTENDED) {
              int colorIndex = pixels[pixelPointer++] & 0xFF;
              int pixelLevel = pixels[pixelPointer++] & 0xFF;
              color = getFromPalette(palette, colorIndex);
              alpha = 0xFF;
            } else if (frameType == BmdFrameInfo.TYPE_NORMAL) {
              alpha = 0xFF;
              color = getFromPalette(palette, pixels[pixelPointer++] & 0xFF);
            } else if (frameType == BmdFrameInfo.TYPE_SHADOW) {
              alpha = 0x80;
              color = 0x000000;
            } else {
              alpha = 0xFF;
              color = 0xFF0000;
              // throw new IllegalArgumentException(String.valueOf(frameType));
            }
            bmp.setPixel(i++, row, color | (alpha << 3 * 8));
          }

        } else {
          i += (pixelBlockLength - 0x80);
        }
        pixelBlockLength = pixels[pixelPointer++] & 0xFF;
      }
    }
    return bmp;
  }

  private boolean isEmpty(BmdFrameRow rowInfo) {
    return rowInfo.getOffset() == 0b00111111_11111111_11111111
        && rowInfo.getIndent() == 0b00000011_11111111;
  }

  private int getFromPalette(byte[] palette, int idx) {
    int pointer = idx * 3;
    int r = palette[pointer] & 0xFF;
    int g = palette[pointer + 1] & 0xFF;
    int b = palette[pointer + 2] & 0xFF;

    int color = 0xFF000000;
    color |= r << 8 * 2;
    color |= g << 8 * 1;
    color |= b << 8 * 0;
    return color;
  }

  private BufferedImage convertToImage(Bitmap bmp) {
    if (bmp.getW() == 0 || bmp.getH() == 0) {
      return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    }

    BufferedImage bu = new BufferedImage(bmp.getW(), bmp.getH(), BufferedImage.TYPE_INT_ARGB);
    bu.getRaster().setDataElements(0, 0, bmp.getW(), bmp.getH(), bmp.getColors());
    return bu;
  }
}
