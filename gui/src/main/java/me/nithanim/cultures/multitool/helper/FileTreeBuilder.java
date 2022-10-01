package me.nithanim.cultures.multitool.helper;

import java.io.InputStream;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javafx.scene.control.TreeItem;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import me.nithanim.cultures.format.lib.io.reading.ReadableLibFile;
import me.nithanim.cultures.multitool.FxUtil;
import me.nithanim.cultures.multitool.TreeData;
import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular;
import org.kordamp.ikonli.javafx.FontIcon;

@UtilityClass
public class FileTreeBuilder {
  public static TreeItem<TreeData> buildTree(
      String path, ReadableLibFile.LibFileDirectory rootDir) {
    TreeItem<TreeData> rootItem = new TreeItem<>();
    rootItem.setGraphic(FontIcon.of(FontAwesomeRegular.FILE_ARCHIVE));
    Collection<ReadableLibFile.LibFileDirectory> sortedDirectories =
        rootDir.getDirectories().values().stream()
            .sorted(Comparator.comparing(ReadableLibFile.LibFileDirectory::getName))
            .collect(Collectors.toList());
    for (ReadableLibFile.LibFileDirectory dir : sortedDirectories) {
      TreeItem<TreeData> sub = buildTree(path + "\\" + dir.getName(), dir);
      sub.setValue(new TreeData(true, path + "\\" + dir.getName(), dir.getName(), null));
      sub.setGraphic(FontIcon.of(FontAwesomeRegular.FOLDER));
      rootItem.getChildren().add(sub);
    }
    List<ReadableLibFile.LibFileFile> sortedFiles =
        rootDir.getFiles().values().stream()
            .sorted(Comparator.comparing(ReadableLibFile.LibFileFile::getName))
            .collect(Collectors.toList());
    for (ReadableLibFile.LibFileFile file : sortedFiles) {
      TreeItem<TreeData> item = new TreeItem<>();
      item.setValue(
          new TreeData(
              false,
              path + "\\" + file.getName(),
              file.getName(),
              new Supplier<InputStream>() {
                @Override
                @SneakyThrows
                public InputStream get() {
                  return file.getData().getInputStream();
                }
              }));
      item.setGraphic(FxUtil.getIconForFile(file));
      rootItem.getChildren().add(item);
    }
    return rootItem;
  }
}
