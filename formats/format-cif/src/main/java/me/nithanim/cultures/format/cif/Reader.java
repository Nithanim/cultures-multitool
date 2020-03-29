package me.nithanim.cultures.format.cif;

import io.netty.buffer.ByteBuf;
import java.io.IOException;

public interface Reader<T> {
    T unpack(ByteBuf buf) throws IOException;
}
