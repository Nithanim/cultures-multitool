/*
The basis for this file was a copy of fr.gnodet.githubfs.GitHubFileSystem
from https://github.com/gnodet/githubfs/ created by Guillaume Nodet.
It was adapted to fit the need of accessing the contents of a container
file with custom format.
Many thanks to the original author!

The original file is licensed under:

 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package me.nithanim.cultures.format.lib.io.reading.fs;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.StandardOpenOption;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import me.nithanim.cultures.format.io.LimitedInputStream;
import me.nithanim.cultures.format.lib.LibFileInfo;
import me.nithanim.cultures.format.lib.LibFileUtil;
import me.nithanim.cultures.format.lib.LibFormat;
import me.nithanim.cultures.format.lib.io.FileMeta;

public class LibFileFileSystem extends FileSystem {
    private final LibFileFileSystemProvider fileSystemProvider;
    private final Path pathToFile;
    private final LibFileInfo meta;
    private final FileTree fileTree;

    public LibFileFileSystem(LibFileFileSystemProvider fileSystemProvider, Path pathToFile, Map<String, ?> env) throws IOException {
        this.fileSystemProvider = fileSystemProvider;
        this.pathToFile = pathToFile;
        meta = LibFileUtil.read(Files.newInputStream(pathToFile, StandardOpenOption.READ), (LibFormat) env.get("type"));
        fileTree = new FileTree(meta);
    }

    @Override
    public FileSystemProvider provider() {
        return fileSystemProvider;
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public String getSeparator() {
        return "\\";
    }

    @Override
    public Iterable<Path> getRootDirectories() {
        return Collections.<Path>singleton(new LibFilePath(this, new byte[] {'\\'}));
    }

    public InputStream newInputStream(Path path, OpenOption[] options) throws IOException {
        if ((options.length == 1 && options[0] != StandardOpenOption.READ) || options.length > 1) {
            throw new IllegalArgumentException("Only \"READ\" is supported as open option!");
        }
        FileMeta fileMeta = fileTree.getFile(path);
        SeekableByteChannel ch = Files.newByteChannel(pathToFile, StandardOpenOption.READ);
        ch.position(fileMeta.getPos());

        return new LimitedInputStream(Channels.newInputStream(ch), fileMeta.getLen());
    }

    @Override
    public Iterable<FileStore> getFileStores() {
        return null;
    }

    @Override
    public Set<String> supportedFileAttributeViews() {
        return null;
    }

    @Override
    public Path getPath(String first, String... more) {
        String path;
        if (more.length == 0) {
            path = first;
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(first);
            for (String segment : more) {
                if (segment.length() > 0) {
                    if (sb.length() > 0) {
                        sb.append('\\');
                    }
                    sb.append(segment);
                }
            }
            path = sb.toString();
        }
        return new LibFilePath(this, path.getBytes(StandardCharsets.ISO_8859_1));
    }

    @Override
    public PathMatcher getPathMatcher(String syntaxAndPattern) {
        int colonIndex = syntaxAndPattern.indexOf(':');
        if (colonIndex <= 0 || colonIndex == syntaxAndPattern.length() - 1) {
            throw new IllegalArgumentException("syntaxAndPattern must have form \"syntax:pattern\" but was \"" + syntaxAndPattern + "\"");
        }

        String syntax = syntaxAndPattern.substring(0, colonIndex);
        String pattern = syntaxAndPattern.substring(colonIndex + 1);
        String expr;
        switch (syntax) {
            case "glob":
                expr = globToRegex(pattern);
                break;
            case "regex":
                expr = pattern;
                break;
            default:
                throw new UnsupportedOperationException("Unsupported syntax \'" + syntax + "\'");
        }
        final Pattern regex = Pattern.compile(expr);
        return (Path path) -> regex.matcher(path.toString()).matches();
    }

    private String globToRegex(String pattern) {
        StringBuilder sb = new StringBuilder(pattern.length());
        int inGroup = 0;
        int inClass = 0;
        int firstIndexInClass = -1;
        char[] arr = pattern.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            char ch = arr[i];
            switch (ch) {
                case '\\':
                    if (++i >= arr.length) {
                        sb.append('\\');
                    } else {
                        char next = arr[i];
                        switch (next) {
                            case ',':
                                // escape not needed
                                break;
                            case 'Q':
                            case 'E':
                                // extra escape needed
                                sb.append('\\');
                            default:
                                sb.append('\\');
                        }
                        sb.append(next);
                    }
                    break;
                case '*':
                    if (inClass == 0) {
                        sb.append(".*");
                    } else {
                        sb.append('*');
                    }
                    break;
                case '?':
                    if (inClass == 0) {
                        sb.append('.');
                    } else {
                        sb.append('?');
                    }
                    break;
                case '[':
                    inClass++;
                    firstIndexInClass = i + 1;
                    sb.append('[');
                    break;
                case ']':
                    inClass--;
                    sb.append(']');
                    break;
                case '.':
                case '(':
                case ')':
                case '+':
                case '|':
                case '^':
                case '$':
                case '@':
                case '%':
                    if (inClass == 0 || (firstIndexInClass == i && ch == '^')) {
                        sb.append('\\');
                    }
                    sb.append(ch);
                    break;
                case '!':
                    if (firstIndexInClass == i) {
                        sb.append('^');
                    } else {
                        sb.append('!');
                    }
                    break;
                case '{':
                    inGroup++;
                    sb.append('(');
                    break;
                case '}':
                    inGroup--;
                    sb.append(')');
                    break;
                case ',':
                    if (inGroup > 0) {
                        sb.append('|');
                    } else {
                        sb.append(',');
                    }
                    break;
                default:
                    sb.append(ch);
            }
        }
        return sb.toString();
    }

    @Override
    public UserPrincipalLookupService getUserPrincipalLookupService() {
        throw new UnsupportedOperationException();
    }

    @Override
    public WatchService newWatchService() throws IOException {
        throw new UnsupportedOperationException();
    }

    public DirectoryStream<Path> newDirectoryStream(Path dir, DirectoryStream.Filter<? super Path> filter) throws IOException {
        return fileTree.newDirectoryStream(dir, filter);
    }

    public <A extends BasicFileAttributes> SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>[] attrs) throws IOException {
        Object content = null;
        if (content instanceof List) {
            throw new IOException("Is a directory");
        }
        String base64 = ((Map<String, String>) content).get("content");
        final byte[] data = new byte[0];
        return new SeekableByteChannel() {
            long position;

            @Override
            public int read(ByteBuffer dst) throws IOException {
                int l = (int) Math.min(dst.remaining(), size() - position);
                dst.put(data, (int) position, l);
                position += l;
                return l;
            }

            @Override
            public int write(ByteBuffer src) throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public long position() throws IOException {
                return position;
            }

            @Override
            public SeekableByteChannel position(long newPosition) throws IOException {
                position = newPosition;
                return this;
            }

            @Override
            public long size() throws IOException {
                return data.length;
            }

            @Override
            public SeekableByteChannel truncate(long size) throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean isOpen() {
                return true;
            }

            @Override
            public void close() throws IOException {
            }
        };
    }

    public <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> clazz, LinkOption... options) throws IOException {
        if (clazz != BasicFileAttributes.class) {
            throw new UnsupportedOperationException(clazz.toString());
        }

        return (A) fileTree.getAttributes(path);
    }

}
