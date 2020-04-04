package me.nithanim.cultures.multitool;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.nio.IntBuffer;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import me.nithanim.cultures.format.lib.io.reading.ReadableLibFile.LibFileFile;
import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class FxUtil {

  public static Image convertToFxImage(BufferedImage bufferedImage) {
    WritableImage writableImage =
        new WritableImage(bufferedImage.getWidth(), bufferedImage.getHeight());
    WritableRaster raster = bufferedImage.getRaster();
    SampleModel sm = raster.getSampleModel();
    int scanStride;
    if (sm instanceof SinglePixelPackedSampleModel) {
      scanStride = ((SinglePixelPackedSampleModel) sm).getScanlineStride();
    } else {
      scanStride = 0;
    }

    PixelFormat<IntBuffer> pixelFormat =
        bufferedImage.isAlphaPremultiplied()
            ? PixelFormat.getIntArgbPreInstance()
            : PixelFormat.getIntArgbInstance();

    int dataBufferOffset = raster.getDataBuffer().getOffset();
    DataBufferInt db = (DataBufferInt) raster.getDataBuffer();
    writableImage
        .getPixelWriter()
        .setPixels(
            0,
            0,
            bufferedImage.getWidth(),
            bufferedImage.getHeight(),
            pixelFormat,
            db.getData(),
            dataBufferOffset,
            scanStride);
    return writableImage;
  }

  static FontIcon getIconForFile(LibFileFile file) {
    if (file.getName().endsWith(".bmd")) {
      return FontIcon.of(FontAwesomeRegular.FILE_IMAGE);
    } else if (file.getName().endsWith(".cif")) {
      return FontIcon.of(FontAwesomeRegular.FILE_WORD);
    } else if (file.getName().endsWith(".hlt")) {
      return FontIcon.of(FontAwesomeRegular.FILE_WORD);
    } else if (file.getName().endsWith(".txt")) {
      return FontIcon.of(FontAwesomeRegular.FILE_WORD);
    } else if (file.getName().endsWith(".bmp")) {
      return FontIcon.of(FontAwesomeRegular.FILE_IMAGE);
    } else if (file.getName().endsWith(".fnt")) {
      return FontIcon.of(FontAwesomeRegular.FILE_IMAGE);
    } else if (file.getName().endsWith(".pcx")) {
      return FontIcon.of(FontAwesomeRegular.FILE_IMAGE);
    } else if (file.getName().endsWith(".c2m")) {
      return FontIcon.of(FontAwesomeSolid.GLOBE_EUROPE);
    } else if (file.getName().endsWith(".dat")) {
      return FontIcon.of(FontAwesomeSolid.GLOBE_EUROPE);
    } else if (file.getName().endsWith(".wav")) {
      return FontIcon.of(FontAwesomeRegular.FILE_AUDIO);
    } else {
      return null;
    }
  }
}
