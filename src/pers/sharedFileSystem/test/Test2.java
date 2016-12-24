package pers.sharedFileSystem.test;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.hadoop.fs.*;
import pers.sharedFileSystem.communicationObject.ExpandFileStoreInfo;
import pers.sharedFileSystem.communicationObject.MessageProtocol;
import pers.sharedFileSystem.communicationObject.MessageType;
import pers.sharedFileSystem.configManager.Config;
import pers.sharedFileSystem.configManager.Constant;
import pers.sharedFileSystem.convenientUtil.CommonUtil;
import pers.sharedFileSystem.convenientUtil.SHA1_MD5;
import pers.sharedFileSystem.communicationObject.FingerprintInfo;
import pers.sharedFileSystem.entity.*;
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
//        for(int i=1;i<=4;i++) {
//            String name=i+".jpg";
//            inputStream = new FileInputStream(new File(
//                    "E:/图片视频/"+name));
//            fileAdapter = new FileAdapter(inputStream, map);
////            map.put("activityId", ""+i);
//            JSONObject re = fileAdapter.saveFileTo("hallType",
//                    i+"-"+i+".jpg", map);
//            System.out.println(re);
//        }
        for(int i=2;i<=2;i++) {
            String name=i+".jpg";
            inputStream = new FileInputStream(new File(
                    "E:/图片视频/"+name));
            fileAdapter = new FileAdapter(inputStream, map);
//            map.put("fileSuffix","txt");
            JSONObject re = fileAdapter.saveFileTo("temp189_a2_d1",
                    i+"-"+new Random(System.currentTimeMillis()).nextLong()+".jpg", map);
            System.out.println(re);
        }
//        FileAdapter fileAdapter2 = new FileAdapter("categoryId", "3.jpg", map);
//        JSONObject re2 = fileAdapter2.saveFileTo("temp",
//                "3.jpg", map);
//        System.out.println(re2);
//        if (re2.getInt("Errorcode") != 3000) {
//            System.out.println("false");
//        } else {
//            System.out.println("success");
//        }
        System.exit(0);
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
        fileNames.add("2.jpg");
