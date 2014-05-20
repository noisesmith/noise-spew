package org.noisesmith.noisespew.commands;

import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.NoiseSpew.ControlEnv;
import org.noisesmith.noisegenerator.Engine.EngineEnv;
import java.util.function.Function;

public class Master extends Command {
    public static Function<String[], Command> parse = s -> new Master(s);

    double amp;

    public Master (String[] args) {
        amp = Double.parseDouble(args[0]);
    }

    public String execute ( ControlEnv environment ) {return null;}

    public String execute ( EngineEnv environment ) {
        environment.master = amp;
        return null;
    }
}
