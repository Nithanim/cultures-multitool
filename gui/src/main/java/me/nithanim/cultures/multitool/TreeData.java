package me.nithanim.cultures.multitool;

import java.io.InputStream;
import java.util.function.Supplier;
import lombok.Value;

@Value
public class TreeData {
  boolean isDir;
  String fullPath;
  String name;
  Supplier<InputStream> data;

  @Override
  public String toString() {
    return name;
  }
}
