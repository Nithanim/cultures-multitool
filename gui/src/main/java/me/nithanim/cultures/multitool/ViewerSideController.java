package me.nithanim.cultures.multitool;

import java.io.IOException;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.SneakyThrows;
import me.nithanim.cultures.multitool.MainController.TreeData;

public class ViewerSideController {
  private Parent bmdToolParent;
  private BmdToolController bmdToolController;

  private VBox viewerPane;

  private CheckBox chbBmdView;
  private ReadOnlyObjectProperty<TreeItem<TreeData>> treeSelectedItem;

  private boolean bmdOpen = false;

  public ViewerSideController(
      VBox viewerPane,
      ReadOnlyObjectProperty<TreeItem<TreeData>> treeSelectedItem,
      CheckBox chbBmdView) {
    this.viewerPane = viewerPane;
    this.treeSelectedItem = treeSelectedItem;
    this.chbBmdView = chbBmdView;

    initBmdTool();
    chbBmdView
        .selectedProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              TreeItem<TreeData> item = treeSelectedItem.get();
              TreeData value = item == null ? null : item.getValue();
              handle(value);
            });

    treeSelectedItem.addListener(
        (observable, oldValue, newValue) -> handle(newValue == null ? null : newValue.getValue()));
  }

  private void handle(TreeData value) {
    try {
      _handle(viewerPane, value);
    } catch (Exception ex) {
      viewerPane.getChildren().clear();
      viewerPane.getChildren().add(Util.makeTextAreaWithText(Util.exceptionToString(ex)));
    }
  }

  public void _handle(VBox pane, TreeData file) throws IOException {
    if (file == null) {
      pane.getChildren().clear();
      return;
    }
    boolean newBmdOpen = false;
    if (file.getName().endsWith(".txt") || file.getName().endsWith(".hlt")) {
      pane.getChildren().clear();
      ViewerSideHandlers.handleTxt(file, pane);
    } else if (file.getName().endsWith(".cif")) {
      pane.getChildren().clear();
      ViewerSideHandlers.handleCif(file, pane);
    } else if (file.getName().endsWith(".bmp")) {
      pane.getChildren().clear();
      ViewerSideHandlers.handleBmp(file, pane);
    } else if (file.getName().endsWith(".pcx")) {
      if (!chbBmdView.isSelected()) {
        pane.getChildren().clear();
        ViewerSideHandlers.handlePcx(file, pane);
      } else {
        if (!bmdOpen) {
          showBmdTool(pane);
        }
        newBmdOpen = true;
      }
      bmdToolController.onTreeChange(file);
    } else if (file.getName().endsWith(".bmd")) {
      if (!bmdOpen) {
        showBmdTool(pane);
      }
      newBmdOpen = true;
      bmdToolController.onTreeChange(file);
    }
    bmdOpen = newBmdOpen;
  }

  private void showBmdTool(VBox viewerPane) {
    viewerPane.getChildren().clear();
    VBox.setVgrow(bmdToolParent, Priority.ALWAYS);
    viewerPane.getChildren().add(bmdToolParent);
  }

  @SneakyThrows
  private void initBmdTool() {
    FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/fxml/bmdtool.fxml"));
    bmdToolController = new BmdToolController();
    loader.setController(bmdToolController);
    loader.setClassLoader(this.getClass().getClassLoader());
    bmdToolParent = loader.load();
  }
}
