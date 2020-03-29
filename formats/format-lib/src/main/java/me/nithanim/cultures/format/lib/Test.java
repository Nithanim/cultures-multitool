package me.nithanim.cultures.format.lib;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import me.nithanim.cultures.format.lib.io.reading.ReadableLibFile;

public class Test {
  public static void main(String[] args) throws IOException {
    Path p = Paths.get(
        "/mount/data/games/weltwunder/dosdevices/c:/GOG Games/8th Wonder of the World/DataX/Libs/data0001.lib");
    ReadableLibFile lib = new ReadableLibFile(p);
  }
}
