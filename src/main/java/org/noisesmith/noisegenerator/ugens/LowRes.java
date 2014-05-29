/*
  The DSP code in this file is derived from a function in csound
  http://www.csounds.com/

  original code is from lowpassr.c, Copyright (C) 1998 Gabriel Maldonado

 */

package org.noisesmith.noisegenerator.ugens;

import org.noisesmith.noisegenerator.UGen;
import java.util.Arrays;

public class LowRes extends Am {
    LowPass filterLeft;
    LowPass filterRight;
    double center, resonance;
    double[] left, right;

    @Override
    public double setAmp(double value) {
        double old = amp;
        amp = value;
        return old;
    }

    public double setCenter(double value) {
        double old = center;
        center = value;
        return old;
    }

    public double setResonance(double value) {
        double old = resonance;
        resonance = value;
        return old;
    }

    public LowRes(UGen input, double c, double res) {
        super(input, input);
        filterLeft = new LowPass();
        filterRight = new LowPass();
        center = c;
        resonance = res;
        amp = 1.0;
        active = true;
    }

    public String statusString() {
        if (a != null)
            return "LowRes " + (active ? "on" : "off") +
                " (" + amp + ")" +
                " center: " + center + " resonance: " + resonance +
                "\n\tsource: " + a.statusString();
        else
            return "LowRes (inactive)";
    }

    class LowPass {
        double ynm1, ynm2, prevFco, prevRes, k, coef1, coef2, b;

        LowPass() {
            ynm1 = 0.0;
            ynm2 = 0.0;
            prevFco = 0.0;
            prevRes = 0.0;
            k = 0.0;
            coef1 = 0.0;
            coef2 = 0.0;
            b = 0.0;
        }

        double[] calc(double fco, double res, double[] sig) {
            double yn = 0.0;
            double[] result;
            int nsmps = sig.length;
            if (prevFco != fco || prevRes != res) { // only if changed
                b = 10.0 / (res * Math.sqrt(fco)) - 1.0;
                k = 1000.0 / fco;
                coef1 = (b + 2.0*k);
                coef2 = 1.0/(1.0 + b + k);
                prevFco = fco;
                prevRes = res;
            }
            result = new double[nsmps];
            for (int n = 0; n < nsmps; n++) {
                yn = (coef1 * ynm1 - k * ynm2 + sig[n]) * coef2;
                result[n] = yn;
                ynm2 = ynm1;
                ynm1 = yn;
            }
            return result;
        }
    }

    public double[] gen( int size, long index ) {
        double[] output = out(size*2);
        if (index == produced)
            return output;
        produced = index;
        if (a != null) {
            double[] asig = a.gen(size, index);
            if (left == null || size > left.length) {
                left = new double[size];
                right = new double[size];
            }
            for(int i = 0; i < size; i++) {
                left[i] = asig[i*2];
                right[i] = asig[i*2+1];
            }
            double[] l = filterLeft.calc(center, resonance, left);
            double[] r = filterRight.calc(center, resonance, right);
            for(int i = 0; i < size; i++) {
                output[i*2] = l[i]*amp;
                output[i*2+1] = r[i]*amp;
            }
        } else {
            Arrays.fill(output, 0.0);
        }
        return output;
    }
}
