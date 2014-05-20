package org.noisesmith.noisespew.commands;

import org.noisesmith.noisespew.Command;
import org.noisesmith.noisespew.NoiseSpew;
import org.noisesmith.noisegenerator.Engine;
import org.noisesmith.noisegenerator.UGen;
import org.noisesmith.noisegenerator.StereoUGen;
import java.util.function.Function;
import java.io.File;

public class AddLoop extends Command {
    int index;
    String source;

    public static Function<String[], Command> parse = s -> new AddLoop(s);

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
                b = UGen.fileBuffer(source);
                environment.buffers.put(i, b);
            }
            StereoUGen u = new StereoUGen(b);
            u.description = source;
            environment.sources.add(0, u);
        } catch (Exception ex) {
            System.out.println("could not load loop: " + source);
            ex.printStackTrace();
        } finally {
            return null;
        }
    }
}
