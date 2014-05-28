package org.noisesmith.noisegenerator;

public interface UGen {
    // interface for inputs to Engine instances

    // full scale is -1.0 ... 1.0 - anything larger will clip unless truncated

    public double setAmp(double value);

    public double setRate(double value);

    public String setDescription(String desc);

    public String getDescription();

    public String statusString();

    public boolean isActive();

    public void toggle();

    public void stop ();

    public void start ();

    public double[] gen(int size, long index);
}
