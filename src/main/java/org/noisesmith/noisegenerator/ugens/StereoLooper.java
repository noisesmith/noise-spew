package org.noisesmith.noisegenerator.ugens;

public class StereoLooper extends Looper {
    public StereoLooper (int size) {
        super(size);
    }

    public StereoLooper (double[] buf) {
        super(buf);
    }

    public StereoLooper () {
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
