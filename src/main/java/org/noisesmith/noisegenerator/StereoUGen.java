package org.noisesmith.noisegenerator;

public class StereoUGen extends UGen {
    public StereoUGen (int size) {
        super(size);
    }

    public StereoUGen (double[] buf) {
        super(buf);
    }

    public StereoUGen () {
        super();
    }

    void fill ( int count ) {
        for(int i = 0; i < count; i += 2) {
            int idx = position*2;
            outBuffer[i] = buffer[idx]*amp;
            outBuffer[i+1] = buffer[idx+1]*amp;
            updatePhase();
        }
    }
}
