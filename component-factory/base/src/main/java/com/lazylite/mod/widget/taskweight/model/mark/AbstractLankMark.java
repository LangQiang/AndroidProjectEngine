package com.lazylite.mod.widget.taskweight.model.mark;

public abstract class AbstractLankMark implements ILandMark {

    private int landmarkLength;

    AbstractLankMark(int landmarkLength) {
        this.landmarkLength = landmarkLength;
    }

    @Override
    public int getLandMarkLength() {
        return landmarkLength;
    }
}
