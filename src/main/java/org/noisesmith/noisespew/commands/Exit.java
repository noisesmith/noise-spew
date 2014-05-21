package org.noisesmith.noisespew.commands;

import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.NoiseSpew;
import org.noisesmith.noisegenerator.Engine;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.function.Function;

public class Exit extends Command {
    public static Function<String[], Command> parse = s -> new Exit(s);

    public static final String name = "exit";

    public Exit(){};
    public Exit ( String[] args ) {
    }

    public String execute ( NoiseSpew.ControlEnv environment ) {
        System.exit(0);
        return null;
    }

    public String execute ( Engine.EngineEnv environment ) {return null;}
    public LinkedHashMap serialize(LinkedHashMap<String,Object> to) {
        to.put("name", name);
        to.put("time", moment / 1000.0);
        return to;
    }
    public static Function<Hashtable, Command> deserialize = from -> {
        Exit instance = new Exit();
        instance.moment = (long) ((double) from.get("time"))*1000;
        instance.interactive = false;
        return instance;
    };
}
