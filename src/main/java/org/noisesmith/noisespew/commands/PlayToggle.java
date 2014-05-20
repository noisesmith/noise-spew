package org.noisesmith.noisespew.commands;

import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.NoiseSpew;
import org.noisesmith.noisegenerator.Engine;
import java.util.function.Function;

public class PlayToggle extends Command {
    int index;

    public static Function<String[], Command> parse = s -> new PlayToggle(s);

    public PlayToggle (String[] args) {
        index = Integer.parseInt(args[0]);
    }

    public String execute ( NoiseSpew.ControlEnv environment ) {return null;}

    public String execute ( Engine.EngineEnv environment ) {
        String error = Engine.badIndex(index,
                                       "could not toggle playback of",
                                       environment.sources);
        if (error != null) {
            return error;
        } else {
            environment.sources.get(index).toggle();
            return null;
        }
    }
}
