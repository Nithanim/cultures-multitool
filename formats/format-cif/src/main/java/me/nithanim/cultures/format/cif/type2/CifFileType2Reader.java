package me.nithanim.cultures.format.cif.type2;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import me.nithanim.cultures.format.cif.EncryptedInformation;
import me.nithanim.cultures.format.cif.Reader;

public class CifFileType2Reader implements Reader<EncryptedInformation> {
    @Override
    public EncryptedInformation unpack(ByteBuf buf) throws IOException {
        buf.skipBytes(4 + 4);
        int numberOfEntries = buf.readInt();
        buf.skipBytes(5 * 4);
        
        int indexLength = buf.readInt();
        ByteBuf encryptedIndexTable = buf.slice(buf.readerIndex(), indexLength);
        buf.skipBytes(indexLength);
        
        buf.skipBytes(1 + 4 + 4);
        
        int contentLength = buf.readInt();
        ByteBuf encryptedContentTable = buf.slice(buf.readerIndex(), contentLength);
        buf.skipBytes(contentLength);
        return new EncryptedInformation(numberOfEntries, indexLength, encryptedIndexTable, contentLength, encryptedContentTable);
    }
}
