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
import java.util.Map;
import java.util.Hashtable;
import java.util.stream.Stream;
import java.io.File;
import java.util.function.Function;

public class Engine implements Runnable {
    public int buffSize;
    public int cardIndex;
    public int sr;
    public Input sourceLeft;
    public Input sourceRight;
    SourceDataLine sink;
    public SynchronousQueue<Command.ICommand> messages;

    public static final class EngineEnv {
        public SynchronousQueue in;
        public Input sourceLeft;
        public Input sourceRight;
        public Map<String,UGen> ugens;
        public double master;
        public Hashtable <String, double[]> buffers;
        public EngineEnv ( SynchronousQueue i, Input l, Input r) {
            in = i;
            sourceLeft = l;
            sourceRight = r;
            master = 0.5;
            buffers = new Hashtable<String, double[]>();
            ugens = new Hashtable<String,UGen>();
        }
        public UGen getUGen(String toMatch) {
            ArrayList<UGen> found = new ArrayList<UGen>();
            for (UGen source : ugens.values())
                if(source.getId().startsWith(toMatch))
                    found.add(source);
            switch (found.size()) {
            case 1:
                return found.get(0);
            case 0:
            default:
                return null;
            }
        }
        public boolean putUGen(String id, UGen ugen) {
            if(ugens.containsKey(id))
                return false;
            ugens.put(id, ugen);
            return true;
        }
        public boolean deleteUGen(String id) {
            if(ugens.containsKey(id))
                return false;
            else
                ugens.remove(id);
            // TODO also eliminate it from the signal flow graph
            return true;
        }
    }

    public Engine(int card, int buffering, int sampleRate,
                  SynchronousQueue<Command.ICommand> m) {
        cardIndex = card;
        buffSize = buffering*4;
        sr = sampleRate;
        messages = m;
        sourceLeft = new Input("left");
        sourceRight = new Input("right");
    }

    public Engine(SynchronousQueue m) {
        this(0, 2048, 44100, m);
    }

    public void run () {
        try {
            SourceDataLine sink = sink(cardIndex, sr, buffSize);
            int  dataCount = buffSize / 2; // always shorts
            int frames = dataCount / 2; // always stereo
            byte[] buffer = new byte[buffSize];
            ByteBuffer out = ByteBuffer.wrap(buffer);
            double[] leftBuf, rightBuf;
            double left, right;
            EngineEnv environment = new EngineEnv(messages, sourceLeft, sourceRight);
            String result;
            sink.start();
            long index = 0;
            while (true) {
                Command.ICommand toRun = messages.poll();
                if (toRun != null) {
                    result = toRun.execute(environment);
                    result = (result == null) ? "" : result;
                    if(toRun.getSender() != null) {
                        try {
                            toRun.getSender().add(result);
                        } catch (Exception e) {
                            System.out.println("engine reply queue error");
                            e.printStackTrace();
                        }
                    }
                }
                out.clear();
                out.order(ByteOrder.LITTLE_ENDIAN);
                leftBuf = sourceLeft.gen(frames, index);
                rightBuf = sourceRight.gen(frames, index);
                for(int i = 0; i < frames; i++) {
                    left = leftBuf[i];
                    right = rightBuf[i];
                    left = Math.min(left, 1.0);
                    right = Math.min(right, 1.0);
                    left = Math.max(left, -1.0);
                    right = Math.max(right, -1.0);
                    out.putShort((short) Math.floor(left
                                                    * Short.MAX_VALUE
                                                    * environment.master));
                    out.putShort((short) Math.floor(right
                                                    * Short.MAX_VALUE
                                                    * environment.master));
                }
                sink.write(buffer, 0, buffSize);
                index++;
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

