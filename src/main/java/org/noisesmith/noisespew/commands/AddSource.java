package org.noisesmith.noisespew.commands;

import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.NoiseSpew;
import org.noisesmith.noisegenerator.Engine;
import java.util.function.Function;

public class AddSource extends Command {
    String source;
    public static Function<String[], Command> parse = s -> new AddSource(s);

    public AddSource (String[] args) {
        source = String.join(" ", args);
    }

    public String execute ( NoiseSpew.ControlEnv environment ) {
        environment.resources.put(source);
        return null;
    }

    public String execute ( Engine.EngineEnv environment ) { return null; }
}
