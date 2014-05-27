package org.noisesmith.noisespew.commands;

import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.NoiseSpew;
import org.noisesmith.noisegenerator.Engine;
import java.util.Map;
import java.util.function.Function;

public class Exit extends Command implements Command.ICommand {
    public Function<String[], Command> getParser() {return s -> new Exit(s);}

    public String[] getInvocations() {return new String[] {"q", "Q", "e"};}
    public String getName() {return "exit";}
    public String[] getArgs() {return new String[0];}
    public String getHelp() {return "exit now";}

    public Exit(){};
    public Exit ( String[] args ) {
    }

    public String execute ( NoiseSpew.ControlEnv environment ) {
        System.exit(0);
        return null;
    }

    public String execute ( Engine.EngineEnv environment ) {return null;}
    public Map serialize(Map<String,Object> to) {
        to.put("name", getName());
        to.put("time", getMoment() / 1000.0);
        return to;
    }
    public Function<Map, Command> getDeserializer() {
        return from -> {
            Exit instance = new Exit();
            double time = (double) from.get("time");
            instance.setMoment((long) (time*1000));
            instance.setInteractive(false);
            return instance;
        };
    }
}
