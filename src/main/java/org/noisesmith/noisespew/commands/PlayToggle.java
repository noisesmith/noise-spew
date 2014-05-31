package org.noisesmith.noisespew.commands;

import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.NoiseSpew;
import org.noisesmith.noisegenerator.Engine;
import org.noisesmith.noisegenerator.UGen;
import java.util.Map;
import java.util.function.Function;

public class PlayToggle extends Command implements Command.ICommand {
    String identifier;

    public Function<String[], Command> getParser() {
        return s -> new PlayToggle(s);
    }

    public String getName() {return "play toggle";}
    public String[] getInvocations() {return new String[] {"p"};}
    public String[] getArgs() {return new String[] {"id"};}
    public String getHelp() {return "toggle playback of loop <id>";}

    public PlayToggle(){};
    public PlayToggle (String[] args) {
        identifier = args[0];
    }

    public String execute ( NoiseSpew.ControlEnv environment ) {return null;}

    public String execute ( Engine.EngineEnv environment ) {
        UGen ugen = environment.getUGen(identifier);
        if (ugen == null) {
            return "Cannot toggle playback of " + identifier;
        } else {
            ugen.toggle();
            return null;
        }
    }
    public Map serialize(Map<String,Object> to) {
        to.put("name", getName());
        to.put("identifier", identifier);
        to.put("time", getMoment() / 1000.0);
        return to;
    }
    public Function<Map, Command> getDeserializer() {
        return from -> {
            PlayToggle instance = new PlayToggle();
            instance.identifier = (String) from.get("identifier");
            double time = (double) from.get("time");
            instance.setMoment((long) (time*1000));
            instance.setInteractive(false);
            return instance;
        };
    }
}
