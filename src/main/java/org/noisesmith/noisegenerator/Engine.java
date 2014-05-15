package org.noisesmith.noisegenerator;

import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class Engine implements Runnable {
    public int buffSize;
    public int cardIndex;
    public int sr;
    public ArrayList<UGen> sources = new ArrayList();
    private SourceDataLine sink;
    public double master = 0.5;
    private double[][] ugenBuffers;

    public Engine(int card, int buffering, int sampleRate) {
        cardIndex = card;
        buffSize = buffering*4;
        sr = sampleRate;
    }

    public Engine() {
        this(0, 2048, 44100);
    }

    static double acc(double[][] buffers, int index) {
        double result = 0.0;
        for (double[] buffer : buffers) {
            result += buffer[index];
        }
        return result;
    }

    public void run ( ) {
        try {
            SourceDataLine sink = sink(cardIndex, sr, buffSize);
            int  dataCount = buffSize / 2; // always shorts
            int frames = dataCount / 2; // always stereo
            byte[] buffer = new byte[buffSize];
            ByteBuffer out = ByteBuffer.wrap(buffer);
            ugenBuffers = new double[0][];
            double left, right;
            sink.start();
            while (true) {
                if(sources.size() > ugenBuffers.length) {
                    ugenBuffers = new double[sources.size()][];
                } else {
                }
                for(int i = 0; i < sources.size(); i++) {
                    ugenBuffers[i] = sources.get(i).gen(frames);
                }
                out.clear();
                out.order(ByteOrder.LITTLE_ENDIAN);
                for(int i = 0; i < dataCount; i += 2) {
                    left = 0.0;
                    right = 0.0;
                    for(int j = 0; j < ugenBuffers.length; j++) {
                        left += ugenBuffers[j][i];
                        right += ugenBuffers[j][i+1];
                    }
                    left = Math.min(left, 1.0);
                    left = Math.max(left, -1.0);
                    right = Math.min(right, 1.0);
                    right = Math.max(right, -1.0);
                    out.putShort((short) Math.floor(left
                                                    * Short.MAX_VALUE
                                                    * master));
                    out.putShort((short) Math.floor(right
                                                    * Short.MAX_VALUE
                                                    * master));
                }
                sink.write(buffer, 0, buffSize);
            }
        } catch (Exception e) {
            System.out.println("error in Engine.run");
            e.printStackTrace();
        }
    }

    public static SourceDataLine sink ( int n, int sr, int buffSize )
        throws javax.sound.sampled.LineUnavailableException {
        Mixer.Info mixer_info = AudioSystem.getMixerInfo()[n];
        Mixer mixer = AudioSystem.getMixer(mixer_info);
        AudioFormat.Encoding encoding = new AudioFormat.Encoding("PCM_SIGNED");
        float r = (float) sr;
        AudioFormat format = new AudioFormat(encoding, r, 16, 2, 4, r, false);
        Class target = SourceDataLine.class;
        DataLine.Info line_info = new DataLine.Info(target, format);
        SourceDataLine sink = (SourceDataLine) mixer.getLine(line_info);
        sink.open(format, buffSize);
        return sink;
    }

}

