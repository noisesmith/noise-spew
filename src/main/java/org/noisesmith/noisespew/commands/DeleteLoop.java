package org.noisesmith.noisespew.commands;

import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.NoiseSpew;
import org.noisesmith.noisegenerator.Engine;
import java.util.Map;
import java.util.function.Function;

public class DeleteLoop extends Command implements Command.ICommand {
    int index;

    public Function<String[], Command> getParser() {
        return s -> new DeleteLoop(s);
    }

    public String getName() {return "delete loop";}
    public String[] getInvocations() {return new String[] {"D"};}
    public String[] getArgs() {return new String[] {"index"};}
    public String getHelp() {return "delete loop <index>";}

    public DeleteLoop(){};
    public DeleteLoop (String[] args) {
        index  = Integer.parseInt(args[0]);
    }

    public String execute ( NoiseSpew.ControlEnv environment ) {return null;}

    public String execute ( Engine.EngineEnv environment ) {
        String error = Engine.badIndex(index,
                                       "could not delete loop",
                                       environment.sources);
        if (error != null) {
            return error;
        } else {
            environment.sources.remove(index);
            return null;
        }
    }

    public Map serialize(Map<String,Object> to) {
        to.put("name", getName());
        to.put("index", index);
        to.put("time", getMoment() / 1000.0);
        return to;
    }
    public Function<Map, Command> getDeserializer() {
        return from -> {
            DeleteLoop instance = new DeleteLoop();
            instance.index = (int) from.get("index");
            double time = (double) from.get("time");
            instance.setMoment((long) (time*1000));
            instance.setInteractive(false);
            return instance;
        };
    }
}
