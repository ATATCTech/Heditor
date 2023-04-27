package com.atatctech.heditor.pattern;

import com.atatctech.hephaestus.component.*;

@FunctionalInterface
public interface Extractor {
    Skeleton extract(Skeleton skeleton, String context, Type type, Styler styler);
}
