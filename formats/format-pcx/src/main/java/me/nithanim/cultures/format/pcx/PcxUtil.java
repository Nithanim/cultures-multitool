package me.nithanim.cultures.format.pcx;

import java.awt.image.BufferedImage;

public class PcxUtil {

  public static int getWidth(PcxHeader header) {
    return header.getXMax() - header.getXMin() + 1;
  }
  public static int getHeight(PcxHeader header) {
    return header.getYMax() - header.getYMin() + 1;
  }


  public static BufferedImage convertToImage(PcxFile pcx) {
    int width = getWidth(pcx.getHeader());
    int height = getHeight(pcx.getHeader());
    BufferedImage bu = new BufferedImage(width,height, BufferedImage.TYPE_INT_ARGB);
    for (int x = 0; x <width; x++) {
      for (int y = 0; y < height; y++) {
        int c = pcx.getPixel(x, y);
        bu.setRGB(x, y, c);
      }
    }
    return bu;
  }
}
