package org.noisesmith.noisespew.commands;

import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.NoiseSpew;
import org.noisesmith.noisespew.NoiseSpew.ControlEnv;
import org.noisesmith.noisegenerator.Engine;
import org.noisesmith.noisegenerator.ugens.Looper;
import java.util.Map;
import java.util.function.Function;

public class LoopPoints extends Command implements Command.ICommand {
    String identifier;
    double start;
    double end;

    public String getName() {return "loop points";}
    public String[] getInvocations() {return new String[] {"x"};}
    public String[] getArgs() {return new String[] {"identifier", "start", "end"};}
    public String getHelp() {return "set loop points for loop <identifier>";}

    public Function<String[], Command> getParser() {
        return s -> new LoopPoints(s);
    }

    public LoopPoints(){};
    public LoopPoints (String[] args) {
        identifier = args[0];
        start = Double.parseDouble(args[1]);
        end = Double.parseDouble(args[2]);
    }

    public String execute ( NoiseSpew.ControlEnv environment ) { return null; }

    public String execute ( Engine.EngineEnv environment ) {
        Looper loop = (Looper) environment.getUGen(identifier);
        if (loop == null) {
            return "cannot set loop points of " + identifier;
        } else {
            loop.in(start);
            loop.out(end);
            loop.start();
            return null;
        }
    }
    public Map serialize(Map<String,Object> to) {
        to.put("name", getName());
        to.put("identifier", identifier);
        to.put("start", start);
        to.put("end", end);
        to.put("time", getMoment() / 1000.0);
        return to;
    }

    public Function<Map, Command> getDeserializer () {
        return from -> {
            LoopPoints instance = new LoopPoints();
            instance.identifier = (String) from.get("identifier");
            instance.start = (double) from.get("start");
            instance.end = (double) from.get("end");
            double time = (double) from.get("time");
            instance.setMoment((long) (time*1000));
            instance.setInteractive(false);
            return instance;
        };
    }
}
