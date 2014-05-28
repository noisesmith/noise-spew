package org.noisesmith.noisegenerator.ugens;

import java.util.Arrays;
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.noisesmith.noisegenerator.UGen;

public class Looper implements UGen {
    // simply outputs the contents of its buffer, same data to each channel,
    // looped

    public enum LoopType {
        LOOP,
        PINGPONG,
        ONESHOT
    }
    double[] buffer;
    double phase = 0.0;
    int position = 0;
    double amp = 1.0;
    double rate = 1.0;
    Boolean active = false;
    int start = 0;
    int end = 0;
    String description = "ugen";
    LoopType looping;

    double[] outBuffer;

    public boolean isActive() {return active;}

    public void setAmp(double value) {amp = value;}

    public LoopType setLooping(LoopType selected) {
        LoopType old = looping;
        looping = selected;
        return old;
    }

    public LoopType setLooping(int selection) {
        LoopType selected;
        switch (selection) {
            case 1:
                selected = LoopType.PINGPONG;
                break;
            case 2:
                selected = LoopType.ONESHOT;
                break;
            case 0:
            default:
                selected = LoopType.LOOP;
            }
        return setLooping(selected);
    }

    public double setRate(double value) {
        double old = rate;
        rate = value;
        return old;
    }

    public String setDescription(String desc) {
        String old = description;
        description = desc;
        return old;
    }

    public String getDescription() {
        return description;
    }

    public double getStart() {
        return start / 44100.0;
    }

    public double getEnd() {
        return end / 44100.0;
    }

    public String isActiveString() {
        return active ? "on" : "off";
    }

    public double getPosition() {
        return position / 44100.0;
    }

    public String statusString() {
        StringBuilder message = new StringBuilder();
        String pos = String.format("%5G", getPosition());
        message
            .append(isActiveString())
            .append("	")
            .append(rate)
            .append("	~")
            .append(pos)
            .append("~ ")
            .append(getStart())
            .append("->")
            .append(getEnd())
            .append("	")
            .append(getDescription())
            .append("\n");
        return message.toString();
    }

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

    public Looper(double[] buf) {
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

    public Looper(int size) {
        this(empty(size));
    }

    public Looper() {
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
