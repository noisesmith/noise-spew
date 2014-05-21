package org.noisesmith.noisespew.commands;

import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.NoiseSpew;
import org.noisesmith.noisegenerator.Engine;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.function.Function;

public class PlayToggle extends Command {
    int index;
    public static final String name = "play toggle";

    public static Function<String[], Command> parse = s -> new PlayToggle(s);

    public PlayToggle(){};
    public PlayToggle (String[] args) {
        index = Integer.parseInt(args[0]);
    }

    public String execute ( NoiseSpew.ControlEnv environment ) {return null;}

    public String execute ( Engine.EngineEnv environment ) {
        String error = Engine.badIndex(index,
                                       "could not toggle playback of",
                                       environment.sources);
        if (error != null) {
            return error;
        } else {
            environment.sources.get(index).toggle();
            return null;
        }
    }
    public LinkedHashMap serialize(LinkedHashMap<String,Object> to) {
        to.put("name", name);
        to.put("index", index);
        to.put("time", moment / 1000.0);
        return to;
    }
    public static Function<Hashtable, Command> deserialize = from -> {
        PlayToggle instance = new PlayToggle();
        instance.index = (int) from.get("index");
        double time = (double) from.get("time");
        instance.moment = (long) (time*1000);
        instance.interactive = false;
        return instance;
    };
}
