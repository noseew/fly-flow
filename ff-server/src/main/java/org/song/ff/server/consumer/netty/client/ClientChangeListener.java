package org.song.ff.server.consumer.netty.client;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.song.ff.common.utils.NettyIpUtil;
import org.song.ff.server.consumer.netty.AppInfo;
import org.song.ff.server.consumer.netty.ClientInfoHolder;

/**
 * 对客户端的管理，新来、断线的管理
 */
public class ClientChangeListener implements IClientChangeListener {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private static final String NEW_CLIENT = "监听到事件";
    private static final String NEW_CLIENT_JOIN = "new client join";
    private static final String CLIENT_LOSE = "client removed ";

    /**
     * 客户端新增
     */
    @Override
    public synchronized void newClient(String appName, String ip, ChannelHandlerContext ctx) {
        logger.info(NEW_CLIENT);

        boolean appExist = false;
        for (AppInfo appInfo : ClientInfoHolder.apps) {
            if (appName.equals(appInfo.getAppName())) {
                appExist = true;
                appInfo.add(ctx);
                break;
            }
        }
        if (!appExist) {
            AppInfo appInfo = new AppInfo(appName);
            ClientInfoHolder.apps.add(appInfo);
            appInfo.add(ctx);
        }

        logger.info(NEW_CLIENT_JOIN);
    }

    @Override
    public synchronized void loseClient(ChannelHandlerContext ctx) {
        for (AppInfo appInfo : ClientInfoHolder.apps) {
            appInfo.remove(ctx);
        }
        logger.info(CLIENT_LOSE + NettyIpUtil.clientIp(ctx));
    }
}
