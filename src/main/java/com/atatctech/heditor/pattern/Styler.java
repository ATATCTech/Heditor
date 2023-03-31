package com.atatctech.heditor.pattern;

import com.atatctech.hephaestus.component.Skeleton;
import com.atatctech.hephaestus.component.Text;

public class Styler {
    public Text transform(Skeleton currentContainer, String comment, Type type) {
        return new Text(comment);
    }
}
