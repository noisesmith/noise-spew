package org.noisesmith.noisespew;

import java.util.Hashtable;
import java.util.function.Function;
import java.util.Arrays;


class Parsed {
    public CommandParser.Action action;
    public Object[] args;
    Parsed(CommandParser.Action a, Object[] o) {
        action = a;
        args = o;
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
    static final public Hashtable<Action,Function<String[],Object[]>>
        defaultParser =  new Hashtable<Action,Function<String[],Object[]>>() {{
            put(Action.EXIT, (s) -> {return new Object[0];});
            put(Action.LIST, (s) -> {return new Object[0];});
            put(Action.PLAYTOGGLE,
                (s) -> {
                    Object[] o = new Object[1];
                    o[0] = Integer.parseInt(s[0]); // index to toggle
                    return o;
                });
            put(Action.LOOPPOINTS,
                (s) -> {
                    Object[] o = new Object[3];
                    o[0] = Integer.parseInt(s[0]); // index to adjust
                    o[1] = Double.parseDouble(s[1]); // start point
                    o[2] = Double.parseDouble(s[2]); // end point
                    return o;
                });
            put(Action.ADDSOURCE,
                (s) -> {
                    Object[] o = new Object[1];
                    o[0] = String.join(" ", s); // source location
                    return o;
                });
            put(Action.ADDLOOP,
                (s) -> {
                    Object[] o = new Object[1];
                    o[0] = Integer.parseInt(s[0]); // index of source to realize
                    return o;
                });
            put(Action.DELETESOURCE,
                (s) -> {
                    Object[] o = new Object[1];
                    o[0] = Integer.parseInt(s[0]); // index of source to delete
                    return o;
                });
            put(Action.DELETELOOP,
                (s) -> {
                    Object[] o = new Object[1];
                    o[0] = Integer.parseInt(s[0]); // index of loop to delete
                    return o;
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
    public static Parsed parse
        (Hashtable<String,Action> dispatcher,
         Hashtable<Action,Function<String[],Object[]>> parser,
         String[] line) {
        Action a = dispatcher.get(line[0]);
        Function<String[],Object[]> f = parser.get(a);
        try {
            Object[] result = f.apply(Arrays.copyOfRange(line, 1, line.length));
            Parsed parsed = new Parsed(a, result);
            return parsed;
        } catch (Exception e) {
            System.out.println("failed to parse command " + a + " on " + line);
            e.printStackTrace();
            return new Parsed(Action.NULL, new Object[0]);
        }
    }
    public static Parsed parse(String[] line) {
        return parse(defaultDispatcher, defaultParser, line);
    }
}
