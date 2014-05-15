package org.noisesmith.noisegenerator;

import java.util.Arrays;
import java.nio.*;
import javax.sound.sampled.*;
import javax.sound.sampled.AudioFormat.Encoding;

public final class Generator {
    public static void main ( String[] args ) {
        try {
            int buffSize = 2048;
            SourceDataLine sink = sink(0, 44100, buffSize);
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

    public static byte[] dummyData(int buffSize) {
        int n = buffSize;
        byte[] data = new byte[n];
        ByteBuffer buff = ByteBuffer.wrap(data);
        buff.order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < n/4; i++) { // writing 4 bytes per iteration
            Double hzAdj = i/82.0; // makes one cycle with 2048 buffsize
            Double scale = (double) Short.MAX_VALUE/2;
            short output = (short) Math.floor(Math.sin(hzAdj) * scale);
            buff.putShort(output); // left then right
            buff.putShort(output);
        }
        return data;
    }

    public static SourceDataLine sink (int n, int sr, int buffSize)
        throws LineUnavailableException {
        Mixer.Info mixer_info = AudioSystem.getMixerInfo()[n];
        Mixer mixer = AudioSystem.getMixer(mixer_info);
        Encoding encoding = new Encoding("PCM_SIGNED");
        float r = (float) sr;
        AudioFormat format = new AudioFormat(encoding, r, 16, 2, 4, r, false);
        Class target = SourceDataLine.class;
        DataLine.Info line_info = new DataLine.Info(target, format);
        SourceDataLine sink = (SourceDataLine) mixer.getLine(line_info);
        sink.open(format, buffSize);
        return sink;
    }
}
