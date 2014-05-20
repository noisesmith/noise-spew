package org.noisesmith.noisespew.commands;

import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.NoiseSpew;
import org.noisesmith.noisegenerator.Engine;
import java.util.function.Function;

public class Exit extends Command {
    public static Function<String[], Command> parse = s -> new Exit(s);

    public Exit ( String[] args ) {
    }

    public String execute ( NoiseSpew.ControlEnv environment ) {
        System.exit(0);
        return null;
    }

    public String execute ( Engine.EngineEnv environment ) {return null;}
}
