package me.nithanim.cultures.format.cif.type1;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import me.nithanim.cultures.format.cif.EncryptedInformation;
import me.nithanim.cultures.format.cif.FileTypeUtil;
import me.nithanim.cultures.format.cif.Writer;

public class CifFileType1Writer implements Writer<EncryptedInformation> {
    @Override
    public void pack(EncryptedInformation ei, ByteBuf buf) throws IOException {
        buf.writeInt(ei.getNumberOfEntries());
        buf.writeInt(ei.getNumberOfEntries());
        
        buf.writeInt(10);
        
        buf.writeInt(ei.getIndexLength());
        ByteBuf decryptedIndexTable = ei.getEncryptedIndexTable();
        ByteBuf encryptedIndexTable = FileTypeUtil.encrypt(decryptedIndexTable);
        buf.writeBytes(encryptedIndexTable);
        decryptedIndexTable.release();
        encryptedIndexTable.release();
        
        buf.writeShort(1);
        buf.writeInt(10);
        
        buf.writeInt(ei.getContentLength());
        ByteBuf decryptedContentTable = ei.getEncryptedContentTable();
        ByteBuf encryptedContentTable = FileTypeUtil.encrypt(decryptedContentTable);
        buf.writeBytes(encryptedContentTable);
        decryptedContentTable.release();
        encryptedContentTable.release();
    }

}
