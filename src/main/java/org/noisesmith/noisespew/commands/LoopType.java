package org.noisesmith.noisespew.commands;

import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.NoiseSpew.ControlEnv;
import org.noisesmith.noisegenerator.Engine.EngineEnv;
import org.noisesmith.noisegenerator.UGen;
import org.noisesmith.noisegenerator.ugens.Looper;
import org.noisesmith.noisegenerator.Engine;
import java.util.Map;
import java.util.function.Function;

public class LoopType extends Command implements Command.ICommand {
    String identifier;
    int selection;

    public Function<String[], Command> getParser() {
        return s -> new LoopType(s);
    }

    public String getName() {return "loop type";}
    public String[] getInvocations() {return new String[]{"t"};}
    public String[] getArgs() {return new String[]{"id", "type"};}
    public String getHelp() {
        return "set looping 0=normal 1=pingpong 2=oneshot";
    }
    public LoopType(){}
    public LoopType (String[] args) {
        identifier = args[0];
        selection = Integer.parseInt(args[1]);
    }

    public String execute ( ControlEnv environment ) {return null;}

    public String execute ( EngineEnv environment ) {
        UGen loop = environment.getUGen(identifier);
        if (loop == null) {
            return "Could not set loop type of " + identifier;
        } else {
            ((Looper) loop).setLooping(selection);
            return null;
        }
    }

    public Map serialize(Map<String,Object> to) {
        to.put("name", getName());
        to.put("identifier", identifier);
        to.put("selection", selection);
        to.put("time", getMoment() / 1000.0);
        return to;
    }
    public Function<Map, Command> getDeserializer() {
        return from -> {
            LoopType instance = new LoopType();
            instance.identifier = (String) from.get("identifier");
            instance.selection = (int) from.get("selection");
            double time = (double) from.get("time");
            instance.setMoment((long) (time*1000));
            instance.setInteractive(false);
            return instance;
        };
    }
}
