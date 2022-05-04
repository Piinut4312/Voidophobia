package net.piinut.voidophobia.block.blockEntity;

public interface VuxContainer {

    int getVuxStored();

    int getVuxCapacity();

    int getVuxTransferRate();

    void addVux(int value);

    void removeVux(int value);

    void setVuxStored(int value);

    void setVuxCapacity(int value);

    void setVuxTransferRate(int value);

}
