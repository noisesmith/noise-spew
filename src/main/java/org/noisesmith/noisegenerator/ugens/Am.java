package org.noisesmith.noisegenerator.ugens;

import org.noisesmith.noisegenerator.UGen;
import org.noisesmith.noisegenerator.Input;
import org.noisesmith.noisegenerator.Output;
import java.util.Arrays;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.UUID;

public class Am implements UGen {
    double amp;
    String description;
    Input a;
    Input b;
    Output out;
    boolean active;
    String id;

    public Am(Output left, Output right) {
        a = new Input("a");
        b = new Input("b");
        out = new Output(this, "out");
        a.plug(left);
        b.plug(right);
        active = true;
        id = UUID.randomUUID().toString();
    }

    public String getId() {return id;}

    public double setParameter(String name, double value) {return Double.NaN;}

    public Map<String,Output> getOutputs() {
        Map<String,Output> result = new LinkedHashMap<String,Output>();
        result.put("out", out);
        return result;
    }

    public Map<String,Input> getInputs() {
        LinkedHashMap<String,Input> result = new LinkedHashMap<String,Input>();
        result.put("a", a);
        result.put("b", b);
        return result;
    }

    public void input(UGen in, String which) {
        a.plug(in.getOutputs().get(which));
    }

    public void unplug(UGen in, String which) {
        a.unplug(in.getOutputs().get(which));
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

    double[] outbuffer;
    long produced;

    public double[] gen( String which, int size, long index ) {
        if (which != "out") return null;
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
        if (outbuffer == null || outbuffer.length < count) {
            outbuffer = new double[count];
        }
        return outbuffer;
    }
}
