package me.nithanim.cultures.multitool;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
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
  @FXML private TreeView<TreeData> fileTree;
  @FXML private VBox viewerPane;
  @FXML private CheckBox chbBmdView;

  private Parent bmdToolParent;
  private BmdToolController bmdToolController;

  @SneakyThrows
  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    menuItemOpen.setOnAction(
        ae -> {
          FileChooser fc = new FileChooser();
          File f = fc.showOpenDialog(fileTree.getScene().getWindow());
          if (f == null) {
            return;
          }
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

    chbBmdView
        .selectedProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              TreeItem<TreeData> item = fileTree.getSelectionModel().getSelectedItem();
              TreeData value = item == null ? null : item.getValue();
              if (newValue) {
                viewerPane.getChildren().clear();
                if (bmdToolParent == null) {
                  initBmdTool();
                }
                VBox.setVgrow(bmdToolParent, Priority.ALWAYS);
                viewerPane.getChildren().add(bmdToolParent);
                if (value != null) {
                  bmdToolController.onTreeChange(value);
                }
              } else {
                onFileTreeSelectionChanged(value);
              }
            });
  }

  private void readFile(Path p) throws IOException {
    ReadableLibFile lib = new ReadableLibFile(p);

    fileTree.setShowRoot(false);
    fileTree.setRoot(buildTree("", lib.getRoot()));
    fileTree
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (observable, oldValue, newValue) ->
                onFileTreeSelectionChanged(newValue == null ? null : newValue.getValue()));
  }

  private void onFileTreeSelectionChanged(TreeData value) {
    if (value != null) {
      if (chbBmdView.isSelected()) {
        bmdToolController.onTreeChange(value);
      } else {
        ItemHandler handler = value.getHandler();
        viewerPane.getChildren().clear();
        if (handler != null) {
          try {
            handler.display(viewerPane);
          } catch (Exception ex) {
            viewerPane.getChildren().add(Util.makeTextAreaWithText(Util.exceptionToString(ex)));
          }
        }
        if (bmdToolController != null) {
          bmdToolController.onTreeChange(value);
        }
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
  private void initBmdTool() {
    FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/fxml/bmdtool.fxml"));
    bmdToolController = new BmdToolController();
    loader.setController(bmdToolController);
    loader.setClassLoader(this.getClass().getClassLoader());
    bmdToolParent = loader.load();
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
