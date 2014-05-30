package org.noisesmith.noisespew.commands;

import org.noisesmith.noisespew.Command;
import org.noisesmith.noisegenerator.Channel;
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
    public String[] getArgs() {return new String[] {"indexA", "sourceA",
                                                    "indexB", "sourceB"};}
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
            UGen ugenA = environment.getSource(indexA);
            Channel channelA = ugenA.getOutput(sourceA);
            UGen ugenB = environment.getSource(indexB);
            Channel channelB = ugenB.getOutput(sourceB);
            Xor xor = new Xor(sourceA, sourceB);
            environment.addSource(0, xor);
        } catch (Exception ex) {
            System.out.println("could not xor inputs: " +
                               indexA + ":" + sourceA + " * " +
                               indexB + ":" + srouceB);
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
            instance.indexA = from.get("indexA");
            instance.sourceA = from.get("sourceA");
            instance.indexB = (int) from.get("indexB");
            instance.sourceB = from.get("sourceB");
            double time = (double) from.get("time");
            instance.setMoment((long) (time*1000));
            instance.setInteractive(false);
            return instance;
        };
    }
}
