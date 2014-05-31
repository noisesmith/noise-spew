package org.noisesmith.noisespew.commands;

import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.NoiseSpew;
import org.noisesmith.noisegenerator.Engine;
import org.noisesmith.noisegenerator.UGen;
import org.noisesmith.noisegenerator.Output;
import org.noisesmith.noisegenerator.ugens.LowRes;
import java.util.Map;
import java.util.function.Function;

public class AddLowpass extends Command implements Command.ICommand {
    String identifier;
    String source;
    double center;
    double resonance;

    public String getName() {return "add lowpass";}
    public String[] getInvocations() {return new String[] {"f"};}
    public String[] getArgs() {
        return new String[] {
            "id",
            "output",
            "center",
            "resonance"
        };
    }
    public String getHelp() {return "create a new lowpass filter unit";}

    public Function<String[], Command> getParser() {
        return s -> new AddLowpass(s);
    }

    public AddLowpass(){};
    public AddLowpass (String[] args) {
        identifier = args[0];
        source = args[1];
        center = Double.parseDouble(args[2]);
        resonance = Double.parseDouble(args[3]);
    }

    public String execute ( NoiseSpew.ControlEnv environment ) {
        return null;
    }

    public String execute ( Engine.EngineEnv environment ) {
        try {
            Output channel = environment.getUGen(identifier)
                .getOutputs().get(identifier);
            LowRes filt = new LowRes(channel, center, resonance);
            environment.putUGen(filt.getId(), filt);
        } catch (Exception ex) {
            System.out.println("could not filter input: " + identifier + ":"
                               + source);
            ex.printStackTrace();
        } finally {
            return null;
        }
    }
    public Map serialize(Map<String,Object> to) {
        to.put("name", getName());
        to.put("identifier", identifier);
        to.put("source", source);
        to.put("center", center);
        to.put("resonance", resonance);
        to.put("time", getMoment() / 1000.0);
        return to;
    }
    public Function<Map, Command> getDeserializer () {
        return from -> {
            AddLowpass instance = new AddLowpass();
            instance.identifier = (String) from.get("identifier");
            instance.source = (String) from.get("source");
            instance.center = (double) from.get("center");
            instance.resonance = (double) from.get("resonance");
            double time = (double) from.get("time");
            instance.setMoment((long) (time*1000));
            instance.setInteractive(false);
            return instance;
        };
    }
}
