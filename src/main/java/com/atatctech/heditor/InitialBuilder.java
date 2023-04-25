package com.atatctech.heditor;

import com.atatctech.hephaestus.component.Component;
import com.atatctech.hephaestus.component.Skeleton;
import com.atatctech.hephaestus.component.WrapperComponent;
import com.atatctech.hephaestus.exception.HephaestusException;
import com.atatctech.hephaestus.export.fs.ComponentFile;
import com.atatctech.hephaestus.export.fs.ComponentFolder;
import com.atatctech.hephaestus.export.fs.Transform;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class InitialBuilder extends ComponentFolder {
    public InitialBuilder(@NotNull WrapperComponent component) {
        super(component);
    }

    public static @NotNull InitialBuilder read(@NotNull File dir, @NotNull String dirPath) throws IOException, HephaestusException, ClassNotFoundException {
        WrapperComponent wrapperComponent = new Skeleton();
        File[] files = dir.listFiles();
        if (files == null) throw new FileNotFoundException("Expecting at least one file under `" + dirPath + "`.");
        for (File file : files) {
            if (file.isDirectory()) {
                WrapperComponent component = InitialBuilder.read(file).component();
                Transform transform = Transform.getTransform(component.getClass());
                wrapperComponent.appendChild((transform == null ? new Transform() : transform).handleFilename(file.getName(), component));
            } else {
                Component component = ComponentFile.read(file).component();
                Skeleton skeleton = new Skeleton(file.getName());
                skeleton.setComponent(component);
                wrapperComponent.appendChild(skeleton);
            }
        }
        Transform transform = Transform.getTransform(wrapperComponent.getClass());
        return new InitialBuilder((transform == null ? new Transform() : transform).handleFilename(dir.getName(), wrapperComponent));
    }

    public static @NotNull InitialBuilder read(@NotNull File dir) throws HephaestusException, IOException, ClassNotFoundException {
        return read(dir, dir.getAbsolutePath());
    }
}
