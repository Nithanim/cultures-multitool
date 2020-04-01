package me.nithanim.cultures.multitool;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
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

  static TextArea makeTextAreaWithText(String text) {
    TextArea textArea = new TextArea();
    textArea.setEditable(false);
    textArea.setText(text);
    textArea.setMaxHeight(Double.MAX_VALUE);
    textArea.setMaxWidth(Double.MAX_VALUE);
    textArea.setWrapText(true);
    VBox.setVgrow(textArea, Priority.ALWAYS);
    return textArea;
  }
}
