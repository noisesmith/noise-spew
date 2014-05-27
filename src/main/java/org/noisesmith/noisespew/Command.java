package org.noisesmith.noisespew;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import org.noisesmith.noisespew.NoiseSpew.ControlEnv;
import org.noisesmith.noisegenerator.Engine.EngineEnv;
import java.util.function.Function;

public class Command implements Comparable<Command>{
    long moment;
    public long getMoment() {return moment;}
    public void setMoment(long time) {moment = time;}
    public void offset(long offset) {moment += offset;}
    boolean interactive;
    public boolean isInteractive() {return interactive;}
    public void setInteractive(boolean status) {interactive = status;}
    ArrayBlockingQueue<String> replyTo;
    public ArrayBlockingQueue<String> getSender() {return replyTo;}
    public void setSender(ArrayBlockingQueue<String> sender) {replyTo = sender;}

    public interface ICommand {
        public long getMoment();
        public boolean isInteractive();
        public ArrayBlockingQueue<String> getSender();
        public void setSender(ArrayBlockingQueue<String> sender);

        public Function<String[],Command> getParser();

        public String execute ( ControlEnv environment );
        public String execute ( EngineEnv environment );

        public Map serialize(Map<String,Object> to);
        public Function<Map, Command> getDeserializer();

        public String[] getInvocations();
        public String getName();
        public String[] getArgs();
        public String getHelp();
    }

    @Override
    public int compareTo(Command other){
        return (this.moment == other.moment) ?
            0 : (this.moment > other.moment) ?
            1 : -1;
    }    
}
