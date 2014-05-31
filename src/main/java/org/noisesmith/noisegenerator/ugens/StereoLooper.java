package org.noisesmith.noisegenerator.ugens;

import java.util.Map;
import java.util.LinkedHashMap;
import org.noisesmith.noisegenerator.Output;

public class StereoLooper extends Looper {
    double[] outRight;
    Output right;

    public Map<String,Output> getOutputs() {
        Map<String,Output> result = new LinkedHashMap<String,Output>();
        result.put("left", out);
        result.put("right", right);
        return result;
    }

    public StereoLooper (int size) {
        super(size);
        out = new Output(this, description + " <left>");
        right = new Output(this, description + " <right>");
    }

    public StereoLooper (double[] buf) {
        super(buf);
    }

    public StereoLooper () {
        super();
    }

    public double[] gen(String selection, int count, long index) {
        double[] response;
        switch (selection) {
        case "left":
            response = outBuffer;
            break;
        case "right":
            response = outRight;
            break;
        default:
            return null;
        }
        if (outBuffer == null || count > outBuffer.length) {
            outBuffer = new double[count];
            outRight = new double[count];
        }
        if (index != produced) {
            for(int i = 0; i < count; i++) {
                int idx = position*2;
                outBuffer[i] = buffer[idx]*amp;
                outRight[i] = buffer[idx+1]*amp;
                updatePhase();
            }
            produced = index;
        }
        return response;
    }
}
