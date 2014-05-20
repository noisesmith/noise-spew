package org.noisesmith.noisespew.commands;

import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.NoiseSpew.ControlEnv;
import org.noisesmith.noisegenerator.Engine.EngineEnv;
import java.util.function.Function;

public class DeleteSource extends Command {
    public static Function<String[], Command> parse = s -> new DeleteSource(s);

    int index;

    public DeleteSource (String[] args) {
        index = Integer.parseInt(args[0]);
    }

    public String execute ( ControlEnv environment ) {
        if(environment.resources.containsKey(index)) {
            environment.resources.remove(index);
            return null;
        } else {
            return "could not delete " + index;
        }
    }

    public String execute ( EngineEnv environment ) { return null; }
}
