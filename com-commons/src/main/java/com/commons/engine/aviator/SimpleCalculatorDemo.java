package com.commons.engine.aviator;

import com.googlecode.aviator.AviatorEvaluator;
import java.util.Scanner;

public class SimpleCalculatorDemo {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("请输入一个计算表达式（例如：3 + 5 * (2 + 1)）：");
        String expression = scanner.nextLine();

        try {
            // 动态求解用户输入的表达式
            Object result = AviatorEvaluator.execute(expression);
            System.out.println("计算结果: " + result);
        } catch (Exception e) {
            System.out.println("无效的表达式，请重新输入!");
        }

        scanner.close();
    }
}
