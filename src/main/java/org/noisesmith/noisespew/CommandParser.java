package org.noisesmith.noisespew;

import java.util.Hashtable;
import java.util.function.Function;
import java.util.Arrays;


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

class CommandParser {
    static final public Hashtable<String,Action>
        defaultDispatcher = new Hashtable<String,Action>() {{
            put("e", Action.EXIT);
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
    static final public Hashtable<Action,Function<String[],Command>>
        defaultParser =  new Hashtable<Action,Function<String[],Command>>() {{
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
    public enum Action {
        NULL,
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
    public static Command parse
        (Hashtable<String,Action> dispatcher,
         Hashtable<Action,Function<String[],Command>> parser,
         String[] line) {
        try {
            Action a = dispatcher.get(line[0]);
            Function<String[],Command> f = parser.get(a);
            Command result = f.apply(Arrays.copyOfRange(line, 1, line.length));
            result.action = a;
            return result;
        } catch (Exception e) {
            System.out.print("failed to parse command \"");
            for(String token :  line) System.out.print(' ' + token);
            System.out.println('"');
            e.printStackTrace();
            return new Command(Action.NULL);
        }
    }
    public static Command parse(String[] line) {
        return parse(defaultDispatcher, defaultParser, line);
    }
}
