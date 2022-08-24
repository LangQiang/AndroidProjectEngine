package com.lazylite.mod.utils.psrc;

public class NullInfo implements Nullable{

    @Override
    public String getLcn() {
        return "";
    }

    @Override
    public String getPsrc() {
        return "";
    }

    @Override
    public int getLastNodePos() {
        return PsrcOptional.DEFAULT_POSITION;
    }

}
