package org.noisesmith.noisegenerator;

import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;

public class Input {
    double amp;
    final String name;
    HashSet<Output> sources;
    double[] output;

    public Input(String n) {
        name = n;
        sources = new HashSet<Output>();
        amp = 1.0;
    }

    public String statusString() {
        StringBuilder result = new StringBuilder();
        result.append(name)
            .append(' ');
        for(Output o : sources) {
            result.append(o.statusString());
        }
        return result.toString();
    }

    public String getName() {return name;}

    public double setAmp(double value) {
        double old = amp;
        amp = value;
        return old;
    }

    int lastIndex = -1;
    public double[] gen(int size, long index) {
        if(index == lastIndex) {return output;}
        index = lastIndex;
        if(output == null || output.length < size) {
            output = new double[size];
        }
        Arrays.fill(output, 0.0);
        for(Output source : sources) {
            double[] in = source.gen(size, index);
            for(int i = 0; i < output.length; i++) {
                output[i] += in[i];
            }
        }
        for(int i = 0; i < output.length; i++) {
            output[i] *= amp;
        }
        return output;
    }

    public void plug(Output in) {
        if(!sources.contains(in)) {
            sources.add(in);
        }
    }

    public void unplug(Output in) {
        if(sources.contains(in)) {
            sources.remove(in);
        }
    }
}
