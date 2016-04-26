package pers.sharedFileSystem.test;

import net.sf.json.JSONObject;
import pers.sharedFileSystem.configManager.Config;
import pers.sharedFileSystem.convenientUtil.SHA1_MD5;
import pers.sharedFileSystem.entity.ServerNode;
import pers.sharedFileSystem.shareInterface.FileAdapter;

import java.io.*;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * RRMFS 测试
 */
public class RRMFSTest {
    /**
     * 创建md5  TXT文件
     * @param dirName 测试用例所在文件夹名称，例如：1KB, 1MB
     */
    private File createMd5File(String dirName)throws Exception{
        String name="md5.txt";
        String path="E:/test/1KB_2GB"+"/"+dirName+"/"+name;
        File newFile=new File(path);
        try{
            File oldFile=new File(path);
            if(oldFile.exists()){
                oldFile.delete();
            }
            newFile.createNewFile();
        }catch(Exception e){
            e.printStackTrace();
        }
        return newFile;
    }

    /**
     * 向txt里面写入数据
     * @param md5s 所有的MD5
     * @param md5File 被写入的MD5文件
     * @return
     * @throws Exception
     */
    private boolean writeMd5ToFile(String md5s,File  md5File)throws Exception{
        RandomAccessFile mm=null;
        boolean flag=false;
        FileOutputStream o=null;
        try {
            o = new FileOutputStream(md5File);
            o.write(md5s.getBytes("GBK"));
            flag=true;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }finally{
            if(mm!=null){
                mm.close();
            }
        }
        return flag;
    }

