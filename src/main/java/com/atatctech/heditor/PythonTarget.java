package com.atatctech.heditor;

import com.atatctech.hephaestus.component.Skeleton;
import com.atatctech.hephaestus.export.fs.Transform;

@Transform.RequireTransform
public class PythonTarget extends Skeleton {
    public static final Transform TRANSFORM;

    static {
        TRANSFORM = Skeleton.TRANSFORM;
    }

    protected int indentation = 0;

    public PythonTarget() {
    }

    public PythonTarget(Skeleton skeleton) {
        setId(skeleton.getId());
        setName(skeleton.getName());
        setComponent(skeleton.getComponent());
        setParent(skeleton.getParent());
        setChildren(skeleton.getChildren());
        setStyle(skeleton.getStyle());
    }

    public PythonTarget(String name) {
        setName(name);
    }

    public void setIndentation(int indentation) {
        this.indentation = indentation;
    }

    public int getIndentation() {
        return indentation;
    }

    @Override
    public PythonTarget getParent() {
        return (PythonTarget) super.getParent();
    }
}
