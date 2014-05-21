package org.noisesmith.noisespew;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Hashtable;
import java.util.concurrent.ArrayBlockingQueue;
import org.noisesmith.noisespew.NoiseSpew.ControlEnv;
import org.noisesmith.noisegenerator.Engine.EngineEnv;
import java.util.function.Function;

public abstract class Command {
    public long moment;
    public Boolean interactive;
    public ArrayBlockingQueue<String> replyTo;

    public static Function<String[],Command> parse;

    public abstract String execute ( ControlEnv environment );
    public abstract String execute ( EngineEnv environment );

    public abstract LinkedHashMap serialize(LinkedHashMap<String,Object> to);
    public static Function<Hashtable, Command> deserialize;
}
