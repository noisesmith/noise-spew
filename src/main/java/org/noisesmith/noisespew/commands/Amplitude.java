package org.noisesmith.noisespew.commands;

import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.NoiseSpew;
import java.util.concurrent.ArrayBlockingQueue;
import org.noisesmith.noisegenerator.Engine;
import org.noisesmith.noisegenerator.UGen;
import java.util.Map;
import java.util.function.Function;

public class Amplitude extends Command implements Command.ICommand {
    String identifier;
    double amp;

    public Function<String[], Command> getParser() {
        return s -> new Amplitude(s);
    }

    public String getName() {return  "amplitude";}
    public String[] getInvocations() {return new String[] {"v"};}
    public String[] getArgs() {return new String[]{"id", "amp"};}
    public String getHelp() {
        return "set amplitude of playback for loop <id>";
    }

    public Amplitude() {}
    public Amplitude (String[] args) {
        identifier = args[0];
        amp = Double.parseDouble(args[1]);
    }

    public String execute ( NoiseSpew.ControlEnv environment ) {return null;}

    public String execute ( Engine.EngineEnv environment ) {
        UGen found = environment.getUGen(identifier);
        if (found == null) {
            return "could not change amp of" + identifier;
        } else {
            found.setAmp(amp);
            return null;
        }
    }

    public Map serialize(Map<String,Object> to) {
        to.put("name", getName());
        to.put("identifier", identifier);
        to.put("amp", amp);
        to.put("time", getMoment() / 1000.0);
        return to;
    }
    public Function<Map, Command> getDeserializer() {
        return from -> {
            Amplitude instance = new Amplitude();
            instance.identifier = (String) from.get("identifier");
            instance.amp = (double) from.get("amp");
            double time = (double) from.get("time");
            instance.setMoment((long) (time*1000));
            instance.setInteractive(false);
            return instance;
        };
    }
}
