package com.atatctech.heditor;

import com.atatctech.hephaestus.component.HTMLBlock;
import com.atatctech.hephaestus.component.MDBlock;
import org.jetbrains.annotations.NotNull;

public class MarkdownBlock extends MDBlock {
    public @NotNull HTMLBlock toHTML() {
        return new HTMLBlock();
    }
}
