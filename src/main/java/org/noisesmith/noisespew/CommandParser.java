package org.noisesmith.noisespew;

import java.util.Hashtable;
import java.util.function.Function;
import java.util.Arrays;
import java.util.ArrayList;
import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.commands.*;

public class CommandParser {
    public CommandParser() {
        invocations = new Hashtable<String, Registration>();
        commands.forEach(reg -> {
                for(String input : reg.invoke) {
                    invocations.put(input, reg);
                }
            });
    }

    Hashtable<String, Registration> invocations;

    public static ArrayList<Registration> commands =
        new ArrayList <Registration>() {{
            add(new Registration(new String[] {"h", "H", "?"},
                                 "help",
                                 new String[0],
                                 "show this help message",
                                 Help.parse));
            add(new Registration(new String[] {"q", "Q", "e"},
                                 "exit",
                                 new String[0],
                                 "exit now",
                                 Exit.parse));
            add(new Registration(new String[] {"l"},
                                 "list",
                                 new String[0],
                                 "list the status of the program",
                                 ListStatus.parse));
            add(new Registration(new String[] {"p"},
                                 "play toggle",
                                 new String[] {"index"},
                                 "toggle playback of loop <index>",
                                 PlayToggle.parse));
            add(new Registration(new String[] {"x"},
                                 "loop points",
                                 new String[] {"index", "start", "end"},
                                 "set loop points for loop <index>",
                                 LoopPoints.parse));
            add(new Registration(new String[] {"s"},
                                 "add source",
                                 new String[] {"source"},
                                 "add a source file from path <source>",
                                 AddSource.parse));
            add(new Registration(new String[] {"a"},
                                 "add loop",
                                 new String[] {"index"},
                                 "create a loop for source <index>",
                                 AddLoop.parse));
            add(new Registration(new String[] {"d"},
                                 "delete source",
                                 new String[] {"index"},
                                 "delete source <index>",
                                 DeleteSource.parse));
            add(new Registration(new String[] {"D"},
                                 "delete loop",
                                 new String[] {"index"},
                                 "delete loop <index>",
                                 DeleteLoop.parse));
            add(new Registration(new String[] {"J"},
                                 "store commands",
                                 new String[]{"file"},
                                 "save json data of all commands to <file>",
                                 StoreCommands.parse));
            add(new Registration(new String[] {"j"},
                                 "load commands",
                                 new String[]{"file"},
                                 "replay commands from json file <file>",
                                 LoadCommands.parse));
            add(new Registration(new String[] {"v"},
                                 "amplitude",
                                 new String[]{"index", "amp"},
                                 "set amplitude of playback for loop <index>",
                                 Amplitude.parse));
            add(new Registration(new String[] {"r"},
                                 "rate",
                                 new String[]{"index", "rate"},
                                 "set rate of playback for loop <index>",
                                 Rate.parse));
            add(new Registration(new String[]{"t"},
                                 "loop type",
                                 new String[]{"index", "type"},
                                 "set looping 0=normal 1=pingpong 2=oneshot",
                                 LoopType.parse));
            add(new Registration(new String[]{"V"},
                                 "master",
                                 new String[] {"amp"},
                                 "set master amplitude factor for all audio",
                                 Master.parse));
        }};

    public Command parse (String[] line) {
        try {
            Registration registered = invocations.get(line[0]);
            String[] args = Arrays.copyOfRange(line, 1, line.length);
            Command command = registered.fun.apply(args);
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
