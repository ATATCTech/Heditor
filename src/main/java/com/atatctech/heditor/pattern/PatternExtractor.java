package com.atatctech.heditor.pattern;

import com.atatctech.hephaestus.component.*;

@FunctionalInterface
public interface PatternExtractor {
    Skeleton extract(Skeleton skeleton, String context, Language language);
}
