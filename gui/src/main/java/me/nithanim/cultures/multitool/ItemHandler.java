package me.nithanim.cultures.multitool;

import static me.nithanim.cultures.multitool.Util.exceptionToString;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javafx.beans.InvalidationListener;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import me.nithanim.cultures.format.cif.CifFile;
import me.nithanim.cultures.format.cif.CifFileUtil;
import me.nithanim.cultures.format.lib.io.reading.ReadableLibFile.LibFileFile;
import me.nithanim.cultures.format.pcx.PcxFile;
import me.nithanim.cultures.format.pcx.PcxFileReader;
import me.nithanim.cultures.format.pcx.PcxUtil;
import org.apache.commons.io.IOUtils;

interface ItemHandler {
  void display(Pane pane) throws Exception;

  public static ItemHandler of(LibFileFile file) {
    if (file.getName().endsWith(".txt") || file.getName().endsWith(".hlt")) {
      return pane -> handleTxt(file, pane);
    } else if (file.getName().endsWith(".cif")) {
      return pane -> handleCif(file, pane);
    } else if (file.getName().endsWith(".bmp")) {
      return pane -> handleBmp(file, pane);
    } else if (file.getName().endsWith(".pcx")) {
      return pane -> handlePcx(file, pane);
    } else {
      return null;
    }
  }

  private static void handleBmp(LibFileFile file, Pane pane) throws IOException {
    Image img = new Image(file.getData().getInputStream());
    pane.getChildren().add(new ImageView(img));
  }

  private static void handleTxt(LibFileFile file, Pane pane) throws IOException {
    String text = IOUtils.toString(file.getData().getInputStream(), StandardCharsets.ISO_8859_1);
    TextArea textArea = Util.makeTextAreaWithText(text);
    pane.getChildren().add(textArea);
  }

  private static void handleCif(LibFileFile file, Pane pane) throws IOException {
    byte[] bytes = IOUtils.toByteArray(file.getData().getInputStream());
    CifFile cif = CifFileUtil.unpack(bytes);
    TextArea textArea = Util.makeTextAreaWithText(String.join("\n", cif.getLines()));
    pane.getChildren().add(textArea);
  }

  private static void handlePcx(LibFileFile file, Pane pane) {
    try {
      PcxFile pcx = new PcxFileReader().read(file.getData().getInputStream());
      Image image = FxUtil.convertToFxImage(PcxUtil.convertToImage(pcx));
      InvalidationListener il =
          observable -> {
            if (!image.isBackgroundLoading()) {
              if (image.isError()) {
                pane.getChildren()
                    .add(Util.makeTextAreaWithText(exceptionToString(image.getException())));
              } else {
                pane.getChildren().add(new ImageView(image));
              }
            }
          };
      image.errorProperty().addListener(il);
      il.invalidated(image.errorProperty());
    } catch (Exception ex) {
      pane.getChildren().clear();
      pane.getChildren().add(Util.makeTextAreaWithText(exceptionToString(ex)));
    }
  }

}
