/*
  The DSP code in this file is derived from a function in csound
  http://www.csounds.com/

  original code is from lowpassr.c, Copyright (C) 1998 Gabriel Maldonado

 */

package org.noisesmith.noisegenerator.ugens;

import org.noisesmith.noisegenerator.UGen;
import org.noisesmith.noisegenerator.Output;
import java.util.Arrays;

public class LowRes extends Am {
    LowPass filter;
    double center, resonance;
    double[] output;

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

    public LowRes(Output input, double c, double res) {
        super(input, input);
        filter = new LowPass();
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

        double[] calc(double fco, double res, double[] sig, double amp) {
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
                result[n] = yn * amp;
                ynm2 = ynm1;
                ynm1 = yn;
            }
            return result;
        }
    }

    public double[] gen( String which, int size, long index ) {
        if (which != "out") return null;
        output = out(size);
        if (index == produced)
            return output;
        produced = index;
        if (a != null) {
            double[] asig = a.gen(size, index);
            output = filter.calc(center, resonance, asig, amp);
        } else {
            Arrays.fill(output, 0.0);
        }
        return output;
    }
}
