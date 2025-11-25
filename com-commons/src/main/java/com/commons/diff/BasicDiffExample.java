package com.commons.diff;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;

import java.util.Arrays;
import java.util.List;

public class BasicDiffExample {
    public static void main(String[] args) {
        String original = "Hello World\nJava Diff Utils\nExample Text";
        String revised = "Hello World\nJava Diff Utils\nUpdated Example Text";
        
        // 将文本按行分割
        List<String> originalLines = Arrays.asList(original.split("\n"));
        List<String> revisedLines = Arrays.asList(revised.split("\n"));
        
        // 计算差异
        Patch<String> patch = DiffUtils.diff(originalLines, revisedLines);
        
        // 输出差异
        for (AbstractDelta<String> delta : patch.getDeltas()) {
            System.out.println("Delta Type: " + delta.getType());
            System.out.println("Original Position: " + delta.getSource().getPosition());
            System.out.println("Original Lines: " + delta.getSource().getLines());
            System.out.println("Revised Position: " + delta.getTarget().getPosition());
            System.out.println("Revised Lines: " + delta.getTarget().getLines());
            System.out.println("---");
        }
    }
}