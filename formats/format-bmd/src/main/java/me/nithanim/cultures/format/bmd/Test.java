package me.nithanim.cultures.format.bmd;

import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import javax.imageio.ImageIO;
import lombok.Value;
import me.nithanim.cultures.format.bmd.BmdFile.Type4AlphaInterpretation;

public class Test {
  public static void main(String[] args) throws IOException, BmdDecodeException {
    Path pathBmd =
        Paths.get(
            "/mount/data/games/weltwunder/dosdevices/c:/GOG Games/8th Wonder of the World/data/engine2d/bin/bobs/ls_ground_s.bmd");
    Path pathPcx =
        Paths.get(
            "/mount/data/games/weltwunder/dosdevices/c:/GOG Games/8th Wonder of the World/data/engine2d/bin/palettes/landscapes/tree01.pcx");

    RawBmdFile rawBmd =
        new RawBmdFileReader()
            .read(
                new DataInputStream(
                    new LittleEndianDataInputStream(Files.newInputStream(pathBmd))));
    BmdFile bmd = new BmdFile(rawBmd);

    byte[] palette = generateRandomPalette();

    for (int i = 0; i < bmd.getSize(); i++) {
      BufferedImage img = bmd.getFrame(i, palette, Type4AlphaInterpretation.ALPHA);
      if (img != null) {
        ImageIO.write(img, "PNG", Files.newOutputStream(Paths.get("/tmp/test" + i + ".png")));
      }
    }
  }

  @Value
  public static class Bitmap {
    int w;
    int h;
    int[] colors;

    Bitmap(int w, int h) {
      this.w = w;
      this.h = h;
      this.colors = new int[w * h];
    }

    public int getPixel(int x, int y) {
      return colors[y * w + x];
    }

    public void setPixel(int x, int y, int rgba) {
      colors[y * w + x] = rgba;
    }
  }

  private static byte[] generateRandomPalette() {
    byte[] palette = new byte[256 * 3];
    Random rand = new Random(234234);
    for (int i = 0; i < palette.length; i++) {
      palette[i] = (byte) rand.nextInt();
    }
    return palette;
  }
}
