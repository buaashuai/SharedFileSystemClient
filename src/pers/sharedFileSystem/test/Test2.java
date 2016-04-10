package pers.sharedFileSystem.test;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import pers.sharedFileSystem.configManager.Config;
import pers.sharedFileSystem.convenientUtil.CommonUtil;
import pers.sharedFileSystem.convenientUtil.SHA1_MD5;
import pers.sharedFileSystem.communicationObject.FingerprintInfo;
import pers.sharedFileSystem.entity.Feedback;
import pers.sharedFileSystem.entity.FileType;
import pers.sharedFileSystem.entity.ServerNode;
import pers.sharedFileSystem.entity.SystemConfig;
import pers.sharedFileSystem.logManager.LogRecord;
import pers.sharedFileSystem.networkManager.FileSystemClient;
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
				"E:/图片视频/2.jpg"));
		FileAdapter fileAdapter = new FileAdapter(inputStream, new HashMap<String, String>());
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("categoryId", "5");
        map.put("hallTypeId", "3");
        map.put("hehe", "2");
        map.put("sceneTypeId", "1");
        map.put("hallTypeId", "7");
        map.put("activityId", "1");
        map.put("eventId", "1");
        map.put("categoryId", "1");
//        JSONObject re = fileAdapter.saveFileTo("eventActivityAlbum",
//                "222.jpg", map);
//        System.out.println(re);

//        FileAdapter fileAdapter2 = new FileAdapter("temp", "2.jpg", map);
        for(int i=1;i<=10;i++) {
            String name=i+".jpg";
            inputStream = new FileInputStream(new File(
                    "E:/图片视频/2.jpg"));
            fileAdapter = new FileAdapter(inputStream, new HashMap<String, String>());
//            map.put("activityId", ""+i);
            JSONObject re = fileAdapter.saveFileTo("eventActivityAlbum",
                    i+"-"+i+".jpg", map);
            System.out.println(re);
        }
//        for(int i=2;i<=3;i++) {
//            String name=i+".jpg";
//            inputStream = new FileInputStream(new File(
//                    "E:/图片视频/"+name));
//            fileAdapter = new FileAdapter(inputStream);
////            map.put("fileSuffix","txt");
//            JSONObject re = fileAdapter.saveFileTo("temp",
//                    i+"-"+i+".jpg", map);
//            System.out.println(re);
//        }
//        FileAdapter fileAdapter2 = new FileAdapter("categoryId", "3.jpg", map);
//        JSONObject re2 = fileAdapter2.saveFileTo("temp",
//                "3.jpg", map);
//        System.out.println(re2);
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
        map.put("categoryId", "5");
        map.put("hallTypeId", "3");
        map.put("hehe", "2");
        map.put("sceneTypeId", "1");
        map.put("hallTypeId", "7");
        map.put("activityId", "1");
        map.put("eventId", "1");
        map.put("categoryId", "1");
//        DirectoryAdapter dicAdapter = new DirectoryAdapter("hallType", map);
        List<String> fileNames = new ArrayList<String>();
        fileNames.add("1-1.jpg");
        fileNames.add("3-3.jpg");
//        JSONObject re1 = dicAdapter.deleteSelective(fileNames);
//        System.out.println(re1);
//	fileNames.add("24.txt");
//	FileAdapter fileAdapter = new FileAdapter("temp2",
//			"2-2.jpg", map);
	FileAdapter fileAdapter2 = new FileAdapter("temp2",
			"", map);

	JSONObject re2 =fileAdapter2.delete();
	System.out.println(re2);
