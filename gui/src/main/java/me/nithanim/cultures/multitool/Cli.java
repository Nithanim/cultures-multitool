package me.nithanim.cultures.multitool;

import java.nio.file.Path;
import picocli.CommandLine.Option;

public class Cli {

  @Option(
      names = {"-o", "--open"},
      paramLabel = "FILE",
      description = "s file to open directly on startup")
  Path fileToOpen;
}
