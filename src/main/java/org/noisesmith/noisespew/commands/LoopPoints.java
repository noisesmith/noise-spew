package org.noisesmith.noisespew.commands;

import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.NoiseSpew;
import org.noisesmith.noisespew.NoiseSpew.ControlEnv;
import org.noisesmith.noisegenerator.Engine;
import org.noisesmith.noisegenerator.UGen;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.function.Function;

public class LoopPoints extends Command {
    int index;
    double start;
    double end;

    public static final String name = "loop points";

    public static Function<String[], Command> parse = s -> new LoopPoints(s);

    public LoopPoints(){};
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
    public LinkedHashMap serialize(LinkedHashMap<String,Object> to) {
        to.put("name", name);
        to.put("index", index);
        to.put("start", start);
        to.put("end", end);
        to.put("time", moment / 1000.0);
        return to;
    }

    public static Function<Hashtable, Command> deserialize = from -> {
        LoopPoints instance = new LoopPoints();
        instance.index = (int) from.get("index");
        instance.start = (double) from.get("start");
        instance.end = (double) from.get("end");
        instance.moment = (long) ((double) from.get("time"))*1000;
        instance.interactive = false;
        return instance;
    };
}
