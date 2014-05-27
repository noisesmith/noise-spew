package org.noisesmith.noisespew.commands;

import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.NoiseSpew.ControlEnv;
import org.noisesmith.noisegenerator.Engine.EngineEnv;
import java.util.function.Function;
import java.util.Map;

public class Master extends Command implements Command.ICommand {
    double amp;

    public Function<String[], Command> getParser() {
        return s -> new Master(s);
    }

    public String getName() {return "master volume";}
    public String[] getInvocations() {return new String[]{"V"};}
    public String[] getArgs() {return new String[] {"amp"};}
    public String getHelp() {
        return "set master amplitude factor for all audio";
    }

    public Master() {};
    public Master (String[] args) {
        amp = Double.parseDouble(args[0]);
    }

    public String execute ( ControlEnv environment ) {return null;}

    public String execute ( EngineEnv environment ) {
        environment.master = amp;
        return null;
    }

    public Map serialize(Map<String,Object> to) {
        to.put("name", getName());
        to.put("amp", amp);
        to.put("time", getMoment() / 1000.0);
        return to;
    }
    public Function<Map, Command> getDeserializer() {
        return from -> {
            Master instance = new Master();
            instance.amp = (double) from.get("amp");
            double time = (double) from.get("time");
            instance.setMoment((long) (time*1000));
            instance.setInteractive(false);
            return instance;
        };
    }
}
