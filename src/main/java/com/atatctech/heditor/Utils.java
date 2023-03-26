package com.atatctech.heditor;

import com.atatctech.heditor.pattern.Language;
import com.atatctech.heditor.pattern.PatternExtractor;
import com.atatctech.hephaestus.component.*;
import com.atatctech.packages.basics.Basics;

import java.io.File;
import java.io.IOException;

public class Utils {
    public static Component text2component(Text text, Language language) {
        return switch (language) {
            case PLAIN_TEXT -> text;
            case MARKDOWN -> new MDBlock(text);
            case HTML -> new HTMLBlock(text);
        };
    }

    public static String getTargetName(String context) {
        int end = -1;
        for (int i = context.length(); i > 0; i--) {
            char c = context.charAt(i - 1);
            if (end < 0) {
                if (Character.isLetterOrDigit(c)) end += i;
            }
            else if (c == ' ') return context.substring(i, end);
        }
        return null;
    }

    public static Skeleton read2component(File file, PatternExtractor patternExtractor, Language language) throws IOException {
        if (file.isDirectory()) {
            Skeleton skeleton = new Skeleton(file.getName());
            File[] children = file.listFiles();
            if (children != null) for (File subFile : children) skeleton.appendChild(read2component(subFile, patternExtractor, language));
            return skeleton;
        }
        String content = Basics.NativeHandler.readFile(file);
        return patternExtractor.extract(new Skeleton(file.getName()), content, language);
    }
}
