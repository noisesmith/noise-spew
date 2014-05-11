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
                                  Command parsed) {
        Loop loop;
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
            if (loops.size() > parsed.index) {
                loops.get(parsed.index).toggle();
            } else {
                System.out.println("could not toggle playback of " +
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
                System.out.println("could not load loop: " + parsed.index);
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
        case NULL:
            System.out.println("failed to parse command");
        default:
            System.out.println("invalid command: " + parsed.action);
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
                Command parsed = CommandParser.parse(input);
                docommand(resources, loops, parsed);
                System.out.println();
                System.out.print("noise spew> ");
            });
    }
}
