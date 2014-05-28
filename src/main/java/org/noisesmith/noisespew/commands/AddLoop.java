package org.noisesmith.noisespew.commands;

import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.NoiseSpew;
import org.noisesmith.noisegenerator.Engine;
import org.noisesmith.noisegenerator.ugens.Looper;
import org.noisesmith.noisegenerator.ugens.StereoLooper;
import java.util.Map;
import java.util.function.Function;
import java.io.File;

public class AddLoop extends Command implements Command.ICommand {
    int index;
    String source;
    public String getName() {return "add loop";}
    public String[] getInvocations() {return new String[] {"a"};}
    public String[] getArgs() {return new String[] {"index"};}
    public String getHelp() {return "create a loop for source <index>";}

    public Function<String[], Command> getParser() {
        return s -> new AddLoop(s);
    }

    public AddLoop(){};
    public AddLoop (String[] args) {
        index = Integer.parseInt(args[0]);
    }

    public String execute ( NoiseSpew.ControlEnv environment ) {
        source = environment.resources.get(index);
        return null;
    }

    public String execute ( Engine.EngineEnv environment ) {
        try {
            String i = new File(source).getCanonicalFile().toString();
            double[] b;
            if(environment.buffers.containsKey(i)) {
                b = environment.buffers.get(i);
            } else {
                b = Looper.fileBuffer(source);
                environment.buffers.put(i, b);
            }
            StereoLooper u = new StereoLooper(b);
            u.setDescription(source);
            environment.sources.add(0, u);
        } catch (Exception ex) {
            System.out.println("could not load loop: " + source);
            ex.printStackTrace();
        } finally {
            return null;
        }
    }
    public Map serialize(Map<String,Object> to) {
        to.put("name", getName());
        to.put("index", index);
        to.put("source", source);
        to.put("time", getMoment() / 1000.0);
        return to;
    }
    public Function<Map, Command> getDeserializer () {
        return from -> {
            AddLoop instance = new AddLoop();
            instance.index = (int) from.get("index");
            instance.source = (String) from.get("source");
            double time = (double) from.get("time");
            instance.setMoment((long) (time*1000));
            instance.setInteractive(false);
            return instance;
        };
    }
}
