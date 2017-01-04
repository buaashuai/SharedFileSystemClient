package pers.sharedFileSystem.entity;

import pers.sharedFileSystem.communicationObject.FingerprintInfo;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 文件信息类
 */
public class FileInfo extends FingerprintInfo{
    public FileInfo(FingerprintInfo fingerprintInfo){
        this.setMd5(fingerprintInfo.getMd5());
        this.setNodeId(fingerprintInfo.getNodeId());
        this.setFilePath(fingerprintInfo.getFilePath());
        this.setFileName(fingerprintInfo.getFileName());
        this.setFileType(fingerprintInfo.getFileType());
        this.setFrequency(fingerprintInfo.getFrequency());
        this.setPhysicalDeletedByTrueUserFlag(fingerprintInfo.getPhysicalDeletedByTrueUserFlag());
    }
    public String getAccessUrl() {
        return accessUrl;
    }

    public void setAccessUrl(String accessUrl) {
        this.accessUrl = accessUrl;
    }

    /**
     * 文件访问地址，如：http://localhost:90/portal/static/FileBase/temp/20170104185626_6_-1900482835.jpg
     */
    private String accessUrl;

    public String toString(){
        String str="";
        SimpleDateFormat dateFm = new SimpleDateFormat("yyyyMMddHHmmss"); // 格式化当前系统日期
        String time = dateFm.format(new Date());
        str+="updateTime: "+time+" , ";
        str+="md5: "+ this.getMd5() +" , ";
        str+="nodeId: "+ this.getNodeId() +" , ";
        str+="filePath: "+ this.getFilePath() +" , ";
        str+="fileName: "+ this.getFileName() +" , ";
        str+="fileType: "+this.getFileType()+" , ";
        str+="frequency: "+ this.getFrequency() +" , ";
        str+="physicalDeletedByTrueUserFlag: "+ this.getPhysicalDeletedByTrueUserFlag();
        str+="accessUrl: "+ this.accessUrl;
        return str;
    }
}