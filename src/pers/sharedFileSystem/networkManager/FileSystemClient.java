package pers.sharedFileSystem.networkManager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.json.JSONObject;
import pers.sharedFileSystem.communicationObject.FingerprintInfo;
import pers.sharedFileSystem.communicationObject.MessageProtocol;
import pers.sharedFileSystem.communicationObject.MessageType;
import pers.sharedFileSystem.configManager.Config;
import pers.sharedFileSystem.entity.*;
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
    private static SystemConfig sysConf=Config.SYSTEMCONFIG;
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

    /**
     * 重连冗余验证服务器
     */
    public static void startReconnectServer(){
        try {
            LogRecord.RunningErrorLogger.error("attempt to reconnect to redundancy server.");
            socket = new Socket(sysConf.Ip, sysConf.Port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static {
        SOCKETPOOL = new ConcurrentHashMap<String, Socket>();
        try {
            socket = new Socket(sysConf.Ip, sysConf.Port);
            KeepAliveWatchDog k1 = new KeepAliveWatchDog();
            Thread t1 = new Thread(k1);
            t1.start();

//            Hashtable<String, ServerNode> serverNodes = Config.getConfig();
//            for (ServerNode sNode : serverNodes.values()) {
//                // if(CommonUtil.isRemoteServer(sNode.Ip)){
//                Socket so = new Socket(sNode.Ip, sNode.ServerPort);
//                if(so==null){
//                    LogRecord.RunningErrorLogger.error("socket initial failed. "+sNode.Ip+" : "+sNode.ServerPort);
//                }
//                SOCKETPOOL.put(sNode.Id, so);
//                // }
//            }
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
    public static void sendMessageToRedundancyServer(MessageProtocol mes) throws IOException {
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(mes);
            oos.flush();
    }

    /**
     * 解析冗余验证服务器发来的消息（冗余验证请求回复消息）
     * @param replyMessage
     * @return
     */
    private static JSONObject parseReplyCheckRedundancy(MessageProtocol replyMessage){
        Feedback feedback = null;
        switch (replyMessage.messageCode){
            case 4000:{
                feedback = new Feedback(3000 ,"");
                //并返回指纹信息
//                feedback.addFeedbackInfo(replyMessage.content.get("filePath"));
                return feedback.toJsonObject();
            }
            case 4001:{
                feedback = new Feedback(3010 ,"");
                return feedback.toJsonObject();
            }
            case 4002:{
                feedback = new Feedback(3015 ,"");
                return feedback.toJsonObject();
            }
            default:{
                return feedback.toJsonObject();
            }
        }
    }
    /**
     * 解析冗余验证服务器发来的消息
     * @param replyMessage
     * @return
     */
    private static JSONObject parseMessageFromRedundancy(MessageProtocol replyMessage){
        switch (replyMessage.messageType){
            case REPLY_CHECK_REDUNDANCY:{
                return parseReplyCheckRedundancy(replyMessage);
            }
            case REPLY_ADD_FINGERPRINT:{
                return parseReplyCheckRedundancy(replyMessage);
            }
            default:{
                return null;
            }
        }
    }
    /**
     * 根据指纹信息验证文件是否存在，向冗余验证服务器发送文件指纹验证指令
     *
     * @param figurePrint
     *            文件指纹信息
     * @return 文件存在返回文件绝对地址 ，否则返回false
     */
    public static JSONObject isFileExistInBloomFilter(FingerprintInfo figurePrint) {
        Feedback feedback = null;
        try {
            MessageProtocol queryMessage = new MessageProtocol();
            queryMessage.messageType = MessageType.CHECK_REDUNDANCY;
            queryMessage.content=figurePrint;
            sendMessageToRedundancyServer(queryMessage);
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            MessageProtocol replyMessage = (MessageProtocol) ois.readObject();
            if (replyMessage != null) {
                return parseMessageFromRedundancy(replyMessage);
            }
        } catch (Exception e) {
            LogRecord.RunningErrorLogger.error(e.toString());
        }
        feedback = new Feedback(3001 ,"");
        return feedback.toJsonObject();
    }
    /**
     * 向存储服务器发送添加映射信息指令
     * @param desNodeId
     *            目的节点id
     * @param keyPath 节目相对路径（相对该节点的根存储根路径）
     * @param otherFilePath 该目录节点下的存储在其他文件夹里面的冗余文件的相对路径
     */
    public static JSONObject sendRedundancyFileStoreInfoMessage(String desNodeId, String keyPath, String otherFilePath){
        Feedback feedback = null;
//        try {
//            Node node = Config.getNodeByNodeId(desNodeId);
////			Socket socket = FileSystemClient.getSocketByServerNodeId(node
////					.getServerNode().Id);
//            Socket socket=new Socket(node.getServerNode().Ip, node.getServerNode().ServerPort);
//            if(socket==null){
//                feedback = new Feedback(3001 ,"");
//                LogRecord.RunningErrorLogger.error("socket not initial ["+node.getServerNode().Ip+"]");
//                return feedback.toJsonObject();
//            }
//            MessageProtocol queryMessage = new MessageProtocol();
//            queryMessage.messageType = MessageType.ADD_REDUNDANCY_INFO;
//            queryMessage.content.put("desNodeId",desNodeId);
//            queryMessage.content.put("keyPath",keyPath);
//            queryMessage.content.put("otherFilePath",otherFilePath);
//            ObjectOutputStream oos = new ObjectOutputStream(
//                    socket.getOutputStream());
//            oos.writeObject(queryMessage);
//            ObjectInputStream ois = new ObjectInputStream(
//                    socket.getInputStream());
//            MessageProtocol replyMessage = (MessageProtocol) ois.readObject();
//            if (replyMessage != null
//                    && replyMessage.messageType == MessageType.REPLY_ADD_REDUNDANCY_INFO) {
//                if (replyMessage.content.get("messageCode").equals("4002")) {
//                    feedback = new Feedback(3010 ,"");
//                    return feedback.toJsonObject();
//                }else if (replyMessage.content.get("messageCode").equals("4000")){
//                    feedback = new Feedback(3000 ,"");
//                    //并返回指纹信息
//                    feedback.addFeedbackInfo(replyMessage.content.get("filePath"));
//                    return feedback.toJsonObject();
//                }else if(replyMessage.content.get("messageCode").equals("4003")){
//                    feedback = new Feedback(3015 ,"");
//                    return feedback.toJsonObject();
//                }
//            }
//        } catch (Exception e) {
//            LogRecord.RunningErrorLogger.error(e.toString());
//            feedback = new Feedback(3001 ,e.toString());
//            return feedback.toJsonObject();
//        }
        feedback = new Feedback(3001 ,"");
        return feedback.toJsonObject();
    }
    /**
     * 给服务端的文件系统发送指纹置位命令
     *
     * @param figurePrint
     *            文件指纹信息
     */
    public static JSONObject sendAddFigurePrintMessage(FingerprintInfo figurePrint) {
        Feedback feedback = null;
        try {
            MessageProtocol queryMessage = new MessageProtocol();
            queryMessage.messageType = MessageType.ADD_FINGERPRINT;
            queryMessage.content=figurePrint;
            FileSystemClient.sendMessageToRedundancyServer(queryMessage);
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            MessageProtocol replyMessage = (MessageProtocol) ois.readObject();
            if (replyMessage != null) {
                return parseMessageFromRedundancy(replyMessage);
            }
        } catch (Exception e) {
            LogRecord.RunningErrorLogger.error(e.toString());
        }
        feedback = new Feedback(3001 ,"");
        return feedback.toJsonObject();
    }

}
