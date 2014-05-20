package org.noisesmith.noisespew.commands;

import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.Preset;
import org.noisesmith.noisespew.NoiseSpew.ControlEnv;
import org.noisesmith.noisegenerator.Engine.EngineEnv;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class LoadCommands extends Command {
    String source;
    long offset;

    public static Function<String[], Command> parse = s -> new LoadCommands(s);

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
                c instanceof StoreCommands || c instanceof ListStatus;
            in.removeIf(p);
            in.forEach(c -> {
                c.moment += offset;
                c.interactive = false;
            });
            in.forEach(c -> {
                    Runnable r = new Runnable() {
                            @Override
                            public void run () {
                                try {
                                    Thread.sleep(c.moment);
                                    environment.in.put(c);
                                } catch (Exception e) {
                                    System.out.println("error loading" + c);
                                    e.printStackTrace();
                                }
                            }
                        };
                    new Thread(r, "command-" + c).start();
                });
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
}
