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
            if (false) {
                ArrayBlockingQueue printerQueue = new ArrayBlockingQueue(1024);
                SynchronousQueue engineMessage = new SynchronousQueue();
                Engine gen = new Engine(engineMessage, printerQueue);
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
                ArrayBlockingQueue printerQueue =
                    new ArrayBlockingQueue<String>(1024);
                Thread printer = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                while (true) {
                                    String message =
                                        (String) printerQueue.take();
                                    System.out.println(message);
                                }
                            } catch (Exception e) {
                                System.out.println("error in printer");
                                e.printStackTrace();
                            }
                        }
                    }, "PRINTER");
                printer.start();
                printerQueue.put( "starting noise spew:" );
                SynchronousQueue engineMessage = new SynchronousQueue();
                ArrayList<Command> commands = new ArrayList<Command>();
                // for(String arg : args) {
                //     Command c = new Command(CommandParser.Action.ADDSOURCE);
                //     c.source = arg;
                //     c.interactive = false;
                //     commands.add(c);
                // }
                runInteractive(commands, printerQueue);
            }
        } catch (Exception e) {
            System.err.println("in noise-spew " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void runInteractive(ArrayList<Command> initial,
                                      ArrayBlockingQueue<String> printerQueue) {
        SynchronousQueue<Command> commandMessage = new SynchronousQueue();
        SynchronousQueue<Command> engineMessage = new SynchronousQueue();
        Engine gen = new Engine(engineMessage, printerQueue);
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

    public static String prompt = "\nnoise spew> ";

    public static void parse( CommandParser parser,
                              InputStream stream,
                              ArrayBlockingQueue<String> printerQueue,
                              SynchronousQueue queue )
        throws java.io.UnsupportedEncodingException {
        InputStreamReader source = new InputStreamReader(stream, "UTF-8");
        BufferedReader in = new BufferedReader(source);
        long stamp = System.currentTimeMillis();
        try {printerQueue.put(prompt);} catch (Exception x) {}
        in.lines().forEachOrdered(s -> {
                try {
                    String[] input = s.split(" ");
                    Command parsed = parser.parse(input);
                    parsed.moment = System.currentTimeMillis() - stamp;
                    parsed.interactive = true;
                    queue.put(parsed);
                } catch (Exception e) {
                    if (s.matches("^\\s+$")) {
                        try { printerQueue.put(prompt); } catch (Exception x) {}
                    } else {
                        try {
                            printerQueue.put("error in parsing " + s);
                            printerQueue.put(prompt);
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
        String output;
        while (true) {
            Command parsed = queue.take();
            if(parsed != null) {
                environment.commands.add(parsed);
                output = parsed.execute(environment);
                if(output != null) printerQueue.put(output);
                gen.messages.put(parsed);
            }
            printerQueue.put(prompt);
        }
    }
}
