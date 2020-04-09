package me.nithanim.cultures.multitool.viewer;

import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;
import javax.imageio.ImageIO;
import lombok.SneakyThrows;
import me.nithanim.cultures.format.bmd.BmdDecodeException;
import me.nithanim.cultures.format.bmd.BmdFile;
import me.nithanim.cultures.format.bmd.LittleEndianDataInputStream;
import me.nithanim.cultures.format.bmd.RawBmdFile;
import me.nithanim.cultures.format.bmd.RawBmdFileReader;
import me.nithanim.cultures.format.bmd.RawBmdFileReader.BmdFrameInfo;
import me.nithanim.cultures.format.pcx.PcxFile;
import me.nithanim.cultures.format.pcx.PcxFileReader;
import me.nithanim.cultures.multitool.FxUtil;
import me.nithanim.cultures.multitool.TreeData;

public class BmdToolController implements Initializable {
  @FXML private TextField tfBmdPath;
  @FXML private CheckBox chbBmdSync;
  @FXML private TextField tfPcxPath;
  @FXML private CheckBox chbPcxSync;
  @FXML private FlowPane paneFrames;
  @FXML private Label lblFrameStats;
  @FXML private CheckBox chbFrameSelection;
  @FXML private TextField tfFrameSelection;

  private BmdFile bmdFile;
  private PcxFile pcxFile;
  private ContextMenu contextMenu;
  private Map<ImageView, BufferedImage> imageMap = new HashMap<>();

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    chbFrameSelection
        .selectedProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              tfFrameSelection.setDisable(!newValue);
              updateFrames();
            });
    tfFrameSelection.textProperty().addListener(observable -> updateFrames());

    contextMenu = new ContextMenu();
    MenuItem menuItem1 = new MenuItem("Export as ...");
    menuItem1.setOnAction(
        ae -> {
          Node source = getContextMenuSourceNode(ae);
          if (source instanceof ImageView) {
            exportImage((ImageView) source);
          }
        });
    contextMenu.getItems().add(menuItem1);
  }

  @SneakyThrows
  private void exportImage(ImageView source) {
    BufferedImage img = imageMap.get(source);
    FileChooser fc = new FileChooser();
    fc.getExtensionFilters().add(new ExtensionFilter("PNG Image", "*.png"));
    fc.setSelectedExtensionFilter(fc.getExtensionFilters().get(0));
    File target = fc.showSaveDialog(paneFrames.getScene().getWindow());
    if (target != null) {
      if (!target.getName().endsWith(".png")) {
        target = new File(target.getParent(), target.getName() + ".png");
      }
      ImageIO.write(img, "png", target);
    }
  }

  @SneakyThrows
  private void setBmd(TreeData treeData) {
    tfBmdPath.setText(treeData.getFullPath());
    bmdFile = readBmd(treeData.getData().get());
    onFileSelectionChanged();
  }

  @SneakyThrows
  private void setPcx(TreeData treeData) {
    tfPcxPath.setText(treeData.getFullPath());
    pcxFile = new PcxFileReader().read(treeData.getData().get());
    onFileSelectionChanged();
  }

  private void onFileSelectionChanged() {
    updateBmdStats();
    updateFrames();
  }

  private void updateBmdStats() {
    if (bmdFile == null) {
      lblFrameStats.setText("");
    } else {
      StringBuilder sb = new StringBuilder();
      sb.append("Frames: ").append(bmdFile.getSize()).append('\n');
      Map<Integer, Long> byFrameType =
          bmdFile.getRawBmdFile().getFrameInfo().stream()
              .map(BmdFrameInfo::getType)
              .collect(Collectors.groupingBy(i -> i, Collectors.counting()));
      sb.append("Frames Type 1: ").append(byFrameType.getOrDefault(1, 0L)).append('\n');
      sb.append("Frames Type 2: ").append(byFrameType.getOrDefault(2, 0L)).append('\n');
      sb.append("Frames Type 4: ").append(byFrameType.getOrDefault(4, 0L)).append('\n');
      sb.append("Frames empty: ")
          .append(
              bmdFile.getRawBmdFile().getFrameInfo().stream()
                  .filter(i -> i.getWidth() == 0 || i.getLen() == 0)
                  .count())
          .append('\n');
      lblFrameStats.setText(sb.toString());
    }
  }

  @SneakyThrows
  private void updateFrames() {
    if (bmdFile != null && pcxFile != null) {
      imageMap.clear();
      paneFrames.getChildren().clear();
      int start = getRenderingStartFrame();
      int end = getRenderingEndFrame();
      for (int i = start; i < end; i++) {
        addFrameToView(i);
      }
    }
  }

  private int getRenderingStartFrame() {
    int userSelectedFrame = getUserSelectedFrame();
    return Math.max(userSelectedFrame, 0);
  }

  private int getRenderingEndFrame() {
    int userSelectedFrame = getUserSelectedFrame();
    if (userSelectedFrame >= 0) {
      return Math.min(userSelectedFrame + 1, bmdFile.getSize());
    } else {
      return bmdFile.getSize();
    }
  }

  private int getUserSelectedFrame() {
    if (chbFrameSelection.isSelected()) {
      try {
        return Integer.parseInt(tfFrameSelection.getText());
      } catch (NumberFormatException ex) {
        return -1;
      }
    } else {
      return -1;
    }
  }

  private void addFrameToView(int i) throws BmdDecodeException {
    BufferedImage img = bmdFile.getFrame(i, pcxFile.getPalette());
    if (img != null) {
      Image image = FxUtil.convertToFxImage(img);
      ImageView imageView = new ImageView(image);
      setTooltipCreator(i, imageView);
      imageView.setOnContextMenuRequested(this::showContextMenu);
      paneFrames.getChildren().add(imageView);
      imageMap.put(imageView, img);
    }
  }

  private void showContextMenu(ContextMenuEvent cme) {
    contextMenu.show((Node) cme.getSource(), Side.BOTTOM, 0, 0);
  }

  private void setTooltipCreator(int i, ImageView imageView) {
    imageView.setOnMouseEntered(
        me -> {
          Tooltip.install(
              imageView, createTooltip(i, bmdFile.getRawBmdFile().getFrameInfo().get(i)));
          imageView.setOnMouseEntered(null);
        });
  }

  private Tooltip createTooltip(int id, BmdFrameInfo frameInfo) {
    StringBuilder sb = new StringBuilder();
    sb.append("#").append(id).append("\n");
    sb.append("Type: ").append(frameInfo.getType()).append("\n");
    sb.append("dx: ").append(frameInfo.getDx()).append("\n");
    sb.append("dy: ").append(frameInfo.getDy()).append("\n");
    sb.append("Width: ").append(frameInfo.getWidth()).append("\n");
    sb.append("Len: ").append(frameInfo.getLen()).append("\n");
    sb.append("Off: ").append(frameInfo.getOff());
    Tooltip tooltip = new Tooltip(sb.toString());
    tooltip.setShowDelay(Duration.ZERO);
    tooltip.setShowDuration(Duration.hours(1));
    return tooltip;
  }

  public void onTreeChange(TreeData treeData) {
    String name = treeData.getName();
    if (name.endsWith(".bmd")) {
      if (chbBmdSync.isSelected()) {
        setBmd(treeData);
      }
    }
    if (name.endsWith(".pcx")) {
      if (chbPcxSync.isSelected()) {
        setPcx(treeData);
      }
    }
  }

  private BmdFile readBmd(InputStream in) throws IOException {
    RawBmdFile rawBmd =
        new RawBmdFileReader().read(new DataInputStream(new LittleEndianDataInputStream(in)));
    return new BmdFile(rawBmd);
  }

  private Node getContextMenuSourceNode(ActionEvent ae) {
    MenuItem source = (MenuItem) ae.getTarget();
    ContextMenu popup = source.getParentPopup();
    if (popup != null) {
      Node ownerNode = popup.getOwnerNode();
      if (ownerNode instanceof ImageView) {
        return ownerNode;
      }
    }
    return null;
  }
}
