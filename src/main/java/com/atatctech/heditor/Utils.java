package com.atatctech.heditor;

import com.atatctech.heditor.pattern.Styler;
import com.atatctech.heditor.pattern.Type;
import com.atatctech.heditor.pattern.CommentExtractor;
import com.atatctech.hephaestus.component.*;
import com.atatctech.packages.basics.Basics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

public final class Utils {
    public static Component text2component(Text text, @NotNull Type type) {
        return switch (type) {
            case PLAIN_TEXT -> text;
            case MARKDOWN -> new MDBlock(text);
            case HTML -> new HTMLBlock(text);
        };
    }

    public static @NotNull String removeRedundantCharacter(@NotNull String s, char c) {
        StringBuilder r = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char chr = s.charAt(i);
            if (chr != c) {
                if (Character.isLetterOrDigit(chr)) return r + s.substring(i);
                r.append(chr);
            }
        }
        return r.toString();
    }

    public static @NotNull String removeRedundantCharacterByLines(@NotNull String s, char c) {
        String lineSeparator = System.lineSeparator();
        String[] lines = s.split(lineSeparator);
        StringBuilder r = new StringBuilder();
        for (String line : lines) r.append(removeRedundantCharacter(line, c)).append(lineSeparator);
        return r.toString();
    }

    public static @NotNull String removeRedundantCharactersByLines(@NotNull String s, char... cs) {
        for (char c : cs) s = removeRedundantCharacterByLines(s, c);
        return s;
    }

    static Text.@Nullable IndexPair getTargetName(String context, int fromIndex, int toIndex, char trigger) {
        int end = 0;
        boolean triggered = false;
        for (int i = toIndex; i > fromIndex; i--) {
            char c = context.charAt(i - 1);
            if (triggered) {
                if (Character.isLetterOrDigit(c)) {
                    if (end < 1) end += i;
                }
                else if (end >= 1) return new Text.IndexPair(i, end);
            } else if (c == trigger) triggered = true;
        }
        return null;
    }

    public static Text.IndexPair getMethodName(String context, int fromIndex, int toIndex) {
        return getTargetName(context, fromIndex, toIndex, '(');
    }

    public static Text.IndexPair getFieldName(String context, int fromIndex, int toIndex) {
        return getTargetName(context, fromIndex, toIndex, '=');
    }

    public static Text.@Nullable IndexPair getClassName(String context, int fromIndex, int toIndex) {
        int start = -1;
        for (int i = fromIndex; i < toIndex; i++) {
            char c = context.charAt(i);
            if (Character.isLetterOrDigit(c)) {
                if (start < 0) start = i;
            }
            else if (start >= 0) return new Text.IndexPair(start, i);
        }
        return null;
    }

    public static Text.IndexPair getClassName(String context, int fromIndex) {
        return getClassName(context, fromIndex, context.length());
    }

    public static Skeleton extract(@NotNull File file, CommentExtractor commentExtractor, Type type, Styler styler) throws IOException {
        if (file.isDirectory()) {
            Skeleton skeleton = new Skeleton(file.getName());
            File[] children = file.listFiles();
            if (children != null) for (File subFile : children) skeleton.appendChild(extract(subFile, commentExtractor, type, styler));
            return skeleton;
        }
        String content = Basics.NativeHandler.readFile(file);
        return commentExtractor.extract(new Skeleton(file.getName()), content, type, styler);
    }
}
