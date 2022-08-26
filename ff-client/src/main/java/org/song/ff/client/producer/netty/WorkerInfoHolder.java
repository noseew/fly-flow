package org.song.ff.client.producer.netty;

import io.netty.channel.Channel;
import org.apache.commons.lang3.StringUtils;
import org.song.ff.client.producer.netty.client.NettyClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 */
public class WorkerInfoHolder {
    /**
     * 保存worker的ip地址和Channel的映射关系，这是有序的。每次client发送消息时，都会根据该map的size进行hash
     * 如key-1就发送到workerHolder的第1个Channel去，key-2就发到第2个Channel去
     */
    private static final List<Server> WORKER_HOLDER = new CopyOnWriteArrayList<>();

    private WorkerInfoHolder() {
    }

    /**
     * 判断某个worker是否已经连接过了
     */
    public static boolean hasConnected(String address) {
        for (Server server : WORKER_HOLDER) {
            if (address.equals(server.address)) {
                return channelIsOk(server.channel);
            }
        }
        return false;
    }

    private static boolean channelIsOk(Channel channel) {
        return channel != null && channel.isActive();
    }

    public static Channel chooseChannel(String key) {
        int size = WORKER_HOLDER.size();
        if (StringUtils.isEmpty(key) || size == 0) {
            return null;
        }
        int index = Math.abs(key.hashCode() % size);

        return WORKER_HOLDER.get(index).channel;
    }

    /**
     * 监听到worker信息变化后
     * 将新的worker信息和当前的进行合并，并且连接新的address
     * address例子：10.12.139.152:11111
     */
    public static void mergeAndConnectNew(List<String> allAddresses) {
        removeNoneUsed(allAddresses);

        //去连接那些在etcd里有，但是list里没有的
        List<String> needConnectWorkers = newWorkers(allAddresses);
        if (needConnectWorkers.size() == 0) {
            return;
        }
        //再连接，连上后，value就有值了
        NettyClient.getInstance().connect(needConnectWorkers);
        Collections.sort(WORKER_HOLDER);
    }

    /**
     * 增加一个新的worker
     */
    public synchronized static void put(String address, Channel channel) {
        Iterator<Server> it = WORKER_HOLDER.iterator();
        boolean exist = false;
        while (it.hasNext()) {
            Server server = it.next();
            if (address.equals(server.address)) {
                server.channel = channel;
                exist = true;
                break;
            }
        }
        if (!exist) {
            Server server = new Server();
            server.address = address;
            server.channel = channel;
            WORKER_HOLDER.add(server);
        }
    }

    /**
     * 根据传过来的所有的worker地址，返回当前尚未连接的新的worker地址集合，用以创建新连接
     */
    private static List<String> newWorkers(List<String> allAddresses) {
        Set<String> set = new HashSet<>(WORKER_HOLDER.size());
        for (Server server : WORKER_HOLDER) {
            set.add(server.address);
        }

        List<String> list = new ArrayList<>();
        for (String s : allAddresses) {
            if (!set.contains(s)) {
                list.add(s);
            }
        }
        return list;
    }

    /**
     * 移除那些在最新的worker地址集里没有的那些
     */
    private static void removeNoneUsed(List<String> allAddresses) {
        for (Server server : WORKER_HOLDER) {
            boolean exist = false;
            //判断现在的worker里是否存在，如果当前的不存在，则删掉
            String nowAddress = server.address;
            for (String address : allAddresses) {
                if (address.equals(nowAddress)) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                //如果最新地址集里已经没了，就把他关闭掉
                if (server.channel != null) {
                    server.channel.close();
                }
                WORKER_HOLDER.remove(server);
            }
        }
    }

    private static class Server implements Comparable<Server> {
        private String address;
        private Channel channel;


        @Override
        public int compareTo(Server o) {
            //按address排序
            return this.address.compareTo(o.address);
        }

        @Override
        public String toString() {
            return "Server{" +
                    "address='" + address + '\'' +
                    ", channel=" + channel +
                    '}';
        }
    }
}
