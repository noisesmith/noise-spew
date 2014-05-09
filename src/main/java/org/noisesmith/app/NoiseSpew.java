package org.noisesmith.app;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Hashtable;
import java.util.Arrays;
import java.util.function.BiConsumer;

class BiMap<V> {
    // for now specializing on V <-> int
    public Hashtable<Integer,V> store;
    public Hashtable<V,Integer> values;
    public int index;
    public BiMap ( V[] initVals ) {
        this(new ArrayList<V> (Arrays.asList(initVals)));
    }
    public BiMap ( ArrayList<V> initVals ) {
        store = new Hashtable<Integer,V>();
        values = new Hashtable<V,Integer>();
        initVals.forEach((v) -> put(v));
    }
    public Integer put( V v ) {
        int at;
        if (values.containsKey(v)) {
            return values.get(v);
        } else {
            at = index++;
            store.put(at, v);
            values.put(v, at);
            return at;
        }
    }
    public V get( Integer i ) {
        return store.get(i);
    }
    public BiConsumer<Integer,V> reindex = (i, s) -> values.put(s, i);
    public BiConsumer<V,Integer> indexre = (s, i) -> store.put(i, s);
    public void forEach(BiConsumer<Integer,V> action) {
        store.forEach(action);
        values.clear();
        store.forEach(reindex);
        store.clear();
        values.forEach(indexre);
    }
}

class Loop {
    public String source;
    public Clip clip;
    public double start;
    public double end;
    public Loop (String input)
        throws LineUnavailableException,
               UnsupportedAudioFileException,
               IOException {
        source = input;
        File sourcefile = new File(source);
        clip = AudioSystem.getClip();
        AudioInputStream instream = AudioSystem.getAudioInputStream(sourcefile);
        clip.open(instream);
        start = 0.0;
        end = (clip.getMicrosecondLength() / 1e+6) -1;
    }
    public void start () {
        if (clip.isRunning()) {
            clip.stop();
        }
        double tt = clip.getMicrosecondLength() / 1e+6;
        long tf = clip.getFrameLength();
        double sr = tf / tt;
        int i = (int) Math.floor(sr * start);
        int o = (int) Math.floor(sr * end);
        if (i >= 0 && i < o && o < tf) {
            clip.setLoopPoints(i, o);
            if (clip.getFramePosition() < i || clip.getFramePosition() > o) {
                clip.setFramePosition(i);
            }
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } else {
            System.out.println("bad loop: " + start + '-' + end +
                               " [" + tt + ']');
        }
    }
    public void stop () {
        clip.stop();
    }
    public void toggle () {
        if (clip.isRunning()) {
            stop();
        } else {
            start();
        }
    }
}

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
                                       ArrayList<Loop> loops ) {
        parse(System.in, resources, loops);
    }
    private static char getCommand(Scanner in, char defaultc) {
        try {
            return in.next().charAt(0);
        } catch (Exception e) {
            return defaultc;
        }
    }
    private static int getIdx(Scanner in, int length) {
        int idx;
        char command = getCommand(in, '0');
        idx = command - '0';
        idx = (idx < 0) ? 0 : idx;
        idx = (idx >= length) ? length - 1 : idx;
        return idx;
    }
    public static void parse( InputStream stream,
                              BiMap<String> resources,
                              ArrayList<Loop> loops ) {
        Scanner in = new Scanner(stream);
        char command;
        String line;
        String msg;
        Loop loop;
        BiMap<Loop> loopmap;
        int idx;
        do {
            System.out.print("noise spew> ");
            command = getCommand(in, 'e');
            System.out.println();
            switch (command) {
            case 'e': System.exit(0);
                break;
            case 'l': // list resources, clips
                line = in.nextLine();
                resources.forEach((k, v) -> System.out.println(k + " : " + v));
                System.out.println("---");
                loopmap = new BiMap<Loop> (loops);
                loopmap.forEach((i, l) -> {
                        String mesg;
                        mesg = i + " - " + (l.clip.isRunning() ? "on" : "off");
                        mesg += " " + l.start + "->" + l.end;
                        mesg +=  "	" + l.source;
                        System.out.println(mesg);
                    });
                break;
            case 'p': // toggle playback
                idx = getIdx(in, loops.size());
                loops.get(idx).toggle();
                break;
            case 'x': // set loop points
                idx = getIdx(in, loops.size());
                double i = in.nextDouble();
                double o = in.nextDouble();
                loop = loops.get(idx);
                loop.start = i;
                loop.end = o;
                loop.start();
                break;
            case 's': // add source
                line = in.nextLine().substring(1);
                resources.put(line);
                break;
            case 'a': // add loop
                try {
                    idx = in.nextInt();
                    line = resources.get(idx);
                    loops.add(new Loop(line));
                } catch (Exception e) {
                    System.out.println("could not load loop: " +
                                       e.getMessage());
                }
                break;
            default:
                line = in.nextLine();
                System.out.println("invalid command: " + command + line);
            }
        } while (command != 0);
    }
}
