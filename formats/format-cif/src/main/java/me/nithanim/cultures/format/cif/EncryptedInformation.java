package me.nithanim.cultures.format.cif;

import io.netty.buffer.ByteBuf;

public class EncryptedInformation {
    private int numberOfEntries;
    private final int indexLength;
    private ByteBuf encryptedIndexTable;
    private int contentLength;
    private ByteBuf encryptedContentTable;

    public EncryptedInformation(int numberOfEntries, int indexLength, ByteBuf encryptedIndexTable, int contentLength, ByteBuf encryptedContentTable) {
        this.numberOfEntries = numberOfEntries;
        this.indexLength = indexLength;
        this.encryptedIndexTable = encryptedIndexTable;
        this.contentLength = contentLength;
        this.encryptedContentTable = encryptedContentTable;
    }

    public int getNumberOfEntries() {
        return numberOfEntries;
    }

    public int getIndexLength() {
        return indexLength;
    }

    public ByteBuf getEncryptedIndexTable() {
        return encryptedIndexTable;
    }

    public ByteBuf getEncryptedContentTable() {
        return encryptedContentTable;
    }

    public int getContentLength() {
        return contentLength;
    }
}
