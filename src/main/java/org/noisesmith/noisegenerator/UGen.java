package org.noisesmith.noisegenerator;

import java.util.Map;

public interface UGen {
    // interface for inputs to Engine instances

    // full scale is -1.0 ... 1.0 - anything larger will clip unless truncated

    public double setAmp(double value);

    public double setParameter(String name, double value);

    public String setDescription(String desc);

    public String getDescription();

    public String getId();

    public String statusString();

    public boolean isActive();

    public void toggle();

    public void stop();

    public void start();

    public double[] gen(String name, int size, long index);

    public Map<String,Input> getInputs();

    public Map<String,Output> getOutputs();

    public boolean connect (UGen owner, String port, String into) {
        try {
            getInputs().get(into).input(owner.getOutputs().get(port));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
