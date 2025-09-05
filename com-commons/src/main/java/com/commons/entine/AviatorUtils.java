package com.commons.entine;

import com.googlecode.aviator.AviatorEvaluator;

public class AviatorUtils {

    public static void main(String[] args) {
        Boolean b = (Boolean) AviatorEvaluator.execute("3>1 && 2!=4 || true");
        System.out.println(b);
    }
}
