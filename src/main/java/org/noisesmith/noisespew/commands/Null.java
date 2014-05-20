package org.noisesmith.noisespew.commands;

import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.NoiseSpew;
import org.noisesmith.noisegenerator.Engine;
import java.util.function.Function;

public class Null extends Command {
    public static Function<String[], Command> parse = s -> new Null(s);

    public Null (String[] args) {}

    public String execute ( NoiseSpew.ControlEnv environment ) {return null;}

    public String execute ( Engine.EngineEnv environment ) {return null;}
}
