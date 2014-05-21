package org.noisesmith.noisespew.commands;

import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.NoiseSpew;
import org.noisesmith.noisespew.BiMap;
import org.noisesmith.noisegenerator.Engine;
import org.noisesmith.noisegenerator.UGen;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.function.Function;

public class ListStatus extends Command {
    public static Function<String[], Command> parse = s -> new ListStatus(s);

    static public String name = "list";

    private StringBuilder out;

    public ListStatus(){};
    public ListStatus (String[] args) {}


    public String execute ( NoiseSpew.ControlEnv environment ) {
        out = new StringBuilder();
        out.append("\nresources:\n");
        environment.resources.forEach((k, v) -> out.append(k + " : " + v));
        return out.toString();
    }

    public String execute ( Engine.EngineEnv environment ) {
        out = new StringBuilder();
        out.append("\nloops:\n");
        BiMap<UGen> loopmap = new BiMap<UGen> (environment.sources);
        loopmap.forEach((i, l) -> {
                out.append(i)
                    .append(" - ")
                    .append(l.active ? "on" : "off")
                    .append(" ")
                    .append(l.start / 44100.0)
                    .append("->")
                    .append(l.end / 44100.0)
                    .append("	")
                    .append(l.description)
                    .append("\n");
            });
        return out.toString();
    }

    public LinkedHashMap serialize(LinkedHashMap<String,Object> to) {
        to.put("name", name);
        to.put("time", moment / 1000.0);
        return to;
    }
    public static Function<Hashtable, Command> deserialize = from -> {
        ListStatus instance = new ListStatus();
        double time = (double) from.get("time");
        instance.moment = (long) (time*1000);
        instance.interactive = false;
        return instance;
    };
}