    /**
     * 读取MD5
     * @param dirName 测试用例所在文件夹名称，例如：1KB, 1MB
     */
    private String[] readMd5FromFile(String dirName)throws Exception{
        String[] result=new String[3];
        int len=0;
        String name="md5.txt";
        String path="E:/test/1KB_2GB"+"/"+dirName+"/"+name;
        File newFile=new File(path);
        FileReader fileReader=null;
        BufferedReader bufferedReader=null;
        try{
            fileReader=new FileReader(newFile);
            bufferedReader=new BufferedReader(fileReader);
            try{
                String read=null;
                while((read=bufferedReader.readLine())!=null&&len<3){
                    result[len++]=read;
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(bufferedReader!=null){
                bufferedReader.close();
            }
            if(fileReader!=null){
                fileReader.close();
            }
        }
        return result;
    }

    /**
     * RRMFS 系统写性能测试（冗余度 0%）
     * @param dirName 测试用例所在文件夹名称，例如：1KB, 1MB
     */
    private void rrmfsWritePerformanceTest_0(String dirName) throws Exception {
        try
        {
            Hashtable<String, ServerNode> config = Config.getConfig();
            ServerNode serverNode = config.get("storeNode");
            Thread.sleep(5000);//停顿5秒，保证在系统运行稳定的情况下测试
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        String path="E:/test/1KB_2GB";
        FileInputStream inputStream =null;
        FileAdapter fileAdapter = null;
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("hallTypeId", "1992");
        String[] md5s=readMd5FromFile(dirName);
        long starTime=System.currentTimeMillis();
        for(int i=1;i<=3;i++) {
            map.put("fingerPrint", md5s[i-1]);
            String fileName="m"+i+".txt";
            inputStream =  new FileInputStream(new File(
                    path+"/"+dirName+"/"+fileName));
            fileAdapter = new FileAdapter(inputStream, new HashMap<String, String>());
            String newName=dirName+"_"+"m"+i+".txt";
            JSONObject re = fileAdapter.saveFileTo("renderConfig",
                    newName, map);
        }
        long endTime=System.currentTimeMillis();
        long time=endTime-starTime;
        double timeSpan=(double)time/1000;
        System.out.println("time [ "+dirName+" ]: "+timeSpan+" 秒");
    }

    /**
     * RRMFS 系统写性能测试（冗余度 25%）
     * @param dirName 测试用例所在文件夹名称，例如：1KB, 1MB
     */
    private void rrmfsWritePerformanceTest_25(String dirName) throws Exception {
        try
        {
            Hashtable<String, ServerNode> config = Config.getConfig();
            ServerNode serverNode = config.get("storeNode");
            Thread.sleep(5000);//停顿5秒，保证在系统运行稳定的情况下测试
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        String path="E:/test/1KB_2GB";
        FileInputStream inputStream =null;
        FileAdapter fileAdapter = null;
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("hallTypeId", "1992");
        String[] md5s=readMd5FromFile(dirName);
        long starTime=System.currentTimeMillis();
        for(int i=1;i<=3;i++) {
            map.put("fingerPrint", md5s[i-1]);
            String fileName="m"+i+".txt";
            inputStream =  new FileInputStream(new File(
                    path+"/"+dirName+"/"+fileName));
            fileAdapter = new FileAdapter(inputStream, new HashMap<String, String>());
            String newName=dirName+"_"+"m"+i+".txt";
            JSONObject re = fileAdapter.saveFileTo("renderConfig",
                    newName, map);
        }
        try
        {
            Thread.sleep(2000);//停顿2秒，保证在系统运行稳定的情况下测试
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        for(int i=1;i<=1;i++) {
            map.put("fingerPrint", md5s[i-1]);
            String fileName="m"+i+".txt";
            inputStream =  new FileInputStream(new File(
                    path+"/"+dirName+"/"+fileName));
            fileAdapter = new FileAdapter(inputStream, new HashMap<String, String>());
            String newName=dirName+"_"+"m"+i+"_1"+".txt";
            JSONObject re = fileAdapter.saveFileTo("renderConfig",
                    newName, map);
        }
        long endTime=System.currentTimeMillis();
        long time=endTime-starTime;
        double timeSpan=(double)time/1000-2;
        System.out.println("time [ "+dirName+" ]: "+timeSpan+" 秒");
    }
    /**
     * RRMFS 系统写性能测试（冗余度 40%）
     * @param dirName 测试用例所在文件夹名称，例如：1KB, 1MB
     */
    private void rrmfsWritePerformanceTest_40(String dirName) throws Exception {
        try
        {
            Hashtable<String, ServerNode> config = Config.getConfig();
            ServerNode serverNode = config.get("storeNode");
            Thread.sleep(3000);//停顿5秒，保证在系统运行稳定的情况下测试
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        String path="E:/test/1KB_2GB";
        FileInputStream inputStream =null;
        FileAdapter fileAdapter = null;
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("hallTypeId", "1992");
        String[] md5s=readMd5FromFile(dirName);
        long starTime=System.currentTimeMillis();
        for(int i=1;i<=3;i++) {
            map.put("fingerPrint", md5s[i-1]);
            String fileName="m"+i+".txt";
            inputStream =  new FileInputStream(new File(
                    path+"/"+dirName+"/"+fileName));
            fileAdapter = new FileAdapter(inputStream, new HashMap<String, String>());
            String newName=dirName+"_"+"m"+i+".txt";
            JSONObject re = fileAdapter.saveFileTo("renderConfig",
                    newName, map);
        }
        try
        {
            Thread.sleep(2000);//停顿2秒，保证在系统运行稳定的情况下测试
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        for(int i=1;i<=2;i++) {
            map.put("fingerPrint", md5s[i-1]);
            String fileName="m"+i+".txt";
            inputStream =  new FileInputStream(new File(
                    path+"/"+dirName+"/"+fileName));
            fileAdapter = new FileAdapter(inputStream, new HashMap<String, String>());
            String newName=dirName+"_"+"m"+i+"_1"+".txt";
            JSONObject re = fileAdapter.saveFileTo("renderConfig",
                    newName, map);
        }
        long endTime=System.currentTimeMillis();
        long time=endTime-starTime;
        double timeSpan=(double)time/1000-2;
        System.out.println("time [ "+dirName+" ]: "+timeSpan+" 秒");
    }

    /**
     * RRMFS 系统写性能测试（冗余度 50%）
     * @param dirName 测试用例所在文件夹名称，例如：1KB, 1MB
     */
    private void rrmfsWritePerformanceTest_50(String dirName) throws Exception {
        try
        {
            Hashtable<String, ServerNode> config = Config.getConfig();
            ServerNode serverNode = config.get("storeNode");
            Thread.sleep(3000);//停顿5秒，保证在系统运行稳定的情况下测试
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        String path="E:/test/1KB_2GB";
        FileInputStream inputStream =null;
        FileAdapter fileAdapter = null;
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("hallTypeId", "1992");
        String[] md5s=readMd5FromFile(dirName);
        long starTime=System.currentTimeMillis();
        for(int i=1;i<=3;i++) {
            map.put("fingerPrint", md5s[i-1]);
            String fileName="m"+i+".txt";
            inputStream =  new FileInputStream(new File(
                    path+"/"+dirName+"/"+fileName));
            fileAdapter = new FileAdapter(inputStream, new HashMap<String, String>());
            String newName=dirName+"_"+"m"+i+".txt";
            JSONObject re = fileAdapter.saveFileTo("renderConfig",
                    newName, map);
        }
        try
        {
            Thread.sleep(4000);//停顿4秒，保证在系统运行稳定的情况下测试
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        for(int i=1;i<=3;i++) {
            map.put("fingerPrint", md5s[i-1]);
            String fileName="m"+i+".txt";
            inputStream =  new FileInputStream(new File(
                    path+"/"+dirName+"/"+fileName));
            fileAdapter = new FileAdapter(inputStream, new HashMap<String, String>());
            String newName=dirName+"_"+"m"+i+"_1"+".txt";
            JSONObject re = fileAdapter.saveFileTo("renderConfig",
                    newName, map);
        }
        long endTime=System.currentTimeMillis();
        long time=endTime-starTime;
        double timeSpan=(double)time/1000-4;
        System.out.println("time [ "+dirName+" ]: "+timeSpan+" 秒");
    }

    /**
     * 生成每个测试文件的MD5
     */
    public void generateMD5() throws Exception {
        try {
            BufferedReader strin=new BufferedReader(new InputStreamReader(System.in));
            System.out.println("[MD5生成测试] 请输入测试用例所在文件夹名称：");
            String testDir=strin.readLine();
            while(!testDir.equals("#")) {
                File md5File=createMd5File(testDir);
                String md5s="";
                System.out.println("开始生成MD5：" + testDir);
                long starTime=System.currentTimeMillis();
                for(int i=1;i<=3;i++) {
                    String fileName="m"+i+".txt";
                    String filePath="E:\\test\\1KB_2GB\\" + testDir+"\\"+fileName;
                    SHA1_MD5 sha1_md5=new SHA1_MD5();
                    String m5=sha1_md5.digestFile(filePath, SHA1_MD5.MD5);
                    md5s+=m5+"\n";
                }
                writeMd5ToFile(md5s,md5File);
                long endTime=System.currentTimeMillis();
                long time=endTime-starTime;
                double timeSpan=(double)time/1000;
                System.out.println("time [ "+testDir+" ]: "+timeSpan+" 秒");
                System.out.println("\n[MD5生成测试] 请输入测试用例所在文件夹名称：");
                testDir=strin.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取每个测试文件的MD5
     */
    public void readMD5() throws Exception {
        try {
            BufferedReader strin=new BufferedReader(new InputStreamReader(System.in));
            System.out.println("[MD5读取测试] 请输入测试用例所在文件夹名称：");
            String testDir=strin.readLine();
            while(!testDir.equals("#")) {
                String[] md5s=readMd5FromFile(testDir);
                long starTime=System.currentTimeMillis();
                for(int i=0;i<md5s.length;i++) {
                    System.out.println("m"+(i+1)+".txt\t"+md5s[i]);
                }
                System.out.println("\n[MD5读取测试] 请输入测试用例所在文件夹名称：");
                testDir=strin.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 写性能测试
     */
    public void writeRRMFS() throws Exception {
        try {
            BufferedReader strin=new BufferedReader(new InputStreamReader(System.in));
            System.out.println("[RRMFS写测试] 请输入测试用例所在文件夹名称：");
            String testDir=strin.readLine();
            while(!testDir.equals("#")) {
                rrmfsWritePerformanceTest_50(testDir);
                break;
//                System.out.println("\n[RRMFS写测试] 请输入测试用例所在文件夹名称：");
//                testDir=strin.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        RRMFSTest testRRMFS=new RRMFSTest();
//        testRRMFS.generateMD5();
//        testRRMFS.readMD5();
        testRRMFS.writeRRMFS();
    }
}
