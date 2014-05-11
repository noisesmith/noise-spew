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
    Command(CommandParser.Action a) {
        action = a;
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
        }};
    static final public Hashtable<Action,Function<String[],Command>>
        defaultParser =  new Hashtable<Action,Function<String[],Command>>() {{
            put(Action.EXIT, (s) -> {return new Command(Action.EXIT);});
            put(Action.LIST, (s) -> {return new Command(Action.LIST);});
            put(Action.PLAYTOGGLE,
                (s) -> {
                    Command c = new Command(Action.PLAYTOGGLE);
                    c.index = Integer.parseInt(s[0]);
                    return c;
                });
            put(Action.LOOPPOINTS,
                (s) -> {
                    Command c = new Command(Action.LOOPPOINTS);
                    c.index = Integer.parseInt(s[0]);
                    c.start = Double.parseDouble(s[1]);
                    c.end = Double.parseDouble(s[2]);
                    return c;
                });
            put(Action.ADDSOURCE,
                (s) -> {
                    Command c = new Command(Action.ADDSOURCE);
                    c.source = String.join(" ", s);
                    return c;
                });
            put(Action.ADDLOOP,
                (s) -> {
                    Command c = new Command(Action.ADDLOOP);
                    c.index = Integer.parseInt(s[0]);
                    return c;
                });
            put(Action.DELETESOURCE,
                (s) -> {
                    Command c = new Command(Action.DELETESOURCE);
                    c.index = Integer.parseInt(s[0]);
                    return c;
                });
            put(Action.DELETELOOP,
                (s) -> {
                    Command c = new Command(Action.DELETELOOP);
                    c.index  = Integer.parseInt(s[0]);
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
    }
    public static Command parse
        (Hashtable<String,Action> dispatcher,
         Hashtable<Action,Function<String[],Command>> parser,
         String[] line) {
        Action a = dispatcher.get(line[0]);
        Function<String[],Command> f = parser.get(a);
        try {
            Command result = f.apply(Arrays.copyOfRange(line, 1, line.length));
            return result;
        } catch (Exception e) {
            System.out.println("failed to parse command " + a + " on " + line);
            e.printStackTrace();
            return new Command(Action.NULL);
        }
    }
    public static Command parse(String[] line) {
        return parse(defaultDispatcher, defaultParser, line);
    }
}
