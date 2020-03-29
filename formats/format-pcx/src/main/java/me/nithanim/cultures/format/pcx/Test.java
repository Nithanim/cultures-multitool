package me.nithanim.cultures.format.pcx;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import me.nithanim.cultures.format.pcx.LittleEndianDataInputStream;

public class Test {
  public static void main(String[] args) throws IOException {
    Path p = Paths.get(
        "/mount/data/games/weltwunder/dosdevices/c:/GOG Games/8th Wonder of the World/data/engine2d/bin/textures/text_193.pcx");

    PcxFile pcx = new PcxFileReader().read(Files.newInputStream(p));
    System.out.println(pcx);
  }
}
