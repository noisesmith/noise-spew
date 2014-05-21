package org.noisesmith.noisespew.commands;

import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.NoiseSpew.ControlEnv;
import org.noisesmith.noisegenerator.Engine.EngineEnv;
import org.noisesmith.noisegenerator.UGen;
import org.noisesmith.noisegenerator.Engine;
import java.util.LinkedHashMap;
import java.util.Hashtable;
import java.util.function.Function;

public class LoopType extends Command {
    public static Function<String[], Command> parse = s -> new LoopType(s);

    int index;
    int selection;

    public static final String name = "loop type";

    public LoopType(){}
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

    public LinkedHashMap serialize(LinkedHashMap<String,Object> to) {
        to.put("name", name);
        to.put("index", index);
        to.put("selection", selection);
        to.put("time", moment / 1000.0);
        return to;
    }
    public static Function<Hashtable, Command> deserialize = from -> {
        LoopType instance = new LoopType();
        instance.index = (int) from.get("index");
        instance.selection = (int) from.get("selection");
        double time = (double) from.get("time");
        instance.moment = (long) (time*1000);
        instance.interactive = false;
        return instance;
    };
}
