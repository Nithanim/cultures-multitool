package me.nithanim.cultures.multitool;

import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import lombok.SneakyThrows;
import me.nithanim.cultures.format.bmd.BmdDecodeException;
import me.nithanim.cultures.format.bmd.BmdFile;
import me.nithanim.cultures.format.bmd.LittleEndianDataInputStream;
import me.nithanim.cultures.format.bmd.RawBmdFile;
import me.nithanim.cultures.format.bmd.RawBmdFileReader;
import me.nithanim.cultures.format.pcx.PcxFile;
import me.nithanim.cultures.format.pcx.PcxFileReader;
import me.nithanim.cultures.multitool.MainController.TreeData;

public class BmdToolController implements Initializable {
  @FXML private TextField tfBmdPath;
  @FXML private CheckBox chbBmdSync;
  @FXML private TextField tfPcxPath;
  @FXML private CheckBox chbPcxSync;
  @FXML private FlowPane paneFrames;

  private BmdFile bmdFile;
  private PcxFile pcxFile;

  @Override
  public void initialize(URL location, ResourceBundle resources) {}

  @SneakyThrows
  private void setBmd(TreeData treeData) {
    tfBmdPath.setText(treeData.getFullPath());
    bmdFile = readBmd(treeData.getData().getInputStream());
    updateFrames();
  }

  @SneakyThrows
  private void setPcx(TreeData treeData) {
    tfPcxPath.setText(treeData.getFullPath());
    pcxFile = new PcxFileReader().read(treeData.getData().getInputStream());
    updateFrames();
  }

  private void updateFrames() throws BmdDecodeException {
    if (bmdFile != null && pcxFile != null) {
      paneFrames.getChildren().clear();
      for (int i = 0; i < bmdFile.getSize(); i++) {
        BufferedImage img = bmdFile.getFrame(i, pcxFile.getPalette());
        if (img != null) {
          Image image = FxUtil.convertToFxImage(img);
          paneFrames.getChildren().add(new ImageView(image));
        }
      }
    }
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
}
