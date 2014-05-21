package org.noisesmith.noisespew.commands;

import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.NoiseSpew;
import org.noisesmith.noisegenerator.Engine;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.function.Function;

public class AddSource extends Command {
    String source;
    public static Function<String[], Command> parse = s -> new AddSource(s);

    public static final String name = "add source";

    public AddSource(){};
    public AddSource (String[] args) {
        source = String.join(" ", args);
    }

    public String execute ( NoiseSpew.ControlEnv environment ) {
        environment.resources.put(source);
        return null;
    }

    public String execute ( Engine.EngineEnv environment ) { return null; }
    public LinkedHashMap serialize(LinkedHashMap<String,Object> to) {
        to.put("name", name);
        to.put("source", source);
        to.put("time", moment / 1000.0);
        return to;
    }
    public static Function<Hashtable, Command> deserialize = from -> {
        AddSource instance = new AddSource();
        instance.source = (String) from.get("source");
        instance.moment = (long) ((double) from.get("time"))*1000;
        instance.interactive = false;
        return instance;
    };
}
