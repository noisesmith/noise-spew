package org.noisesmith.noisespew.commands;

import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.NoiseSpew;
import org.noisesmith.noisespew.NoiseSpew.ControlEnv;
import org.noisesmith.noisegenerator.Engine;
import org.noisesmith.noisegenerator.UGen;
import java.util.Map;
import java.util.function.Function;

public class Rate extends Command implements Command.ICommand {
    String identifier;
    double rate;

    public Function<String[], Command> getParser() {return s -> new Rate(s);}

    public String getName() {return  "rate";}
    public String[] getInvocations() {return new String[] {"r"};}
    public String[] getArgs() {return new String[]{"id", "rate"};}
    public String getHelp() {return "set rate of playback for loop <id>";}

    public Rate(){};
    public Rate (String[] args) {
        identifier = args[0];
        rate = Double.parseDouble(args[1]);
    }

    public String execute ( NoiseSpew.ControlEnv environment ) {return null;}

    public String execute ( Engine.EngineEnv environment ) {
        UGen ugen = environment.getUGen(identifier);
        if (ugen == null) {
            return "cannot change rate of " + identifier;
        } else {
            ugen.setParameter("rate", rate);
            return null;
        }
    }

    public Map serialize(Map<String,Object> to) {
        to.put("name", getName());
        to.put("identifier", identifier);
        to.put("rate", rate);
        to.put("time", getMoment() / 1000.0);
        return to;
    }

    public Function<Map, Command> getDeserializer() {
        return from -> {
            Rate instance = new Rate();
            instance.identifier = (String) from.get("identifier");
            instance.rate = (double) from.get("rate");
            double time = (double) from.get("time");
            instance.setMoment((long) (time*1000));
            instance.setInteractive(false);
            return instance;
        };
    }
}
