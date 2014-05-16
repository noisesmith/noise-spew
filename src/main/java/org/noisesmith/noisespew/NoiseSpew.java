package org.noisesmith.noisespew;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.SynchronousQueue;
import java.util.function.Predicate;
import org.noisesmith.noisegenerator.Generator;
import org.noisesmith.noisegenerator.Engine;
import org.noisesmith.noisegenerator.UGen;
import org.noisesmith.noisegenerator.StereoUGen;
import org.noisesmith.noisegenerator.DebugBytes;

class NoiseSpew {
    public static void main( String[] args ) {
        try {
            if (false) {
                SynchronousQueue engineMessage = new SynchronousQueue();
                Engine gen = new Engine(engineMessage);
                Thread generator = new Thread(gen, "ENGINE");
                generator.start();
                double[] buf = UGen.fileBuffer("example.wav");
                StereoUGen sample = new StereoUGen(buf);
                sample.in(10.0);
                sample.out(14.4);
                sample.start();
                gen.sources.add(sample);
                Thread.sleep(20000);
                sample.in(18.9);
            } else {
                System.out.println( "starting noise spew:" );
                ArrayList<Command> commands = new ArrayList<Command>();
                for(String arg : args) {
                    Command c = new Command(CommandParser.Action.ADDSOURCE);
                    c.source = arg;
                    c.interactive = false;
                    commands.add(c);
                }
                runInteractive(commands);
            }
        } catch (Exception e) {
            System.err.println("in noise-spew " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void runInteractive(ArrayList<Command> initial) {
        SynchronousQueue<Command> commandMessage = new SynchronousQueue();
        SynchronousQueue engineMessage = new SynchronousQueue();
        Engine gen = new Engine(engineMessage);
        Thread audio = new Thread(gen, "ENGINE");
        audio.start();
        Thread looper = new Thread(new Runnable() {
                @Override
                public void run () {
                    try {
                        loopWorker(commandMessage, gen);
                    } catch (Exception e) {
                        System.out.println("error in NoiseSpew looper thread");
                        e.printStackTrace();
                    }
                }},
            "COMMAND PARSER");
        looper.start();
        initial.forEach((c) -> {
                try { commandMessage.put(c);
                } catch (Exception e) {
                    System.out.println("error queing command");
                    e.printStackTrace();
                }
            });
        try {parse(System.in, commandMessage);
        } catch (Exception e) {
            System.out.println("error in parse");
            e.printStackTrace();
        }
    }

    public static void prompt() {
        System.out.print("\nnoise spew> ");
    }

    public static void parse( InputStream stream, SynchronousQueue queue )
        throws java.io.UnsupportedEncodingException {
        InputStreamReader source = new InputStreamReader(stream, "UTF-8");
        BufferedReader in = new BufferedReader(source);
        long stamp = System.currentTimeMillis();
        prompt();
        in.lines().forEachOrdered((s) -> {
                try {
                    String[] input = s.split(" ");
                    Command parsed = CommandParser.parse(input);
                    parsed.moment = System.currentTimeMillis() - stamp;
                    parsed.interactive = true;
                    queue.put(parsed);
                } catch (Exception e) {
                    if (s.matches("^\\s+$")) {
                        prompt();
                    } else {
                        System.out.println("error in parsing " + s);
                        prompt();
                    }
                }
            });
    }

    public static void loopWorker( SynchronousQueue<Command> queue, Engine gen )
        throws InterruptedException {
        BiMap<String> resources = new BiMap<String>();
        ArrayList<Command> commands = new ArrayList<Command>();
        while (true) {
            Command parsed = queue.take();
            commands.add(parsed);
            switch (parsed.action) {
            case EXIT:
                System.exit(0);
                break;
            case LIST:
                list(resources, gen.sources);
                // gen.messages.put(new Engine.Exec(Engine.Action.DEBUG));
                break;
            case PLAYTOGGLE:
                gen.messages.put(new Engine.Exec(Engine.Action.TOGGLE,
                                                 parsed.index));
                break;
            case LOOPPOINTS:
                gen.messages.put(new Engine.Exec(Engine.Action.LOOPPOINTS,
                                                 parsed.index,
                                                 parsed.start,
                                                 parsed.end));
                break;
            case ADDSOURCE:
                resources.put(parsed.source);
                break;
            case ADDLOOP:
                gen.messages.put(new Engine.Exec(Engine.Action.CREATE,
                                                 resources.get(parsed.index)));
                break;
            case DELETESOURCE:
                if(resources.containsKey(parsed.index)) {
                    resources.remove(parsed.index);
                } else {
                    System.out.println("could not delete " + parsed.index);
                }
                break;
            case DELETELOOP:
                gen.messages.put(new Engine.Exec(Engine.Action.DELETE,
                                                 parsed.index));
                break;
            case STORECOMMANDS:
                try {
                    storeCommands(parsed.destination, commands);
                } catch (Exception e) {
                    System.out.println("could not store commands");
                    e.printStackTrace();
                }
                break;
            case LOADCOMMANDS:
                try {
                    // TODO: option to offset by first time * -1
                    loadCommands(parsed.source, 0, queue);
                } catch (Exception e) {
                    System.out.println("could not load commands");
                    e.printStackTrace();
                }
                break;
            case HELP:
                System.out.println(parsed.source);
                break;
            case AMPLITUDE:
                gen.messages.put(new Engine.Exec(Engine.Action.GAIN,
                                                 parsed.index,
                                                 parsed.parameter));
                break;
            case RATE:
                gen.messages.put(new Engine.Exec(Engine.Action.RATE,
                                                 parsed.index,
                                                 parsed.parameter));
                break;
            case NULL:
                System.out.println("failed to parse command");
            default:
                System.out.println("invalid command: " + parsed.action);
            }
            if (parsed.interactive != null && parsed.interactive) {
                prompt();
            } else {
                list(resources, gen.sources);
                prompt();
            }
        }
    }

    public static void list( BiMap<String> resources, ArrayList<UGen> loops) {
        System.out.println("\nresources:\n");
        resources.forEach((k, v) -> System.out.println(k + " : " + v));
        System.out.println("\nloops:\n");
        BiMap<UGen> loopmap = new BiMap<UGen> (loops);
        loopmap.forEach((i, l) -> {
                String mesg;
                mesg = i + " - " + (l.active ? "on" : "off");
                mesg += " " + l.start / 44100.0 + "->" + l.end / 44100.0;
                mesg +=  "	" + l.description;
                System.out.println(mesg);
            });
    }

    public static void storeCommands(String destination,
                                     ArrayList<Command> commands)
        throws java.io.IOException {
        ArrayList<Command> cs = (ArrayList<Command>) commands.clone();
        Predicate<Command> p = (c) ->
            c.action == CommandParser.Action.HELP ||
            c.action == CommandParser.Action.LIST;
        cs.removeIf(p);
        Command[] carray = cs.toArray(new Command[0]);
        Preset.store(carray, destination);
    }

    public static void loadCommands(String source, long offset,
                                    SynchronousQueue queue)
        throws java.io.IOException {
        Command[] carray = Preset.load(source);
        List<Command> al = Arrays.asList(carray);
        ArrayList<Command> in = new ArrayList<Command>(al);
        Predicate<Command> p = (c) ->
            c.action == CommandParser.Action.STORECOMMANDS ||
            c.action == CommandParser.Action.LIST;
        in.removeIf(p);
        in.forEach((c) -> {
                c.moment += offset;
                c.interactive = false;
            });
        in.forEach((c) -> {
                Runnable r = new Runnable() {
                                    @Override
                                    public void run () {
                                        try {
                                            Thread.sleep(c.moment);
                                            queue.put(c);
                                        } catch (Exception e) {
                                            System.out.println("error loading" +
                                                               c.action);
                                            e.printStackTrace();
                                        }
                                    }
                    };
                new Thread(r, "command-" + c.action).start();
            });
    }
}
