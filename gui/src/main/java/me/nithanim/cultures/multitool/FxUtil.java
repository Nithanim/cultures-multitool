package me.nithanim.cultures.multitool;

import java.awt.image.BufferedImage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import me.nithanim.cultures.format.lib.io.reading.ReadableLibFile.LibFileFile;
import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class FxUtil {
  public static Image convertToFxImage(BufferedImage image) {
    WritableImage wr = null;
    if (image != null) {
      wr = new WritableImage(image.getWidth(), image.getHeight());
      PixelWriter pw = wr.getPixelWriter();
      for (int x = 0; x < image.getWidth(); x++) {
        for (int y = 0; y < image.getHeight(); y++) {
          pw.setArgb(x, y, image.getRGB(x, y));
        }
      }
    }

    return new ImageView(wr).getImage();
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
