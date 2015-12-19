package pers.sharedFileSystem.shareInterface;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

import com.sun.corba.se.spi.activation.Server;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import pers.sharedFileSystem.communicationObject.FingerprintInfo;
import pers.sharedFileSystem.configManager.Config;
import pers.sharedFileSystem.convenientUtil.AdvancedFileUtil;
import pers.sharedFileSystem.convenientUtil.CommonFileUtil;
import pers.sharedFileSystem.convenientUtil.CommonUtil;
import pers.sharedFileSystem.entity.DirectoryNode;
import pers.sharedFileSystem.entity.Feedback;
import pers.sharedFileSystem.entity.Node;
import pers.sharedFileSystem.entity.ServerNode;
import pers.sharedFileSystem.exceptionManager.ErrorHandler;
import pers.sharedFileSystem.ftpManager.FTPUtil;
import pers.sharedFileSystem.logManager.LogRecord;
import pers.sharedFileSystem.networkManager.FileSystemClient;

/**
 * 文件目录适配器提供文件目录操作的基本功能
 *
 * @author buaashuai
 */
public class DirectoryAdapter extends Adapter {
    /**
     * 删除本目录
     */
    public JSONObject delete() {
        Feedback feedback = null;
        if (AdvancedFileUtil.delete(this.NODE,
                this.FILEPATH))
            feedback = new Feedback(3000, "");
        else
            feedback = new Feedback(3001, "");
        return feedback.toJsonObject();
    }

    /**
     * @param nodeId 目录节点名称
     * @param parms  节点路径参数
     */
    public DirectoryAdapter(String nodeId, Map<String, String> parms) {
        Node n=Config.getNodeByNodeId(nodeId);
        if(n==null||n instanceof ServerNode) {
            LogRecord.FileHandleErrorLogger.error("["+nodeId+"] is not a DirectoryNode id");
            return;
        }
        DirectoryNode node = (DirectoryNode)n;
        ServerNode rootNode = node.getServerNode();
        // DirectoryAdapter.rootNode = rootNode;
        this.NODEID = nodeId;
        // Node node = rootNode.NodeTable.get(nodeId);
        this.NODE = node;
        // 初始化节点的相对路径
        JSONObject nodePathFeed = CommonFileUtil.initFilePath(nodeId, parms,
                false);
        if (nodePathFeed.getInt("Errorcode") != 3000) {
            System.out.println(ErrorHandler.getErrorInfo(3005, ""));
        }
        JSONArray jsonArray = nodePathFeed.getJSONArray("Info");
        if (jsonArray.size() > 0)
            this.FILEPATH =node.StorePath + jsonArray.getString(0);
        else
            this.FILEPATH = node.StorePath;
    }

    /**
     * 获取该目录下所有的文件相对路径（不包括目录文件）
     */
    public JSONArray getAllFilePaths() {
        ArrayList<FingerprintInfo> files = new ArrayList<FingerprintInfo>();
        ServerNode serverNode = this.NODE.getServerNode();
        String relativePath = this.FILEPATH.substring( this.NODE.StorePath.length());
        if (!CommonUtil.isRemoteServer(serverNode.Ip)) {
            File desFolderPath = new File(this.FILEPATH);
            String[] filelist = null;
            if (desFolderPath.isDirectory()) {
                //首先获取本文件夹里面的文件
                filelist = desFolderPath.list();
                for (int i = 0; i < filelist.length; i++) {
                    File readfile = new File(this.FILEPATH + "/"
                            + filelist[i]);
                    if (readfile.isFile()) {
                        FingerprintInfo fInfo=new FingerprintInfo();
                        fInfo.setFilePath(relativePath + "/");
                        fInfo.setNodeId(this.NODE.Id);
                        fInfo.setFileName(readfile.getName());
                        files.add(fInfo);
                    }
                }
            }
        } else {
            FTPClient ftpClient = FTPUtil.getFTPClientByServerNode(serverNode,
                    false);
            boolean flag = false;
            try {
                flag = ftpClient.changeWorkingDirectory(relativePath);//new String(relativePath.getBytes(), "ISO-8859-1")
                if (flag) {//是文件夹
                    ftpClient.changeToParentDirectory();
                    FTPFile[] ftpFiles = ftpClient.listFiles(relativePath);
                    for (FTPFile f : ftpFiles) {
                        FingerprintInfo fInfo=new FingerprintInfo();
                        fInfo.setFilePath(relativePath + "/");
                        fInfo.setNodeId(this.NODE.Id);
                        fInfo.setFileName(f.getName());
                        files.add(fInfo);
                    }
                }
            } catch (Exception e) {
                LogRecord.FileHandleErrorLogger.error(e.toString());
            }
        }
        //如果是去冗文件夹，还需要获取该文件夹里面存放在其他目录（或者本目录）下的冗余文件
        if(this.NODE.Redundancy.Switch){
            Feedback feedback= FileSystemClient.sendGetRedundancyInfo(serverNode.Id,relativePath+ "/" );
            if(feedback.getErrorcode()==3000) {//表示存在冗余文件
                ArrayList<FingerprintInfo> otherPath = (ArrayList<FingerprintInfo>) feedback.getFeedbackInfo("otherPath");
                files.addAll(otherPath);
            }
        }
        return JSONArray.fromObject(files);
    }

