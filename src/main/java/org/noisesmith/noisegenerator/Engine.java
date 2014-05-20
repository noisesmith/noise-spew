package org.noisesmith.noisegenerator;

import org.noisesmith.noisespew.Command;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.EnumMap;
import java.util.Hashtable;
import java.util.stream.Stream;
import java.io.File;

public class Engine implements Runnable {
    public int buffSize;
    public int cardIndex;
    public int sr;
    public ArrayList<UGen> sources;
    SourceDataLine sink;
    double[][] ugenBuffers;
    public SynchronousQueue<Command> messages;
    public ArrayBlockingQueue<String> replies;

    public static String badIndex(int index, String s, ArrayList<UGen> src) {
        if (src.size() <= index || index < 0) {
            return "\nEngine error: " +
                s +
                " " +
                index;
        } else {
            return null;
        }
    }

    public static final class EngineEnv {
        public SynchronousQueue in;
        public ArrayList<UGen> sources;
        public double master;
        public Hashtable <String, double[]> buffers;
        public EngineEnv ( SynchronousQueue i, ArrayList<UGen> s ) {
            in = i;
            sources = s;
            master = 0.5;
            buffers = new Hashtable<String, double[]>();
        }
    }

    public Engine(int card, int buffering, int sampleRate,
                  SynchronousQueue<Command> m,
                  ArrayBlockingQueue<String> p) {
        cardIndex = card;
        buffSize = buffering*4;
        sr = sampleRate;
        messages = m;
        replies = p;
        sources = new ArrayList();
    }

    public Engine(SynchronousQueue m, ArrayBlockingQueue p) {
        this(0, 2048, 44100, m, p);
    }

    public void run () {
        try {
            SourceDataLine sink = sink(cardIndex, sr, buffSize);
            int  dataCount = buffSize / 2; // always shorts
            int frames = dataCount / 2; // always stereo
            byte[] buffer = new byte[buffSize];
            ByteBuffer out = ByteBuffer.wrap(buffer);
            double left, right;
            EngineEnv environment = new EngineEnv(messages, sources);
            String result;
            sink.start();
            while (true) {
                Command toRun = messages.poll();
                if (toRun != null) {
                    result = toRun.execute(environment);
                    if(result != null) replies.put(result);
                }
                ugenBuffers = sources
                    .stream()
                    .filter(u -> u.active)
                    .map(u -> u.gen(frames))
                    .toArray(x -> new double[x][]);
                out.clear();
                out.order(ByteOrder.LITTLE_ENDIAN);
                for(int i = 0; i < dataCount; i += 2) {
                    left = 0.0;
                    right = 0.0;
                    for(double[] b : ugenBuffers) {
                        left += b[i];
                        right += b[i+1];
                    }
                    left = Math.min(left, 1.0);
                    left = Math.max(left, -1.0);
                    right = Math.min(right, 1.0);
                    right = Math.max(right, -1.0);
                    out.putShort((short) Math.floor(left
                                                    * Short.MAX_VALUE
                                                    * environment.master));
                    out.putShort((short) Math.floor(right
                                                    * Short.MAX_VALUE
                                                    * environment.master));
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

