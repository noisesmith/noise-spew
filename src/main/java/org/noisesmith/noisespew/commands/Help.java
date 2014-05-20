package org.noisesmith.noisespew.commands;

import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.CommandParser;
import org.noisesmith.noisespew.NoiseSpew;
import org.noisesmith.noisegenerator.Engine;
import java.util.function.Function;

public class Help extends Command {
    public static Function<String[], Command> parse = s -> new Help(s);

    public Help ( String[] args ) {}

    public String execute( NoiseSpew.ControlEnv environment ) {
        StringBuilder helpstring = new StringBuilder();
        CommandParser.commands.forEach(reg -> {
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
}
