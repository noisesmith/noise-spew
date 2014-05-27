package org.noisesmith.noisespew;

import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.function.Function;
import java.util.Arrays;
import java.util.ArrayList;
import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.commands.*;

public class CommandParser {
    Hashtable<String, Command.ICommand> invocations;
    Hashtable<String, Command.ICommand> deserializations;
    public ArrayList<Command.ICommand> commands;

    public CommandParser() {
        invocations = new Hashtable<String, Command.ICommand>();
        deserializations = new Hashtable<String, Command.ICommand>();
        commands = new ArrayList<Command.ICommand>() {{
                add((Command.ICommand) new Help());
                add((Command.ICommand) new Exit());
                add((Command.ICommand) new ListStatus());
                add((Command.ICommand) new PlayToggle());
                add((Command.ICommand) new LoopPoints());
                add((Command.ICommand) new AddSource());
                add((Command.ICommand) new DeleteSource());
                add((Command.ICommand) new AddLoop());
                add((Command.ICommand) new DeleteLoop());
                add((Command.ICommand) new StoreCommands());
                add((Command.ICommand) new LoadCommands());
                add((Command.ICommand) new Amplitude());
                add((Command.ICommand) new Rate());
                add((Command.ICommand) new LoopType());
                add((Command.ICommand) new Master());
            }};
        commands.forEach(command -> {
                for(String input : command.getInvocations())
                    invocations.put(input, command);
                deserializations.put(command.getName(), command);
            });
    }

    public LinkedHashMap[] serialize ( Command[] commands ) {
        LinkedHashMap <String, Object> results[] =
            new LinkedHashMap[commands.length];
        for(int i = 0; i < commands.length; i++) {
            results[i] = new LinkedHashMap();
            ((Command.ICommand) commands[i]).serialize(results[i]);
        }
        return results;
    }

    public Command[] deserialize ( Hashtable[] hashes ) {
        Command[] results = new Command[hashes.length];
        for (int i = 0; i < hashes.length; i++) {
            Hashtable h = hashes[i];
            Command.ICommand command = deserializations.get(h.get("name"));
            results[i] = command.getDeserializer().apply(h);
        }
        return results;
    }

    public Command parse (String[] line) {
        try {
            Command.ICommand commandInstance = invocations.get(line[0]);
            String[] args = Arrays.copyOfRange(line, 1, line.length);
            Command command = commandInstance.getParser().apply(args);
            return command;
        } catch (Exception e) {
            if (line.length > 0 && !(line[0].isEmpty())) {
                System.out.print("failed to parse command \"");
                for(String token :  line) System.out.print(' ' + token);
                System.out.println('"');
            }
            return new Null(null);
        }
    }
}
