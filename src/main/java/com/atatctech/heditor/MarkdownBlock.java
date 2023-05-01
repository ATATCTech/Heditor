package com.atatctech.heditor;

import com.atatctech.hephaestus.component.HTMLBlock;
import com.atatctech.hephaestus.component.MDBlock;
import com.atatctech.hephaestus.component.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MarkdownBlock extends MDBlock {
    public MarkdownBlock(@Nullable Text markdown) {
        super(markdown);
    }

    public @NotNull HTMLBlock toHTML() {
        return new HTMLBlock(null);
    }
}
