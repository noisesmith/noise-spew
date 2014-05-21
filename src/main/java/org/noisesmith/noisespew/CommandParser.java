package org.noisesmith.noisespew;

import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.function.Function;
import java.util.Arrays;
import java.util.ArrayList;
import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.commands.*;

public class CommandParser {
    public CommandParser() {
        invocations = new Hashtable<String, Registration>();
        deserializations = new Hashtable<String, Registration>();
        commands = new ArrayList <Registration>() {{
                add(new Registration(new String[] {"h", "H", "?"},
                                     Help.name,
                                     new String[0],
                                     "show this help message",
                                     Help.parse,
                                     Help.deserialize));
                add(new Registration(new String[] {"q", "Q", "e"},
                                     Exit.name,
                                     new String[0],
                                     "exit now",
                                     Exit.parse,
                                     Exit.deserialize));
                add(new Registration(new String[] {"l"},
                                     ListStatus.name,
                                     new String[0],
                                     "list the status of the program",
                                     ListStatus.parse,
                                     ListStatus.deserialize));
                add(new Registration(new String[] {"p"},
                                     PlayToggle.name,
                                     new String[] {"index"},
                                     "toggle playback of loop <index>",
                                     PlayToggle.parse,
                                     PlayToggle.deserialize));
                add(new Registration(new String[] {"x"},
                                     LoopPoints.name,
                                     new String[] {"index", "start", "end"},
                                     "set loop points for loop <index>",
                                     LoopPoints.parse,
                                     LoopPoints.deserialize));
                add(new Registration(new String[] {"s"},
                                     AddSource.name,
                                     new String[] {"source"},
                                     "add a source file from path <source>",
                                     AddSource.parse,
                                     AddSource.deserialize));
                add(new Registration(new String[] {"a"},
                                     AddLoop.name,
                                     new String[] {"index"},
                                     "create a loop for source <index>",
                                     AddLoop.parse,
                                     AddLoop.deserialize));
                add(new Registration(new String[] {"d"},
                                     DeleteSource.name,
                                     new String[] {"index"},
                                     "delete source <index>",
                                     DeleteSource.parse,
                                     DeleteSource.deserialize));
                add(new Registration(new String[] {"D"},
                                     DeleteLoop.name,
                                     new String[] {"index"},
                                     "delete loop <index>",
                                     DeleteLoop.parse,
                                     DeleteLoop.deserialize));
                add(new Registration(new String[] {"J"},
                                     StoreCommands.name,
                                     new String[]{"file"},
                                     "save json data of all commands to <file>",
                                     StoreCommands.parse,
                                     StoreCommands.deserialize));
                add(new Registration(new String[] {"j"},
                                     LoadCommands.name,
                                     new String[]{"file"},
                                     "replay commands from json file <file>",
                                     LoadCommands.parse,
                                     LoadCommands.deserialize));
                add(new Registration(new String[] {"v"},
                                     Amplitude.name,
                                     new String[]{"index", "amp"},
                                     "set amplitude of playback " +
                                     "for loop <index>",
                                     Amplitude.parse,
                                     Amplitude.deserialize));
                add(new Registration(new String[] {"r"},
                                     Rate.name,
                                     new String[]{"index", "rate"},
                                     "set rate of playback for loop <index>",
                                     Rate.parse,
                                     Rate.deserialize));
                add(new Registration(new String[]{"t"},
                                     LoopType.name,
                                     new String[]{"index", "type"},
                                     "set looping 0=normal 1=pingpong" +
                                     " 2=oneshot",
                                     LoopType.parse,
                                     LoopType.deserialize));
                add(new Registration(new String[]{"V"},
                                     Master.name,
                                     new String[] {"amp"},
                                     "set master amplitude factor for all" +
                                     " audio",
                                     Master.parse,
                                     Master.deserialize));
            }};
        commands.forEach(reg -> {
                for(String input : reg.invoke) {
                    invocations.put(input, reg);
                }
                deserializations.put(reg.name, reg);
            });
    }

    Hashtable<String, Registration> invocations;
    Hashtable<String, Registration> deserializations;

    public LinkedHashMap[] serialize ( Command[] commands ) {
        LinkedHashMap <String, Object> results[] =
            new LinkedHashMap[commands.length];
        for(int i = 0; i < commands.length; i++) {
            results[i] = new LinkedHashMap();
            commands[i].serialize(results[i]);
        }
        return results;
    }

    public Command[] deserialize ( Hashtable[] hashes ) {
        Command[] results = new Command[hashes.length];
        for (int i = 0; i < hashes.length; i++) {
            Hashtable h = hashes[i];
            Registration r = deserializations.get(h.get("name"));
            if (r.deserialize != null) {
                results[i] = r.deserialize.apply(h);
            } else {
                System.out.println("no deserialization for: " + r.name);
            }
        }
        return results;
    }

    public ArrayList<Registration> commands;
    public Command parse (String[] line) {
        try {
            Registration registered = invocations.get(line[0]);
            String[] args = Arrays.copyOfRange(line, 1, line.length);
            Command command = registered.parse.apply(args);
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
