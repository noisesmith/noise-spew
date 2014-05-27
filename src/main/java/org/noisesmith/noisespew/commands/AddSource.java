package org.noisesmith.noisespew.commands;

import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.NoiseSpew;
import org.noisesmith.noisegenerator.Engine;
import java.util.Map;
import java.util.function.Function;

public class AddSource extends Command implements Command.ICommand {
    String source;

    public Function<String[], Command> getParser() {
        return s -> new AddSource(s);
    }

    public String getName() {return "add source";}
    public String[] getInvocations() {return new String[] {"s"};}
    public String[] getArgs() {return new String[] {"source"};}
    public String getHelp() {return "add a source file from path <source>";}

    public AddSource(){};
    public AddSource (String[] args) {
        source = String.join(" ", args);
    }

    public String execute ( NoiseSpew.ControlEnv environment ) {
        environment.resources.put(source);
        return null;
    }

    public String execute ( Engine.EngineEnv environment ) { return null; }
    public Map serialize(Map<String,Object> to) {
        to.put("name", getName());
        to.put("source", source);
        to.put("time", getMoment() / 1000.0);
        return to;
    }
    public Function<Map, Command> getDeserializer() {
        return from -> {
            AddSource instance = new AddSource();
            instance.source = (String) from.get("source");
            double time = (double) from.get("time");
            instance.setMoment((long) (time*1000));
            instance.setInteractive(false);
            return instance;
        };
    }
}
