package me.nithanim.cultures.format.cif;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MainTest {
    
    public MainTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of main method, of class Test.
     */
    @Test
    public void testReencode() throws Exception {
        byte[] original = IOUtils.toByteArray(this.getClass().getResourceAsStream("/cif_test_file.cif"));
        
        CifFile r = new CifFileReader().unpack(Unpooled.wrappedBuffer(original).order(ByteOrder.LITTLE_ENDIAN));
        
        ByteBuf encrypted = ByteBufAllocator.DEFAULT.buffer().order(ByteOrder.LITTLE_ENDIAN);
        new CifFileWriter().pack(r, encrypted);
        
        CifFile redecrypted = new CifFileReader().unpack(encrypted);
        
        Assert.assertEquals(r.getLines().size(), redecrypted.getLines().size());
        for (int i = 0; i < r.getLines().size(); i++) {
            Assert.assertEquals(r.getLines().get(i), redecrypted.getLines().get(i));
        }
    }
    
    @Test
    public void testDecode() throws Exception {
        List<String> originalDecoded = IOUtils.readLines(this.getClass().getResourceAsStream("/cif_test_file.ini"), StandardCharsets.ISO_8859_1);
        byte[] originalEncoded = IOUtils.toByteArray(this.getClass().getResourceAsStream("/cif_test_file.cif"));
        
        CifFile decoded = new CifFileReader().unpack(Unpooled.wrappedBuffer(originalEncoded).order(ByteOrder.LITTLE_ENDIAN));
        
        Assert.assertEquals(originalDecoded.size(), decoded.getLines().size());
        for (int i = 0; i < originalDecoded.size(); i++) {
            Assert.assertEquals(originalDecoded.get(i), decoded.getLines().get(i));
        }
    }
    
}
