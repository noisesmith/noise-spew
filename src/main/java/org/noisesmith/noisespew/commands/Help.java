package org.noisesmith.noisespew.commands;

import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.CommandParser;
import org.noisesmith.noisespew.NoiseSpew;
import org.noisesmith.noisegenerator.Engine;
import java.util.function.Function;
import java.util.Map;

public class Help extends Command implements Command.ICommand {
    public Function<String[], Command> getParser() {
        return s -> new Help(s);
    }
    public String getName() {return "help";}
    public String[] getInvocations() {return new String[] {"h", "H", "?"};}
    public String[] getArgs() {return new String[0];}
    public String getHelp() {return "show this help message";}

    public Help () {}
    public Help ( String[] args ) {}

    public String execute( NoiseSpew.ControlEnv environment ) {
        StringBuilder helpstring = new StringBuilder();
        new CommandParser().commands.forEach(command -> {
                helpstring.append("\n\n");
                for(String invocation : command.getInvocations())
                    helpstring.append(" >").append(invocation);
                helpstring
                    .append(" -- ")
                    .append(command.getName())
                    .append(" (")
                    .append(String.join(", ", command.getArgs()))
                    .append(")\n")
                    .append(command.getHelp());
            });
        return helpstring.toString();
    }

    public String execute( Engine.EngineEnv environment ) {return null;}

    public Map serialize(Map<String,Object> to) {
        to.put("name", getName());
        to.put("time", getMoment() / 1000.0);
        return to;
    }
    public Function<Map, Command> getDeserializer() {
        return from -> {
            Help instance = new Help();
            double time = (double) from.get("time");
            instance.setMoment((long) (time*1000));
            instance.setInteractive(false);
            return instance;
        };
    }
}
