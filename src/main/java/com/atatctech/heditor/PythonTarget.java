package com.atatctech.heditor;

import com.atatctech.hephaestus.component.Skeleton;

public class PythonTarget extends Skeleton {
    protected int indentation = 0;

    public PythonTarget(String name, int indentation) {
        setName(name);
        setIndentation(indentation);
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
