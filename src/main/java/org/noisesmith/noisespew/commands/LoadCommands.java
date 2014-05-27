package org.noisesmith.noisespew.commands;

import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.Preset;
import org.noisesmith.noisespew.NoiseSpew.ControlEnv;
import org.noisesmith.noisegenerator.Engine.EngineEnv;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.List;
import java.util.Collections;
import java.util.function.Function;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;

public class LoadCommands extends Command implements Command.ICommand {
    String source;
    long offset;

    public Function<String[], Command> getParser() {
        return s -> new LoadCommands(s);
    }

    public String getName() {return "load commands";}
    public String[] getInvocations() {return new String[] {"j"};}
    public String[] getArgs() {return new String[]{"file"};}
    public String getHelp() {return "replay commands from json file <file>";}

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
                    c.offset(offset);
                    c.setInteractive(false);
                });
            Collections.sort(in);
            BinaryOperator<Command> reducer = (last, c) -> {
                try {
                    Thread.sleep(c.getMoment() - last.getMoment());
                    environment.in.put(c);
                } catch (Exception e) {
                    System.out.println("error loading " +
                                       ((Command.ICommand) c).getName() + " " +
                                       "<" + c.getMoment() + ">");
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

    public Map serialize(Map<String,Object> to) {
        to.put("name", getName());
        to.put("source", source);
        to.put("offset", offset);
        to.put("time", getMoment() / 1000.0);
        return to;
    }
    public Function<Map, Command> getDeserializer() {
        return from -> {
            LoadCommands instance = new LoadCommands();
            instance.source = (String) from.get("source");
            instance.offset = (long) from.get("offset");
            double time = (double) from.get("time");
            instance.setMoment((long) (time*1000));
            instance.setInteractive(false);
            return instance;
        };
    }
}
