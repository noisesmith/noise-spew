package org.noisesmith.noisegenerator;

import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;

public class Channel implements UGen {
    double amp;
    String description;
    boolean active;
    double[] output;
    long lastIndex;
    String id;

    HashSet<UGen> inputs;
    HashSet<UGen> outputs;

    public Channel(String name) {
        description = name + " -- " + id;
        active = true;
        amp = 1.0;
        inputs = new HashSet<UGen>();
        outputs = new HashSet<UGen>();
        id = name;
    }

    public String getId() {return id;}

    public double setAmp(double value) {
        double old = amp;
        amp = value;
        return old;
    }

    public double setParameter(String name, double value) {return Double.NaN;}

    public String setDescription(String desc) {
        String old = description;
        description = desc;
        return old;
    }

    public String getDescription() {return description;}

    public String statusString() {
        return new StringBuilder()
            .append("Channel ")
            .append(active ? "on" : "off")
            .append(" ")
            .append(" amp:")
            .append(amp)
            .append(" ")
            .append(description)
            .toString();
    }

    public boolean isActive() {return active;}

    public void toggle() {active = !active;}

    public void stop() {active = false;}

    public void start() {active = true;}

    public double[] gen(int size, long index) {
        if(index == lastIndex) {return output;}
        index = lastIndex;
        if(output == null || output.length < size) {
            output = new double[size];
        }
        Arrays.fill(output, 0.0);
        if(!active) {
            return output;
        }
        for(UGen input : inputs) {
            double[] in = input.gen(size, index);
            for(int i = 0; i < output.length; i++) {
                output[i] += in[i];
            }
        }
        for(int i = 0; i < output.length; i++) {
            output[i] *= amp;
        }
        return output;
    }

    public void input(UGen in) {
        if(!inputs.contains(in)) {
            inputs.add(in);
            in.output(this);
        }
    }

    public void output(UGen out) {
        outputs.add(out);
    }

    public void unplug(UGen in) {
        if(inputs.contains(in)) {
            inputs.remove(in);
            in.disconnect(this);
        }
    }

    public void disconnect(UGen out) {
        if(outputs.contains(out)) {
            outputs.remove(out);
            out.unplug(this);
        }
    }

    public Set<UGen> getSources() {
        return inputs;
    }

    public Channel getSource(String which) {return this;}

    public Set<UGen> getSinks() {
        return outputs;
    }

    public Channel getSink(String which) {return this;}
}
