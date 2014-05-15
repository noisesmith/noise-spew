package org.noisesmith.noisegenerator;

import java.util.Arrays;

public class UGen {
    // parent class for inputs to Engine instances

    // full scale is -1.0 ... 1.0 - anything larger will clip unless truncated

    // simply outputs the contents of its buffer, same data to each channel,
    // looped
    public double[] buffer;
    public int phase = 0;
    private double[] outBuffer;

    public double[] gen(int size) {
        int count = size*2; // stereo
        if (count > outBuffer.length) {
            outBuffer = new double[count];
        } else {
        }
        for(int i = 0; i < count; i += 2) {
            outBuffer[i] = buffer[phase];
            outBuffer[i+1] = buffer[phase];
            phase = (phase + 1) % buffer.length;
        }
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
}

