package org.noisesmith.noisespew;

import java.util.Hashtable;
import java.util.function.Function;
import java.util.Arrays;
import java.util.EnumMap;

class Command {
    public CommandParser.Action action;

    public long moment; // min in ms since start
    public int index; // numeric input selection
    public int selection;
    public double start;
    public double end;
    public double parameter;
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
        LOADCOMMANDS,
        AMPLITUDE,
        RATE,
        LOOPTYPE,
        MASTER
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
            put(Action.AMPLITUDE, new String[]{
                    "index, amp",
                    "set amplitude of playback for loop <index>"
                });
            put(Action.RATE, new String[]{
                    "index, rate",
                    "set rate of playback for loop <index>"
                });
            put(Action.LOOPTYPE, new String[]{
                    "index, type",
                    "set looping type 0=normal 1=pingpong 2=oneshot"
                });
            put(Action.MASTER, new String[]{
                    "amp",
                    "set master amplitude factor for all audio"
                });
        }};

    static final public Hashtable<String,Action>
        defaultDispatcher = new Hashtable<String,Action>() {{
            put("q", Action.EXIT);
            put("Q", Action.EXIT);
            put("e", Action.EXIT);
            put("h", Action.HELP);
            put("H", Action.HELP);
            put("?", Action.HELP);
            put("l", Action.LIST);
            put("p", Action.PLAYTOGGLE);
            put("x", Action.LOOPPOINTS);
            put("s", Action.ADDSOURCE);
            put("a", Action.ADDLOOP);
            put("d", Action.DELETESOURCE);
            put("D", Action.DELETELOOP);
            put("J", Action.STORECOMMANDS);
            put("j", Action.LOADCOMMANDS);
            put("v", Action.AMPLITUDE);
            put("r", Action.RATE);
            put("t", Action.LOOPTYPE);
            put("V", Action.MASTER);
        }};

    static final public EnumMap<Action,Function<String[],Command>>
        defaultParser = new EnumMap<Action,Function<String[],Command>>
        (CommandParser.Action.class) {{
            put(Action.HELP, s -> {return new Command();});
            put(Action.EXIT, s -> {return new Command();});
            put(Action.LIST, s -> {return new Command();});
            put(Action.PLAYTOGGLE,
                s -> {
                    Command c = new Command();
                    c.index = Integer.parseInt(s[0]);
                    return c;
                });
            put(Action.LOOPPOINTS,
                s -> {
                    Command c = new Command();
                    c.index = Integer.parseInt(s[0]);
                    c.start = Double.parseDouble(s[1]);
                    c.end = Double.parseDouble(s[2]);
                    return c;
                });
            put(Action.ADDSOURCE,
                s -> {
                    Command c = new Command();
                    c.source = String.join(" ", s);
                    return c;
                });
            put(Action.ADDLOOP,
                s -> {
                    Command c = new Command();
                    c.index = Integer.parseInt(s[0]);
                    return c;
                });
            put(Action.DELETESOURCE,
                s -> {
                    Command c = new Command();
                    c.index = Integer.parseInt(s[0]);
                    return c;
                });
            put(Action.DELETELOOP,
                s -> {
                    Command c = new Command();
                    c.index  = Integer.parseInt(s[0]);
                    return c;
                });
            put(Action.STORECOMMANDS,
                s -> {
                    Command c = new Command();
                    c.destination = String.join(" ", s);
                    return c;
                });
            put(Action.LOADCOMMANDS,
                s -> {
                    Command c = new Command();
                    c.source = String.join(" ", s);
                    return c;
                });
            put(Action.AMPLITUDE,
                s -> {
                    Command c = new Command();
                    c.index = Integer.parseInt(s[0]);
                    c.parameter = Double.parseDouble(s[1]);
                    return c;
                });
            put(Action.RATE,
                s -> {
                    Command c = new Command();
                    c.index = Integer.parseInt(s[0]);
                    c.parameter = Double.parseDouble(s[1]);
                    return c;
                });
            put(Action.LOOPTYPE,
                s -> {
                    Command c = new Command();
                    c.index = Integer.parseInt(s[0]);
                    c.selection = Integer.parseInt(s[1]);
                    return c;
                });
            put(Action.MASTER,
                s -> {
                    Command c = new Command();
                    c.parameter = Double.parseDouble(s[0]);
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
                                    helpstring.append(" >")
                                        .append(c);
                                }
                            });
                        helpstring
                            .append(" -- ")
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
            if (line.length > 0 && !(line[0].isEmpty())) {
                System.out.print("failed to parse command \"");
                for(String token :  line) System.out.print(' ' + token);
                System.out.println('"');
            }
            return new Command(Action.NULL);
        }
    }
}
