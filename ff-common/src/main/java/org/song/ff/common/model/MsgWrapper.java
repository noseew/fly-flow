package org.song.ff.common.model;

public class MsgWrapper {
    private int msgType;
    private String clientIp;
    private String clientName;
    private byte[] data;

    public MsgWrapper() {
    }

    public MsgWrapper(int msgType, String clientIp, String clientName) {
        this.msgType = msgType;
        this.clientIp = clientIp;
        this.clientName = clientName;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
