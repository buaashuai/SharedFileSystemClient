package pers.sharedFileSystem.shareInterface;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

import com.sun.corba.se.spi.activation.Server;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import pers.sharedFileSystem.communicationObject.FingerprintInfo;
import pers.sharedFileSystem.configManager.Config;
import pers.sharedFileSystem.configManager.Constant;
import pers.sharedFileSystem.convenientUtil.AdvancedFileUtil;
import pers.sharedFileSystem.convenientUtil.CommonFileUtil;
import pers.sharedFileSystem.convenientUtil.CommonUtil;
import pers.sharedFileSystem.entity.*;
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
     * 删除本目录(会递归删除目录中的全部文件)
     */
    public JSONObject delete() {
        FileAdapter fileAdapter = new FileAdapter(this.NODEID,
                "", this.PARM);
        JSONObject re =fileAdapter.delete();
        return re;
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
        this.PARM=parms;
        this.NODEID = nodeId;
        // Node node = rootNode.NodeTable.get(nodeId);
        this.NODE = node;
        // 初始化节点的相对路径
        JSONObject nodePathFeed = CommonFileUtil.initFilePath(nodeId, parms,
                false,this.getOperationInfo());
        if (nodePathFeed.getInt("Errorcode") != 3000) {
            System.out.println(ErrorHandler.getErrorInfo(3005, ""));
        }
        JSONArray jsonArray = nodePathFeed.getJSONArray("Info");
        if (jsonArray.size() > 0) {
            this.FILEPATH = node.StorePath + jsonArray.getString(0);
            this.RELATIVE_FILEPATH= jsonArray.getString(0)+ "/";//文件相对路径
        }
        else {
            this.FILEPATH = node.StorePath;
            this.RELATIVE_FILEPATH= "/";//文件相对路径
        }
    }

    /**
     * 获取该目录下所有的文件相对路径（不包括目录文件）
     */
    public JSONArray getAllFilePaths() {
        ArrayList<FingerprintInfo> files = new ArrayList<FingerprintInfo>();
        ArrayList<FileInfo> result = new ArrayList<FileInfo>();
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
                        fInfo.setFilePath(this.RELATIVE_FILEPATH);
                        fInfo.setNodeId(this.NODE.Id);
                        fInfo.setFileName(readfile.getName());
                        FileInfo fileInfo = new FileInfo(fInfo);
                        fileInfo.setAccessUrl(serverNode.URL+this.RELATIVE_FILEPATH+readfile.getName());
                        files.add(fInfo);
                        result.add(fileInfo);
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
                        fInfo.setFilePath(this.RELATIVE_FILEPATH);
                        fInfo.setNodeId(this.NODE.Id);
                        fInfo.setFileName(f.getName());
                        FileInfo fileInfo = new FileInfo(fInfo);
                        fileInfo.setAccessUrl(serverNode.URL+this.RELATIVE_FILEPATH+f.getName());
                        files.add(fInfo);
                        result.add(fileInfo);
                    }
                }
            } catch (Exception e) {
                LogRecord.FileHandleErrorLogger.error(e.toString());
            }
        }
        //如果是去冗文件夹，还需要获取该文件夹里面存放在其他目录（或者本目录）下的冗余文件，同时还需要过滤掉已经被删除的文件（由于文件被引用导致未被物理删除）
        //
        if(this.NODE.Redundancy.Switch){
            Feedback feedback2=FileSystemClient.sendValidateFileNames(serverNode.Id,files);
            if(feedback2.getErrorcode()==3000) {//
                ArrayList<FingerprintInfo> validateFiles = (ArrayList<FingerprintInfo>) feedback2.getFeedbackInfo("validateFiles");
                files=validateFiles;
            }
            Feedback feedback= FileSystemClient.sendGetRedundancyInfo(serverNode.Id,relativePath+ "/" );
            if(feedback.getErrorcode()==3000) {//表示存在冗余文件
                ArrayList<FingerprintInfo> otherPath = (ArrayList<FingerprintInfo>) feedback.getFeedbackInfo("otherPath");
                for(FingerprintInfo finfo: otherPath){
                    FileInfo fileInfo = new FileInfo(finfo);
                    fileInfo.setAccessUrl(Config.getNodeByNodeId(finfo.getNodeId()).getServerNode().URL+fileInfo.getFilePath()+finfo.getFileName());
                    result.add(fileInfo);
                }
                files.addAll(otherPath);
            }
        }
        if(Config.SYSTEMCONFIG.Expand) {// 如果系统支持扩容，还需要获取扩容目录中的文件
            Feedback feedback2=FileSystemClient.sendGetExpandInfoMessage(serverNode.Id,this.NODEID);
            if(feedback2.getErrorcode()==3000) {
                ArrayList<String> expandNodeList = (ArrayList<String>) feedback2.getFeedbackInfo("expandNodeList");
                for(String nodeId : expandNodeList){
                    Map<String, String> parms = new HashedMap();
                    parms.putAll(this.PARM);
                    parms.put("EXPAND_FLAG", "1");//表示原始存储目录结点被扩容的标记
                    parms.put("PREDIR", this.NODEID);
                    DirectoryAdapter directoryAdapter = new DirectoryAdapter(nodeId, parms);
                    JSONArray tmp = directoryAdapter.getAllFilePaths();
                    result.addAll(tmp);
                }
            }
        }
        JSONArray result_final=JSONArray.fromObject(result);
        //如果是文件夹具有扩展属性，还需要获取扩展的文件夹里面的文件内容
