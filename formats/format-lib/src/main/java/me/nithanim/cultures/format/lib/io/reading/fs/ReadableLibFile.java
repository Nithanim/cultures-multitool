package me.nithanim.cultures.format.lib.io.reading.fs;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;
import me.nithanim.cultures.format.lib.LibFormat;

public class ReadableLibFile {
    private static volatile FileSystemProvider provider;

    public static FileSystem fromFile(Path p, LibFormat f) throws IOException {
        if (provider != null) {
            return newFileSystem(p, f);
        }

        synchronized (ReadableLibFile.class) {
            for (FileSystemProvider fsp : FileSystemProvider.installedProviders()) {
                if (fsp instanceof LibFileFileSystemProvider) {
                    provider = fsp;
                    return newFileSystem(p, f);
                }
            }
            provider = new LibFileFileSystemProvider();
            return newFileSystem(p, f);
        }
    }

    private static FileSystem newFileSystem(Path p, LibFormat f) throws IOException {
        return provider.newFileSystem(p, Collections.singletonMap("type", f));
    }
}
