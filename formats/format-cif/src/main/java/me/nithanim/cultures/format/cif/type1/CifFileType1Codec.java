package me.nithanim.cultures.format.cif.type1;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import me.nithanim.cultures.format.cif.Codec;
import me.nithanim.cultures.format.cif.EncryptedInformation;

public class CifFileType1Codec implements Codec<EncryptedInformation>{
    private CifFileType1Reader reader = new CifFileType1Reader();
    
    @Override
    public EncryptedInformation unpack(ByteBuf buf) throws IOException {
        return reader.unpack(buf);
    }

    @Override
    public void pack(EncryptedInformation o, ByteBuf buf) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
