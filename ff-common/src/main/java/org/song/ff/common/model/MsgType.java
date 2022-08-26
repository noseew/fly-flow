package org.song.ff.common.model;

public enum MsgType {
    ping(0),
    pong(1),
    data(3),
    ;
    private int type;

    MsgType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
