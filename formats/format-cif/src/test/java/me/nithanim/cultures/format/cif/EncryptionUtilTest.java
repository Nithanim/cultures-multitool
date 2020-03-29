package me.nithanim.cultures.format.cif;

import java.util.Arrays;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

public class EncryptionUtilTest {
    @Test
    public void testDecryptCommon_byteArr() throws Exception {
        byte[] dec = IOUtils.toByteArray(this.getClass().getResource("/cif_enc_data_only.bin"));
        EncryptionUtil.decryptCommon(dec);
        
        byte[] enc = Arrays.copyOf(dec, dec.length);
        EncryptionUtil.encryptCommon(enc);
        
        EncryptionUtil.decryptCommon(enc);
        Assert.assertArrayEquals(dec, enc);
    }
}
