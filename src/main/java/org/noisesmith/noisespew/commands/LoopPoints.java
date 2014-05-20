package org.noisesmith.noisespew.commands;

import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.NoiseSpew;
import org.noisesmith.noisespew.NoiseSpew.ControlEnv;
import org.noisesmith.noisegenerator.Engine;
import org.noisesmith.noisegenerator.UGen;
import java.util.function.Function;

public class LoopPoints extends Command {
    int index;
    double start;
    double end;

    public static Function<String[], Command> parse = s -> new LoopPoints(s);

    public LoopPoints (String[] args) {
        index = Integer.parseInt(args[0]);
        start = Double.parseDouble(args[1]);
        end = Double.parseDouble(args[2]);
    }

    public String execute ( NoiseSpew.ControlEnv environment ) { return null; }

    public String execute ( Engine.EngineEnv environment ) {
        String error = Engine.badIndex(index,
                                       "could not reloop",
                                       environment.sources);
        if (error != null) {
            return error;
        } else {
            UGen loop = environment.sources.get(index);
            loop.in(start);
            loop.out(end);
            loop.start();
            return null;
        }
    }
}
