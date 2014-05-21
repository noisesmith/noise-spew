package org.noisesmith.noisespew;

import java.util.function.Function;
import java.util.Hashtable;

public class Registration {
    public String[] invoke;
    public String name;
    public String[] formals;
    public String description;
    Function<String[],Command> parse;
    Function<Hashtable,Command> deserialize;
    Registration(String[] i,
                 String n,
                 String[] a,
                 String d,
                 Function<String[],Command> p,
                 Function<Hashtable,Command> ds) {
        invoke = i;
        name = n;
        formals = a;
        description = d;
        parse = p;
        deserialize = ds;
    }
}
