package org.noisesmith.noisespew.commands;

import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.NoiseSpew.ControlEnv;
import org.noisesmith.noisegenerator.Engine.EngineEnv;
import java.util.function.Function;
import java.util.Hashtable;
import java.util.LinkedHashMap;

public class Master extends Command {
    public static Function<String[], Command> parse = s -> new Master(s);

    double amp;

    public static final String name = "master volume";

    public Master() {};
    public Master (String[] args) {
        amp = Double.parseDouble(args[0]);
    }

    public String execute ( ControlEnv environment ) {return null;}

    public String execute ( EngineEnv environment ) {
        environment.master = amp;
        return null;
    }

    public LinkedHashMap serialize(LinkedHashMap<String,Object> to) {
        to.put("name", name);
        to.put("amp", amp);
        to.put("time", moment / 1000.0);
        return to;
    }
    public static Function<Hashtable, Command> deserialize = from -> {
        Master instance = new Master();
        instance.amp = (double) from.get("amp");
        double time = (double) from.get("time");
        instance.moment = (long) (time*1000);
        instance.interactive = false;
        return instance;
    };
}
