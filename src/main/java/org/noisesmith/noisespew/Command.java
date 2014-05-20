package org.noisesmith.noisespew;

import java.util.ArrayList;
import org.noisesmith.noisespew.NoiseSpew.ControlEnv;
import org.noisesmith.noisegenerator.Engine.EngineEnv;
import java.util.function.Function;

public abstract class Command {
    public long moment;
    public Boolean interactive;

    public static Function<String[],Command> parse;

    public abstract String execute ( ControlEnv environment );
    public abstract String execute ( EngineEnv environment );
}
