package me.nithanim.cultures.format.cif;

import io.netty.buffer.ByteBuf;

public class EncryptionUtil {
    public static void decryptCommon(byte[] encrypted) {
        int c = 71;
        int d = 126;
        for (int i = 0; i < encrypted.length; i++) {
            encrypted[i] = (byte) (((encrypted[i] & 0xFF) - 1) ^ c);
            c += d;
            d += 33;
        }
    }

    public static void decryptCommon(ByteBuf src, ByteBuf dest) {
        int c = 71;
        int d = 126;
        while (src.readableBytes() > 0) {
            dest.writeByte((byte) (((src.readByte() & 0xFF) - 1) ^ c));
            c += d;
            d += 33;
        }
    }

    public static void encryptCommon(byte[] decrypted) {
        int c = 71;
        int d = 126;
        for (int i = 0; i < decrypted.length; i++) {
            decrypted[i] = (byte) (((decrypted[i] & 0xFF) ^ c) + 1);
            c += d;
            d += 33;
        }
    }

    public static void encryptCommon(ByteBuf src, ByteBuf dest) {
        int c = 71;
        int d = 126;
        while (src.readableBytes() > 0) {
            dest.writeByte((byte) (((src.readByte() & 0xFF) ^ c) + 1));
            c += d;
            d += 33;
        }
    }

    private EncryptionUtil() {
    }
}
