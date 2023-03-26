package com.atatctech.heditor;

import com.atatctech.heditor.pattern.Language;
import com.atatctech.heditor.pattern.PatternExtractor;
import com.atatctech.hephaestus.component.*;

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
            Skeleton target = new Skeleton(Utils.getTargetName(context.substring(end, parenthesesStart)) + context.substring(parenthesesStart, context.indexOf(')', parenthesesStart) + 1));
            target.setComponent(Utils.text2component(new Text(context.substring(start + 3, end)), language));
            skeleton.appendChild(target);
        }
    };

    public static void main(String[] args) throws IOException {
        int lengthOfArgs = args.length;
        if (lengthOfArgs < 1) throw new RuntimeException("Must have at least one argument.");
        File file = new File(args[lengthOfArgs - 1]);
        Skeleton skeleton = Utils.read2component(file, JAVA, Language.MARKDOWN);
        System.out.println(skeleton);
    }
}
