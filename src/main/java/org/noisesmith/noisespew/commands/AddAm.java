package org.noisesmith.noisespew.commands;

import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.NoiseSpew;
import org.noisesmith.noisegenerator.Engine;
import org.noisesmith.noisegenerator.UGen;
import org.noisesmith.noisegenerator.ugens.Am;
import java.util.Map;
import java.util.function.Function;
import java.io.File;

public class AddAm extends Command implements Command.ICommand {
    int indexA;
    int indexB;

    public String getName() {return "add am";}
    public String[] getInvocations() {return new String[] {"A"};}
    public String[] getArgs() {return new String[] {"sourceA", "sourceB"};}
    public String getHelp() {return "create a new AM unit modulating sources";}

    public Function<String[], Command> getParser() {
        return s -> new AddAm(s);
    }

    public AddAm(){};
    public AddAm (String[] args) {
        indexA = Integer.parseInt(args[0]);
        indexB = Integer.parseInt(args[1]);
    }

    public String execute ( NoiseSpew.ControlEnv environment ) {
        return null;
    }

    public String execute ( Engine.EngineEnv environment ) {
        try {
            UGen sourceA = environment.sources.get(indexA);
            UGen sourceB = environment.sources.get(indexB);
            Am modulator = new Am(sourceA, sourceB);
            environment.sources.add(0, modulator);
        } catch (Exception ex) {
            System.out.println("could not modulate inputs: " +
                               indexA + " * " + indexB);
            ex.printStackTrace();
        } finally {
            return null;
        }
    }
    public Map serialize(Map<String,Object> to) {
        to.put("name", getName());
        to.put("indexA", indexA);
        to.put("indexB", indexB);
        to.put("time", getMoment() / 1000.0);
        return to;
    }
    public Function<Map, Command> getDeserializer () {
        return from -> {
            AddAm instance = new AddAm();
            instance.indexA = (int) from.get("indexA");
            instance.indexB = (int) from.get("indexB");
            double time = (double) from.get("time");
            instance.setMoment((long) (time*1000));
            instance.setInteractive(false);
            return instance;
        };
    }
}
