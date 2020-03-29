package me.nithanim.cultures.format.lib.io.reading;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.Value;
import me.nithanim.cultures.format.io.LittleEndianDataInputStream;
import me.nithanim.cultures.format.lib.LibFileInfo;
import me.nithanim.cultures.format.lib.LibFileUtil;
import me.nithanim.cultures.format.lib.LibFormat;
import me.nithanim.cultures.format.lib.io.FileMeta;
import me.nithanim.cultures.format.io.NonClosableInputStream;
import org.apache.commons.io.input.BoundedInputStream;

public class ReadableLibFile implements AutoCloseable {
  private final Path path;
  private final SeekableByteChannel channel;
  private final LibFileDirectory root;

  public ReadableLibFile(Path p) throws IOException {
    this.path = p;
    this.channel = Files.newByteChannel(p);
    this.root = new LibFileDirectory(null);
    LibFileInfo metas = LibFileUtil.read(new NonClosableInputStream(Channels.newInputStream(channel)), LibFormat.CULTURES2);
    initTree(metas);
  }

  private void initTree(LibFileInfo metas) {
    for (FileMeta fileMeta : metas.getFileMetas()) {
      String[] parts = fileMeta.getName().split("\\\\");
      LibFileDirectory parent = root;
      for (int i = 0; i < parts.length - 1; i++) {
        parent = parent.getDirectories().computeIfAbsent(parts[i], LibFileDirectory::new);
      }
      String filename = parts[parts.length - 1];
      parent
          .getFiles()
          .put(
              filename,
              new LibFileFile(
                  filename,
                  fileMeta.getLen(),
                  new ChannelFileData(channel, fileMeta.getPos(), fileMeta.getLen())));
    }
  }

  public LibFileDirectory getRoot() {
    return root;
  }

  @Override
  public void close() throws Exception {
    channel.close();
  }

  @Data
  public static class LibFileDirectory {
    String name;
    Map<String, LibFileDirectory> directories = new HashMap<>();
    Map<String, LibFileFile> files = new HashMap<>();

    public LibFileDirectory(String name) {
      this.name = name;
    }
  }

  @Value
  public static class LibFileFile {
    String name;
    int size;
    FileData data;
  }

  @Value
  private static class ChannelFileData implements FileData {
    SeekableByteChannel channel;
    long position;
    int size;

    @Override
    public InputStream getInputStream() throws IOException {
      channel.position(position);
      return new BoundedInputStream(
          new LittleEndianDataInputStream(
              new NonClosableInputStream(Channels.newInputStream(channel))),
          size);
    }
  }
}
