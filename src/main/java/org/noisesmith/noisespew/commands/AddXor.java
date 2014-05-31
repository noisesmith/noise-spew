package org.noisesmith.noisespew.commands;

import org.noisesmith.noisespew.Command;
import org.noisesmith.noisegenerator.Output;
import org.noisesmith.noisespew.NoiseSpew;
import org.noisesmith.noisegenerator.Engine;
import org.noisesmith.noisegenerator.UGen;
import org.noisesmith.noisegenerator.ugens.Xor;
import java.util.Map;
import java.util.function.Function;

public class AddXor extends Command implements Command.ICommand {
    String indexA;
    String sourceA;
    String indexB;
    String sourceB;

    public String getName() {return "add xor";}
    public String[] getInvocations() {return new String[] {"X"};}
    public String[] getArgs() {return new String[] {"source", "output",
                                                    "source", "output"};}
    public String getHelp() {return "create a new Xor unit modulating sources";}

    public Function<String[], Command> getParser() {
        return s -> new AddXor(s);
    }

    public AddXor(){};
    public AddXor (String[] args) {
        indexA = args[0];
        sourceA = args[1];
        indexB = args[2];
        sourceB = args[3];
    }

    public String execute ( NoiseSpew.ControlEnv environment ) {
        return null;
    }

    public String execute ( Engine.EngineEnv environment ) {
        try {
            UGen ugenA = environment.getUGen(indexA);
            Output channelA = ugenA.getOutputs().get(sourceA);
            UGen ugenB = environment.getUGen(indexB);
            Output channelB = ugenB.getOutputs().get(sourceB);
            Xor xor = new Xor(channelA, channelB);
            environment.putUGen(xor.getId(), xor);
        } catch (Exception ex) {
            System.out.println("could not xor inputs: " +
                               indexA + ":" + sourceA + " * " +
                               indexB + ":" + sourceB);
            ex.printStackTrace();
        } finally {
            return null;
        }
    }
    public Map serialize(Map<String,Object> to) {
        to.put("name", getName());
        to.put("indexA", indexA);
        to.put("sourceA", sourceA);
        to.put("indexB", indexB);
        to.put("sourceB", sourceB);
        to.put("time", getMoment() / 1000.0);
        return to;
    }
    public Function<Map, Command> getDeserializer () {
        return from -> {
            AddXor instance = new AddXor();
            instance.indexA = (String) from.get("indexA");
            instance.sourceA = (String) from.get("sourceA");
            instance.indexB = (String) from.get("indexB");
            instance.sourceB = (String) from.get("sourceB");
            double time = (double) from.get("time");
            instance.setMoment((long) (time*1000));
            instance.setInteractive(false);
            return instance;
        };
    }
}
