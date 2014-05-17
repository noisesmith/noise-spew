package org.noisesmith.noisegenerator;

import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.concurrent.SynchronousQueue;
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
    public double master = 0.5;
    double[][] ugenBuffers;
    public SynchronousQueue<Exec> messages;
    Hashtable <String, double[]> buffers;

    public enum Action {
        STOP,
        START,
        TOGGLE,
        CREATE,
        DELETE,
        LOOPPOINTS,
        GAIN,
        RATE,
        DEBUG,
        LOOPTYPE,
        MASTER
    }

    public static class Exec {
        Action action;
        int index;
        int selection;
        double in;
        double out;
        double parameter;
        double gain;
        String input;

        public Exec( Action a, int idx ) {
            action = a;
            index = idx;
        }
        public Exec( Action a, int idx, int s) {
            action = a;
            index = idx;
            selection = s;
        }
        public Exec( Action a, int idx, double p ) {
            action = a;
            index = idx;
            parameter = p;
        }
        public Exec( Action a, int idx, double i, double o ) {
            action = a;
            index = idx;
            in = i;
            out = o;
        }
        public Exec( Action a, String i ) {
            action = a;
            input = i;
        }
        public Exec( Action a, double p ) {
            action = a;
            parameter = p;
        }
        public Exec( Action a ) {
            action = a;
        }
    }

    static final EnumMap<Action,String>
        errors = new EnumMap<Action,String>(Engine.Action.class) {{
            put(Action.TOGGLE, "could not toggle playback of");
            put(Action.LOOPPOINTS, "could not reloop");
            put(Action.CREATE, "could not load loop");
            put(Action.DELETE, "could not delete");
            put(Action.GAIN, "could not change amp of");
            put(Action.RATE, "cound not change rate of");
        }};

    Boolean badIndex(Exec e) {
        if (sources.size() <= e.index || e.index < 0) {
            System.out.println("\nEngine error: " +
                               errors.get(e.action) + " " +
                               e.index);
            return true;
        } else {
            return false;
        }
    }

    void respond(Exec e) {
        UGen loop;
        if (e == null) return;
        if (e.action == null) return;
        try {
            switch(e.action) {
            case TOGGLE:
                if(badIndex(e)) return;
                sources.get(e.index).toggle();
                break;
            case LOOPPOINTS:
                if(badIndex(e)) return;
                loop = sources.get(e.index);
                loop.in(e.in);
                loop.out(e.out);
                loop.start();
                break;
            case CREATE:
                try {
                    String i = new File(e.input).getCanonicalFile().toString();
                    double[] b;
                    if(buffers.containsKey(i)) {
                        b = buffers.get(i);
                    } else {
                        b = UGen.fileBuffer(e.input);
                        buffers.put(i, b);
                    }
                    StereoUGen u = new StereoUGen(b);
                    u.description = e.input;
                    sources.add(0, u);
                } catch (Exception ex) {
                    System.out.println("could not load loop: " + e.input);
                    ex.printStackTrace();
                }
                break;
            case DELETE:
                if(badIndex(e)) return;
                sources.remove(e.index);
                break;
            case GAIN:
                if(badIndex(e)) return;
                sources.get(e.index).amp = e.parameter;
                break;
            case RATE:
                if(badIndex(e)) return;
                sources.get(e.index).rate = e.parameter;
                break;
            case DEBUG:
                System.out.println("DEBUG");
                break;
            case LOOPTYPE:
                if(badIndex(e)) return;
                loop = sources.get(e.index);
                switch (e.selection) {
                case 1:
                    loop.looping = UGen.LoopType.PINGPONG;
                    break;
                case 2:
                    loop.looping = UGen.LoopType.ONESHOT;
                    break;
                case 0:
                default:
                    loop.looping = UGen.LoopType.LOOP;
                }
                break;
            case MASTER:
                master = e.parameter;
                break;
            default:
                System.out.println("Engine - not handled - : " + e.action);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Engine(int card, int buffering, int sampleRate,
                  SynchronousQueue<Exec> m) {
        cardIndex = card;
        buffSize = buffering*4;
        sr = sampleRate;
        messages = m;
        sources = new ArrayList();
        buffers = new Hashtable<String, double[]>();
    }

    public Engine(SynchronousQueue m) {
        this(0, 2048, 44100, m);
    }

    static double acc(double[][] buffers, int index) {
        double result = 0.0;
        for (double[] buffer : buffers) {
            result += buffer[index];
        }
        return result;
    }

    public void run () {
        try {
            SourceDataLine sink = sink(cardIndex, sr, buffSize);
            int  dataCount = buffSize / 2; // always shorts
            int frames = dataCount / 2; // always stereo
            byte[] buffer = new byte[buffSize];
            ByteBuffer out = ByteBuffer.wrap(buffer);
            double left, right;
            sink.start();
            while (true) {
                respond(messages.poll());
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

