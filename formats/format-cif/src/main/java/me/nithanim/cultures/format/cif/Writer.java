package me.nithanim.cultures.format.cif;

import io.netty.buffer.ByteBuf;
import java.io.IOException;

public interface Writer<T> {
    void pack(T o, ByteBuf buf) throws IOException;
}
