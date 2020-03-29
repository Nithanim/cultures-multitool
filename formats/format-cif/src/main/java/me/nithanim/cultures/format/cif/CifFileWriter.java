/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.nithanim.cultures.format.cif;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import me.nithanim.cultures.format.cif.type1.CifFileType1Writer;
import me.nithanim.cultures.format.cif.type2.CifFileType2Writer;

public class CifFileWriter implements Writer<CifFile> {
    private Writer<EncryptedInformation> type1Writer = new CifFileType1Writer();
    private Writer<EncryptedInformation> type2Writer = new CifFileType2Writer();

    @Override
    public void pack(CifFile o, ByteBuf buf) throws IOException {
        List<String> lines = o.getLines();

        ByteBufAllocator alloc = ByteBufAllocator.DEFAULT;
        ByteBuf indexTable = alloc.buffer(o.getLines().size()).order(ByteOrder.LITTLE_ENDIAN);
        ByteBuf contentTable = alloc.buffer(o.getLines().size() * 10).order(ByteOrder.LITTLE_ENDIAN);
        if (o.getFileFormat() == CifFile.FileFormat.CIF) {
            for (String l : lines) {
                int idx = contentTable.writerIndex();
                ByteBufUtil.hexDump(contentTable);
                indexTable.writeInt(idx);

                l = l.trim();
                if (l.startsWith("[")) {
                    l = l.substring(1, l.length() - 1);
                    contentTable.writeByte(1);
                } else {
                    contentTable.writeByte(2);
                }

                contentTable.writeBytes(l.getBytes(StandardCharsets.ISO_8859_1));
                contentTable.writeByte('\0');
            }
        } else {
            for (String l : lines) {
                int idx = contentTable.writerIndex();
                indexTable.writeInt(idx);

                l = l.trim();

                contentTable.writeBytes(l.getBytes(StandardCharsets.ISO_8859_1));
                contentTable.writeByte('\0');
            }
        }
        EncryptedInformation ei = new EncryptedInformation(
            o.getLines().size(),
            indexTable.writerIndex(),
            indexTable,
            contentTable.writerIndex(),
            contentTable
        );

        Writer<EncryptedInformation> eiw;
        if (o.getInternalFormat() == CifFile.InternalFormat.TYPE1) {
            buf.writeInt(65601);
            eiw = type1Writer;
        } else if (o.getInternalFormat() == CifFile.InternalFormat.TYPE2) {
            buf.writeInt(1021);
            eiw = type2Writer;
        } else {
            throw new IllegalStateException("The given data is not a cif file!");
        }
        eiw.pack(ei, buf);
    }
}
