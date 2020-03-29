package me.nithanim.cultures.format.lib.io.reading.fs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.Value;
import me.nithanim.cultures.format.lib.LibFileInfo;
import me.nithanim.cultures.format.lib.io.DirMeta;
import me.nithanim.cultures.format.lib.io.FileMeta;

class FileTree {
  private final Node root;

  public FileTree(LibFileInfo lif) {
    root = new Node(null);

    lif.getDirMetas().forEach(this::insert);
    lif.getFileMetas().forEach(this::insert);
  }

  public FileMeta getFile(Path path) throws FileNotFoundException {
    Node last = getLastNode(path);

    FileMeta fileMeta = last.filesMap.get(path.getName(path.getNameCount() - 1).toString());
    if (fileMeta == null) {
      throw new FileNotFoundException("Unable to find " + path);
    }
    return fileMeta;
  }

  private Node getLastNode(Path path) throws FileNotFoundException {
    Node last = root;
    for (int i = 0; i < path.getNameCount() - 1; i++) {
      last = last.subfolderMap.get(path.getName(i).toString());
      if (last == null) {
        throw new FileNotFoundException(
            "Unable to find part " + i + " (" + path.getName(i) + ") in " + path);
      }
    }
    return last;
  }

  public LibFileFileAttributes getAttributes(Path path) throws FileNotFoundException {
    Node last = getLastNode(path);

    if (path.getNameCount() == 0) { // for '\\'
      return new LibFileFileAttributes(LibFileFileAttributes.Type.DIR, 0);
    }

    String lastName = path.getName(path.getNameCount() - 1).toString();
    Node dir = last.subfolderMap.get(lastName);
    if (dir != null) {
      return new LibFileFileAttributes(LibFileFileAttributes.Type.DIR, 0);
    }

    FileMeta fileMeta = last.filesMap.get(lastName);
    if (fileMeta != null) {
      return new LibFileFileAttributes(LibFileFileAttributes.Type.FILE, fileMeta.getLen());
    }

    throw new FileNotFoundException("Unable to find " + path);
  }

  public DirectoryStream<Path> newDirectoryStream(
      Path dir, DirectoryStream.Filter<? super Path> filter) throws IOException {
    Node dirNode = getDirNode(dir);

    return new DirectoryStream<Path>() {
      @Override
      public Iterator<Path> iterator() {
        return new DirIterator(dir, dirNode);
      }

      @Override
      public void close() throws IOException {}
    };
  }

  private static class DirIterator implements Iterator<Path> {
    private final Path base;
    private final List<Node> remainingDirectories;
    private final List<String> remainingFiles;

    public DirIterator(Path base, Node node) {
      this.base = base;
      this.remainingDirectories = new ArrayList<>(node.getSubfolderList());
      this.remainingFiles =
          new ArrayList<>(
              node.getFilesList().stream().map(FileMeta::getName).collect(Collectors.toList()));
    }

    @Override
    public boolean hasNext() {
      return !remainingDirectories.isEmpty() || !remainingFiles.isEmpty();
    }

    @Override
    public Path next() {
      if (!remainingDirectories.isEmpty()) {
        return base.resolve(remainingDirectories.remove(remainingDirectories.size() - 1).getName());
      } else {
        return base.getFileSystem().getPath(remainingFiles.remove(remainingFiles.size() - 1));
      }
    }
  }

  private Node getDirNode(Path path) throws FileNotFoundException {
    Node last = root;
    for (int i = 0; i < path.getNameCount(); i++) {
      last = last.subfolderMap.get(path.getName(i).toString());
      if (last == null) {
        throw new FileNotFoundException("Unable to find " + path.getName(i) + " in " + path);
      }
    }
    return last;
  }

  private void insert(DirMeta dm) {
    if ("\\".equals(dm.getName())) {
      return;
    }
    String[] parts = dm.getName().split(Pattern.quote("\\"));

    Node node = root;
    for (String part : parts) {
      Node sub = node.subfolderMap.get(part);
      if (sub == null) {
        Node n = new Node(part);
        node.subfolderList.add(n);
        node.subfolderMap.put(part, n);
        sub = n;
      }
      node = sub;
    }
  }

  private void insert(FileMeta fm) {
    String[] parts = fm.getName().split(Pattern.quote("\\"));

    Node node = root;
    for (int i = 0; i < parts.length - 1; i++) {
      Node sub = node.subfolderMap.get(parts[i]);
      if (sub == null) {
        throw new IllegalArgumentException("Folder does not exist!" + fm.getName());
      }
      node = sub;
    }
    node.filesList.add(fm);
    node.filesMap.put(parts[parts.length - 1], fm);
  }

  @Value
  private static class Node {
    String name;
    List<Node> subfolderList = new ArrayList<>();
    Map<String, Node> subfolderMap = new HashMap<>();
    List<FileMeta> filesList = new ArrayList<>();
    Map<String, FileMeta> filesMap = new HashMap<>();

    public Node(String name) {
      this.name = name;
    }
  }
}
