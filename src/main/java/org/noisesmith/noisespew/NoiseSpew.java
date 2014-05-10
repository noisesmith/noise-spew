package org.noisesmith.noisespew;

import javax.sound.sampled.Clip;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Arrays;

class NoiseSpew {
    public static void main( String[] args ) {
        try {
            System.out.println( "starting noise spew:" );
            if (args.length < 1) {
                System.out.println("must specify a file");
                System.exit(2);
            }
            ArrayList<Loop> loops = new ArrayList<Loop>();
            Arrays.asList(args)
                .forEach((a) -> {
                        try {
                            System.out.println("loading " + a);
                            loops.add(new Loop(a));
                        } catch (Exception e) {
                            System.err.println("loading loop" + e.getMessage());
                            e.printStackTrace();
                        }
                    });
            BiMap<String> sources = new BiMap<String>(args);
            runInteractive(sources, loops);
        } catch (Exception e) {
            System.err.println("in noise-spew " + e.getMessage());
            e.printStackTrace();
        }
    }
    public static void runInteractive( BiMap<String> resources,
                                       ArrayList<Loop> loops )
    throws java.io.UnsupportedEncodingException {
        parse(System.in, resources, loops);
    }
    public static void docommand( BiMap<String> resources,
                                  ArrayList<Loop> loops,
                                  Parsed parsed) {
        int idx;
        Loop loop;
        Object[] args = parsed.args;
        switch (parsed.action) {
        case EXIT:
            System.exit(0);
            break;
        case LIST:
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
            break;
        case PLAYTOGGLE:
            idx = (int) args[0];
            loops.get(idx).toggle();
            break;
        case LOOPPOINTS:
            idx = (int) args[0];
            if (loops.size() > idx) {
                loop = loops.get(idx);
                loop.start = (double) args[1];
                loop.end = (double) args[2];
                loop.start();
            } else {
                System.out.println("could not reloop " + idx);
            }
            break;
        case ADDSOURCE:
            resources.put((String) args[0]);
            break;
        case ADDLOOP:
            try {
                idx = (int) args[0];
                loops.add(new Loop(resources.get(idx)));
            } catch (Exception e) {
                System.out.println("could not load loop: " + args);
            }
            break;
        case DELETESOURCE:
            idx = (int) args[0];
            if(resources.containsKey(idx)) {
                resources.remove(idx);
            } else {
                System.out.println("could not delete " + idx);
            }
            break;
        case DELETELOOP:
            idx = (int) args[0];
            if(loops.size() > idx) {
                loops.get(idx).stop();
                loops.remove(idx);
            } else {
                System.out.println("could not delete " + idx);
            }
            break;
        case NULL:
            System.out.println("failed to parse command");
        default:
            System.out.println("invalid command: " +
                               parsed.action + " " +
                               parsed.args);
        }
    }
    public static void parse( InputStream stream,
                              BiMap<String> resources,
                              ArrayList<Loop> loops )
    throws java.io.UnsupportedEncodingException {
        InputStreamReader source = new InputStreamReader(stream, "UTF-8");
        BufferedReader in = new BufferedReader(source);
        System.out.print("noise spew> ");
        in.lines().forEachOrdered((s) -> {
                String[] input = s.split(" ");
                Parsed parsed = CommandParser.parse(input);
                docommand(resources, loops, parsed);
                System.out.println();
                System.out.print("noise spew> ");
            });
    }
}
