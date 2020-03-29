package me.nithanim.cultures.format.cif;

import java.util.List;

public class CifFile {
    private final InternalFormat internalFormat;
    private final FileFormat fileFormat;
    private final List<String> lines;

    public CifFile(FileFormat fileFormat, InternalFormat internalFormat, List<String> lines) {
        this.internalFormat = internalFormat;
        this.lines = lines;
        this.fileFormat = fileFormat;
    }

    public FileFormat getFileFormat() {
        return fileFormat;
    }

    public InternalFormat getInternalFormat() {
        return internalFormat;
    }

    public List<String> getLines() {
        return lines;
    }

    public enum InternalFormat {
        TYPE1, TYPE2;
    }

    public enum FileFormat {
        CIF, TAB_SAL;
    }
}
