package me.nithanim.cultures.multitool;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import lombok.SneakyThrows;
import me.nithanim.cultures.format.cif.CifFile;
import me.nithanim.cultures.format.cif.CifFileUtil;
import me.nithanim.cultures.format.lib.io.reading.ReadableLibFile;
import me.nithanim.cultures.multitool.helper.FileTreeBuilder;
import me.nithanim.cultures.multitool.helper.FolderTreeBuilder;
import me.nithanim.cultures.multitool.viewer.ViewerSideController;
import org.apache.commons.io.IOUtils;

public class MainController implements Initializable {
  @FXML private Button btnFileReload;
  @FXML private TextField tfFilePath;
  @FXML private MenuItem menuItemOpenFile;
  @FXML private MenuItem menuItemOpenFolder;
  @FXML private MenuItem menuItemExtractAll;
  @FXML private TreeView<TreeData> fileTree;
  @FXML private VBox viewerPane;
  @FXML private CheckBox chbBmdView;

  @SneakyThrows
  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    new ViewerSideController(
        viewerPane, fileTree.getSelectionModel().selectedItemProperty(), chbBmdView);
    menuItemOpenFile.setOnAction(
        ae -> {
          FileChooser fc = new FileChooser();
          fc.getExtensionFilters().add(new ExtensionFilter("lib/c2m", "*.lib", "*.c2m"));
          fc.getExtensionFilters()
              .add(new ExtensionFilter("cif/tab/sal", "*.cif", "*.tab", "*.sal"));
          fc.getExtensionFilters()
              .add(FxUtil.generateExtensionFilterForAllTypes(fc.getExtensionFilters()));
          fc.setSelectedExtensionFilter(
              fc.getExtensionFilters().get(fc.getExtensionFilters().size() - 1));
          File f = fc.showOpenDialog(fileTree.getScene().getWindow());
          if (f != null) {
            open(f.toPath());
          }
        });
    menuItemOpenFolder.setOnAction(
        ae -> {
          DirectoryChooser dc = new DirectoryChooser();
          File f = dc.showDialog(fileTree.getScene().getWindow());
          if (f != null) {
            open(f.toPath());
          }
        });
    btnFileReload.setOnAction(ae -> open(Paths.get(tfFilePath.getText())));

    menuItemExtractAll.setOnAction(this::extractAllAction);
  }

  public void open(Path p) {
    try {
      fileTree.setRoot(null);
      readAndUsePath(p);
      tfFilePath.setText(p.toString());
    } catch (Exception ex) {
      viewerPane.getChildren().clear();
      viewerPane.getChildren().add(Util.makeTextAreaWithText(Util.exceptionToString(ex)));
    }
  }

  private void readAndUsePath(Path p) throws IOException {
    if (Files.isDirectory(p)) {
      readAndUseFolder(p);
    } else {
      readAndUseFileLib(p);
    }
  }

  private void readAndUseFolder(Path p) throws IOException {
    fileTree.setShowRoot(false);
    fileTree.setRoot(FolderTreeBuilder.buildTree(p));
    TreeItem<TreeData> root = fileTree.getRoot();
    if (root != null) {
      root.getChildren().forEach(c -> c.setExpanded(true));
    }
  }

  private void readAndUseFileLib(Path p) throws IOException {
    SeekableByteChannel channel = Files.newByteChannel(p);
    ReadableLibFile lib = new ReadableLibFile(channel);

    fileTree.setShowRoot(false);
    fileTree.setRoot(FileTreeBuilder.buildTree("", lib.getRoot()));
    TreeItem<TreeData> root = fileTree.getRoot();
    if (root != null) {
      root.getChildren().forEach(c -> c.setExpanded(true));
    }
  }

  @SneakyThrows
  private void extractAllAction(ActionEvent ae) {
    if (fileTree.getRoot() == null) {
      return;
    }
    DirectoryChooser ds = new DirectoryChooser();
    File f = ds.showDialog(fileTree.getScene().getWindow());
    if (f == null) {
      return;
    }
    Alert alert = new Alert(AlertType.CONFIRMATION, "Do you want to decode cif files?");
    boolean decodeCif = alert.showAndWait().orElse(ButtonType.NO) == ButtonType.OK;
    Path p = f.toPath();
    extractAllAction(fileTree.getRoot(), p, decodeCif);
  }

  private void extractAllAction(TreeItem<TreeData> item, Path p, boolean decodeCif)
      throws IOException {
    TreeData v = item.getValue();
    Path n = v == null ? p : p.resolve(v.getName());
    if (v == null || v.isDir()) {
      if (!Files.exists(n)) {
        Files.createDirectory(n);
      }
      for (TreeItem<TreeData> child : item.getChildren()) {
        extractAllAction(child, n, decodeCif);
      }
    } else {
      boolean isCifFile = decodeCif && v.getName().endsWith(".cif");
      Path targetFile = isCifFile ? Util.changeFileExtension(n, "ini") : n;
      try (OutputStream out =
          Files.newOutputStream(
              targetFile, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
        try (InputStream in = v.getData().get()) {
          if (isCifFile) {
            CifFile cif = CifFileUtil.unpack(IOUtils.toByteArray(in));
            IOUtils.write(String.join("\r\n", cif.getLines()), out, StandardCharsets.ISO_8859_1);
          } else {
            in.transferTo(out);
          }
        }
      }
    }
  }
}
