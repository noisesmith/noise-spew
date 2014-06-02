package org.noisesmith.noisegenerator;

import java.util.Map;
import java.util.Vector;

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
            Input input = getInputs().get(into);
            input.input(owner.getOutputs().get(port));
            Vector<Input> connected = connections.get(owner.getId());
            if (connected == null) connected = new Vector<Input>();
            connected.add(this);
            connections.put(owner.getId(), connected);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    private static Map<String,Vector<UGen>> connections
        = new LinkedHashMap<String, Vector<UGen>>();
}
