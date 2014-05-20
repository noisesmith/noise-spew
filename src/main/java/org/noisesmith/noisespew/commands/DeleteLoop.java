package org.noisesmith.noisespew.commands;

import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.NoiseSpew;
import org.noisesmith.noisegenerator.Engine;
import java.util.function.Function;

public class DeleteLoop extends Command {
    public static Function<String[], Command> parse = s -> new DeleteLoop(s);

    int index;

    public DeleteLoop (String[] args) {
        index  = Integer.parseInt(args[0]);
    }

    public String execute ( NoiseSpew.ControlEnv environment ) {return null;}

    public String execute ( Engine.EngineEnv environment ) {
        String error = Engine.badIndex(index,
                                       "could not delete loop",
                                       environment.sources);
        if (error != null) {
            return error;
        } else {
            environment.sources.remove(index);
            return null;
        }
    }
}
