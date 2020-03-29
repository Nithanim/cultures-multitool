package me.nithanim.cultures.format.cif;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.nio.ByteOrder;
import lombok.SneakyThrows;

public class CifFileUtil {
  @SneakyThrows
  public static CifFile unpack(byte[] data) {
    ByteBuf buf = Unpooled.wrappedBuffer(data).order(ByteOrder.LITTLE_ENDIAN);

    CifFileCodec codec = new CifFileCodec();
    return codec.unpack(buf);
  }
}
