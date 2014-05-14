package org.noisesmith.noisespew;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.Clip;
import java.io.IOException;
import java.io.File;

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
        clip = AudioSystem.getClip();
        File sourcefile = new File(source);
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

