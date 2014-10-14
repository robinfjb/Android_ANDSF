package com.chinamobile.android.connectionmanager.omadm.protocol;

public class CmdId {
	private int value;

    public CmdId(int value) {
        this.value = value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void increment() {
        ++value;
    }

    public int next() {
        increment();
        return getValue();
    }
}
