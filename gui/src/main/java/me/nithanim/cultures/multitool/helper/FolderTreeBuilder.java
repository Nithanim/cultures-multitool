package me.nithanim.cultures.multitool.helper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayDeque;
import java.util.function.Supplier;
import javafx.scene.control.TreeItem;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import me.nithanim.cultures.multitool.FxUtil;
import me.nithanim.cultures.multitool.TreeData;
import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular;
import org.kordamp.ikonli.javafx.FontIcon;

@UtilityClass
public class FolderTreeBuilder {

  @SneakyThrows
  public static TreeItem<TreeData> buildTree(Path rootDir) {
    TreeItem<TreeData> rootItem = new TreeItem<>();
    rootItem.setGraphic(FontIcon.of(FontAwesomeRegular.FILE_ARCHIVE));

    Files.walkFileTree(
        rootDir,
        new FileVisitor<>() {
          private final ArrayDeque<TreeItem<TreeData>> stack = new ArrayDeque<>();

          {
            stack.add(rootItem);
          }

          @Override
          public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
              throws IOException {
            TreeItem<TreeData> item = new TreeItem<>();
            item.setValue(new TreeData(true, dir.toString(), dir.getFileName().toString(), null));
            item.setGraphic(FontIcon.of(FontAwesomeRegular.FOLDER));
            stack.add(item);
            return FileVisitResult.CONTINUE;
          }

          @Override
          public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
              throws IOException {
            TreeItem<TreeData> item = new TreeItem<>();
            item.setValue(
                new TreeData(
                    false,
                    file.toString(),
                    file.getFileName().toString(),
                    new Supplier<InputStream>() {
                      @Override
                      @SneakyThrows
                      public InputStream get() {
                        return Files.newInputStream(file);
                      }
                    }));
            item.setGraphic(FxUtil.getIconForFile(file.getFileName().toString()));
            stack.getLast().getChildren().add(item);
            return FileVisitResult.CONTINUE;
          }

          @Override
          public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            return FileVisitResult.CONTINUE;
          }

          @Override
          public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            TreeItem<TreeData> leaving = stack.removeLast();
            stack.getLast().getChildren().add(leaving);
            return FileVisitResult.CONTINUE;
          }
        });
    return rootItem;
  }
}
