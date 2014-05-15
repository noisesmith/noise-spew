package org.noisesmith.noisegenerator;

import java.util.Arrays;
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class UGen {
    // parent class for inputs to Engine instances

    // full scale is -1.0 ... 1.0 - anything larger will clip unless truncated

    // simply outputs the contents of its buffer, same data to each channel,
    // looped
    public double[] buffer;
    public int phase = 0;
    double[] outBuffer;

    void fill ( int count ) {
        for(int i = 0; i < count; i += 2) {
            outBuffer[i] = buffer[phase];
            outBuffer[i+1] = buffer[phase];
            phase = (phase + 1) % buffer.length;
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
    public UGen(int size) {
        buffer = new double[size];
        Arrays.fill(buffer, 0.0);
        outBuffer = new double[0];
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
