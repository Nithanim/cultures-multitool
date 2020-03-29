package me.nithanim.cultures.format.cif;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class Test {
    public static void main(String[] args) throws Exception {
        a();
    }
    
    static void a() throws Exception {
        File file = new File("cif_test_file.cif");
        byte[] bytes = Files.readAllBytes(Paths.get(file.toURI()));
        ByteBuf buf = Unpooled.wrappedBuffer(bytes).order(ByteOrder.LITTLE_ENDIAN);

        CifFileCodec codec = new CifFileCodec();
        CifFile cif = codec.unpack(buf);
        Files.write(
            Paths.get(
                new File(
                    file.getParentFile(),
                    file.getName() + ".ini").toURI()
            ),
            cif.getLines(),
            StandardCharsets.ISO_8859_1,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING);
    }
    
    static void b() throws Exception {
        String name = "cif_test_file";
        File file = new File(name + ".ini");
        List<String> lines = Files.readAllLines(Paths.get(file.toURI()));
        ByteBuf buf = Unpooled.buffer().order(ByteOrder.LITTLE_ENDIAN);
        CifFile o = new CifFile(CifFile.FileFormat.CIF, CifFile.InternalFormat.TYPE2, lines);

        CifFileCodec codec = new CifFileCodec();
        codec.pack(o, buf);
        
        
        Files.write(
            Paths.get(
                new File(
                    file.getParentFile(),
                    name + ".cif").toURI()
            ),
            ByteBufUtil.getBytes(buf),
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING);
    }
}
