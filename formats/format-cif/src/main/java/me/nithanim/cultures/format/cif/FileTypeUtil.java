package me.nithanim.cultures.format.cif;

import io.netty.buffer.ByteBuf;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class FileTypeUtil {
    public static ByteBuf decrypt(ByteBuf src) {
        ByteBuf dest = src.alloc().buffer(src.writerIndex()).order(ByteOrder.LITTLE_ENDIAN);
        EncryptionUtil.decryptCommon(src, dest);
        return dest;
    }
    
    public static ByteBuf encrypt(ByteBuf src) {
        ByteBuf dest = src.alloc().buffer(src.writerIndex()).order(ByteOrder.LITTLE_ENDIAN);
        EncryptionUtil.encryptCommon(src, dest);
        return dest;
    }
    
    public static CifFile.FileFormat isCifFile(ByteBuf indexTable, ByteBuf contentTable) {
        //guessing the type based on 0x1 and 0x2
        int count = 0;
        for(int i = 0; i < indexTable.writerIndex(); i+=4) {
            int idx = indexTable.getInt(i);
            byte b = contentTable.getByte(idx);
            if (1 == b || b == 2) {
                count++;
            }
        }
        indexTable.readerIndex(0);
        return (count == indexTable.capacity() / 4) ? CifFile.FileFormat.CIF : CifFile.FileFormat.TAB_SAL;
    }

    public static String javaStringFromCString(ByteBuf buf) {
        int start = buf.readerIndex();
        int length = 0;
        for (; buf.readableBytes() > 0; length++) {
            byte b = buf.readByte();
            if (b == 0) {
                break;
            }
        }

        buf.readerIndex(start);
        byte[] arr = new byte[length];
        buf.readBytes(arr);
        return new String(arr, StandardCharsets.ISO_8859_1);
    }
}
