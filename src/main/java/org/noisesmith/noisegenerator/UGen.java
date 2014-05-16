package org.noisesmith.noisegenerator;

import java.util.Arrays;
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class UGen {
    public enum LoopType {
        LOOP,
        PINGPONG,
        ONESHOT
    }
    // parent class for inputs to Engine instances

    // full scale is -1.0 ... 1.0 - anything larger will clip unless truncated

    // simply outputs the contents of its buffer, same data to each channel,
    // looped
    public double[] buffer;
    public double phase = 0.0;
    public int position = 0;
    public double amp = 1.0;
    public double rate = 1.0;
    public Boolean active = false;
    public int start = 0;
    public int end = 0;
    public String description = "ugen";
    public LoopType looping;

    double[] outBuffer;

    void normalizePosition() {
        int lower = Math.min(start, end);
        int upper = Math.max(start, end);
        if (position < lower) {
            position = lower;
            phase = 0.0;
        } else if (position > upper) {
            position = upper;
            phase = (double) upper - lower;
        }
    }

    int getTarget( double where ) {
        int target = (int) (where * 44100);
        target = Math.min(target, buffer.length/2);
        target = Math.max(target, 0);
        return target;
    }

    public void in(double where) {
        start = getTarget(where);
        normalizePosition();
    }

    public void out(double where) {
        end = getTarget(where);
        normalizePosition();
    }

    public void toggle () {
        active = !active;
    }

    public void stop () {
        active = false;
    }

    public void start () {
        active = true;
    }

    void updatePhase () {
        int loopsize = (end > start) ? end - start : start - end;
        int direction = (end > start) ? 1 : -1;
        if(loopsize == 0) {
            phase = 0.0;
            position = 0;
        } else {
            phase += rate;
            if(phase >= loopsize || phase <= 0) {
                switch (looping) {
                    case ONESHOT:
                        active = false;
                        phase = 0;
                        break;
                    case PINGPONG:
                        rate *= -1;
                        phase += rate;
                        break;
                    case LOOP:
                    default:
                        phase %= loopsize;
                    }
            }
            position = ((int)phase)*direction+start;
        }
    }

    void fill ( int count ) {
        for(int i = 0; i < count; i += 2) {
            double out = buffer[position]*amp;
            outBuffer[i] = out;
            outBuffer[i+1] = out;
            updatePhase();
        }
    }

    public double[] gen(int size) {
        int count = size*2; // stereo
        if (count > outBuffer.length) {
            outBuffer = new double[count];
        } else {
        }
        fill(count);
        return outBuffer;
    }

    public UGen(double[] buf) {
        buffer = buf;
        rate = 1.0;
        amp = 1.0;
        active = false;
        start = 0;
        end = buf.length/2;
        looping = LoopType.LOOP;
        outBuffer = new double[0];
        description = "unit generator";
    }

    private static double[] empty(int size) {
        double[] buf = new double[size];
        Arrays.fill(buf, 0.0);
        return buf;
    }

    public UGen(int size) {
        this(empty(size));
    }

    public UGen() {
        this(2048);
    }

    public void sine(double hz, int sr) {
        int count = (int) Math.ceil(sr / hz);
        buffer = new double[count];
        double increment = (Math.PI*2)/count;
        double hzAdj;
        for(int i = 0; i < count; i++) {
            hzAdj = i*increment;
            buffer[i] = Math.sin(hzAdj);
        }
    }

    // for now this only works with CD format uncompressed files
    public static double[] fileBuffer ( String input )
        throws javax.sound.sampled.UnsupportedAudioFileException,
               java.io.IOException {
        File inputfile = new File(input);
        AudioInputStream source = AudioSystem.getAudioInputStream(inputfile);
        int size = (int)source.getFrameLength() * 2; // stereo
        int bytesize = size*2; // short / byte
        double[] contents = new double[size];
        byte[] rawbytes = new byte[bytesize];
        int target = size*2;
        int read = 0;
        int status = 0;
        while (read < target && status != -1) {
            status = source.read(rawbytes, read, target-read);
            read += status;
        }
        ByteBuffer rawcontents = ByteBuffer.wrap(rawbytes);
        rawcontents.order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < size; i++) {
            contents[i] = (rawcontents.getShort() / (Short.MAX_VALUE * 1.0));
        }
        return contents;
    }
}
