package org.noisesmith.noisespew.commands;

import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.NoiseSpew;
import org.noisesmith.noisegenerator.Engine;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.function.Function;

public class Null extends Command {
    public static Function<String[], Command> parse = s -> new Null(s);

    public static final String name = "null command";

    public Null(){};
    public Null (String[] args) {}

    public String execute ( NoiseSpew.ControlEnv environment ) {return null;}

    public String execute ( Engine.EngineEnv environment ) {return null;}
    public LinkedHashMap serialize(LinkedHashMap<String,Object> to) {
        to.put("name", name);
        to.put("time", moment / 1000.0);
        return to;
    }
    public static Function<Hashtable, Command> deserialize = from -> {
        Null instance = new Null();
        instance.moment = (long) ((double) from.get("time"))*1000;
        instance.interactive = false;
        return instance;
    };
}