//	JSONObject re3 =fileAdapter2.delete();
//	System.out.println(re3);
    }

    /**
     * 测试删除目录接口
     */
    private void deleteDirectoryTest() throws Exception {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("categoryId", "5");
        map.put("hallTypeId", "3");
        map.put("hehe", "2");
        map.put("sceneTypeId", "1");
        map.put("hallTypeId", "7");
        map.put("activityId", "1");
        map.put("eventId", "1");
        map.put("categoryId", "1");
        DirectoryAdapter dicAdapter = new DirectoryAdapter("temp", map);
        List<String> fileNames = new ArrayList<String>();
        fileNames.add("1-1.jpg");
        fileNames.add("3-3.jpg");
        JSONObject re1 = dicAdapter.delete();
        System.out.println(re1);
//	fileNames.add("24.txt");
//	FileAdapter fileAdapter = new FileAdapter("temp2",
//			"2-2.jpg", map);
//        FileAdapter fileAdapter2 = new FileAdapter("temp2",
//                "", map);
//
//        JSONObject re2 =fileAdapter2.delete();
//        System.out.println(re2);
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
        map.put("categoryId", "5");
        map.put("hallTypeId", "3");
        map.put("hehe", "2");
        map.put("sceneTypeId", "1");
        map.put("hallTypeId", "7");
        map.put("activityId", "1");
        map.put("eventId", "1");
        map.put("categoryId", "1");
        DirectoryAdapter dicAdapter = new DirectoryAdapter("eventActivityAlbum", map);
        List<String> fileNames = dicAdapter.getAllFileNames();
        System.out.println(fileNames);
    }

    /**
     * 测试获取文件夹下全部文件相对路径接口（不包括目录）
     */
    private void getAllFilePathsTest() throws Exception {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("categoryId", "5");
        map.put("hallTypeId", "3");
        map.put("hehe", "2");
        map.put("sceneTypeId", "1");
        map.put("hallTypeId", "7");
        map.put("activityId", "1");
        map.put("eventId", "1");
        map.put("categoryId", "1");
        DirectoryAdapter dicAdapter = new DirectoryAdapter("eventActivityAlbum", map);
        JSONArray re = dicAdapter.getAllFile();
//        ArrayList<String> re2 = dicAdapter.getAllFileNames();
//        JSONArray re3 = dicAdapter.getAllFile();
//        ArrayList<String> paths = new ArrayList<String>();
//        for (int i = 0; i < re3.size(); i++) {
//            JSONObject o = re3.getJSONObject(i);
//            String name= o.getString("fileName");
//            name=new String(name.getBytes("ISO-8859-1"), "gb2312");//转化中文文件名，保证中文不乱码
//            System.out.println(name);
//        }
        System.out.println(re);
//        System.out.println(re2);
//        System.out.println(re3);
    }

    /**
     * 配置文件解析测试
     *
     * @throws Exception
     */
    private void configTest() throws Exception {
        Hashtable<String, ServerNode> config = Config.getConfig();
        ServerNode serverNode = config.get("storeNode");
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

    /**
     * 指纹信息是否存在测试
     */
    private void isFileExistInBloomFilterTest(){
        String fingerPrint="b9a9a033372818b7c6d6078c2657db2a";
        FingerprintInfo fInfo=new FingerprintInfo(fingerPrint,FileType.UNCERTAIN);
        Feedback re= FileSystemClient.isFileExistInBloomFilter(fInfo);
        System.out.println(re);
    }

    /**
     * 添加指纹信息测试
     */
    private void sendAddFigurePrintMessageTest(){
        String fingerPrint="b9a9a033372818b7c6d6078c2657db2a";
        String destFilePath="temp";
        String node="dd";
        String fileName="aa.txt";
        FileType type=FileType.UNCERTAIN;
        FingerprintInfo fInfo = new FingerprintInfo(fingerPrint, node,destFilePath, fileName,type);
        FileSystemClient.sendAddFigurePrintMessage(fInfo);//向布隆过滤器添加指纹
    }

    /**
     * 测试return和Finally哪个优先执行
     */
    private boolean returnFinallyPriorityTest(){
        FileOutputStream fout=null;
        ObjectOutputStream sout =null;
        String fingerprintInfo="hello";
        try{
            fout= new FileOutputStream("D:/a/a/aaaa.txt", true);
            sout= new ObjectOutputStream(fout);
            sout.writeObject(fingerprintInfo);
            System.out.println("i am OK");
        }catch (FileNotFoundException e){
            e.printStackTrace();
            System.out.println("i am FileNotFoundException");
            return false;
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("i am IOException");
            return false;
        }finally {
            System.out.println("i am finally");
            try {
                if(fout!=null)
                    fout.close();
                if(sout!=null)
                    sout.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("i am return");
        return true;
    }

    /**
     * Feedback类toJsonObject测试
     */
    private void FeedbackTest(){
        Feedback feedback = null;
        feedback = new Feedback(3000 ,"");
        FingerprintInfo fingerprintInfo=new FingerprintInfo("13456","temp","e:/df","a.txt", FileType.ANY);
        feedback.addFeedbackInfo("FingerprintInfo",fingerprintInfo);
        feedback.addFeedbackInfo("sdfgsdfg");
        feedback.addFeedbackInfo("sdf23234");
        JSONObject re=feedback.toJsonObject();
        System.out.println(re);
    }

    public static ConcurrentHashMap<String,FingerprintInfo> fileReferenceInfoMap=new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String,FingerprintInfo> fingerprintInfoMap=new ConcurrentHashMap<>();

    /**
     * 测试双索引，其中一个对象变了之后，另外一个索引能否获取到最新的对象信息
     */
    private void hashMapTest(){
        FingerprintInfo fingerprintInfo=new FingerprintInfo("13456","temp","e:/df/","a.txt", FileType.ANY);

        fileReferenceInfoMap.put(fingerprintInfo.getMd5(),fingerprintInfo);
        fingerprintInfoMap.put(fingerprintInfo.getFilePath()+fingerprintInfo.getFileName(),fingerprintInfo);
        FingerprintInfo tmp=fileReferenceInfoMap.get("13456");
        tmp.setFrequency(10);
        System.out.println(fingerprintInfoMap.get(fingerprintInfo.getFilePath()+fingerprintInfo.getFileName()));
        System.out.println(fileReferenceInfoMap.get("13456"));
        FingerprintInfo tmp2=fingerprintInfoMap.get(fingerprintInfo.getFilePath()+fingerprintInfo.getFileName());
        tmp2.setFrequency(20);
        System.out.println(fingerprintInfoMap.get(fingerprintInfo.getFilePath()+fingerprintInfo.getFileName()));
        System.out.println(fileReferenceInfoMap.get("13456"));
        fileReferenceInfoMap.remove("13456");
        System.out.println(fingerprintInfoMap.get(fingerprintInfo.getFilePath()+fingerprintInfo.getFileName()));
        System.out.println(fileReferenceInfoMap.get("13456"));
    }

    private void stringTest(){
        String str1="1234";
        String str2="1234";
        System.out.println(str1.compareTo(str2));
    }

    private void sizeOfObjectTest() throws IllegalAccessException {
        FingerprintInfo fingerprintInfo=new FingerprintInfo("13456","temp","e:/df/","a.txt", FileType.ANY);
        System.out.println("sizeOf(new FingerprintInfo())=" + SizeOfObject.fullSizeOf(fingerprintInfo));
    }

    /**
     * 生成指定大小文件
     * @param num 文件个数
     * @param size 文件大小，KB
     * @param path 文件保存路径
     */
    private void generateFileTest(int num, int size, String path){
        byte[] buf = new byte[1024];//1KB

        for (int k=1;k<=num;k++) {
            try {
                String str=""+k;
                buf=str.getBytes();
                String fullPath = path + "/m"+k+".txt";
                FileOutputStream fos = new FileOutputStream(fullPath);
                for (long i = 0; i < size; i++) {
                    fos.write(buf, 0, buf.length);
                }
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 系统写性能测试
     */
    private void writePerformanceTest() throws FileNotFoundException {
        String path="E:/test/5MB_100MB";
        FileInputStream inputStream =null;
        FileAdapter fileAdapter = null;
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("hallTypeId", "1992");
        map.put("fileSuffix", "txt");
        for(int i=5;i<=100;i+=5) {
            long starTime=System.currentTimeMillis();

            String name="m"+i+".txt";
            inputStream =  new FileInputStream(new File(
                    path+"/"+name));
            fileAdapter = new FileAdapter(inputStream, new HashMap<String, String>());
            JSONObject re = fileAdapter.saveFileTo("renderConfig",
                    name, map);

            long endTime=System.currentTimeMillis();
            long time=endTime-starTime;
            double timeSpan=(double)time/1000;
            System.out.println("time [ "+name+" ]: "+timeSpan+" 秒");
        }
    }

    /**
     * 系统读性能测试
     */
    private void readPerformanceTest() throws IOException {
        String path="E:/test/5MB_100MB";
        InputStream inputStream =null;
        FileAdapter fileAdapter = null;
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("hallTypeId", "1992");
        map.put("fileSuffix", "txt");
        byte[] buf = new byte[1024];//1KB
        for(int i=5;i<=100;i+=5) {
            long starTime=System.currentTimeMillis();
            buf = new byte[1024];
            String name="m"+i+".txt";
            fileAdapter = new FileAdapter("renderConfig",
                    name, map);
//            inputStream=fileAdapter.getFileInputStream();
//            inputStream.read(buf);
//            inputStream.close();
            long endTime=System.currentTimeMillis();
            long time=endTime-starTime;
            double timeSpan=(double)time/1000;
            System.out.println("time [ "+name+" ]: "+timeSpan+" 秒");
        }
    }

    /**
     * 内存利用率测试
     */
    private void memoryPerformanceTest(int start, int end) throws FileNotFoundException {
        String path="E:/test/1KB_10000";
        FileInputStream inputStream =null;
        FileAdapter fileAdapter = null;
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("hallTypeId", "1992");
        map.put("fileSuffix", "txt");
        for(int i=start;i<=end;i++) {
            long starTime=System.currentTimeMillis();

            String name="m"+i+".txt";
            inputStream =  new FileInputStream(new File(
                    path+"/"+name));
            fileAdapter = new FileAdapter(inputStream, new HashMap<String, String>());
            JSONObject re = fileAdapter.saveFileTo("renderConfig",
                    name, map);

            long endTime=System.currentTimeMillis();
            long time=endTime-starTime;
            double timeSpan=(double)time/1000;
//            System.out.println("time [ "+name+" ]: "+timeSpan+" 秒");
        }
    }

    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub
        Test2 test2 = new Test2();
        test2.memoryPerformanceTest(1, 2000);
//       test2.generateFileTest(10000,1, "E:/test/1KB_10000");
//        System.out.println(re);
    }

}
