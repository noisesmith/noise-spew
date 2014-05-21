package org.noisesmith.noisespew.commands;

import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.NoiseSpew;
import org.noisesmith.noisespew.NoiseSpew.ControlEnv;
import org.noisesmith.noisegenerator.Engine;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.function.Function;

public class Rate extends Command {
    public static Function<String[], Command> parse = s -> new Rate(s);

    int index;
    double rate;

    public static final String name = "rate";

    public Rate(){};
    public Rate (String[] args) {
        index = Integer.parseInt(args[0]);
        rate = Double.parseDouble(args[1]);
    }

    public String execute ( NoiseSpew.ControlEnv environment ) {return null;}

    public String execute ( Engine.EngineEnv environment ) {
        String error = Engine.badIndex(index,
                                       "could not change rate of",
                                       environment.sources);
        if (error != null) {
            return error;
        } else {
            environment.sources.get(index).rate = rate;
            return null;
        }
    }
    public LinkedHashMap serialize(LinkedHashMap<String,Object> to) {
        to.put("name", name);
        to.put("index", index);
        to.put("rate", rate);
        to.put("time", moment / 1000.0);
        return to;
    }
    public static Function<Hashtable, Command> deserialize = from -> {
        Rate instance = new Rate();
        instance.index = (int) from.get("index");
        instance.rate = (double) from.get("rate");
        double time = (double) from.get("time");
        instance.moment = (long) (time*1000);
        instance.interactive = false;
        return instance;
    };
}
