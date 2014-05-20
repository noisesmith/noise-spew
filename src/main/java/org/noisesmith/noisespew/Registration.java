package org.noisesmith.noisespew;

import java.util.function.Function;

public class Registration {
    public String[] invoke;
    public String name;
    public String[] formals;
    public String description;
    public Function<String[], Command> fun;
    Registration(String[] i,
                 String n,
                 String[] a,
                 String d,
                 Function<String[], Command> f) {
        invoke = i;
        name = n;
        formals = a;
        description = d;
        fun = f;
    }
}
