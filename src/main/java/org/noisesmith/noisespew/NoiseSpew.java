package org.noisesmith.noisespew;

import javax.sound.sampled.Clip;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.SynchronousQueue;
import java.util.function.Predicate;

class NoiseSpew {
    public static void main( String[] args ) {
        try {
            System.out.println( "starting noise spew:" );
            ArrayList<Command> commands = new ArrayList<Command>();
            for(String arg : args) {
                Command c = new Command(CommandParser.Action.ADDSOURCE);
                c.source = arg;
                c.interactive = false;
                commands.add(c);
            }
            runInteractive(commands);
        } catch (Exception e) {
            System.err.println("in noise-spew " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void runInteractive(ArrayList<Command> initial) {
        SynchronousQueue<Command> queue = new SynchronousQueue();
        Thread looper = new Thread(new Runnable() {
                @Override
                public void run () {
                    try {
                        loopWorker(queue);
                    } catch (Exception e) {
                        System.out.println("error in NoiseSpew looper thread");
                        e.printStackTrace();
                    }
                }});
        looper.start();
        initial.forEach((c) -> {
                try { queue.put(c);
                } catch (Exception e) {
                    System.out.println("error queing command");
                    e.printStackTrace();
                }
            });
        try {parse(System.in, queue);
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
                    if (s == "") {
                        prompt();
                    } else {
                        System.out.println("error in parsing " + s);
                        prompt();
                    }
                }
            });
    }

    public static void loopWorker( SynchronousQueue<Command> queue )
        throws InterruptedException {
        BiMap<String> resources = new BiMap<String>();
        ArrayList<Loop> loops = new ArrayList<Loop>();
        ArrayList<Command> commands = new ArrayList<Command>();
        Command[] carray;
        Loop loop;
        while (true) {
            Command parsed = queue.take();
            commands.add(parsed);
            switch (parsed.action) {
            case EXIT:
                System.exit(0);
                break;
            case LIST:
                list(resources, loops);
                break;
            case PLAYTOGGLE:
                if (loops.size() > parsed.index) {
                    loops.get(parsed.index).toggle();
                } else {
                    System.out.println("\ncould not toggle playback of " +
                                       parsed.index);
                }
                break;
            case LOOPPOINTS:
                if (loops.size() > parsed.index) {
                    loop = loops.get(parsed.index);
                    loop.start = parsed.start;
                    loop.end = parsed.end;
                    loop.start();
                } else {
                    System.out.println("could not reloop " + parsed.index);
                }
                break;
            case ADDSOURCE:
                resources.put(parsed.source);
                break;
            case ADDLOOP:
                try {
                    loops.add(new Loop(resources.get(parsed.index)));
                } catch (Exception e) {
                    System.out.println("could not load loop: " +
                                       parsed.index);
                }
                break;
            case DELETESOURCE:
                if(resources.containsKey(parsed.index)) {
                    resources.remove(parsed.index);
                } else {
                    System.out.println("could not delete " + parsed.index);
                }
                break;
            case DELETELOOP:
                if(loops.size() > parsed.index) {
                    loops.get(parsed.index).stop();
                    loops.remove(parsed.index);
                } else {
                    System.out.println("could not delete " + parsed.index);
                }
                break;
            case STORECOMMANDS:
                try {
                    carray = commands.toArray(new Command[0]);
                    Preset.store(carray, parsed.destination);
                } catch (Exception e) {
                    System.out.println("could not store commands");
                    e.printStackTrace();
                }
                break;
            case LOADCOMMANDS:
                try {
                    carray = Preset.load(parsed.source);
                    List<Command> al = Arrays.asList(carray);
                    ArrayList<Command> in = new ArrayList<Command>(al);
                    Predicate<Command> p = (c) ->
                        c.action == CommandParser.Action.STORECOMMANDS ||
                        c.action == CommandParser.Action.LIST;
                    in.removeIf(p);
                    in.subList(1, in.size())
                        .forEach((c) -> {
                                c.moment -= in.get(0).moment;
                                c.moment += parsed.moment;
                                c.interactive = false;
                            });
                    in.get(0).moment = parsed.moment;
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
                } catch (Exception e) {
                    System.out.println("could not load commands");
                    e.printStackTrace();
                }
                break;
            case HELP:
                System.out.println(parsed.source);
                break;
            case NULL:
                System.out.println("failed to parse command");
            default:
                System.out.println("invalid command: " + parsed.action);
            }
            if (parsed.interactive != null && parsed.interactive) {
                prompt();
            } else {
                list(resources, loops);
                prompt();
            }
        }
    }

    public static void list( BiMap<String> resources, ArrayList<Loop> loops) {
        resources.forEach((k, v) -> System.out.println(k + " : " + v));
        System.out.println("---");
        BiMap<Loop> loopmap = new BiMap<Loop> (loops);
        loopmap.forEach((i, l) -> {
                String mesg;
                mesg = i + " - " + (l.clip.isRunning() ? "on" : "off");
                mesg += " " + l.start + "->" + l.end;
                mesg +=  "	" + l.source;
                System.out.println(mesg);
            });
    }
}
