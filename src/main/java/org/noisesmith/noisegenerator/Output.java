package org.noisesmith.noisegenerator;

public class Output {
    double amp;
    final String name;
    final UGen source;
    double[] output;

    public Output(UGen s, String n) {
        name = n;
        source = s;
        amp = 1.0;
    }

    public String getName() {return name;}

    public double setAmp(double value) {
        double old = amp;
        amp = value;
        return old;
    }

    int lastIndex;
    public double[] gen(int size, long index) {
        if(index == lastIndex) {return output;}
        index = lastIndex;
        if(output == null || output.length < size) {
            output = new double[size];
        }
        double[] in = source.gen(name, size, index);
        for(int i = 0; i < output.length; i++) {
            output[i] = in[i] * amp;
        }
        return output;
    }

    public String statusString() {
        return source.getId() + ": " + name;
    }


    public UGen getSource() {return source;}
}
