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

public class Engine implements Runnable {
    public enum Action {
        STOP,
        START,
        TOGGLE,
        CREATE,
        DELETE,
        LOOPPOINTS,
        GAIN,
        RATE,
        DEBUG
    }

    public static class Exec {
        Action action;
        int index;
        double in;
        double out;
        double parameter;
        double gain;
        String input;

        public Exec( Action a, int idx ) {
            action = a;
            index = idx;
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
        public Exec( Action a ) {
            action = a;
        }
    }

    void respond(Exec e) {
        UGen loop;
        if (e == null) return;
        try {
            switch(e.action) {
            case TOGGLE:
                if (sources.size() > e.index) {
                    sources.get(e.index).toggle();
                } else {
                    System.out.println("\ncould not toggle playback of " +
                                       e.index);
                }
                break;
            case LOOPPOINTS:
                if (sources.size() > e.index) {
                    loop = sources.get(e.index);
                    loop.in(e.in);
                    loop.out(e.out);
                    loop.start();
                } else {
                    System.out.println("could not reloop " + e.index);
                }
                break;
            case CREATE:
                try {
                    double[] b = UGen.fileBuffer(e.input);
                    StereoUGen u = new StereoUGen(b);
                    u.description = e.input;
                    sources.add(0, u);
                } catch (Exception ex) {
                    System.out.println("could not load loop: " + e.input);
                    ex.printStackTrace();
                }
                break;
            case DELETE:
                if (sources.size() > e.index) {
                    loop = sources.get(e.index);
                    // loop.active = false;
                    sources.remove(loop);
                } else {
                    System.out.println("could not delete " + e.index);
                }
                break;
            case GAIN:
                if (sources.size() > e.index) {
                    sources.get(e.index).amp = e.parameter;
                } else {
                    System.out.println("could not change amp of " + e.index);
                }
                break;
            case RATE:
                if (sources.size() > e.index) {
                    sources.get(e.index).rate = e.parameter;
                } else {
                    System.out.println("could not change amp of " + e.index);
                }
                break;
            case DEBUG:
                System.out.println("DEBUG");
                break;
            default:
                System.out.println("not handled: " + e.action);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public int buffSize;
    public int cardIndex;
    public int sr;
    public ArrayList<UGen> sources;
    SourceDataLine sink;
    public double master = 0.5;
    double[][] ugenBuffers;
    public SynchronousQueue<Exec> messages;

    public Engine(int card, int buffering, int sampleRate,
                  SynchronousQueue<Exec> m) {
        cardIndex = card;
        buffSize = buffering*4;
        sr = sampleRate;
        messages = m;
        sources = new ArrayList();
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
            ugenBuffers = new double[0][];
            ArrayList<UGen> resources;
            double left, right;
            sink.start();
            while (true) {
                respond(messages.poll());
                resources = (ArrayList<UGen>) sources.clone();
                resources.removeIf((u) -> !u.active);
                int size = resources.size();
                if(size > ugenBuffers.length) {
                    ugenBuffers = new double[size][];
                } else {
                }
                for(int i = 0; i < size; i++) {
                    ugenBuffers[i] = resources.get(i).gen(frames);
                }
                out.clear();
                out.order(ByteOrder.LITTLE_ENDIAN);
                for(int i = 0; i < dataCount; i += 2) {
                    left = 0.0;
                    right = 0.0;
                    for(int j = 0; j < size; j++) {
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

