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
import java.util.function.BiConsumer;

class BiMap<V> {
    // for now specializing on V <-> int
    public Hashtable<Integer,V> store;
    public Hashtable<V,Integer> values;
    public int index;
    public BiMap ( V[] initVals ) {
        store = new Hashtable<Integer,V>();
        values = new Hashtable<V,Integer>();
        for (V v : initVals) {
            put(v);
        }
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
        end = (clip.getMicrosecondLength() / 1e+6) - 1;
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
        if (start >= 0 && start < end && end < tf) {
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
            Loop[] loops = new Loop[args.length];
            for (int i = 0; i < args.length; i++) {
                System.out.println("loading " + args[i]);
                loops[i] = new Loop(args[i]);
            }
            runInteractive(args, loops);
        } catch (Exception e) {
            System.err.println("in noise-spew " + e.getMessage());
            e.printStackTrace();
        }
    }
    public static void runInteractive( String[] resources, Loop[] loops ) {
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
                              String[] iNresources,
                              Loop[] iNloops ) {
        ArrayList<String> resources = new ArrayList<String>();
        for(String s : iNresources) {
            resources.add(s);
        }
        ArrayList<Loop> loops = new ArrayList<Loop>();
        for (Loop l : iNloops) {
            loops.add(l);
        }
        Scanner in = new Scanner(stream);
        char command;
        String line;
        String msg;
        Loop l;
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
                idx = 0;
                for(ListIterator<String> s = resources.listIterator(0);
                    s.hasNext();) {
                    msg = idx + " : " + s.next();
                    System.out.println(msg);
                    idx++;
                }
                System.out.println("---");
                idx = 0;
                for(ListIterator<Loop> li = loops.listIterator(0);
                    li.hasNext();) {
                    l = li.next();
                    msg = l.clip.isRunning() ? "on" : "off";
                    msg = msg + " " + l.start + "->" + l.end;
                    msg  = + idx + " - " + msg + " " + l.source;
                    System.out.println(msg);
                    idx++;
                }
                break;
            case 'p': // toggle playback
                idx = getIdx(in, loops.size());
                loops.get(idx).toggle();
                break;
            case 'x': // set loop points
                idx = getIdx(in, loops.size());
                double i = in.nextDouble();
                double o = in.nextDouble();
                l = loops.get(idx);
                l.start = i;
                l.end = o;
                l.start();
                break;
            default:
                line = in.nextLine();
                System.out.println("invalid command: " + command + line);
            }
        } while (command != 0);
    }
}
