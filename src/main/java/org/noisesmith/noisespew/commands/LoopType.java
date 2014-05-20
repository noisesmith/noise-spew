package org.noisesmith.noisespew.commands;

import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.NoiseSpew.ControlEnv;
import org.noisesmith.noisegenerator.Engine.EngineEnv;
import org.noisesmith.noisegenerator.UGen;
import org.noisesmith.noisegenerator.Engine;
import java.util.function.Function;

public class LoopType extends Command {
    public static Function<String[], Command> parse = s -> new LoopType(s);

    int index;
    int selection;

    public LoopType (String[] args) {
        index = Integer.parseInt(args[0]);
        selection = Integer.parseInt(args[1]);
    }

    public String execute ( ControlEnv environment ) {return null;}

    public String execute ( EngineEnv environment ) {
        String error = Engine.badIndex(index,
                                       "could not change loop type of",
                                       environment.sources);
        if (error != null) {
            return error;
        } else {
            UGen loop = environment.sources.get(index);
            switch (selection) {
            case 1:
                loop.looping = UGen.LoopType.PINGPONG;
                break;
            case 2:
                loop.looping = UGen.LoopType.ONESHOT;
                break;
            case 0:
            default:
                loop.looping = UGen.LoopType.LOOP;
            }
            return null;
        }
    }
}
