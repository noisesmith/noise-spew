package org.noisesmith.noisespew.commands;

import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.Preset;
import org.noisesmith.noisespew.NoiseSpew.ControlEnv;
import org.noisesmith.noisegenerator.Engine.EngineEnv;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Collections;
import java.util.function.Function;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;

public class LoadCommands extends Command {
    String source;
    long offset;

    public static final String name = "load commands";

    public static Function<String[], Command> parse = s -> new LoadCommands(s);

    public LoadCommands() {
    }
    public LoadCommands (String[] args) {
        source = String.join(" ", args);
        offset = 0; // TODO : expose offset as an option
    }

    public String execute ( ControlEnv environment ) {
        try {
            Command[] carray = Preset.load(source);
            List<Command> al = Arrays.asList(carray);
            ArrayList<Command> in = new ArrayList<Command>(al);
            Predicate<Command> p = c ->
                c instanceof StoreCommands
                || c instanceof ListStatus
                || c instanceof Help
                || c instanceof Null;
            in.removeIf(p);
            in.forEach(c -> {
                c.moment += offset;
                c.interactive = false;
            });
            Collections.sort(in);
            BinaryOperator<Command> reducer = (last, c) -> {
                try {
                    Thread.sleep(c.moment - last.moment);
                    environment.in.put(c);
                } catch (Exception e) {
                    System.out.println("error loading " + c + " " + "<" +
                                       c.moment + ">");
                    e.printStackTrace();
                }
                return c;
            };
            Runnable r = new Runnable() {
                    @Override
                    public void run () {
                        in.stream().reduce(in.get(0), reducer);
                    }
                };
            new Thread(r, "command-" + source).start();
        } catch (Exception e) {
            StringBuilder error = new StringBuilder();
            error.append("error in load commands\n")
                .append(e)
                .append("\n");
            for(StackTraceElement s : e.getStackTrace()) {
                error.append(e)
                    .append(s.getFileName())
                    .append(s.getLineNumber())
                    .append(s.getClassName())
                    .append(s.getMethodName())
                    .append("\n");
            }
            return error.toString();
        }
        return null;
    }
    public String execute ( EngineEnv environment ) {return null;}

    public LinkedHashMap serialize(LinkedHashMap<String,Object> to) {
        to.put("name", name);
        to.put("source", source);
        to.put("offset", offset);
        to.put("time", moment / 1000.0);
        return to;
    }
    public static Function<Hashtable, Command> deserialize = from -> {
        LoadCommands instance = new LoadCommands();
        instance.source = (String) from.get("source");
        instance.offset = (long) from.get("offset");
        double time = (double) from.get("time");
        instance.moment = (long) (time*1000);
        instance.interactive = false;
        return instance;
    };
}
