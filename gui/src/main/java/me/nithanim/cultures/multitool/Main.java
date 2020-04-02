package me.nithanim.cultures.multitool;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import picocli.CommandLine;

public class Main extends Application {
  @Override
  public void start(Stage stage) throws Exception {
    Cli cli = new Cli();
    new CommandLine(cli).parseArgs(getParameters().getRaw().toArray(String[]::new));

    MainController controller = new MainController();

    FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/fxml/main.fxml"));
    loader.setController(controller);
    loader.setClassLoader(this.getClass().getClassLoader());
    Parent root = loader.load();
    Scene scene = new Scene(root);
    scene.getStylesheets().add(this.getClass().getResource("/styles/styles.css").toString());
    stage.setTitle("Cultures Multitool");
    stage.setScene(scene);
    stage.show();

    if (cli.fileToOpen != null) {
      controller.openFile(cli.fileToOpen);
    }
  }
}
