package org.noisesmith.noisegenerator.ugens;

import org.noisesmith.noisegenerator.UGen;
import java.util.Arrays;

public class Xor extends Am {
    public Xor(UGen left, UGen right) {
        super(left, right);
    }

    public String statusString() {
        if (a != null && b !=null)
            return "Xor" + (active ? "on" : "off") +
                " (" + amp + ")\n" +
                "\tA: " + a.statusString() +
                "\tB: " + b.statusString();
        else
            return "Xor (inactive)";
    }

    double xor (double left, double right) {
        long a = (long) Math.floor(left * Long.MAX_VALUE);
        long b = (long) Math.floor(right * Long.MAX_VALUE);
        long result = a ^ b;
        return result / (Long.MAX_VALUE * 1.0);
    }

    public double[] gen( int size, long index ) {
        double[] output = out(size*2);
        if (index == produced)
            return output;
        produced = index;
        if (a != null && b != null) {
            double[] aSig = a.gen(size, index);
            double[] bSig = b.gen(size, index);
            for(int i = 0; i < size*2; i += 2) {
                output[i] = xor(aSig[i], bSig[i]) * amp;
                output[i+1] = xor(aSig[i+1], bSig[i+1]) * amp;
            }
        } else {
            Arrays.fill(output, 0.0);
        }
        return output;
    }
}
