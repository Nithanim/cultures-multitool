package me.nithanim.cultures.format.cif;

import io.netty.buffer.ByteBuf;
import java.io.IOException;

public class CifFileCodec implements Codec<CifFile> {
    CifFileReader reader = new CifFileReader();
    CifFileWriter writer = new CifFileWriter();

    @Override
    public CifFile unpack(ByteBuf buf) throws IOException {
        return reader.unpack(buf);
    }

    @Override
    public void pack(CifFile o, ByteBuf buf) throws IOException {
        writer.pack(o, buf);
    }
}
