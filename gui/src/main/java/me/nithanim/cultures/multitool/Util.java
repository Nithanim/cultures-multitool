package me.nithanim.cultures.multitool;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class Util {
  public static String exceptionToString(Exception ex) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintWriter pw = new PrintWriter(baos);
    ex.printStackTrace(pw);
    pw.flush();
    return baos.toString();
  }

  public static TextArea makeTextAreaWithText(String text) {
    TextArea textArea = new TextArea();
    textArea.setEditable(false);
    textArea.setText(text);
    textArea.setMaxHeight(Double.MAX_VALUE);
    textArea.setMaxWidth(Double.MAX_VALUE);
    textArea.setWrapText(true);
    VBox.setVgrow(textArea, Priority.ALWAYS);
    return textArea;
  }

  public static Path changeFileExtension(Path p, String ext) {
    String fn = p.getFileName().toString();
    int idx = fn.lastIndexOf('.');
    return p.getParent().resolve(fn.substring(0, idx) + "." + ext);
  }
}
