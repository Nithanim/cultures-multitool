package me.nithanim.cultures.multitool;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
  @Override
  public void start(Stage stage) throws Exception {
    FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/fxml/main.fxml"));
    loader.setController(new MainController());
    loader.setClassLoader(this.getClass().getClassLoader());
    Parent root = loader.load();
    Scene scene = new Scene(root);
    scene.getStylesheets().add(this.getClass().getResource("/styles/styles.css").toString());
    stage.setTitle("Cultures Multitool");
    stage.setScene(scene);
    stage.show();
  }
}
