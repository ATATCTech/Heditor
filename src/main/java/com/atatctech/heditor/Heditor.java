package com.atatctech.heditor;

import com.atatctech.heditor.pattern.PatternExtractor;
import com.atatctech.heditor.pattern.Type;
import com.atatctech.hephaestus.component.*;
import com.atatctech.hephaestus.export.fs.ComponentFolder;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Heditor {
    public static final PatternExtractor JAVA = (skeleton, context, type) -> {
        Skeleton currentBranch = skeleton;
        int classDeclarationStartsAt = -1;
        String javadocBuffer = null;
        int endOfJavadoc = -1;
        for (int i = 0; i < context.length(); i++) {
            char c = context.charAt(i);
            if (context.startsWith("class", i)) {
                i += 5;
                classDeclarationStartsAt = i;
            }
            else if (context.startsWith("/**", i)) {
                endOfJavadoc = context.indexOf("*/", i + 3);
                if (endOfJavadoc < 0) break;
                javadocBuffer = context.substring(i + 3, endOfJavadoc);
                i = endOfJavadoc;
            }
            else if (c == '{') {
                if (javadocBuffer != null) {
                    Text.IndexPair indexPair;
                    if (classDeclarationStartsAt > 0) {
                        indexPair = Utils.getClassName(context, classDeclarationStartsAt);
                        if (indexPair == null) break;
                        classDeclarationStartsAt = -1;
                    } else {
                        indexPair = Utils.getMethodName(context, endOfJavadoc, i);
                        if (indexPair == null) continue;
                    }
                    Skeleton newSkeleton = new Skeleton(context.substring(indexPair.start(), indexPair.end()));
                    newSkeleton.setComponent(Utils.text2component(new Text(javadocBuffer), type));
                    currentBranch.appendChild(newSkeleton);
                    currentBranch = newSkeleton;
                    javadocBuffer = null;
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
            default -> throw new RuntimeException("Unexpected type \"" + language + "\".");
        }
        switch (action) {
            case "extract" -> {
                Skeleton skeleton = Utils.extract(file, patternExtractor, Type.MARKDOWN);
                new ComponentFolder(skeleton).write("C:\\Users\\futer\\Downloads\\Hephaestus");
                System.out.println(skeleton);
            }
            case "inject" -> throw new UnsupportedOperationException("Injection not supported yet.");
        }
    }
}
