package pers.sharedFileSystem.networkManager;

import pers.sharedFileSystem.communicationObject.MessageProtocol;
import pers.sharedFileSystem.communicationObject.MessageType;
import pers.sharedFileSystem.logManager.LogRecord;

import java.io.IOException;

/**
 * 客户端和存储服务器之间的长连接维护线程
 *
 */
public class KeepAliveWatchStore implements Runnable {
	private long checkDelay = 500;//线程多久循环一次
	private long keepAliveDelay = 3000;//握手协议发送时间间隔
	private boolean run;// 线程是否运行
	private long lastSendTime;//上次发送时间
	private String serverNodeId;//存储服务器id

	public KeepAliveWatchStore(String serverNodeId) {
		lastSendTime=System.currentTimeMillis();
		this.run = true;
		this.serverNodeId=serverNodeId;
	}

	/**
	 * 终止线程
	 */
	public void stop() {
		run = false;
	}

	public void run() {
		while (run) {
			// 长时间未给服务端发送数据，维持长连接
			if (System.currentTimeMillis() - lastSendTime> keepAliveDelay) {// 长连接维护
				try {
					MessageProtocol queryMessage = new MessageProtocol();
					queryMessage.messageType = MessageType.KEEP_ALIVE;
					FileSystemClient.sendMessageToStoreServer(serverNodeId,queryMessage);
					lastSendTime=System.currentTimeMillis();
					LogRecord.RunningInfoLogger.info("send handshake");
				} catch (IOException e) {
					e.printStackTrace();
					FileSystemClient.restartConnectToStoreServer(serverNodeId);
					// 发送的处理方法
					LogRecord.RunningErrorLogger.error("net work error, can not connect to storeServer."+e.toString());
				}
			} else {
				try {
					Thread.sleep(checkDelay);
				} catch (InterruptedException e) {
					e.printStackTrace();
					break;
				}
			}
		}
	}
}
