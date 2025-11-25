package com.commons.diff;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.Patch;
import com.github.difflib.patch.PatchFailedException;

import java.util.Arrays;
import java.util.List;

public class UnifiedDiffExample {
    public static void main(String[] args) throws PatchFailedException {
        List<String> original = Arrays.asList(
            "public class Test {",
            "    public void method1() {",
            "        System.out.println(\"Old method\");",
            "    }",
            "}"
        );
        
        List<String> revised = Arrays.asList(
            "public class Test {",
            "    public void method1() {",
            "        System.out.println(\"New method\");",
            "    }",
            "    public void method2() {",
            "        System.out.println(\"Added method\");",
            "    }",
            "}"
        );
        
        Patch<String> patch = DiffUtils.diff(original, revised);
        List<String> unifiedDiff = DiffUtils.unpatch(
                revised,patch
        );
        
        // 输出统一差异格式
        for (String line : unifiedDiff) {
            System.out.println(line);
        }
    }
}