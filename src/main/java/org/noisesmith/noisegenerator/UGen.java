package org.noisesmith.noisegenerator;

import java.util.Arrays;
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public interface UGen {
    // interface for inputs to Engine instances

    // full scale is -1.0 ... 1.0 - anything larger will clip unless truncated

    public void setAmp(double value);

    public double setRate(double value);

    public String setDescription(String desc);

    public String getDescription();

    public String statusString();

    public boolean isActive();

    public void toggle();

    public void stop ();

    public void start ();

    public double[] gen(int size);
}
