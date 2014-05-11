package org.noisesmith.noisespew;

import org.codehaus.jackson.map.ObjectMapper;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

class Preset {
    static ObjectMapper json = new ObjectMapper();
    public static Command[] load ( InputStream in )
        throws java.io.IOException {
        Command[] commands = json.readValue(in, Command[].class);
        return commands;
    }
    public static Command[] load ( File in )
        throws java.io.FileNotFoundException,
               java.io.IOException {
        return load(new FileInputStream(in));
    }
    public static Command[] load ( URL in )
    throws java.io.IOException {
        return load(in.openStream());
    }
    public static Command[] load ( String in )
        throws java.io.IOException,
               java.io.FileNotFoundException {
        Command[] commands = new Command[0];
        try {
            commands = load(new File(in));
        } catch (Exception e) {
            try {
                commands = load(new URL(in));
            } catch (Exception e2) {
                    byte[] bytes = in.getBytes("UTF-8");
                    InputStream bais = new ByteArrayInputStream(bytes);
                    commands = load(bais);
            }
        }
        return commands;
    }
    public static void store ( Command[] commands, OutputStream destination )
        throws java.io.IOException {
        json.writeValue(destination, commands);
    }
    public static void store ( Command[] commands, File destination )
        throws java.io.IOException,
               java.io.FileNotFoundException {
        store(commands, new FileOutputStream(destination));
    }
    public static void store ( Command[] commands, String destination )
        throws java.io.IOException,
               java.io.FileNotFoundException {
        store(commands, new File(destination));
    }
    public static String store ( Command[] commands ) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            store(commands, baos);
            byte[] data = baos.toByteArray();
            return new String(data, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}

