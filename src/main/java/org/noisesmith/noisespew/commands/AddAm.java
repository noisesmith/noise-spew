package org.noisesmith.noisespew.commands;

import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.NoiseSpew;
import org.noisesmith.noisegenerator.Engine;
import org.noisesmith.noisegenerator.Output;
import org.noisesmith.noisegenerator.ugens.Am;
import java.util.Map;
import java.util.function.Function;
import java.io.File;

public class AddAm extends Command implements Command.ICommand {
    String idA;
    String srcA;
    String idB;
    String srcB;

    public String getName() {return "add am";}
    public String[] getInvocations() {return new String[] {"A"};}
    public String[] getArgs() {
        return new String[] {
            "idA", "sourceA",
            "idB", "sourceB"
        };
    }
    public String getHelp() {return "create a new AM unit modulating sources";}

    public Function<String[], Command> getParser() {
        return s -> new AddAm(s);
    }

    public AddAm(){};
    public AddAm (String[] args) {
        idA = args[0];
        srcA = args[1];
        idB = args[2];
        srcB = args[3];
    }

    public String execute ( NoiseSpew.ControlEnv environment ) {
        return null;
    }

    public String execute ( Engine.EngineEnv environment ) {
        try {
            Output sourceA = environment.getUGen(idA).getOutputs().get(srcA);
            Output sourceB = environment.getUGen(idB).getOutputs().get(srcB);
            Am modulator = new Am(sourceA, sourceB);
            environment.putUGen(modulator.getId(), modulator);
        } catch (Exception ex) {
            System.out.println("could not modulate inputs: " +
                               idA + ":" + srcA + " * " +
                               idB + ":" + srcB);
            ex.printStackTrace();
        } finally {
            return null;
        }
    }
    public Map serialize(Map<String,Object> to) {
        to.put("name", getName());
        to.put("idA", idA);
        to.put("sourceA", srcA);
        to.put("idB", idB);
        to.put("sourceB", srcB);
        to.put("time", getMoment() / 1000.0);
        return to;
    }
    public Function<Map, Command> getDeserializer () {
        return from -> {
            AddAm instance = new AddAm();
            instance.idA = (String) from.get("idA");
            instance.srcA = (String) from.get("sourceA");
            instance.idB = (String) from.get("idB");
            instance.srcB = (String) from.get("sourceB");
            double time = (double) from.get("time");
            instance.setMoment((long) (time*1000));
            instance.setInteractive(false);
            return instance;
        };
    }
}
