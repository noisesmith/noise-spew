package org.noisesmith.app;

public class NoiseSpew
{
    public static void main( String[] args )
    {
        System.out.println( "noise spew:" );
        for (int i=0; i < args.length; i++) {
            System.out.println(args[i]);
        }
    }
}
