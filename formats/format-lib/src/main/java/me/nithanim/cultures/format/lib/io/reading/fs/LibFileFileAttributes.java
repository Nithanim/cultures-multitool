package me.nithanim.cultures.format.lib.io.reading.fs;

import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

class LibFileFileAttributes implements BasicFileAttributes {
  private static final FileTime UNAVAILABLE_TIME = FileTime.fromMillis(0);
  private final Type type;
  private final long size;

  public LibFileFileAttributes(Type type, long size) {
    this.type = type;
    this.size = size;
  }

  @Override
  public FileTime lastModifiedTime() {
    return UNAVAILABLE_TIME;
  }

  @Override
  public FileTime lastAccessTime() {
    return UNAVAILABLE_TIME;
  }

  @Override
  public FileTime creationTime() {
    return UNAVAILABLE_TIME;
  }

  @Override
  public boolean isRegularFile() {
    return type == Type.FILE;
  }

  @Override
  public boolean isDirectory() {
    return type == Type.DIR;
  }

  @Override
  public boolean isSymbolicLink() {
    return false;
  }

  @Override
  public boolean isOther() {
    return false;
  }

  @Override
  public long size() {
    return size;
  }

  @Override
  public Object fileKey() {
    return null;
  }

  enum Type {
    FILE,
    DIR
  }
}
