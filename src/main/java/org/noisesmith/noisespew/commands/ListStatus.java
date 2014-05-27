package org.noisesmith.noisespew.commands;

import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.NoiseSpew;
import org.noisesmith.noisespew.BiMap;
import org.noisesmith.noisegenerator.Engine;
import org.noisesmith.noisegenerator.UGen;
import java.util.Map;
import java.util.function.Function;

public class ListStatus extends Command implements Command.ICommand {
    public Function<String[], Command> getParser () {
        return s -> new ListStatus(s);
    }
    private StringBuilder out;

    public String getName() {return "list";}
    public String[] getInvocations() {return new String[] {"l"};}
    public String[] getArgs() {return new String[0];}
    public String getHelp() {return "list the status of the program";}

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

    public Map serialize(Map<String,Object> to) {
        to.put("name", getName());
        to.put("time", getMoment() / 1000.0);
        return to;
    }
    public Function<Map, Command> getDeserializer() {
        return from -> {
            ListStatus instance = new ListStatus();
            double time = (double) from.get("time");
            instance.setMoment((long) (time*1000));
            instance.setInteractive(false);
            return instance;
        };
    }
}
