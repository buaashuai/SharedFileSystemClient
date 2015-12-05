package pers.sharedFileSystem.test;

import java.io.File;
import java.io.FileInputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import pers.sharedFileSystem.configManager.Config;
import pers.sharedFileSystem.convenientUtil.SHA1_MD5;
import pers.sharedFileSystem.entity.FingerprintInfo;
import pers.sharedFileSystem.entity.ServerNode;
import pers.sharedFileSystem.entity.SystemConfig;
import pers.sharedFileSystem.shareInterface.DirectoryAdapter;
import pers.sharedFileSystem.shareInterface.FileAdapter;

public class Test2 {

    /**
     * 验证文件是否存在，测试接口 AdvancedFileUtil.isFileExist（）
     *
     * @throws Exception
     */
    private void isFileExistTest() throws Exception {
        // Node node = Config.getNodeByNodeId("renderConfig");
        ServerNode rootNode = Config.getConfig().get("renderNode");
        String fileName = "config.ini";// buaashuai1.txt
        // infoLog.txt
        String filePath = "D:/Hundsun/HsClient";// D:/FileSystemLog/info
        // E:/ftpServer
//		boolean re = AdvancedFileUtil.isFileExist(rootNode, filePath, fileName,
//				false);
//		System.out.println(re);
    }

    /**
     * 验证文件夹是否存在，不存在就建立，测试接口AdvancedFileUtil.validateDirectory
     *
     * @throws Exception
     */
    private void isFolderExistTest() throws Exception {
        ServerNode rootNode = Config.getConfig().get("renderNode");
        String root = "D:/Hundsun/test";
//		AdvancedFileUtil.validateDirectory(rootNode, root);
    }

    /**
     * 测试文件保存接口，测试接口 FileAdapter.saveFileTo
     *
     * @throws Exception
     */
    private void saveFileToTest() throws Exception {
		FileInputStream inputStream = new FileInputStream(new File(
				"E:/图片视频/30939_1132245_133682.jpg"));
		FileAdapter fileAdapter = new FileAdapter(inputStream);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("categoryId", "5");
        map.put("hallTypeId", "3");
        map.put("hehe", "2");
        map.put("sceneTypeId", "1");
        map.put("hallTypeId", "7");
//        FileAdapter fileAdapter = new FileAdapter("hallType", "1.txt", map);
        JSONObject re = fileAdapter.saveFileTo("temp",
                "2.jpg", map);
        System.out.println(re);
        FileAdapter fileAdapter2 = new FileAdapter("categoryId", "3.jpg", map);
        JSONObject re2 = fileAdapter2.saveFileTo("temp",
                "3.jpg", map);
        System.out.println(re2);
//        if (re2.getInt("Errorcode") != 3000) {
//            System.out.println("false");
//        } else {
//            System.out.println("success");
//        }
    }

    /**
     * 测试删除文件接口
     */
    private void deleteFileTest() throws Exception {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("sceneTypeId", "2");
        map.put("hallTypeId", "3");
        map.put("categoryId", "5");
        map.put("hehe", "2");
        map.put("sceneTypeId", "1");
        map.put("hallTypeId", "7");
        DirectoryAdapter dicAdapter = new DirectoryAdapter("tempStoreNode", map);
        List<String> fileNames = new ArrayList<String>();
        fileNames.add("2.jpg");
        fileNames.add("32.jpg");
//	fileNames.add("24.txt");
	FileAdapter fileAdapter = new FileAdapter("tempStoreNode",
			"3.jpg", map);
//	FileAdapter fileAdapter2 = new FileAdapter("hehe",
//			"4.jpg", map);
        JSONObject re1 = dicAdapter.deleteSelective(fileNames);
        System.out.println(re1);
	JSONObject re2 =fileAdapter.delete();
	System.out.println(re2);
//	JSONObject re3 =fileAdapter2.delete();
//	System.out.println(re3);
    }

    /**
     * 测试获取文件夹下的全部文件名接口
     *
     * @throws Exception
     */
    private void getAllFileNamesTest() throws Exception {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("sceneTypeId", "2");
        map.put("hallTypeId", "3");
        map.put("categoryId", "5");
        map.put("hehe", "2");
        DirectoryAdapter dicAdapter = new DirectoryAdapter("tempStoreNode", map);
        List<String> fileNames = dicAdapter.getAllFileNames();
        System.out.println(fileNames);
    }

    /**
     * 测试获取文件夹下全部文件相对路径接口（不包括目录）
     */
    private void getAllFilePathsTest() throws Exception {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("sceneTypeId", "2");
        map.put("hallTypeId", "3");
        map.put("categoryId", "5");
        map.put("hehe", "2");
        DirectoryAdapter dicAdapter = new DirectoryAdapter("tempStoreNode", map);
        JSONArray re = dicAdapter.getAllFilePaths();
        System.out.println(re);
    }

    /**
     * 配置文件解析测试
     *
     * @throws Exception
     */
    private void configTest() throws Exception {
        Hashtable<String, ServerNode> config = Config.getConfig();
        ServerNode serverNode = config.get("tempNode");
        serverNode.print("");
        System.out.println("*****************");
        SystemConfig systemConfig = Config.SYSTEMCONFIG;
        systemConfig.print("");
        System.out.println("*****************");
    }

    /**
     * 哈希函数测试
     * @throws Exception
     */
    private void md5Test(){
        SHA1_MD5 sha1_md5=new SHA1_MD5();
        String fileName="E:/图片视频/30939_1132245_133682.jpg";
        String md5= sha1_md5.digestFile(fileName,SHA1_MD5.MD5);
        String sha_1= sha1_md5.digestFile(fileName,SHA1_MD5.SHA_1);
        String SHA_256= sha1_md5.digestFile(fileName,SHA1_MD5.SHA_256);
        String SHA_512= sha1_md5.digestFile(fileName,SHA1_MD5.SHA_512);
        String SHA_384= sha1_md5.digestFile(fileName,SHA1_MD5.SHA_384);
        System.out.println(md5.length()+": "+md5);
        System.out.println(sha_1.length()+": "+sha_1);
        System.out.println(SHA_256.length()+": "+SHA_256);
        System.out.println(SHA_512.length()+": "+SHA_512);
        System.out.println(SHA_384.length()+": "+SHA_384);
    }
    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub
        Test2 test2 = new Test2();
        test2.md5Test();
    }

}
