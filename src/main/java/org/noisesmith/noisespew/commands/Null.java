package org.noisesmith.noisespew.commands;

import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.NoiseSpew;
import org.noisesmith.noisegenerator.Engine;
import java.util.Map;
import java.util.function.Function;

public class Null extends Command implements Command.ICommand {
    public Function<String[], Command> getParser() {
        return s -> new Null(s);
    }

    public String getName() {return "null command";}
    public String[] getInvocations() {return new String[0];}
    public String[] getArgs() {return new String[0];}
    public String getHelp() {return "not a command";}    

    public Null(){};
    public Null (String[] args) {}

    public String execute ( NoiseSpew.ControlEnv environment ) {return null;}

    public String execute ( Engine.EngineEnv environment ) {return null;}
    public Map serialize(Map<String,Object> to) {
        to.put("name", getName());
        to.put("time", getMoment() / 1000.0);
        return to;
    }
    public Function<Map, Command> getDeserializer () {
        return from -> {
            Null instance = new Null();
            double time = (double) from.get("time");
            instance.setMoment((long) (time*1000));
            instance.setInteractive(false);
            return instance;
        };
    }
    
}
