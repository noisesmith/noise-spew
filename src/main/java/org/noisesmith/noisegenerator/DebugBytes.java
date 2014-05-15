package org.noisesmith.noisegenerator;

import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class DebugBytes {
    public static void toFile ( String location, byte[] bytes ) {
        try {
            Path target = Paths.get(location);
            Files.write(target, bytes, StandardOpenOption.CREATE);
        } catch (Exception e) {
            System.out.println("DebugBytes failed to write");
            e.printStackTrace();
        }
    }
    public static void toFile ( String location, double[] doubles ) {
        byte[] buffer = new byte[doubles.length * 2];
        ByteBuffer out = ByteBuffer.wrap(buffer);
        out.order(ByteOrder.LITTLE_ENDIAN);
        for(int i = 0; i < doubles.length; i++) {
            out.putShort((short) (Math.floor(doubles[i]*Short.MAX_VALUE)));
        }
        toFile(location, buffer);
    }
}
