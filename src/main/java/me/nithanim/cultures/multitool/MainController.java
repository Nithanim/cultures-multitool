package me.nithanim.cultures.multitool;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.SneakyThrows;
import lombok.Value;
import me.nithanim.cultures.format.cif.CifFile;
import me.nithanim.cultures.format.cif.CifFileUtil;
import me.nithanim.cultures.format.lib.io.reading.ReadableLibFile;
import me.nithanim.cultures.format.lib.io.reading.ReadableLibFile.LibFileDirectory;
import me.nithanim.cultures.format.lib.io.reading.ReadableLibFile.LibFileFile;
import me.nithanim.cultures.format.pcx.PcxFileReader;
import me.nithanim.cultures.format.pcx.PcxFile;
import me.nithanim.cultures.format.pcx.PcxUtil;
import org.apache.commons.io.IOUtils;
import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class MainController implements Initializable {
  @FXML private MenuItem menuItemOpen;
  @FXML private TreeView<TreeData> fileTree;
  @FXML private VBox viewerPane;

  @SneakyThrows
  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    Path p =
        Paths.get(
            "/mount/data/games/weltwunder/dosdevices/c:/GOG Games/8th Wonder of the World/DataX/Libs/data0001.lib");
    ReadableLibFile lib = new ReadableLibFile(p);

    fileTree.setShowRoot(false);
    fileTree.setRoot(buildTree(lib.getRoot()));
    fileTree
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue.getValue() != null) {
                ItemHandler handler = newValue.getValue().getHandler();
                viewerPane.getChildren().clear();
                if (handler != null) {
                  try {
                    handler.display(viewerPane);
                  } catch (Exception ex) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ex.printStackTrace(new PrintWriter(baos));
                    viewerPane.getChildren().add(makeItemHandlerTextArea(baos.toString()));
                  }
                }
              }
            });
  }

  private TreeItem<TreeData> buildTree(LibFileDirectory rootDir) {
    TreeItem<TreeData> rootItem = new TreeItem<>();
    rootItem.setGraphic(FontIcon.of(FontAwesomeRegular.FILE_ARCHIVE));
    for (LibFileDirectory dir : rootDir.getDirectories().values()) {
      TreeItem<TreeData> sub = buildTree(dir);
      sub.setValue(new TreeData(true, dir.getName(), null));
      sub.setGraphic(FontIcon.of(FontAwesomeRegular.FOLDER));
      rootItem.getChildren().add(sub);
    }
    for (LibFileFile file : rootDir.getFiles().values()) {
      TreeItem<TreeData> item = new TreeItem<>();
      item.setValue(new TreeData(false, file.getName(), getItemHandler(file)));
      item.setGraphic(getIconForFile(file));
      rootItem.getChildren().add(item);
    }
    return rootItem;
  }

  private ItemHandler getItemHandler(LibFileFile file) {
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

  private void handleBmp(LibFileFile file, Pane pane) throws IOException {
    Image img = new Image(file.getData().getInputStream());
    pane.getChildren().add(new ImageView(img));
  }

  private void handleTxt(LibFileFile file, Pane pane) throws IOException {
    String text =
        IOUtils.toString(file.getData().getInputStream(), StandardCharsets.ISO_8859_1);
    TextArea textArea = makeItemHandlerTextArea(text);
    pane.getChildren().add(textArea);
  }

  private void handleCif(LibFileFile file, Pane pane) throws IOException {
    byte[] bytes = IOUtils.toByteArray(file.getData().getInputStream());
    CifFile cif = CifFileUtil.unpack(bytes);
    TextArea textArea = makeItemHandlerTextArea(String.join("\n", cif.getLines()));
    pane.getChildren().add(textArea);
  }

  private void handlePcx(LibFileFile file, Pane pane) {
    try {
      PcxFile pcx = new PcxFileReader().read(file.getData().getInputStream());
      Image image = convertToFxImage(PcxUtil.convertToImage(pcx));
      InvalidationListener il =
          observable -> {
            if (!image.isBackgroundLoading()) {
              if (image.isError()) {
                pane.getChildren()
                    .add(
                        makeItemHandlerTextArea(exceptionToString(pane, image.getException())));
              } else {
                pane.getChildren().add(new ImageView(image));
              }
            }
          };
      image.errorProperty().addListener(il);
      il.invalidated(image.errorProperty());
    } catch (Exception ex) {
      pane.getChildren().clear();
      pane.getChildren().add(makeItemHandlerTextArea(exceptionToString(pane, ex)));
    }
  }

  private String exceptionToString(Pane pane, Exception ex) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintWriter pw = new PrintWriter(baos);
    ex.printStackTrace(pw);
    pw.flush();
    pane.getChildren().clear();
    return baos.toString();
  }

  private TextArea makeItemHandlerTextArea(String text) {
    TextArea textArea = new TextArea();
    textArea.setEditable(false);
    textArea.setText(text);
    textArea.setMaxHeight(Double.MAX_VALUE);
    textArea.setMaxWidth(Double.MAX_VALUE);
    textArea.setWrapText(true);
    VBox.setVgrow(textArea, Priority.ALWAYS);
    return textArea;
  }

  @Value
  private static class TreeData {
    boolean isDir;
    String name;
    ItemHandler handler;

    @Override
    public String toString() {
      return name;
    }
  }

  private interface ItemHandler {
    void display(Pane pane) throws Exception;
  }

  private FontIcon getIconForFile(LibFileFile file) {
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

  private static Image convertToFxImage(BufferedImage image) {
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
}
