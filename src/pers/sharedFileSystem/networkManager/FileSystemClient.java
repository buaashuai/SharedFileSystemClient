package pers.sharedFileSystem.networkManager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;

import pers.sharedFileSystem.communicationObject.*;
import pers.sharedFileSystem.configManager.Config;
import pers.sharedFileSystem.entity.*;
import pers.sharedFileSystem.logManager.LogRecord;

/**
 * 应用程序端的文件系统与服务端的文件系统之间的socket长连接池，以及信息交互方法类
 *
 * @author buaashuai
 */
public class FileSystemClient {

    /**
     * 客户端和冗余验证服务器之间的socket连接
     */
    private static  Socket socket;
    private static SystemConfig sysConf=Config.SYSTEMCONFIG;
    private static Hashtable<String, ServerNode> fileConfig=Config.getConfig();
    /**
     * 客户端和存储服务器之间的socket连接
     */
    private static ConcurrentHashMap<String,Socket>storeSockets=new ConcurrentHashMap<String,Socket>();

    /**
     * 重新建立客户端和冗余验证服务器之间的socket连接
     */
    public static void restartConnectToRedundancyServer(){
        try {
            LogRecord.RunningErrorLogger.error("attempt to reconnect to redundancy server.");
            socket = new Socket(sysConf.Ip, sysConf.Port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 重新建立客户端和某个存储服务器之间的socket连接
     */
    public static void restartConnectToStoreServer(String serverNodeId){
        try {
            ServerNode serverNode=fileConfig.get(serverNodeId);
            LogRecord.RunningErrorLogger.error("attempt to reconnect to store server. [ "+serverNode.Ip+" : "+serverNode.Port+" ]");
            Socket so = new Socket(serverNode.Ip, serverNode.Port);
            storeSockets.put(serverNodeId,so);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static {
        try {
            boolean isRudundancyStarted=false;
            // 如果某个ServerNode（存储服务器）节点中的某个目录需要进行文件去冗，就建立客户端和冗余验证服务器之间的长连接
            //同时需要建立客户端和该ServerNode（存储服务器）之间的长连接
            for(ServerNode sn:fileConfig.values()) {
                if(sn.ServerRedundancy.Switch) {
                    if (!isRudundancyStarted) {
                        socket = new Socket(sysConf.Ip, sysConf.Port);
                        KeepAliveWatchRedundancy k1 = new KeepAliveWatchRedundancy();
                        Thread t1 = new Thread(k1);
                        t1.start();
                        LogRecord.RunningInfoLogger.info("connect to RudundancyServer successful. [ "+sysConf.Ip+" : "+sysConf.Port+" ]");
                        isRudundancyStarted = true;
                    }
                    Socket sk=new Socket(sn.Ip, sn.ServerPort);
                    KeepAliveWatchStore ks=new KeepAliveWatchStore(sn.Id);
                    Thread t2 = new Thread(ks);
                    t2.start();
                    //保存该长连接
                    storeSockets.put(sn.Id,sk);
                    LogRecord.RunningInfoLogger.info("connect to StoreServer successful. [ "+sn.Ip+" : "+sn.ServerPort+" ]");
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            LogRecord.RunningErrorLogger.error(e.toString());
        }
    }

    /**
     * 将一个消息对象发送给冗余验证服务器
     *
     */
    public static void sendMessageToRedundancyServer(MessageProtocol mes) throws IOException {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(mes);
            oos.flush();
    }
    /**
     * 将一个消息对象发送给某个存储服务器
     *@param serverNodeId 存储服务器编号
     *@param  mes 消息对象
     */
    public static void sendMessageToStoreServer(String serverNodeId,MessageProtocol mes) throws IOException {
        Socket so=storeSockets.get(serverNodeId);
        ObjectOutputStream oos = new ObjectOutputStream(so.getOutputStream());
        oos.writeObject(mes);
        oos.flush();
    }
    /**
     * 解析冗余验证服务器发来的消息（冗余验证请求回复消息）
     * @param replyMessage
     * @return
     */
    private static Feedback parseReplyFromRedundancyServer(MessageProtocol replyMessage){
        Feedback feedback = null;
        switch (replyMessage.messageCode){
            case 4000:{
                feedback = new Feedback(3000 ,"");
                //并返回指纹信息
                if(replyMessage.messageType==MessageType.REPLY_CHECK_REDUNDANCY) {
                    FingerprintInfo fInfo=(FingerprintInfo)replyMessage.content;
                    feedback.addFeedbackInfo("FingerprintInfo",fInfo);
                }
                return feedback;
            }
            case 4001:{
                feedback = new Feedback(3010 ,"");
                return feedback;
            }
            case 4002:{
                feedback = new Feedback(3015 ,"");
                return feedback;
            }
            default:{
                return feedback;
            }
        }
    }
    /**
     * 解析存储服务器发来的消息（冗余验证请求回复消息）
     * @param replyMessage
     * @return
     */
    private static Feedback parseReplyFromStoreServer(MessageProtocol replyMessage){
        Feedback feedback = null;
        switch (replyMessage.messageCode){
            case 4000:{
                feedback = new Feedback(3000 ,"");
                if(replyMessage.messageType==MessageType.REPLY_GET_REDUNDANCY_INFO) {
                    ArrayList<FingerprintInfo>fingerprintInfos=(ArrayList<FingerprintInfo>)replyMessage.content;
                    feedback.addFeedbackInfo("otherPath",fingerprintInfos);
                }
                if(replyMessage.messageType==MessageType.REPLY_VALIDATE_FILENAMES) {
                    ArrayList<FingerprintInfo>fingerprintInfos=(ArrayList<FingerprintInfo>)replyMessage.content;
                    feedback.addFeedbackInfo("validateFiles",fingerprintInfos);
                }
                return feedback;
            }
            case 4003:{
                feedback = new Feedback(3019 ,"");
                return feedback;
            }
            case 4004:{
                feedback = new Feedback(3020 ,"");
                return feedback;
            }
            case 4005:{
                feedback = new Feedback(3021 ,"");
                return feedback;
            }
            case 4006:{
                feedback = new Feedback(3023 ,"");
                return feedback;
            }
            case 4007:{
                feedback = new Feedback(3024 ,"");
                return feedback;
            }
            case 4008:{
                feedback = new Feedback(3025 ,"");
                return feedback;
            }
            case 4009:{
                feedback = new Feedback(3026 ,"");
                return feedback;
            }
            default:{
                return feedback;
            }
        }
    }
    /**
     * 解析收到的消息
     * @param replyMessage
     * @return
     */
    private static Feedback parseMessage(MessageProtocol replyMessage){
        switch (replyMessage.messageType){
            case REPLY_CHECK_REDUNDANCY:{
                return parseReplyFromRedundancyServer(replyMessage);
            }
            case REPLY_ADD_FINGERPRINT:{
                return parseReplyFromRedundancyServer(replyMessage);
            }
            case REPLY_ADD_REDUNDANCY_INFO:{
                return parseReplyFromStoreServer(replyMessage);
            }
            case REPLY_DELETE_REDUNDANCY_INFO:{
                return parseReplyFromStoreServer(replyMessage);
            }
            case REPLY_ADD_FINGERPRINTINFO:{
                return parseReplyFromStoreServer(replyMessage);
            }
            case REPLY_DELETE_FINGERPRINTINFO:{
                return parseReplyFromStoreServer(replyMessage);
            }
            case REPLY_ADD_FREQUENCY:{
                return parseReplyFromStoreServer(replyMessage);
            }
            case REPLY_DELETE_FREQUENCY:{
                return parseReplyFromStoreServer(replyMessage);
            }
            case REPLY_GET_REDUNDANCY_INFO:{
                return parseReplyFromStoreServer(replyMessage);
            }
            case REPLY_VALIDATE_FILENAMES:{
                return parseReplyFromStoreServer(replyMessage);
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
    public static Feedback isFileExistInBloomFilter(FingerprintInfo figurePrint) {
        Feedback feedback = null;
        try {
            MessageProtocol queryMessage = new MessageProtocol();
            queryMessage.messageType = MessageType.CHECK_REDUNDANCY;
            queryMessage.content=figurePrint;
            sendMessageToRedundancyServer(queryMessage);
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            MessageProtocol replyMessage = (MessageProtocol) ois.readObject();
            if (replyMessage != null) {
                return parseMessage(replyMessage);
            }
        } catch (Exception e) {
            LogRecord.RunningErrorLogger.error(e.toString());
        }
        feedback = new Feedback(3001 ,"");
        return feedback;
    }
    /**
     * 向存储服务器发送获取某个路径下的冗余文件信息指令
     * @param serverNodeId
     *            存储服务器编号
     * @param essentialStorePath 节点相对路径
     */
    public static Feedback sendGetRedundancyInfo(String serverNodeId, String essentialStorePath){
        Feedback feedback = null;
        try {
            MessageProtocol queryMessage = new MessageProtocol();
            queryMessage.messageType = MessageType.GET_REDUNDANCY_INFO;
            queryMessage.content=essentialStorePath;
            sendMessageToStoreServer(serverNodeId,queryMessage);
            Socket so=storeSockets.get(serverNodeId);
            ObjectInputStream ois = new ObjectInputStream(so.getInputStream());
            MessageProtocol replyMessage = (MessageProtocol) ois.readObject();
            if (replyMessage != null) {
                return parseMessage(replyMessage);
            }
        } catch (Exception e) {
            LogRecord.RunningErrorLogger.error(e.toString());
        }
        feedback = new Feedback(3001 ,"");
        return feedback;
    }
    /**
     * 向存储服务器发送验证文件名集合是否有效指令
     * @param serverNodeId
     *            存储服务器编号
     */
    public static Feedback sendValidateFileNames(String serverNodeId,  ArrayList<FingerprintInfo>fingerprintInfos){
        Feedback feedback = null;
        try {
            MessageProtocol queryMessage = new MessageProtocol();
            queryMessage.messageType = MessageType.VALIDATE_FILENAMES;
            queryMessage.content=fingerprintInfos;
            sendMessageToStoreServer(serverNodeId,queryMessage);
            Socket so=storeSockets.get(serverNodeId);
            ObjectInputStream ois = new ObjectInputStream(so.getInputStream());
            MessageProtocol replyMessage = (MessageProtocol) ois.readObject();
            if (replyMessage != null) {
                return parseMessage(replyMessage);
            }
        } catch (Exception e) {
            LogRecord.RunningErrorLogger.error(e.toString());
        }
        feedback = new Feedback(3001 ,"");
        return feedback;
    }
    /**
     * 向存储服务器发送添加冗余文件映射信息指令
     * @param serverNodeId
     *            存储服务器编号
     * @param redundancyFileStoreInfo 冗余文件信息
     */
    public static Feedback sendAddRedundancyFileStoreInfoMessage(String serverNodeId, RedundancyFileStoreInfo redundancyFileStoreInfo){
        Feedback feedback = null;
        try {
            MessageProtocol queryMessage = new MessageProtocol();
            queryMessage.messageType = MessageType.ADD_REDUNDANCY_INFO;
            queryMessage.content=redundancyFileStoreInfo;
            sendMessageToStoreServer(serverNodeId,queryMessage);
            Socket so=storeSockets.get(serverNodeId);
            ObjectInputStream ois = new ObjectInputStream(so.getInputStream());
            MessageProtocol replyMessage = (MessageProtocol) ois.readObject();
            if (replyMessage != null) {
                return parseMessage(replyMessage);
            }
        } catch (Exception e) {
            LogRecord.RunningErrorLogger.error(e.toString());
        }
        feedback = new Feedback(3001 ,"");
        return feedback;
    }
    /**
     * 向存储服务器发送删除冗余文件映射信息指令
     * @param serverNodeId
     *            存储服务器编号
     * @param redundancyFileStoreInfo 冗余文件信息
     */
    public static Feedback sendDeleteRedundancyFileStoreInfoMessage(String serverNodeId, RedundancyFileStoreInfo redundancyFileStoreInfo){
        Feedback feedback = null;
        try {
            MessageProtocol queryMessage = new MessageProtocol();
            queryMessage.messageType = MessageType.DELETE_REDUNDANCY_INFO;
            queryMessage.content=redundancyFileStoreInfo;
            sendMessageToStoreServer(serverNodeId,queryMessage);
            Socket so=storeSockets.get(serverNodeId);
            ObjectInputStream ois = new ObjectInputStream(so.getInputStream());
            MessageProtocol replyMessage = (MessageProtocol) ois.readObject();
            if (replyMessage != null) {
                return parseMessage(replyMessage);
            }
        } catch (Exception e) {
            LogRecord.RunningErrorLogger.error(e.toString());
        }
        feedback = new Feedback(3001 ,"");
        return feedback;
    }
    /**
     * 向存储服务器发送添加文件引用频率指令
     * @param serverNodeId
     *            存储服务器编号
     * @param fingerprintInfo 文件相关信息
     */
    public static Feedback sendAddFrequencyMessage(String serverNodeId, FingerprintInfo fingerprintInfo){
        Feedback feedback = null;
        try {
            MessageProtocol queryMessage = new MessageProtocol();
            queryMessage.messageType = MessageType.ADD_FREQUENCY;
            queryMessage.content=fingerprintInfo;
            sendMessageToStoreServer(serverNodeId,queryMessage);
            Socket so=storeSockets.get(serverNodeId);
            ObjectInputStream ois = new ObjectInputStream(so.getInputStream());
            MessageProtocol replyMessage = (MessageProtocol) ois.readObject();
            if (replyMessage != null) {
                return parseMessage(replyMessage);
            }
        } catch (Exception e) {
            LogRecord.RunningErrorLogger.error(e.toString());
        }
        feedback = new Feedback(3001 ,"");
        return feedback;
    }
    /**
     * 向存储服务器发送删除文件引用频率指令
     * @param serverNodeId
     *            存储服务器编号
     * @param fingerprintInfo 文件相关信息
     */
    public static Feedback sendDeleteFrequencyMessage(String serverNodeId, FingerprintInfo fingerprintInfo){
        Feedback feedback = null;
        try {
            MessageProtocol queryMessage = new MessageProtocol();
            queryMessage.messageType = MessageType.DELETE_FREQUENCY;
            queryMessage.content=fingerprintInfo;
            sendMessageToStoreServer(serverNodeId,queryMessage);
            Socket so=storeSockets.get(serverNodeId);
            ObjectInputStream ois = new ObjectInputStream(so.getInputStream());
            MessageProtocol replyMessage = (MessageProtocol) ois.readObject();
            if (replyMessage != null) {
                return parseMessage(replyMessage);
            }
        } catch (Exception e) {
            LogRecord.RunningErrorLogger.error(e.toString());
        }
        feedback = new Feedback(3001 ,"");
        return feedback;
    }
    /**
     * 向存储服务器发送添加指纹信息指令
     * @param serverNodeId
     *            存储服务器编号
     * @param fingerprintInfo 指纹信息
     */
    public static Feedback sendAddFingerprintInfoMessage(String serverNodeId, FingerprintInfo fingerprintInfo){
        Feedback feedback = null;
        try {
            MessageProtocol queryMessage = new MessageProtocol();
            queryMessage.messageType = MessageType.ADD_FINGERPRINTINFO;
            queryMessage.content=fingerprintInfo;
            sendMessageToStoreServer(serverNodeId,queryMessage);
            Socket so=storeSockets.get(serverNodeId);
            ObjectInputStream ois = new ObjectInputStream(so.getInputStream());
            MessageProtocol replyMessage = (MessageProtocol) ois.readObject();
            if (replyMessage != null) {
                return parseMessage(replyMessage);
            }
        } catch (Exception e) {
            LogRecord.RunningErrorLogger.error(e.toString());
        }
        feedback = new Feedback(3001 ,"");
        return feedback;
    }
    /**
     * 向存储服务器发送删除指纹信息指令
     * @param serverNodeId
     *            存储服务器编号
     * @param fingerprintInfo 指纹信息
     */
    public static Feedback sendDeleteFingerprintInfoMessage(String serverNodeId, FingerprintInfo fingerprintInfo){
        Feedback feedback = null;
        try {
            MessageProtocol queryMessage = new MessageProtocol();
            queryMessage.messageType = MessageType.DELETE_FINGERPRINTINFO;
            queryMessage.content=fingerprintInfo;
            sendMessageToStoreServer(serverNodeId,queryMessage);
            Socket so=storeSockets.get(serverNodeId);
            ObjectInputStream ois = new ObjectInputStream(so.getInputStream());
            MessageProtocol replyMessage = (MessageProtocol) ois.readObject();
            if (replyMessage != null) {
                return parseMessage(replyMessage);
            }
        } catch (Exception e) {
            LogRecord.RunningErrorLogger.error(e.toString());
        }
        feedback = new Feedback(3001 ,"");
        return feedback;
    }
    /**
     * 给冗余验证服务端发送指纹置位命令
     *
     * @param figurePrint
     *            文件指纹信息
     */
    public static Feedback sendAddFigurePrintMessage(FingerprintInfo figurePrint) {
        Feedback feedback = null;
        try {
            MessageProtocol queryMessage = new MessageProtocol();
            queryMessage.messageType = MessageType.ADD_FINGERPRINT;
            queryMessage.content=figurePrint;
            sendMessageToRedundancyServer(queryMessage);
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            MessageProtocol replyMessage = (MessageProtocol) ois.readObject();
            if (replyMessage != null) {
                return parseMessage(replyMessage);
            }
        } catch (Exception e) {
            LogRecord.RunningErrorLogger.error(e.toString());
        }
        feedback = new Feedback(3001 ,"");
        return feedback;
    }

}