    /**
     * 获取该目录下所有的文件名称
     */
    public ArrayList<String> getAllFileNames() {
        ArrayList<String> files = new ArrayList<String>();
        ServerNode serverNode = this.NODE.getServerNode();
        String relativePath = this.FILEPATH.substring(this.NODE.StorePath.length());
        if (!CommonUtil.isRemoteServer(serverNode.Ip)) {
            File desFolderPath = new File(this.FILEPATH);
            String[] filelist = null;
            if (desFolderPath.isDirectory()) {
                filelist = desFolderPath.list();
                Collections.addAll(files, filelist);
            }
        } else {
            FTPClient ftpClient = FTPUtil.getFTPClientByServerNode(serverNode,
                    false);
            boolean flag = false;
            try {
                flag = ftpClient.changeWorkingDirectory(relativePath);//new String(relativePath.getBytes(), "ISO-8859-1")

                if (flag) {//是文件夹
                    ftpClient.changeToParentDirectory();
                    FTPFile[] ftpFiles = ftpClient.listFiles(relativePath);
                    for (FTPFile f : ftpFiles) {
                        files.add(f.getName());
                    }
                }
            } catch (Exception e) {
                LogRecord.FileHandleErrorLogger.error(e.toString());
            }
        }
        //如果是去冗文件夹，还需要获取该文件夹里面存放在其他目录（或者本目录）下的冗余文件
        if(this.NODE.Redundancy.Switch){
            Feedback feedback= FileSystemClient.sendGetRedundancyInfo(serverNode.Id,relativePath+ "/" );
            if(feedback.getErrorcode()==3000) {//表示存在冗余文件
                ArrayList<FingerprintInfo> otherPath = (ArrayList<FingerprintInfo>) feedback.getFeedbackInfo("otherPath");
                for(FingerprintInfo info:otherPath) {
                    files.add(info.getFileName());
                }
            }
        }
        return files;
    }

    /**
     * 删除本目录下指定的文件
     *
     * @param fileNames 待删除的文件名
     */
    public JSONObject deleteSelective(List<String> fileNames) {
        Feedback feedback = null;
        boolean flag = true;
        Hashtable<String,Boolean> infos=new Hashtable<String,Boolean>();
        int num=0;
        for (String name : fileNames) {
            if (name.equals("Fingerprint.sys")|| !AdvancedFileUtil.delete(this.NODE,
                    this.FILEPATH + "/" + name))
                // 删除失败
                infos.put(name,false);
            else {
                // 删除成功
                num++;
                infos.put(name, true);
            }
        }
        if (num==fileNames.size()) {
            feedback = new Feedback(3000, "");
            feedback.addFeedbackInfo("delete all files successful.");
        } else {
            feedback = new Feedback(3001, "");
            String str="delete file error[ ";
            for (String name : fileNames) {
                str+=name+":"+infos.get(name)+",";
            }
            str+=" ]";
            feedback.addFeedbackInfo(str);
        }
        return feedback.toJsonObject();
    }
}
