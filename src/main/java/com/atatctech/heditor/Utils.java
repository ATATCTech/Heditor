package com.atatctech.heditor;

import com.atatctech.heditor.pattern.Type;
import com.atatctech.heditor.pattern.PatternExtractor;
import com.atatctech.hephaestus.component.*;
import com.atatctech.packages.basics.Basics;

import java.io.File;
import java.io.IOException;

public class Utils {
    public static Component text2component(Text text, Type type) {
        return switch (type) {
            case PLAIN_TEXT -> text;
            case MARKDOWN -> new MDBlock(text);
            case HTML -> new HTMLBlock(text);
        };
    }

    public static Text.IndexPair getMethodName(String context, int fromIndex, int toIndex) {
        int end = 0;
        boolean triggered = false;
        for (int i = toIndex; i > fromIndex; i--) {
            char c = context.charAt(i - 1);
            if (triggered) {
                if (Character.isLetterOrDigit(c)) {
                    if (end < 1) end += i;
                }
                else if (end >= 1) return new Text.IndexPair(i, end);
            } else if (c == '(') triggered = true;
        }
        return null;
    }

    public static Text.IndexPair getClassName(String context, int fromIndex) {
        int start = -1;
        for (int i = fromIndex; i < context.length(); i++) {
            char c = context.charAt(i);
            if (Character.isLetterOrDigit(c)) {
                if (start < 0) start = i;
            }
            else if (start >= 0) return new Text.IndexPair(start, i);
        }
        return null;
    }

    public static Skeleton extract(File file, PatternExtractor patternExtractor, Type type) throws IOException {
        if (file.isDirectory()) {
            Skeleton skeleton = new Skeleton(file.getName());
            File[] children = file.listFiles();
            if (children != null) for (File subFile : children) skeleton.appendChild(extract(subFile, patternExtractor, type));
            return skeleton;
        }
        String content = Basics.NativeHandler.readFile(file);
        return patternExtractor.extract(new Skeleton(file.getName()), content, type);
    }
}
