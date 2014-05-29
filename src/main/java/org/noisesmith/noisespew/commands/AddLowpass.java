package org.noisesmith.noisespew.commands;

import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.NoiseSpew;
import org.noisesmith.noisegenerator.Engine;
import org.noisesmith.noisegenerator.UGen;
import org.noisesmith.noisegenerator.ugens.LowRes;
import java.util.Map;
import java.util.function.Function;

public class AddLowpass extends Command implements Command.ICommand {
    int index;
    double center;
    double resonance;

    public String getName() {return "add lowpass";}
    public String[] getInvocations() {return new String[] {"f"};}
    public String[] getArgs() {return new String[] {
            "index",
            "center",
            "resonance"};}
    public String getHelp() {return "create a new lowpass filter unit";}

    public Function<String[], Command> getParser() {
        return s -> new AddLowpass(s);
    }

    public AddLowpass(){};
    public AddLowpass (String[] args) {
        index = Integer.parseInt(args[0]);
        center = Double.parseDouble(args[1]);
        resonance = Double.parseDouble(args[2]);
    }

    public String execute ( NoiseSpew.ControlEnv environment ) {
        return null;
    }

    public String execute ( Engine.EngineEnv environment ) {
        try {
            UGen source = environment.sources.get(index);
            LowRes filt = new LowRes(source, center, resonance);
            environment.sources.add(0, filt);
        } catch (Exception ex) {
            System.out.println("could not filter input: " + index);
            ex.printStackTrace();
        } finally {
            return null;
        }
    }
    public Map serialize(Map<String,Object> to) {
        to.put("name", getName());
        to.put("index", index);
        to.put("center", center);
        to.put("resonance", resonance);
        to.put("time", getMoment() / 1000.0);
        return to;
    }
    public Function<Map, Command> getDeserializer () {
        return from -> {
            AddLowpass instance = new AddLowpass();
            instance.index = (int) from.get("index");
            instance.center = (double) from.get("center");
            instance.resonance = (double) from.get("resonance");
            double time = (double) from.get("time");
            instance.setMoment((long) (time*1000));
            instance.setInteractive(false);
            return instance;
        };
    }
}
