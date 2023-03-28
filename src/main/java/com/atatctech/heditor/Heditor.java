package com.atatctech.heditor;

import com.atatctech.heditor.pattern.PatternExtractor;
import com.atatctech.heditor.pattern.Type;
import com.atatctech.hephaestus.component.*;
import com.atatctech.hephaestus.export.fs.ComponentFolder;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Heditor {
    public static final PatternExtractor JAVA_LEGACY = (skeleton, context, language) -> {
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
                            Utils.getTargetNameBackward(context.substring(end, parenthesesStart)) + context.substring(parenthesesStart, context.indexOf(')', parenthesesStart) + 1) :
                            Utils.getTargetNameBackward(context.substring(end, assigmentStart))
            );
            target.setComponent(Utils.text2component(new Text(context.substring(start + 3, end)), language));
            skeleton.appendChild(target);
        }
    };

    // Todo
    public static final PatternExtractor JAVA = (skeleton, context, language) -> {
        Skeleton currentBranch = skeleton;
        int classDeclarationStartsAt = -1;
        String javadocBuffer = null;
        for (int i = 0; i < context.length(); i++) {
            char c = context.charAt(i);
            if (context.startsWith("class", i)) classDeclarationStartsAt = i + 5;
            else if (context.startsWith("/**", i)) {
                int end = context.indexOf("*/", i + 3);
                if (end < 0) break;
                javadocBuffer = context.substring(i + 3, end);
            }
            else if (c == '{') {
                javadocBuffer = null;
                if (classDeclarationStartsAt > 0) {
                    Text.IndexPair indexPair = Utils.getTargetName(context, classDeclarationStartsAt);
                    if (indexPair == null) break;
                    i += indexPair.end();
                    Skeleton newSkeleton = new Skeleton(context.substring(indexPair.start(), indexPair.end()));
                    currentBranch.appendChild(newSkeleton);
                    currentBranch = skeleton;
                }
            } else if (c == '}') {
                currentBranch = Objects.requireNonNullElse(currentBranch.getParent(), currentBranch);
            }
        }
        return skeleton;
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
