package pers.sharedFileSystem.test;

import org.apache.hadoop.fs.FileSystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 测试HDFS的写性能
 */
public class HDFSTest {

    /**
     * HDFS写性能测试
     * @param dirName 测试用例所在文件夹名称，例如：1KB, 1MB
     */
    public void hdfsWritePerformanceTest(String dirName) throws Exception {
        try
        {
            Thread.sleep(10000);//停顿10秒，保证在系统运行稳定的情况下测试
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        OperaHDFS.uploadFile("E:\\test\\1KB_2GB\\tmp.txt", "/hadoop/myfile/test/tmp.txt");
        long starTime=System.currentTimeMillis();
        for(int i=1;i<=3;i++) {
            String fileName="m"+i+".txt";
            OperaHDFS.uploadFile("E:\\test\\1KB_2GB\\" + dirName+"\\"+fileName, "/hadoop/myfile/test/"+fileName+"_"+dirName);
        }
        long endTime=System.currentTimeMillis();
        long time=endTime-starTime;
        double timeSpan=(double)time/1000;
        System.out.println("time [ "+dirName+" ]: "+timeSpan+" 秒");
    }

    /**
     * 删除HDFS上的文件
     * @throws Exception
     */
    private void deleteHdfsFileTest() throws Exception {
//        for(int i=1;i<=3;i++) {
//            String fileName="m"+i+".txt";
//            OperaHDFS.deleteFileOnHDFS("/hadoop/myfile/test/" + fileName);
//        }

        long starTime=System.currentTimeMillis();
        try
        {
            Thread.sleep(5000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        long endTime=System.currentTimeMillis();
        long time=endTime-starTime;
        double timeSpan=(double)time/1000;
        System.out.println("time [ "+" ]: "+timeSpan+" 秒");
        System.out.println("hello");
    }

    public static void main(String[] args) {
        HDFSTest testHDFS=new HDFSTest();
        try {
            FileSystem fs=OperaHDFS.fileSystem;
            BufferedReader strin=new BufferedReader(new InputStreamReader(System.in));
            String testDir="";
            while(!testDir.equals("#")) {
                System.out.println("请输入测试用例所在文件夹名称：");
                testDir= strin.readLine();
                System.out.println("开始测试：" + testDir);
                testHDFS.hdfsWritePerformanceTest(testDir);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
