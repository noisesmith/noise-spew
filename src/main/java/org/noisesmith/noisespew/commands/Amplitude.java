package org.noisesmith.noisespew.commands;

import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.NoiseSpew;
import org.noisesmith.noisegenerator.Engine;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.function.Function;

public class Amplitude extends Command {
    public static Function<String[], Command> parse = s -> new Amplitude(s);

    public static final String name = "amplitude";

    int index;
    double amp;

    public Amplitude() {}
    public Amplitude (String[] args) {
        index = Integer.parseInt(args[0]);
        amp = Double.parseDouble(args[1]);
    }

    public String execute ( NoiseSpew.ControlEnv environment ) {return null;}

    public String execute ( Engine.EngineEnv environment ) {
        String error = Engine.badIndex(index,
                                       "could not change amp of",
                                       environment.sources);
        if (error != null) {
            return error;
        } else {
            environment.sources.get(index).amp = amp;
            return null;
        }
    }

    public LinkedHashMap serialize(LinkedHashMap<String,Object> to) {
        to.put("name", name);
        to.put("index", index);
        to.put("amp", amp);
        to.put("time", moment / 1000.0);
        return to;
    }
    public static Function<Hashtable, Command> deserialize = from -> {
        Amplitude instance = new Amplitude();
        instance.index = (int) from.get("index");
        instance.amp = (double) from.get("amp");
        double time = (double) from.get("time");
        instance.moment = (long) (time*1000);
        instance.interactive = false;
        return instance;
    };
}