//        JSONObject re1 = dicAdapter.deleteSelective(fileNames);
//        System.out.println(re1);
//	fileNames.add("24.txt");
//	FileAdapter fileAdapter = new FileAdapter("temp2",
//			"2-2.jpg", map);
	FileAdapter fileAdapter2 = new FileAdapter("temp189_a2_d1",
			"5-1768200017239600802.jpg", map);

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
        DirectoryAdapter dicAdapter = new DirectoryAdapter("temp", map);
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
        DirectoryAdapter dicAdapter = new DirectoryAdapter("temp2", map);
        JSONArray re = dicAdapter.getAllFilePaths();
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

    /**
     * 指纹信息是否存在测试
     */
    private void isFileExistInBloomFilterTest(){
        String fingerPrint="1111";
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
        String base = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQUSTUVWXYZ0123456789";
        Random random = new Random();
        for (int k=51;k<=num+50;k++) {
            try {
                String fullPath = path + "/m"+k+".txt";
                FileOutputStream fos = new FileOutputStream(fullPath);
                for (long i = 0; i < size*1024; i++) {
                    String str=""+base.charAt(random.nextInt(base.length()));
                    buf=str.getBytes();
                    fos.write(buf, 0, buf.length);
                }
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 生成指定长度的随机字符串
     * @param length 字符串长度
     * @return
     */
    private String getRandomString(int length) { //length表示生成字符串的长度
        String base = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQUSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString()+System.currentTimeMillis();
    }
    /**
     * 读取MD5
     * @param fullPath MD5所在绝对路径
     */
    private List<String> readMd5FromFile(String fullPath){
        List<String> result=new ArrayList<>();
        File newFile=new File(fullPath);
        FileReader fileReader=null;
        BufferedReader bufferedReader=null;
        int index=1;
        try{
            fileReader=new FileReader(newFile);
            bufferedReader=new BufferedReader(fileReader);
            try{
                String read=null;
                while((read=bufferedReader.readLine())!=null&&!read.isEmpty()){
                    result.add(read);
                    System.out.println(index+": "+read);
                    index++;
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try {
                if(bufferedReader!=null){
                    bufferedReader.close();
                }
                if(fileReader!=null){
                    fileReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    /**
     * 生成指定个数的文件指纹
     * @param num 指纹个数
     * @param fullPath 文件保存绝对路径（带文件名）
     */
    private void genFingerpringTest(int num, String fullPath) throws NoSuchAlgorithmException {
        byte[] buf = new byte[1024];//1KB
        String md5="";
        for(int i=0; i<num; i++){
            md5+=new SHA1_MD5().digestString(getRandomString(100), SHA1_MD5.MD5)+"\n";
        }
        FileOutputStream fos = null;
        try {
            buf=md5.getBytes();
            fos = new FileOutputStream(fullPath);
            fos.write(buf, 0, buf.length);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 系统读性能测试
     */
    private void readPerformanceTest() throws IOException {
        String path="E:/test/1KB_10000";
        InputStream inputStream =null;
        FileAdapter fileAdapter = null;
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("hallTypeId", "1992");
//        map.put("fileSuffix", "txt");
        byte[] buf = new byte[1024];//1KB
        for(int i=1;i<=5;i++) {
            long starTime=System.currentTimeMillis();
            buf = new byte[1024];
            String name="m"+i+".txt";
            fileAdapter = new FileAdapter("temp2",
                    name, map);
            System.out.println(fileAdapter.FILEPATH);
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
     * 系统写文件响应时间测试
     */
    private void writePerformanceTest() throws IOException {
        String path="E:/test/1KB_10000";
        InputStream inputStream =null;
        FileAdapter fileAdapter = null;
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("hallTypeId", "1992");
//        map.put("fileSuffix", "txt");
        byte[] buf = new byte[1024];//1KB
        for(int i=1;i<=5;i++) {
            long starTime=System.currentTimeMillis();
            buf = new byte[1024];
            String name="m"+i+".txt";
            fileAdapter = new FileAdapter("temp2",
                    name, map);
            fileAdapter.saveFileTo("temp3", name, map);
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
            System.out.println("time [ "+name+" ]: "+timeSpan+" 秒");
        }

    }

    /**
     * MPM测试
     */
    private long memoryMPMTest(int start, int end) throws FileNotFoundException {
        String path="E:/test/1KB_10000";
        FileInputStream inputStream =null;
        FileAdapter fileAdapter = null;
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("hallTypeId", "1992");
        map.put("fileSuffix", "txt");
        inputStream =  new FileInputStream(new File(
                path+"/m1.txt"));
        fileAdapter = new FileAdapter(inputStream, new HashMap<String, String>());
        fileAdapter.getFileInputStream();
        long starTime=System.currentTimeMillis();// 毫秒
        for(int i=start;i<=end;i++) {
            String name="m"+i+".txt";
            inputStream =  new FileInputStream(new File(
                    path+"/"+name));
            fileAdapter = new FileAdapter(inputStream, new HashMap<String, String>());
            fileAdapter.getFileInputStream();
//            JSONObject re = fileAdapter.saveFileTo("renderConfig",
//                    name, map);
        }
        long endTime=System.currentTimeMillis();// 毫秒
        long time=endTime-starTime;
        return time;
    }

    /**
     * 并发保存文件测试
     *@param num 并发请求数
     *@param serverNun 存储服务器个数
     * @throws Exception
     */
    private static int finished=0;// 保存文件完成的线程个数
    private static Vector<Long>redundancyUsedTimes = new Vector<>();
    private static Vector<Long>saveFileTimes = new Vector<>();
    private void concurrenceTest(final int num, final int serverNun) throws Exception {
        String path="E:/test/1KB_10000";
        final String path2="E:/test/1MB_50";
        FileInputStream inputStream =null;
        FileAdapter fileAdapter = null;
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("hallTypeId", "1992");
        inputStream =  new FileInputStream(new File(
                path+"/m10000.txt"));
        fileAdapter = new FileAdapter(inputStream, new HashMap<String, String>());
        fileAdapter.getFileInputStream();

        final long totalStarTime=System.currentTimeMillis();// 毫秒
        for(int i=1;i<=num;i++) {
            final int finalI = i;
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String name = "m" + finalI + ".txt";
                    try {
                        String destNode="";
                        if(finalI%serverNun==0){
                            destNode="temp189_a2_d1";//temp189_a2_d1
                        }else if(finalI%serverNun==1){
                            destNode="tempNode188_a1_d1";
                        }else{
                            destNode="tempNode127_a1_d1";
                        }
                        FileInputStream inputStream = new FileInputStream(new File(
                                path2 + "/" + name));
                        long starTime=System.currentTimeMillis();// 毫秒
                        System.out.println("starTime= "+starTime);
                        FileAdapter fileAdapter = new FileAdapter(inputStream, new HashMap<String, String>());
                        JSONObject re = fileAdapter.saveFileTo(destNode, name, new HashMap<String, String>());
                        System.out.println(re.toString());
                        long endTime=System.currentTimeMillis();
                        System.out.println("endTime= "+endTime);
                        long time=endTime-starTime;
                        finished++;
                        redundancyUsedTimes.add(re.getLong("redundancyUsedTime"));
                        saveFileTimes.add(time);
                        if(finished == num) {
                            long redundancy = 0, saveTime = 0, maxSaveTime = 0;
                            int index = 1;
                            for(Long n : redundancyUsedTimes){
                                redundancy+=n;
                                LogRecord.FileHandleInfoLogger.info("冗余检测耗时(毫秒)[" + index + "]: " + n);
                                index++;
                            }
                            System.out.println("");
                            index=1;
                            for(Long n : saveFileTimes){
                                saveTime+=n;
                                maxSaveTime = n > maxSaveTime ? n : maxSaveTime;
                                LogRecord.FileHandleInfoLogger.info("保存文件耗时(毫秒)[" + index + "]: " + n);
                                index++;
                            }
                            System.out.println("");
                            long totalEndTime=System.currentTimeMillis();
                            long totalTime=totalEndTime-totalStarTime;
                            LogRecord.FileHandleInfoLogger.info("redundancy= " + redundancy);
                            LogRecord.FileHandleInfoLogger.info("totalTime= " + totalTime);
                            LogRecord.FileHandleInfoLogger.info("检测平均时间(毫秒): " + (redundancy/redundancyUsedTimes.size()));
                            LogRecord.FileHandleInfoLogger.info("写速率(MB/s): " + ((double)redundancyUsedTimes.size()*1000/totalTime));
                            System.exit(0);
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
        System.out.println("endend");
    }

    /**
     * 指令大小测试
     */
    private void commandSizeTest(){
        MessageProtocol queryMessage = new MessageProtocol();
        queryMessage.messageType = MessageType.GET_EXPAND_DIRECTORY;
        queryMessage.senderType = SenderType.CLIENT;
        queryMessage.content="123123123123";

        FileOutputStream fout=null;
        ObjectOutputStream sout =null;
        String filePath="E:/MFCSS";//结点扩容信息文件的保存路径
        String fileName="command.sys";
        String tempFileName="tmp_command.sys";
        if(!CommonUtil.validateString(filePath)){
            LogRecord.FileHandleErrorLogger.error("save command error, filePath is null.");
        }
        File file = new File(filePath);
        if (!file.exists() && !file.isDirectory()) {
            LogRecord.RunningErrorLogger.error("save command error, filePath illegal.");
        }
        File oldFile=new File(filePath+"/"+fileName);
        File tempFile=new File(filePath+"/"+ tempFileName);
        if(oldFile.exists()){
            oldFile.renameTo(tempFile);
        }

        try{
            fout = new FileOutputStream(filePath + "/" + fileName, true);
                sout = new ObjectOutputStream(fout);
                sout.writeObject(queryMessage);
            LogRecord.RunningInfoLogger.info("save command successful. ");
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
            File newFile=new File(filePath+"/"+fileName);
            newFile.delete();
            tempFile.renameTo(newFile);
        }finally {
            try {
                //删除临时文件
                if(tempFile.exists()){
                    tempFile.delete();
                }
                if(fout!=null)
                    fout.close();
                if(sout!=null)
                    sout.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub
        Test2 test2 = new Test2();
//        final String path2="E:/test/1KB_2GB/2MB/";
//        String fileName=path2+"m1.txt";
//        String fileName2=path2+"m11.txt";
//        SHA1_MD5 sha1_md5 = new SHA1_MD5();
//        System.out.println(sha1_md5.digestFile(fileName,SHA1_MD5.MD5));
//        System.out.println(sha1_md5.digestFile(fileName2,SHA1_MD5.MD5));

//        test2.getAllFilePathsTest();
//        test2.getAllFileNamesTest();
//        test2.readPerformanceTest();
//        test2.writePerformanceTest();

//        long total = 0;
//        for(int i=0; i <10; i++) {
//            long tmp=test2.memoryMPMTest(1, 10000);
//            total+=tmp;
//            System.out.println("time: "+tmp);
//        }
//        System.out.println(total/10);
//            test2.concurrenceTest(20,3);

            test2.saveFileToTest();
//            test2.commandSizeTest();
//            test2.deleteFileTest();
//       test2.genFingerpringTest(150000, "E:/test/md5_150000.txt");
//        test2.readMd5FromFile("E:/test/md5_150000.txt");
//       test2.generateFileTest(50,1024*1, "E:/test/1MB_50");
//       test2.generateFileTest(1,1024*1, "E:/test/1KB_2GB/1MB");
//        test2.hdfsWritePerformanceTest("256"+"MB");
//        test2.deleteHdfsFile();
//        System.out.println(re);

    }

}