//        if(this.NODE.NameType==NodeNameType.STATIC ) {
//            List<IntervalProperty> Intervals=this.NODE.Intervals;
//            if(Intervals.size()>0) {
//                DirectoryAdapter dicAdapter = new DirectoryAdapter(Intervals.get(0).DirectoryNodeId, parms);
//                JSONArray other=dicAdapter.getAllFilePaths();
//                result.addAll(other);
//            }
//        }
        return result_final;
    }

    /**
     * 获取该目录下所有的文件名称（不包括目录文件）
     */
    public ArrayList<String> getAllFileNames() {
        ArrayList<String> fileNames = new ArrayList<String>();
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
                        fInfo.setFilePath(this.RELATIVE_FILEPATH);
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
                        fInfo.setFilePath(this.RELATIVE_FILEPATH);
                        fInfo.setNodeId(this.NODE.Id);
                        fInfo.setFileName(f.getName());
                        files.add(fInfo);
                    }
                }
            } catch (Exception e) {
                LogRecord.FileHandleErrorLogger.error(e.toString());
            }
        }
        //如果是去冗文件夹，还需要获取该文件夹里面存放在其他目录（或者本目录）下的冗余文件，同时还需要过滤掉已经被删除的文件（由于文件被引用导致未被物理删除）
        //
        if(this.NODE.Redundancy.Switch){
            Feedback feedback2=FileSystemClient.sendValidateFileNames(serverNode.Id,files);
            if(feedback2.getErrorcode()==3000) {//
                ArrayList<FingerprintInfo> validateFiles = (ArrayList<FingerprintInfo>) feedback2.getFeedbackInfo("validateFiles");
                files=validateFiles;
            }
            Feedback feedback= FileSystemClient.sendGetRedundancyInfo(serverNode.Id,relativePath+ "/" );
            if(feedback.getErrorcode()==3000) {//表示存在冗余文件
                ArrayList<FingerprintInfo> otherPath = (ArrayList<FingerprintInfo>) feedback.getFeedbackInfo("otherPath");
                files.addAll(otherPath);
            }
        }
        if(Config.SYSTEMCONFIG.Expand) {// 如果系统支持扩容，还需要获取扩容目录中的文件
            Feedback feedback2=FileSystemClient.sendGetExpandInfoMessage(serverNode.Id,this.NODEID);
            if(feedback2.getErrorcode()==3000) {
                ArrayList<String> expandNodeList = (ArrayList<String>) feedback2.getFeedbackInfo("expandNodeList");
                for(String nodeId : expandNodeList){
                    Map<String, String> parms = new HashedMap();
                    parms.putAll(this.PARM);
                    parms.put("EXPAND_FLAG", "1");//表示原始存储目录结点被扩容的标记
                    parms.put("PREDIR", this.NODEID);
                    DirectoryAdapter directoryAdapter = new DirectoryAdapter(nodeId, parms);
                    ArrayList<String> tmp = directoryAdapter.getAllFileNames();
                    fileNames.addAll(tmp);
                }
            }
        }
        for(FingerprintInfo info:files){
            fileNames.add(info.getFileName());
        }
        return fileNames;
    }
    /**
     * 获取该目录下所有的文件（包括目录文件）
     */
    public JSONArray getAllFile() {
        ArrayList<FingerprintInfo> files = new ArrayList<FingerprintInfo>();//普通文件
        ArrayList<FingerprintInfo> dirFiles = new ArrayList<FingerprintInfo>();//目录文件
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
                        fInfo.setFilePath(this.RELATIVE_FILEPATH);
                        fInfo.setNodeId(this.NODE.Id);
                        fInfo.setFileType(FileType.DOCUMENT);
                        fInfo.setFileName(readfile.getName());
                        files.add(fInfo);
                    }else if(readfile.isDirectory()){
                        FingerprintInfo fInfo=new FingerprintInfo();
                        fInfo.setFilePath(this.RELATIVE_FILEPATH);
                        fInfo.setNodeId(this.NODE.Id);
                        fInfo.setFileType(FileType.DIRECTORY);
                        fInfo.setFileName(readfile.getName());
                        dirFiles.add(fInfo);
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
                        if(f.isFile()) {
                            FingerprintInfo fInfo = new FingerprintInfo();
                            fInfo.setFilePath(this.RELATIVE_FILEPATH);
                            fInfo.setNodeId(this.NODE.Id);
                            fInfo.setFileType(FileType.DOCUMENT);
                            fInfo.setFileName(f.getName());
                            files.add(fInfo);
                        }else if(f.isDirectory()){
                            FingerprintInfo fInfo = new FingerprintInfo();
                            fInfo.setFilePath(this.RELATIVE_FILEPATH);
                            fInfo.setNodeId(this.NODE.Id);
                            fInfo.setFileType(FileType.DIRECTORY);
                            fInfo.setFileName(f.getName());
                            dirFiles.add(fInfo);
                        }
                    }
                }
            } catch (Exception e) {
                LogRecord.FileHandleErrorLogger.error(e.toString());
            }
        }
        //如果是去冗文件夹，还需要获取该文件夹里面存放在其他目录（或者本目录）下的冗余文件，同时还需要过滤掉已经被删除的文件（由于文件被引用导致未被物理删除）
        //
        if(this.NODE.Redundancy.Switch){
            Feedback feedback2=FileSystemClient.sendValidateFileNames(serverNode.Id,files);
            if(feedback2.getErrorcode()==3000) {//
                ArrayList<FingerprintInfo> validateFiles = (ArrayList<FingerprintInfo>) feedback2.getFeedbackInfo("validateFiles");
                files=validateFiles;
            }
            Feedback feedback= FileSystemClient.sendGetRedundancyInfo(serverNode.Id,relativePath+ "/" );
            if(feedback.getErrorcode()==3000) {//表示存在冗余文件
                ArrayList<FingerprintInfo> otherPath = (ArrayList<FingerprintInfo>) feedback.getFeedbackInfo("otherPath");
                files.addAll(otherPath);
            }
        }
        files.addAll(dirFiles);

        if(Config.SYSTEMCONFIG.Expand) {// 如果系统支持扩容，还需要获取扩容目录中的文件
            Feedback feedback2=FileSystemClient.sendGetExpandInfoMessage(serverNode.Id,this.NODEID);
            if(feedback2.getErrorcode()==3000) {
                ArrayList<String> expandNodeList = (ArrayList<String>) feedback2.getFeedbackInfo("expandNodeList");
                for(String nodeId : expandNodeList){
                    Map<String, String> parms = new HashedMap();
                    parms.putAll(this.PARM);
                    parms.put("EXPAND_FLAG", "1");//表示原始存储目录结点被扩容的标记
                    parms.put("PREDIR", this.NODEID);
                    DirectoryAdapter directoryAdapter = new DirectoryAdapter(nodeId, parms);
                    JSONArray tmp = directoryAdapter.getAllFile();
                    files.addAll(tmp);
                }
            }
        }

        JSONArray result=JSONArray.fromObject(files);

        return result;
    }

    /**
     * 删除本目录下指定的文件
     *
     * @param fileNames 待删除的文件名
     */
    public JSONObject deleteSelective(List<String> fileNames) {
        Feedback feedback = null;
        Hashtable<String,Boolean> infos=new Hashtable<String,Boolean>();
        int num=0;
        for (String name : fileNames) {
            FileAdapter fileAdapter=new FileAdapter(this.NODEID,name,this.PARM);
            JSONObject re2 =fileAdapter.delete();
            if(re2.getInt("Errorcode") != 3000){
                // 删除失败
                infos.put(name,false);
            }else {
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
