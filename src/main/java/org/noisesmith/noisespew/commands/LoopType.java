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
    int index;
    int selection;

    public Function<String[], Command> getParser() {
        return s -> new LoopType(s);
    }

    public String getName() {return "loop type";}
    public String[] getInvocations() {return new String[]{"t"};}
    public String[] getArgs() {return new String[]{"index", "type"};}
    public String getHelp() {
        return "set looping 0=normal 1=pingpong 2=oneshot";
    }
    public LoopType(){}
    public LoopType (String[] args) {
        index = Integer.parseInt(args[0]);
        selection = Integer.parseInt(args[1]);
    }

    public String execute ( ControlEnv environment ) {return null;}

    public String execute ( EngineEnv environment ) {
        String error = Engine.badIndex(index,
                                       "could not change loop type of",
                                       environment.sources);
        if (error != null) {
            return error;
        } else {
            UGen loop = environment.sources.get(index);
            ((Looper) loop).setLooping(selection);
            return null;
        }
    }

    public Map serialize(Map<String,Object> to) {
        to.put("name", getName());
        to.put("index", index);
        to.put("selection", selection);
        to.put("time", getMoment() / 1000.0);
        return to;
    }
    public Function<Map, Command> getDeserializer() {
        return from -> {
            LoopType instance = new LoopType();
            instance.index = (int) from.get("index");
            instance.selection = (int) from.get("selection");
            double time = (double) from.get("time");
            instance.setMoment((long) (time*1000));
            instance.setInteractive(false);
            return instance;
        };
    }
}
