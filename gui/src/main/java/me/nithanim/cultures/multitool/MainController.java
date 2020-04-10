package me.nithanim.cultures.multitool;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Supplier;
import java.util.stream.Collectors;
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
import lombok.SneakyThrows;
import me.nithanim.cultures.format.cif.CifFile;
import me.nithanim.cultures.format.cif.CifFileUtil;
import me.nithanim.cultures.format.lib.io.reading.ReadableLibFile;
import me.nithanim.cultures.format.lib.io.reading.ReadableLibFile.LibFileDirectory;
import me.nithanim.cultures.format.lib.io.reading.ReadableLibFile.LibFileFile;
import me.nithanim.cultures.multitool.viewer.ViewerSideController;
import org.apache.commons.io.IOUtils;
import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular;
import org.kordamp.ikonli.javafx.FontIcon;

public class MainController implements Initializable {
  @FXML private Button btnFileReload;
  @FXML private TextField tfFilePath;
  @FXML private MenuItem menuItemOpen;
  @FXML private MenuItem menuItemExtractAll;
  @FXML private TreeView<TreeData> fileTree;
  @FXML private VBox viewerPane;
  @FXML private CheckBox chbBmdView;

  @SneakyThrows
  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    new ViewerSideController(
        viewerPane, fileTree.getSelectionModel().selectedItemProperty(), chbBmdView);
    menuItemOpen.setOnAction(
        ae -> {
          FileChooser fc = new FileChooser();
          File f = fc.showOpenDialog(fileTree.getScene().getWindow());
          if (f == null) {
            return;
          }
          Path p = f.toPath();
          openFile(p);
        });
    btnFileReload.setOnAction(ae -> openFile(Paths.get(tfFilePath.getText())));

    menuItemExtractAll.setOnAction(this::extractAllAction);
  }

  public void openFile(Path p) {
    try {
      fileTree.setRoot(null);
      readAndUseFile(p);
      tfFilePath.setText(p.toString());
    } catch (Exception ex) {
      viewerPane.getChildren().clear();
      viewerPane.getChildren().add(Util.makeTextAreaWithText(Util.exceptionToString(ex)));
    }
  }

  private void readAndUseFile(Path p) throws IOException {
    ReadableLibFile lib = new ReadableLibFile(p);

    fileTree.setShowRoot(false);
    fileTree.setRoot(buildTree("", lib.getRoot()));
    TreeItem<TreeData> root = fileTree.getRoot();
    if (root != null) {
      root.getChildren().forEach(c -> c.setExpanded(true));
    }
  }

  private TreeItem<TreeData> buildTree(String path, LibFileDirectory rootDir) {
    TreeItem<TreeData> rootItem = new TreeItem<>();
    rootItem.setGraphic(FontIcon.of(FontAwesomeRegular.FILE_ARCHIVE));
    Collection<LibFileDirectory> sortedDirectories =
        rootDir.getDirectories().values().stream()
            .sorted(Comparator.comparing(LibFileDirectory::getName))
            .collect(Collectors.toList());
    for (LibFileDirectory dir : sortedDirectories) {
      TreeItem<TreeData> sub = buildTree(path + "\\" + dir.getName(), dir);
      sub.setValue(new TreeData(true, path + "\\" + dir.getName(), dir.getName(), null));
      sub.setGraphic(FontIcon.of(FontAwesomeRegular.FOLDER));
      rootItem.getChildren().add(sub);
    }
    List<LibFileFile> sortedFiles =
        rootDir.getFiles().values().stream()
            .sorted(Comparator.comparing(LibFileFile::getName))
            .collect(Collectors.toList());
    for (LibFileFile file : sortedFiles) {
      TreeItem<TreeData> item = new TreeItem<>();
      item.setValue(
          new TreeData(
              false,
              path + "\\" + file.getName(),
              file.getName(),
              new Supplier<InputStream>() {
                @Override
                @SneakyThrows
                public InputStream get() {
                  return file.getData().getInputStream();
                }
              }));
      item.setGraphic(FxUtil.getIconForFile(file));
      rootItem.getChildren().add(item);
    }
    return rootItem;
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
