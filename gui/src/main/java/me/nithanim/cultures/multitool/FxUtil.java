package me.nithanim.cultures.multitool;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.nio.IntBuffer;
import java.util.Collection;
import java.util.stream.Collectors;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser.ExtensionFilter;
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

  public static FontIcon getIconForFile(LibFileFile file) {
    return getIconForFile(file.getName());
  }

  public static FontIcon getIconForFile(String fileName) {
    if (fileName.endsWith(".bmd")) {
      return FontIcon.of(FontAwesomeRegular.FILE_IMAGE);
    } else if (fileName.endsWith(".cif")) {
      return FontIcon.of(FontAwesomeRegular.FILE_WORD);
    } else if (fileName.endsWith(".ini")) {
      return FontIcon.of(FontAwesomeRegular.FILE_WORD);
    } else if (fileName.endsWith(".hlt")) {
      return FontIcon.of(FontAwesomeRegular.FILE_WORD);
    } else if (fileName.endsWith(".txt")) {
      return FontIcon.of(FontAwesomeRegular.FILE_WORD);
    } else if (fileName.endsWith(".bmp")) {
      return FontIcon.of(FontAwesomeRegular.FILE_IMAGE);
    } else if (fileName.endsWith(".fnt")) {
      return FontIcon.of(FontAwesomeRegular.FILE_IMAGE);
    } else if (fileName.endsWith(".pcx")) {
      return FontIcon.of(FontAwesomeRegular.FILE_IMAGE);
    } else if (fileName.endsWith(".c2m")) {
      return FontIcon.of(FontAwesomeSolid.GLOBE_EUROPE);
    } else if (fileName.endsWith(".dat")) {
      return FontIcon.of(FontAwesomeSolid.GLOBE_EUROPE);
    } else if (fileName.endsWith(".wav")) {
      return FontIcon.of(FontAwesomeRegular.FILE_AUDIO);
    } else {
      return null;
    }
  }

  public static ExtensionFilter generateExtensionFilterForAllTypes(
      ObservableList<ExtensionFilter> efs) {
    return new ExtensionFilter(
        "All known",
        efs.stream()
            .map(ExtensionFilter::getExtensions)
            .flatMap(Collection::stream)
            .distinct()
            .collect(Collectors.toList()));
  }
}
