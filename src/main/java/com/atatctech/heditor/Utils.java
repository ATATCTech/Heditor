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

    public static String getTargetName(String context) {
        int end = 0;
        for (int i = context.length(); i > 0; i--) {
            char c = context.charAt(i - 1);
            if (end < 1) {
                if (Character.isLetterOrDigit(c)) end += i;
            }
            else if (c == ' ') return context.substring(i, end);
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
