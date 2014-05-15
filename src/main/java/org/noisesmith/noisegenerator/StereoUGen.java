package org.noisesmith.noisegenerator;

public class StereoUGen extends UGen {
    void fill ( int count ) {
        for(int i = 0; i < count; i++) {
            outBuffer[i] = buffer[phase];
            phase = (phase + 1) % buffer.length;
        }
    }
}
