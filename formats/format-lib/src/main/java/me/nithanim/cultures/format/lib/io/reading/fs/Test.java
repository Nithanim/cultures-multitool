package me.nithanim.cultures.format.lib.io.reading.fs;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import me.nithanim.cultures.format.lib.LibFormat;
import me.nithanim.cultures.format.lib.io.reading.fs.LibFileFileSystemProvider;
import me.nithanim.cultures.format.lib.io.reading.fs.ReadableLibFile;

public class Test {
    public static void main(String[] args) throws IOException {

        //c:/testfs/my-outer.jar!/my-inner.jar
        //try (FileSystem fs = FileSystems.newFileSystem(Paths.get("E:\\Spiele\\Weltwunder\\DataX\\Libs\\data0001.lib"), null)) {
        LibFileFileSystemProvider fp = new LibFileFileSystemProvider();
        //Path p = Paths.get("E:\\Spiele\\Weltwunder\\DataX\\Libs\\data0001.lib!\\data\\logic\\goodtypes.cif");
        Path p = Paths.get("E:\\Spiele\\Weltwunder\\DataX\\Libs\\data0001.lib");
        try (FileSystem fs = ReadableLibFile.fromFile(p, LibFormat.CULTURES2)) {
            Path f = fs.getPath("\\data\\logic\\goodtypes.cif");
            Files.walkFileTree(fs.getPath("\\"), new PrintAllFileVisitor());

            //Path inPath = fs.getPath("data\\maps\\campaign_03_08\\text\\ger\\briefings\\graphics\\people_dead_f.pcx");
            //try (InputStream in = Files.newInputStream(inPath)) {
            //    Files.copy(in, Paths.get("Y:\\t.pcx"), StandardCopyOption.REPLACE_EXISTING);
            //}
        }
    }

    private static class PrintAllFileVisitor implements FileVisitor<Path> {
        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            System.out.println("ENTER " + dir);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            System.out.println("FILE " + file);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            System.out.println("FILE_FAIL " + file);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            System.out.println("EXIT " + dir);
            return FileVisitResult.CONTINUE;
        }
    }
}
