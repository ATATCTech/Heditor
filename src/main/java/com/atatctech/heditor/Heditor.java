package com.atatctech.heditor;

import com.atatctech.heditor.pattern.PatternExtractor;
import com.atatctech.heditor.pattern.Type;
import com.atatctech.hephaestus.component.*;
import com.atatctech.hephaestus.export.fs.ComponentFolder;
import com.atatctech.packages.basics.Basics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
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

    public static void main(String[] args) {
        Logger logger = LogManager.getLogger();
        try {
            int lengthOfArgs = args.length;
            if (lengthOfArgs < 2) throw new IllegalArgumentException("Must have at least 3 arguments.");
            File file = new File(args[lengthOfArgs - 1]);
            String language = args[1].toLowerCase();
            PatternExtractor patternExtractor;
            Type type = Type.MARKDOWN;
            String outputDir = null;
            File wrapperFile = null;
            switch (language) {
                case "java" -> patternExtractor = JAVA;
                case "python" -> patternExtractor = PYTHON;
                default -> throw new IllegalArgumentException("Unexpected language: \"" + language + "\".");
            }
            for (int i = 2; i < lengthOfArgs; i++) {
                String arg = args[i];
                if (arg.startsWith("--type=")) {
                    String typeString = arg.substring(7);
                    type = switch (typeString) {
                        case "md" -> Type.MARKDOWN;
                        case "html" -> Type.HTML;
                        case "p" -> Type.PLAIN_TEXT;
                        default -> throw new IllegalArgumentException("Unknown type: \"" + typeString + "\".");
                    };
                } else if (arg.startsWith("--wrapperfile=")) {
                    wrapperFile = new File(arg.substring(14));
                } else {
                    outputDir = arg;
                }
            }
            switch (args[0].toLowerCase()) {
                case "extract" -> {
                    Skeleton skeleton = Utils.extract(file, patternExtractor, type);
                    if (outputDir == null) logger.info(skeleton);
                    else if (outputDir.endsWith(".expr")) Basics.NativeHandler.writeFile(outputDir, skeleton.expr());
                    else
                        (wrapperFile == null ? new ComponentFolder(skeleton) : new ComponentFolder(skeleton, wrapperFile)).write("C:\\Users\\futer\\Downloads\\Hephaestus");
                }
                case "inject" -> throw new UnsupportedOperationException("Injection not supported yet.");
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }
}
