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

    treeSelectedItem.addListener(
        (observable, oldValue, newValue) ->
            onFileTreeSelectionChanged(newValue == null ? null : newValue.getValue()));
  }

  private void onFileTreeSelectionChanged(TreeData value) {
    if (value != null) {
      if (chbBmdView.isSelected()) {
        bmdToolController.onTreeChange(value);
      } else {
        viewerPane.getChildren().clear();
        try {
          handle(viewerPane, value);
        } catch (Exception ex) {
          viewerPane.getChildren().add(Util.makeTextAreaWithText(Util.exceptionToString(ex)));
        }
        if (bmdToolController != null) {
          bmdToolController.onTreeChange(value);
        }
      }
    } else {
      viewerPane.getChildren().clear();
    }
  }

  @SneakyThrows
  private void initBmdTool() {
    FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/fxml/bmdtool.fxml"));
    bmdToolController = new BmdToolController();
    loader.setController(bmdToolController);
    loader.setClassLoader(this.getClass().getClassLoader());
    bmdToolParent = loader.load();
  }

  public void handle(VBox pane, TreeData file) throws IOException {
    if (file.getName().endsWith(".txt") || file.getName().endsWith(".hlt")) {
      ViewerSideHandlers.handleTxt(file, pane);
    } else if (file.getName().endsWith(".cif")) {
      ViewerSideHandlers.handleCif(file, pane);
    } else if (file.getName().endsWith(".bmp")) {
      ViewerSideHandlers.handleBmp(file, pane);
    } else if (file.getName().endsWith(".pcx")) {
      ViewerSideHandlers.handlePcx(file, pane);
    } else {
      return;
    }
  }
}
