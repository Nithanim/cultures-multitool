package me.nithanim.cultures.format.cif;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.util.ArrayList;
import me.nithanim.cultures.format.cif.type1.CifFileType1Codec;
import me.nithanim.cultures.format.cif.type2.CifFileType2Codec;

public class CifFileReader implements Reader<CifFile> {
    @Override
    public CifFile unpack(ByteBuf buf) throws IOException {
        int magic = buf.readInt();

        Codec<EncryptedInformation> codec;
        CifFile.InternalFormat internalType;
        if (magic == 65601) {
            codec = new CifFileType1Codec();
            internalType = CifFile.InternalFormat.TYPE1;
        } else if (magic == 1021) {
            codec = new CifFileType2Codec();
            internalType = CifFile.InternalFormat.TYPE2;
        } else {
            throw new IllegalArgumentException("The given data is not a cif file!");
        }

        EncryptedInformation inf = codec.unpack(buf);

        ByteBuf indexTable = FileTypeUtil.decrypt(inf.getEncryptedIndexTable());
        ByteBuf contentTable = FileTypeUtil.decrypt(inf.getEncryptedContentTable());

        ArrayList<String> lines = new ArrayList<>();
        CifFile.FileFormat fileType = FileTypeUtil.isCifFile(indexTable, contentTable);
        while (indexTable.readableBytes() > 0) {
            int index = indexTable.readInt();

            contentTable.readerIndex(index);

            if (fileType == CifFile.FileFormat.CIF) {
                byte meta = contentTable.readByte();
                String line = FileTypeUtil.javaStringFromCString(contentTable);
                if (meta == 1) {
                    lines.add('[' + line + ']');
                } else if(meta == 2) {
                    lines.add(line);
                } else {
                    //warn: won't be thrown normally because the cif/tab_sal detection 
                    //checks if the byte where the meta is on is always 1 or 2 and if
                    //this fails we land in the else branch below for the other type
                    throw new IllegalStateException("Tried to decode .cif entry at idx " + index + " and got unknown meta " + meta);
                }
            } else { //.tab or .sal
                String line = FileTypeUtil.javaStringFromCString(contentTable);
                lines.add(line);
            }
        }
        indexTable.release();
        contentTable.release();
        return new CifFile(fileType, internalType, lines);
    }
}
