package org.noisesmith.noisespew.commands;

import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.CommandParser;
import org.noisesmith.noisespew.NoiseSpew;
import org.noisesmith.noisegenerator.Engine;
import java.util.function.Function;
import java.util.Hashtable;
import java.util.LinkedHashMap;

public class Help extends Command {
    public static Function<String[], Command> parse = s -> new Help(s);

    static public final String name = "help";

    public Help () {}
    public Help ( String[] args ) {}

    public String execute( NoiseSpew.ControlEnv environment ) {
        StringBuilder helpstring = new StringBuilder();
        new CommandParser().commands.forEach(reg -> {
                helpstring.append("\n\n");
                for(String invocation : reg.invoke)
                    helpstring.append(" >").append(invocation);
                helpstring
                    .append(" -- ")
                    .append(reg.name)
                    .append('(')
                    .append(reg.formals)
                    .append(")\n")
                    .append(reg.description);
            });
        return helpstring.toString();
    }

    public String execute( Engine.EngineEnv environment ) {return null;}

    public LinkedHashMap serialize(LinkedHashMap<String,Object> to) {
        to.put("name", name);
        to.put("time", moment / 1000.0);
        return to;
    }
    public static Function<Hashtable, Command> deserialize = from -> {
        Help instance = new Help();
        instance.moment = (long) ((double) from.get("time"))*1000;
        instance.interactive = false;
        return instance;
    };
}
