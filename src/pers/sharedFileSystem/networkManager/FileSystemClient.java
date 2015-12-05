package pers.sharedFileSystem.networkManager;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;

import pers.sharedFileSystem.communicationObject.MessageProtocol;
import pers.sharedFileSystem.configManager.Config;
import pers.sharedFileSystem.entity.ServerNode;
import pers.sharedFileSystem.entity.SystemConfig;
import pers.sharedFileSystem.logManager.LogRecord;

/**
 * 应用程序端的文件系统与服务端的文件系统之间的socket长连接池
 *
 * @author buaashuai
 */
public class FileSystemClient {
    /**
     * 存储服务器和客户端之间的socket连接，ip——> socket
     */
    private static ConcurrentHashMap<String, Socket> SOCKETPOOL;
    /**
     * 客户端和冗余验证服务器之间的socket连接
     */
    private static  Socket socket;
    /**
     * 客户端和冗余验证服务器之间的对象输出流
     */
    private static ObjectOutputStream oos = null;

    /**
     * 获取客户端和冗余验证服务器之间的socket连接
     * @return
     */
    public static Socket getSocket(){
        return socket;
    }
    static {
        SOCKETPOOL = new ConcurrentHashMap<String, Socket>();
        SystemConfig sysConf=Config.SYSTEMCONFIG;
        try {
            socket = new Socket(sysConf.Ip, sysConf.Port);
            KeepAliveWatchDog k1 = new KeepAliveWatchDog();
            Thread t1 = new Thread(k1);
            t1.start();

            Hashtable<String, ServerNode> serverNodes = Config.getConfig();
            for (ServerNode sNode : serverNodes.values()) {
                // if(CommonUtil.isRemoteServer(sNode.Ip)){
                Socket so = new Socket(sNode.Ip, sNode.ServerPort);
                if(so==null){
                    LogRecord.RunningErrorLogger.error("socket initial failed. "+sNode.Ip+" : "+sNode.ServerPort);
                }
                SOCKETPOOL.put(sNode.Id, so);
                // }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            LogRecord.RunningErrorLogger.error(e.toString());
        }
    }

    /**
     * 根据根节点id获取对应的Socket对象
     *
     * @param serverNodeId
     *            根节点ID
     * @return
     */
    public static Socket getSocketByServerNodeId(String serverNodeId) {
        return SOCKETPOOL.get(serverNodeId);
    }
    /**
     * 将一个消息对象发送给冗余验证服务器
     *
     */
    public static void sendMessage(MessageProtocol mes) throws IOException {
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(mes);
            oos.flush();
    }
}
