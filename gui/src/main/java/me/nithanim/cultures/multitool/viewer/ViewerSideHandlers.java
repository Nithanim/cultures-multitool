package me.nithanim.cultures.multitool.viewer;

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
import me.nithanim.cultures.format.pcx.PcxFile;
import me.nithanim.cultures.format.pcx.PcxFileReader;
import me.nithanim.cultures.format.pcx.PcxUtil;
import me.nithanim.cultures.multitool.FxUtil;
import me.nithanim.cultures.multitool.TreeData;
import me.nithanim.cultures.multitool.Util;
import org.apache.commons.io.IOUtils;

public class ViewerSideHandlers {

  public static void handleTxt(TreeData file, Pane pane) throws IOException {
    String text = IOUtils.toString(file.getData().get(), StandardCharsets.ISO_8859_1);
    TextArea textArea = Util.makeTextAreaWithText(text);
    pane.getChildren().add(textArea);
  }

  public static void handleBmp(TreeData file, Pane pane) throws IOException {
    Image img = new Image(file.getData().get());
    pane.getChildren().add(new ImageView(img));
  }

  public static void handleCif(TreeData file, Pane pane) throws IOException {
    byte[] bytes = IOUtils.toByteArray(file.getData().get());
    CifFile cif = CifFileUtil.unpack(bytes);
    TextArea textArea = Util.makeTextAreaWithText(String.join("\n", cif.getLines()));
    pane.getChildren().add(textArea);
  }

  public static void handlePcx(TreeData file, Pane pane) {
    try {
      PcxFile pcx = new PcxFileReader().read(file.getData().get());
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
