package org.noisesmith.noisespew;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ArrayBlockingQueue;
import org.noisesmith.noisegenerator.Generator;
import org.noisesmith.noisegenerator.Engine;
import org.noisesmith.noisegenerator.UGen;
import org.noisesmith.noisegenerator.StereoUGen;
import org.noisesmith.noisegenerator.DebugBytes;

public class NoiseSpew {
    public static void main( String[] args ) {
        try {
            ArrayBlockingQueue printerQueue =
                new ArrayBlockingQueue<String>(1024);
            Thread printer = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            while (true) {
                                String message =
                                    (String) printerQueue.take();
                                System.out.print(message);
                            }
                        } catch (Exception e) {
                            System.out.println("error in printer");
                            e.printStackTrace();
                        }
                    }
                }, "PRINTER");
            printer.start();
            println(printerQueue, "starting noise spew:" );
            SynchronousQueue engineMessage = new SynchronousQueue();
            ArrayList<Command> commands = new ArrayList<Command>();
            // for(String arg : args) {
            //     Command c = new Command(CommandParser.Action.ADDSOURCE);
            //     c.source = arg;
            //     c.interactive = false;
            //     commands.add(c);
            // }
            runInteractive(commands, printerQueue);
        } catch (Exception e) {
            System.err.println("in noise-spew " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void runInteractive(ArrayList<Command> initial,
                                      ArrayBlockingQueue<String> printerQueue) {
        SynchronousQueue<Command> commandMessage = new SynchronousQueue();
        SynchronousQueue<Command> engineMessage = new SynchronousQueue();
        Engine gen = new Engine(engineMessage);
        Thread audio = new Thread(gen, "ENGINE");
        audio.start();
        Thread looper = new Thread(new Runnable() {
                @Override
                public void run () {
                    try {
                        loopWorker(commandMessage, printerQueue, gen);
                    } catch (Exception e) {
                        System.out.println("error in NoiseSpew looper thread");
                        e.printStackTrace();
                    }
                }},
            "COMMAND PARSER");
        looper.start();
        initial.forEach(c -> {
                try { commandMessage.put(c);
                } catch (Exception e) {
                    System.out.println("error queing command");
                    e.printStackTrace();
                }
            });
        CommandParser parser = new CommandParser();
        try {parse(parser, System.in, printerQueue, commandMessage);
        } catch (Exception e) {
            System.out.println("error in parse");
            e.printStackTrace();
        }
    }

    public static void print ( ArrayBlockingQueue<String> printerQueue,
                               String s ) {
        try {
            printerQueue.put(s);
        } catch (Exception e) {
            System.out.println("error printing message: \"" + s + "\"");
        }
    }

    public static void println ( ArrayBlockingQueue<String> printerQueue,
                                 String s ) {
        print(printerQueue, s + "\n");
    }

    public static void prompt ( ArrayBlockingQueue<String> printerQueue ) {
        print(printerQueue, "\nnoise spew> ");
    }

    public static void parse( CommandParser parser,
                              InputStream stream,
                              ArrayBlockingQueue<String> printerQueue,
                              SynchronousQueue queue )
        throws java.io.UnsupportedEncodingException {
        InputStreamReader source = new InputStreamReader(stream, "UTF-8");
        BufferedReader in = new BufferedReader(source);
        long stamp = System.currentTimeMillis();
        prompt(printerQueue);
        in.lines().forEachOrdered(s -> {
                try {
                    String[] input = s.split(" ");
                    Command parsed = parser.parse(input);
                    parsed.moment = System.currentTimeMillis() - stamp;
                    parsed.interactive = true;
                    queue.put(parsed);
                } catch (Exception e) {
                    if (s.matches("^\\s+$")) {
                        prompt(printerQueue);
                    } else {
                        try {
                            println(printerQueue, "error in parsing " + s);
                        prompt(printerQueue);
                        } catch (Exception x) {}
                    }
                }
            });
    }

    public static final class ControlEnv {
        public long offset;
        public SynchronousQueue in;
        public BiMap<String> resources;
        public ArrayList<Command> commands;
        public Engine gen;

        public ControlEnv (SynchronousQueue i, Engine g) {
            resources = new BiMap<String>();
            commands = new ArrayList<Command>();
            in = i;
            gen = g;
        }
    }

    public static void loopWorker( SynchronousQueue<Command> queue,
                                   ArrayBlockingQueue<String> printerQueue,
                                   Engine gen )
        throws InterruptedException {
        ControlEnv environment = new ControlEnv(queue, gen);
        ArrayBlockingQueue<String> replies = new ArrayBlockingQueue(1024);
        String output;
        while (true) {
            Command parsed = queue.take();
            parsed.replyTo = replies;
            if(parsed != null) {
                environment.commands.add(parsed);
                output = parsed.execute(environment);
                if(output != null) println(printerQueue, output);
                gen.messages.put(parsed);
                String engineResponse = replies.take();
                print(printerQueue, engineResponse);
            }
            if(parsed.interactive) prompt(printerQueue);
        }
    }
}
