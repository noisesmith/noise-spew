package org.noisesmith.noisespew.commands;

import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.Preset;
import org.noisesmith.noisespew.NoiseSpew;
import org.noisesmith.noisegenerator.Engine;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public class StoreCommands extends Command implements Command.ICommand {
    String destination;

    public String getName() {return "store commands";}

    public Function<String[], Command> getParser() {
        return s -> new StoreCommands(s);
    }
    public String[] getInvocations() {return new String[] {"J"};}
    public String[] getArgs() {return new String[]{"file"};}
    public String getHelp() {return "save json data of all commands to <file>";}

    public StoreCommands(){};
    public StoreCommands (String[] args) {
        destination = String.join(" ", args);
    }

    public String execute ( NoiseSpew.ControlEnv environment ) {
        ArrayList<Command.ICommand> commands = environment.commands;
        ArrayList<Command.ICommand> cs =
            (ArrayList<Command.ICommand>) commands.clone();
        Predicate<Command.ICommand> p = c ->
            c instanceof Help || c instanceof ListStatus;
        cs.removeIf(p);
        Command[] carray = cs.toArray(new Command[0]);
        try {
            Preset.store(carray, destination);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return "failed to store to " + destination;
        }
    }
    public String execute ( Engine.EngineEnv environment ) {return null;}

    public Map serialize(Map<String,Object> to) {
        to.put("name", getName());
        to.put("destination", destination);
        to.put("time", getMoment() / 1000.0);
        return to;
    }
    public Function<Map, Command> getDeserializer () {
        return from -> {
            StoreCommands instance = new StoreCommands();
            instance.destination = (String) from.get("destination");
            double time = (double) from.get("time");
            instance.setMoment((long) (time*1000));
            instance.setInteractive(false);
            return instance;
        };
    }
}
