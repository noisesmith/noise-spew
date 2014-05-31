package org.noisesmith.noisespew.commands;

import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.NoiseSpew;
import org.noisesmith.noisegenerator.Engine;
import org.noisesmith.noisegenerator.UGen;
import java.util.Map;
import java.util.function.Function;

public class DeleteLoop extends Command implements Command.ICommand {
    String identifier;

    public Function<String[], Command> getParser() {
        return s -> new DeleteLoop(s);
    }

    public String getName() {return "delete loop";}
    public String[] getInvocations() {return new String[] {"D"};}
    public String[] getArgs() {return new String[] {"identifier"};}
    public String getHelp() {return "delete loop <identifier>";}

    public DeleteLoop(){};
    public DeleteLoop (String[] args) {
        identifier  = args[0];
    }

    public String execute ( NoiseSpew.ControlEnv environment ) {return null;}

    public String execute ( Engine.EngineEnv environment ) {
        UGen found = environment.getUGen(identifier);
        if (environment.deleteUGen(identifier))
            return null;
        else
            return "Cannot delete UGen " + identifier;
    }

    public Map serialize(Map<String,Object> to) {
        to.put("name", getName());
        to.put("identifier", identifier);
        to.put("time", getMoment() / 1000.0);
        return to;
    }
    public Function<Map, Command> getDeserializer() {
        return from -> {
            DeleteLoop instance = new DeleteLoop();
            instance.identifier = (String) from.get("identifier");
            double time = (double) from.get("time");
            instance.setMoment((long) (time*1000));
            instance.setInteractive(false);
            return instance;
        };
    }
}
