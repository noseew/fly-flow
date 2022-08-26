package org.song.ff.server.consumer.netty;


import java.util.ArrayList;
import java.util.List;

/**
 * 保存所有与server连接的客户端信息
 */
public class ClientInfoHolder {
    public static List<AppInfo> apps = new ArrayList<>();
}