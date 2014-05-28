package org.noisesmith.noisespew.commands;

import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.NoiseSpew;
import org.noisesmith.noisespew.NoiseSpew.ControlEnv;
import org.noisesmith.noisegenerator.Engine;
import org.noisesmith.noisegenerator.ugens.Looper;
import java.util.Map;
import java.util.function.Function;

public class LoopPoints extends Command implements Command.ICommand {
    int index;
    double start;
    double end;

    public String getName() {return "loop points";}
    public String[] getInvocations() {return new String[] {"x"};}
    public String[] getArgs() {return new String[] {"index", "start", "end"};}
    public String getHelp() {return "set loop points for loop <index>";}

    public Function<String[], Command> getParser() {
        return s -> new LoopPoints(s);
    }

    public LoopPoints(){};
    public LoopPoints (String[] args) {
        index = Integer.parseInt(args[0]);
        start = Double.parseDouble(args[1]);
        end = Double.parseDouble(args[2]);
    }

    public String execute ( NoiseSpew.ControlEnv environment ) { return null; }

    public String execute ( Engine.EngineEnv environment ) {
        String error = Engine.badIndex(index,
                                       "could not reloop",
                                       environment.sources);
        if (error != null) {
            return error;
        } else {
            Looper loop = (Looper) environment.sources.get(index);
            loop.in(start);
            loop.out(end);
            loop.start();
            return null;
        }
    }
    public Map serialize(Map<String,Object> to) {
        to.put("name", getName());
        to.put("index", index);
        to.put("start", start);
        to.put("end", end);
        to.put("time", getMoment() / 1000.0);
        return to;
    }

    public Function<Map, Command> getDeserializer () {
        return from -> {
            LoopPoints instance = new LoopPoints();
            instance.index = (int) from.get("index");
            instance.start = (double) from.get("start");
            instance.end = (double) from.get("end");
            double time = (double) from.get("time");
            instance.setMoment((long) (time*1000));
            instance.setInteractive(false);
            return instance;
        };
    }
}
