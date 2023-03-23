package com.atatctech.heditor;

import com.atatctech.hephaestus.component.Component;
import com.atatctech.packages.basics.Basics;

import java.io.File;
import java.io.IOException;

public class Heditor {
    public Component read2component(File file) throws IOException {
        String content = Basics.NativeHandler.readFile(file);
        return null;
    }

    public static void main(String[] args) {

    }
}
