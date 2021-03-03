package com.kulikov.smart_fin;

public class  MyRoundNumeric {
    public static double roundTo(double value) {
        double scale = Math.pow(10, 2);
        return Math.ceil(value * scale) / scale;
    }
}
