package com.atatctech.heditor;

import com.atatctech.hephaestus.component.Skeleton;

public class PythonTarget extends Skeleton {
    protected int indentation = 0;

    public PythonTarget() {}

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
