package me.nithanim.cultures.multitool;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import lombok.Value;
import me.nithanim.cultures.format.lib.io.reading.FileData;
import me.nithanim.cultures.format.lib.io.reading.ReadableLibFile;
import me.nithanim.cultures.format.lib.io.reading.ReadableLibFile.LibFileDirectory;
import me.nithanim.cultures.format.lib.io.reading.ReadableLibFile.LibFileFile;
import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular;
import org.kordamp.ikonli.javafx.FontIcon;

public class MainController implements Initializable {
  @FXML private MenuItem menuItemOpen;
  @FXML private MenuItem menuItemBmdTool;
  @FXML private TreeView<TreeData> fileTree;
  @FXML private VBox viewerPane;

  private Stage bmdToolStage;
  private BmdToolController bmdToolController;

  @SneakyThrows
  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    menuItemBmdTool.setOnAction(this::openBmdTool);
    menuItemOpen.setOnAction(
        ae -> {
          FileChooser fc = new FileChooser();
          File f = fc.showOpenDialog(fileTree.getScene().getWindow());
          try {
            readFile(f.toPath());
          } catch (Exception ex) {
            viewerPane.getChildren().clear();
            viewerPane.getChildren().add(Util.makeTextAreaWithText(Util.exceptionToString(ex)));
          }
        });

    try {
      Path p =
          Paths.get(
              "/mount/data/games/weltwunder/dosdevices/c:/GOG Games/8th Wonder of the World/DataX/Libs/data0001.lib");
      readFile(p);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void readFile(Path p) throws IOException {
    ReadableLibFile lib = new ReadableLibFile(p);

    fileTree.setShowRoot(false);
    fileTree.setRoot(buildTree("", lib.getRoot()));
    fileTree
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(this::onFileTreeSelectionChanged);
  }

  private void onFileTreeSelectionChanged(
      ObservableValue<? extends TreeItem<TreeData>> observable,
      TreeItem<TreeData> oldValue,
      TreeItem<TreeData> newValue) {
    if (newValue.getValue() != null) {
      ItemHandler handler = newValue.getValue().getHandler();
      viewerPane.getChildren().clear();
      if (handler != null) {
        try {
          handler.display(viewerPane);
        } catch (Exception ex) {
          viewerPane.getChildren().clear();
          viewerPane.getChildren().add(Util.makeTextAreaWithText(Util.exceptionToString(ex)));
        }
      }
      if (bmdToolController != null) {
        bmdToolController.onTreeChange(newValue.getValue());
      }
    }
  }

  private TreeItem<TreeData> buildTree(String path, LibFileDirectory rootDir) {
    TreeItem<TreeData> rootItem = new TreeItem<>();
    rootItem.setGraphic(FontIcon.of(FontAwesomeRegular.FILE_ARCHIVE));
    for (LibFileDirectory dir : rootDir.getDirectories().values()) {
      TreeItem<TreeData> sub = buildTree(path + "\\" + dir.getName(), dir);
      sub.setValue(new TreeData(true, path + "\\" + dir.getName(), dir.getName(), null, null));
      sub.setGraphic(FontIcon.of(FontAwesomeRegular.FOLDER));
      rootItem.getChildren().add(sub);
    }
    for (LibFileFile file : rootDir.getFiles().values()) {
      TreeItem<TreeData> item = new TreeItem<>();
      item.setValue(
          new TreeData(
              false,
              path + "\\" + file.getName(),
              file.getName(),
              file.getData(),
              ItemHandler.of(file)));
      item.setGraphic(FxUtil.getIconForFile(file));
      rootItem.getChildren().add(item);
    }
    return rootItem;
  }

  @SneakyThrows
  private void openBmdTool(ActionEvent ae) {
    if (bmdToolStage == null) {
      FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/fxml/bmdtool.fxml"));
      bmdToolController = new BmdToolController();
      loader.setController(bmdToolController);
      loader.setClassLoader(this.getClass().getClassLoader());
      Parent root = loader.load();
      Scene scene = new Scene(root);
      scene.getStylesheets().add(this.getClass().getResource("/styles/styles.css").toString());
      bmdToolStage = new Stage();
      bmdToolStage.setTitle("BmdTool - Cultures Multitool");
      bmdToolStage.setScene(scene);
    }
    bmdToolStage.show();
  }

  @Value
  static class TreeData {
    boolean isDir;
    String fullPath;
    String name;
    FileData data;
    ItemHandler handler;

    @Override
    public String toString() {
      return name;
    }
  }
}
