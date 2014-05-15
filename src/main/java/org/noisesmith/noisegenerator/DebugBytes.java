package org.noisesmith.noisegenerator;

import java.nio.file.*;

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
}

