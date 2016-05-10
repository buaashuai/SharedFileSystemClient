package pers.sharedFileSystem.communicationObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 冗余文件存储信息
 */
public class RedundancyFileStoreInfo implements Serializable{
    /**
     * 节点相对路径（相对根节点的存储路径，充当key的角色）
     * 根据essentialStorePath找到这个路径下的全部冗余文件
     * essentialStorePath ——> otherFilePath
     */
    public String essentialStorePath;
    /**
     * 该目录下的存储在其他文件夹里面的冗余文件的相对路径
     */
    public ArrayList<FingerprintInfo> otherFileInfo;

    public RedundancyFileStoreInfo(){
        otherFileInfo=new ArrayList<FingerprintInfo>();
    }

    /**
     * 添加一条信息
     * @param f
     */
    public void addFingerprintInfo(FingerprintInfo f){
        otherFileInfo.add(f);
    }
}
