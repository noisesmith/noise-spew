package org.noisesmith.noisegenerator;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.sound.sampled.*;
import javax.sound.sampled.AudioFormat.Encoding;

public final class Generator {
    public static void main ( String[] args ) {
        try {
            System.out.println("opening the sink");
            int buffSize = 2048;
            SourceDataLine sink = sink(0, 44100, buffSize);
            System.out.println("sink is open");
            byte[] bytes = dummyData(buffSize);
            for (int i = 0; i < 100000; i++) {
                sink.write(bytes, 0, buffSize);
            }
            Thread.sleep(30000);
        } catch 	(Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] dummyData(int buffSize) {
        int n = buffSize;
        short[] signal = new short[n/2];
        for (int i = 0; i < signal.length; i += 2) {
            signal[i] = (short) Math.floor(Math.sin(i/100)*Short.MAX_VALUE);
            signal[i+1] = signal[i];
        }
        byte[] data = new byte[n];
        ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(signal);
        return data;
    }

    public static SourceDataLine sink (int n, int sr, int buf)
			throws LineUnavailableException {
		Mixer.Info mixer_info = AudioSystem.getMixerInfo()[n];
		Mixer mixer = AudioSystem.getMixer(mixer_info);
		Encoding encoding = new Encoding("PCM_SIGNED");
		AudioFormat format = new AudioFormat(encoding, (float)sr, 16, 2, 4, (float)sr, false);
		DataLine.Info line_info = new DataLine.Info(SourceDataLine.class, format);
		SourceDataLine sink = (SourceDataLine) mixer.getLine(line_info);
                sink.open(format, buf);
		return sink;
	}
}
