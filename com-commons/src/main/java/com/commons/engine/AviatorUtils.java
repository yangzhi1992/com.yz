package com.commons.engine;

import com.googlecode.aviator.AviatorEvaluator;

/**
 * 执行逻辑表达式
 */
public class AviatorUtils {

    public static void main(String[] args) {
        Boolean b = (Boolean)AviatorEvaluator.execute("3>1 && 2!=4 || true");
        System.out.println(b);
    }
}
