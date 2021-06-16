package utils;

import java.awt.*;

public class Print {
    public static Color ColorToPrint=Color.black;

    public static void error(String text){
        ColorToPrint=Color.red;
        System.out.println(text);
        ColorToPrint=Color.black;
    }

    public static void info(String text){
        ColorToPrint=Color.blue;
        System.out.println(text);
        ColorToPrint=Color.black;
    }

    public static void strong(String text){
        ColorToPrint=new Color(19,123,22);
        System.out.println(text);
        ColorToPrint=Color.black;
    }

    public static void log(String text){
        ColorToPrint=Color.black;
        System.out.println(text);
    }
}
