package org.noisesmith.noisegenerator.ugens;

import org.noisesmith.noisegenerator.UGen;
import java.util.Arrays;

public class Am implements UGen {
    double amp;
    String description;
    UGen a;
    UGen b;
    boolean active;

    public Am(UGen left, UGen right) {
        a = left;
        b = right;
        active = true;
    }

    public double setAmp(double value) {
        double old = amp;
        amp = value;
        return old;
    }

    public double setRate(double value) {return Double.NaN;}

    public String setDescription(String desc) {
        String old = description;
        description = desc;
        return old;
    }

    public String getDescription() {return description;}

    public String statusString() {
        if (a != null && b !=null)
            return "AM " + (active ? "on" : "off") +
                " (" + amp + ")\n" +
                "\tA: " + a.statusString() +
                "\tB: " + b.statusString();
        else
            return "AM (inactive)";
    }

    public boolean isActive() {
        return a != null && b != null && active;
    }

    public void start () {active = true;}
    public void stop () {active = false;}
    public void toggle() {
        active = !active;
    }

    double[] out;
    long produced;

    public double[] gen( int size, long index ) {
        double[] output = out(size*2);
        if (index == produced)
            return output;
        produced = index;
        if (a != null && b != null) {
            double[] aSig = a.gen(size, index);
            double[] bSig = b.gen(size, index);
            for(int i = 0; i < size*2; i += 2) {
                output[i] = aSig[i] * bSig[i] * amp;
                output[i+1] = aSig[i+1] * bSig[i+1] * amp;
            }
        } else {
            Arrays.fill(output, 0.0);
        }
        return output;
    }

    double[] out(int count) {
        if (out == null || out.length < count) {
            out = new double[count];
        }
        return out;
    }
}
