package com.atatctech.heditor;

import com.atatctech.heditor.pattern.PatternExtractor;
import com.atatctech.heditor.pattern.Type;
import com.atatctech.hephaestus.component.*;
import com.atatctech.hephaestus.export.fs.ComponentFolder;

import java.io.File;
import java.io.IOException;

public class Heditor {
    public static final PatternExtractor JAVA = (skeleton, context, language) -> {
        int end = 0;
        for (; ; ) {
            int start = context.indexOf("/**", end);
            if (start < 0) return skeleton;
            end = context.indexOf("*/", start);
            int parenthesesStart = context.indexOf('(', end);
            int assigmentStart = context.indexOf('=', end);
            if (parenthesesStart < 0) parenthesesStart = context.length();
            if (assigmentStart < 0) assigmentStart = context.length();
            Skeleton target = new Skeleton(
                    parenthesesStart < assigmentStart ?
                            Utils.getTargetName(context.substring(end, parenthesesStart)) + context.substring(parenthesesStart, context.indexOf(')', parenthesesStart) + 1) :
                            Utils.getTargetName(context.substring(end, assigmentStart))
            );
            target.setComponent(Utils.text2component(new Text(context.substring(start + 3, end)), language));
            skeleton.appendChild(target);
        }
    };

    public static final PatternExtractor PYTHON = null;

    public static void main(String[] args) throws IOException {
        int lengthOfArgs = args.length;
        if (lengthOfArgs < 2) throw new IllegalArgumentException("Must have at least 3 arguments.");
        String action = args[0].toLowerCase();
        String language = args[1].toLowerCase();
        File file = new File(args[lengthOfArgs - 1]);
        PatternExtractor patternExtractor;
        switch (language) {
            case "java" -> patternExtractor = JAVA;
            case "python" -> patternExtractor = PYTHON;
            default -> throw new RuntimeException("Unexpected language \"" + language + "\".");
        }
        switch (action) {
            case "extract" -> {
                Skeleton skeleton = Utils.extract(file, patternExtractor, Type.MARKDOWN);
                new ComponentFolder(skeleton).write("F:\\Hephaestus");
                System.out.println(skeleton);
            }
            case "inject" -> throw new UnsupportedOperationException("Injection not supported yet.");
        }
    }
}
