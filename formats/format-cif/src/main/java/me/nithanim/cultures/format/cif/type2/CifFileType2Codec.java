package me.nithanim.cultures.format.cif.type2;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import me.nithanim.cultures.format.cif.Codec;
import me.nithanim.cultures.format.cif.EncryptedInformation;

public class CifFileType2Codec implements Codec<EncryptedInformation> {
    private CifFileType2Reader reader = new CifFileType2Reader();
    
    @Override
    public EncryptedInformation unpack(ByteBuf buf) throws IOException {
        return reader.unpack(buf);
    }

    @Override
    public void pack(EncryptedInformation o, ByteBuf buf) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
