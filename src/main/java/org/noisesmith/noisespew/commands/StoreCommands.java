package org.noisesmith.noisespew.commands;

import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.Preset;
import org.noisesmith.noisespew.NoiseSpew;
import org.noisesmith.noisegenerator.Engine;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.Predicate;

public class StoreCommands extends Command {
    String destination;

    public static Function<String[], Command> parse = s -> new StoreCommands(s);

    public StoreCommands (String[] args) {
        destination = String.join(" ", args);
    }

    public String execute ( NoiseSpew.ControlEnv environment ) {
        ArrayList<Command> commands = environment.commands;
        ArrayList<Command> cs = (ArrayList<Command>) commands.clone();
        Predicate<Command> p = c ->
            c instanceof Help || c instanceof ListStatus;
        cs.removeIf(p);
        Command[] carray = cs.toArray(new Command[0]);
        try {
            Preset.store(carray, destination);
            return null;
        } catch (Exception e) {
            return "failed to store to " + destination;
        }
    }

    public String execute ( Engine.EngineEnv environment ) {return null;}
}
