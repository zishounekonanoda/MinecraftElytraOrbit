package org.neko.elytratrail2;


import java.util.Locale;


public enum EmitWhen {
    FLYING, GLIDING, FALLING, ALL;


    public static EmitWhen fromString(String s) {
        try {
            return valueOf(s.toUpperCase(Locale.ROOT));
        } catch (Exception e) {
            return ALL;
        }
    }
}