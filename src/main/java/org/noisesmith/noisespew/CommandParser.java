package org.noisesmith.noisespew;

import java.util.Hashtable;
import java.util.function.Function;
import java.util.Arrays;
import java.util.EnumMap;

class Command {
    public CommandParser.Action action;

    public long moment; // min in ms since start
    public int index; // numeric input selection
    public double start;
    public double end;
    public String source;
    public String destination;
    public Boolean interactive;
    Command(CommandParser.Action a) {
        action = a;
    }

    Command() {
    }
}

/* to add a command:
 add an entry to the Action enum
 add an entry to defaultHelp
 add at least one entry to defaultDispatcher
 add an entry to defaultParser
 add a case to the command processor (ie. NoiseSpew.loopWorker)
*/

class CommandParser {
    public static Command parse(String[] line) {
        return parse(defaultHelp, defaultDispatcher, defaultParser, line);
    }

    public enum Action {
        NULL,
        HELP,
        EXIT,
        LIST,
        PLAYTOGGLE,
        LOOPPOINTS,
        ADDSOURCE,
        ADDLOOP,
        DELETESOURCE,
        DELETELOOP,
        STORECOMMANDS,
        LOADCOMMANDS
    }

    static final public EnumMap<Action,String[]>
        defaultHelp = new EnumMap<Action,String[]>(CommandParser.Action.class)
    {{
            put(Action.HELP, new String[]{
                    "",
                    "show this help message"
                });
            put(Action.EXIT, new String[]{
                    "",
                    "exit now"
                });
            put(Action.LIST, new String[]{
                    "",
                    "list all sources and loops and show indexes"
                });
            put(Action.PLAYTOGGLE, new String[]{
                    "index",
                    "toggle playback of loop <index>"
                });
            put(Action.LOOPPOINTS, new String[]{
                    "index, start, end",
                    "set loop points for loop <index>"
                });
            put(Action.ADDSOURCE, new String[]{
                    "source",
                    "add a source file from path <source>"
                });
            put(Action.ADDLOOP, new String[]{
                    "index",
                    "create a loop for source <index>"
                });
            put(Action.DELETESOURCE, new String[]{
                    "index",
                    "delete source <index>"
                });
            put(Action.DELETELOOP, new String[]{
                    "index",
                    "delete loop <index>"
                });
            put(Action.STORECOMMANDS, new String[]{
                    "file",
                    "save json data of all commands to <file>"
                });
            put(Action.LOADCOMMANDS, new String[]{
                    "file",
                    "replay commands from json file <file>"
                });
        }};

    static final public Hashtable<String,Action>
        defaultDispatcher = new Hashtable<String,Action>() {{
            put("e", Action.EXIT);
            put("h", Action.HELP);
            put("l", Action.LIST);
            put("p", Action.PLAYTOGGLE);
            put("x", Action.LOOPPOINTS);
            put("s", Action.ADDSOURCE);
            put("a", Action.ADDLOOP);
            put("d", Action.DELETESOURCE);
            put("D", Action.DELETELOOP);
            put("J", Action.STORECOMMANDS);
            put("j", Action.LOADCOMMANDS);
        }};

    static final public EnumMap<Action,Function<String[],Command>>
        defaultParser = new EnumMap<Action,Function<String[],Command>>
        (CommandParser.Action.class) {{
            put(Action.HELP, (s) -> {return new Command();});
            put(Action.EXIT, (s) -> {return new Command();});
            put(Action.LIST, (s) -> {return new Command();});
            put(Action.PLAYTOGGLE,
                (s) -> {
                    Command c = new Command();
                    c.index = Integer.parseInt(s[0]);
                    return c;
                });
            put(Action.LOOPPOINTS,
                (s) -> {
                    Command c = new Command();
                    c.index = Integer.parseInt(s[0]);
                    c.start = Double.parseDouble(s[1]);
                    c.end = Double.parseDouble(s[2]);
                    return c;
                });
            put(Action.ADDSOURCE,
                (s) -> {
                    Command c = new Command();
                    c.source = String.join(" ", s);
                    return c;
                });
            put(Action.ADDLOOP,
                (s) -> {
                    Command c = new Command();
                    c.index = Integer.parseInt(s[0]);
                    return c;
                });
            put(Action.DELETESOURCE,
                (s) -> {
                    Command c = new Command();
                    c.index = Integer.parseInt(s[0]);
                    return c;
                });
            put(Action.DELETELOOP,
                (s) -> {
                    Command c = new Command();
                    c.index  = Integer.parseInt(s[0]);
                    return c;
                });
            put(Action.STORECOMMANDS,
                (s) -> {
                    Command c = new Command();
                    c.destination = String.join(" ", s);
                    return c;
                });
            put(Action.LOADCOMMANDS,
                (s) -> {
                    Command c = new Command();
                    c.source = String.join(" ", s);
                    return c;
                });
        }};

    public static Command parse
        (EnumMap<Action,String[]> help,
         Hashtable<String,Action> dispatcher,
         EnumMap<Action,Function<String[],Command>> parser,
         String[] line) {
        try {
            Action action = dispatcher.get(line[0]);
            String[] args = Arrays.copyOfRange(line, 1, line.length);
            Function<String[],Command> f = parser.get(action);
            Command result = f.apply(args);
            result.action = action;
            switch (action) {
            // special casing commands that need access to command parser scope
            case HELP:
                StringBuilder helpstring = new StringBuilder();
                help.forEach((a,s) -> {
                        helpstring.append("\n\n");
                        dispatcher.forEach((c,a2) -> {
                                if (a2 == a) {
                                    helpstring.append("> ")
                                        .append(c)
                                        .append(" -- ");
                                }
                            });
                        helpstring
                            .append(a)
                            .append('(')
                            .append(s[0])
                            .append(")\n")
                            .append(s[1]);
                    });
                result.source = helpstring.toString();
                break;
            default:
                // no action taken for commands with no parser scope needs
            }
            return result;
        } catch (Exception e) {
            System.out.print("failed to parse command \"");
            for(String token :  line) System.out.print(' ' + token);
            System.out.println('"');
            e.printStackTrace();
            return new Command(Action.NULL);
        }
    }
}
