package org.noisesmith.noisespew.commands;

import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.NoiseSpew.ControlEnv;
import org.noisesmith.noisegenerator.Engine.EngineEnv;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.function.Function;

public class DeleteSource extends Command {
    public static Function<String[], Command> parse = s -> new DeleteSource(s);

    public static final String name = "delete source";

    int index;

    public DeleteSource(){};
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
    public LinkedHashMap serialize(LinkedHashMap<String,Object> to) {
        to.put("name", name);
        to.put("index", index);
        to.put("time", moment / 1000.0);
        return to;
    }
    public static Function<Hashtable, Command> deserialize = from -> {
        DeleteSource instance = new DeleteSource();
        instance.index = (int) from.get("index");
        double time = (double) from.get("time");
        instance.moment = (long) (time*1000);
        instance.interactive = false;
        return instance;
    };
}
