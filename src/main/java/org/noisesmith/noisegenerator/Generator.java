package org.noisesmith.noisegenerator;

import javax.sound.sampled.SourceDataLine;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Generator {

    public static void main ( String[] args ) {
        try {
            int buffSize = 2048;
            SourceDataLine sink = Engine.sink(0, 44100, buffSize);
            byte[] bytes = dummyData(buffSize);
            int toWrite = 0,
                index = 0,
                written = 0,
                writeCount = 0;
            sink.start();
            for (int i = 0; i < 1000; i++) {
                sink.write(bytes, 0, bytes.length);
                writeCount++;
            }
            // DebugBytes.toFile("./buffer-contents", bytes);
            sink.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] dummyData( int buffSize ) {
        int n = buffSize;
        byte[] data = new byte[n];
        ByteBuffer buff = ByteBuffer.wrap(data);
        buff.order(ByteOrder.LITTLE_ENDIAN);
        Double scale = (double) Short.MAX_VALUE/2;
        double[] wave = waveForm(buffSize/2); // two bytes per short
        for (int i = 0; i < n/4; i++) { // writing 4 bytes per iteration
            short output = (short) Math.floor(wave[i] * scale);
            buff.putShort(output); // left then right
            buff.putShort(output);
        }
        return data;
    }
    public static double[] waveForm(int count) {
        double[] out = new double[count];
        double hzAdj;
        for(int i = 0; i < count; i++) {
            hzAdj = i/82.0;
            out[i] = Math.sin(hzAdj);
        }
        return out;
    }
}
