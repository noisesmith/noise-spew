package org.noisesmith.noisespew.commands;

import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.NoiseSpew.ControlEnv;
import org.noisesmith.noisegenerator.Engine.EngineEnv;
import java.util.Map;
import java.util.function.Function;

public class DeleteSource extends Command implements Command.ICommand {
    int index;

    public Function<String[], Command> getParser() {
        return s -> new DeleteSource(s);
    }

    public String getName() {return "delete source";}
    public String[] getInvocations() {return new String[] {"d"};}
    public String[] getArgs() {return new String[] {"index"};}
    public String getHelp() {return "delete source <index>";}


    public DeleteSource(){};
    public DeleteSource (String[] args) {
        index = Integer.parseInt(args[0]);
    }

    public String execute ( ControlEnv environment ) {
        if(environment.resources.containsKey(index)) {
            environment.resources.remove(index);
            return null;
        } else {
            return "could not delete " + index;
        }
    }

    public String execute ( EngineEnv environment ) { return null; }
    public Map serialize(Map<String,Object> to) {
        to.put("name", getName());
        to.put("index", index);
        to.put("time", getMoment() / 1000.0);
        return to;
    }
    public Function<Map, Command> getDeserializer () {
        return from -> {
            DeleteSource instance = new DeleteSource();
            instance.index = (int) from.get("index");
            double time = (double) from.get("time");
            instance.setMoment((long) (time*1000));
            instance.setInteractive(false);
            return instance;
        };
    }
}
