package org.noisesmith.noisespew.commands;

import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.NoiseSpew;
import org.noisesmith.noisegenerator.Engine;
import java.util.function.Function;

public class Amplitude extends Command {
    public static Function<String[], Command> parse = s -> new Amplitude(s);

    int index;
    double amp;

    public Amplitude (String[] args) {
        index = Integer.parseInt(args[0]);
        amp = Double.parseDouble(args[1]);
    }

    public String execute ( NoiseSpew.ControlEnv environment ) {return null;}

    public String execute ( Engine.EngineEnv environment ) {
        String error = Engine.badIndex(index,
                                       "could not change amp of",
                                       environment.sources);
        if (error != null) {
            return error;
        } else {
            environment.sources.get(index).amp = amp;
            return null;
        }
    }
}
