package pers.sharedFileSystem.test;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 操作 HDFS 的工具类
 */
public class OperaHDFS {
    /**
     * 加载配置文件
     */
    static Configuration conf = new Configuration();

    /**
     * 重名名一个文件夹或者文件
     *@param sourceFilePath 文件在HDFS的相对路径（带文件名和后缀名）
     * @param destFilePath 文件在HDFS的相对路径（带文件名和后缀名）
     * @throws Exception
     */
    public static void renameFileOrDirectoryOnHDFS(String sourceFilePath, String destFilePath) throws Exception {
        FileSystem fs = FileSystem.get(conf);
        Path p1 = new Path(sourceFilePath);
        Path p2 = new Path(destFilePath);
        fs.rename(p1, p2);
        fs.close();//释放资源
        System.out.println("重命名文件夹或文件成功["+sourceFilePath+"] => ["+destFilePath+"]");
    }

    /**
     * 读取HDFS某个文件夹的所有文件，并打印
     * @param dirPath HDFS的文件夹相对路径
     * @throws Exception
     */
    public static void readHDFSListAll(String dirPath) throws Exception {
        //流读入和写入
        InputStream in = null;
        //获取HDFS的conf
        //读取HDFS上的文件系统
        FileSystem hdfs = FileSystem.get(conf);
        //使用缓冲流，进行按行读取的功能
        BufferedReader buff = null;
        //获取日志文件的根目录
        Path listf = new Path(dirPath);
        //获取根目录下的所有2级子文件目录
        FileStatus stats[] = hdfs.listStatus(listf);
        //自定义j，方便查看插入信息
        int j = 0;
        for (int i = 0; i < stats.length; i++) {
            //获取子目录下的文件路径
            FileStatus temp[] = hdfs.listStatus(new Path(stats[i].getPath().toString()));
            for (int k = 0; k < temp.length; k++) {
                System.out.println("文件路径:" + temp[k].getPath().toString());
                //获取Path
                Path p = new Path(temp[k].getPath().toString());
                //打开文件流
                in = hdfs.open(p);
                //BufferedReader包装一个流
                buff = new BufferedReader(new InputStreamReader(in));
                String str = null;
                while ((str = buff.readLine()) != null) {
                    System.out.println(str);
                }
                buff.close();
                in.close();
            }
        }
        hdfs.close();
    }

    /**
     * 从HDFS上下载文件或文件夹到本地
     * @param sourceFilePath 文件在HDFS的相对路径（带文件名和后缀名）
     * @param destFilePath 本地文件夹绝对路径
     * @throws Exception
     */
    public static void downloadFileorDirectoryOnHDFS(String sourceFilePath, String destFilePath)throws Exception{
        FileSystem fs=FileSystem.get(conf);
        Path p1 =new Path(sourceFilePath);
        Path p2 =new Path(destFilePath);
        fs.copyToLocalFile(p1, p2);
        fs.close();//释放资源
        System.out.println("下载文件夹或文件成功["+destFilePath+"]");
    }

    /**
     * 在HDFS上创建一个文件夹
     * @param relativeDirPath 文件夹相对路径
     * @throws Exception
     */
    public static void createDirectoryOnHDFS(String relativeDirPath)throws Exception{
        FileSystem fs=FileSystem.get(conf);
        Path p =new Path(relativeDirPath);
        fs.mkdirs(p);
        fs.close();//释放资源
        System.out.println("创建文件夹成功 ["+relativeDirPath+"]");
    }

    /**
     * 在HDFS上删除一个文件夹
     * @param relativeDirPath 文件夹相对路径
     * @throws Exception
     */
    public static void deleteDirectoryOnHDFS(String relativeDirPath)throws Exception{
        FileSystem fs=FileSystem.get(conf);
        Path p =new Path(relativeDirPath);
        fs.deleteOnExit(p);
        fs.close();//释放资源
        System.out.println("删除文件夹成功 ["+relativeDirPath+"]");
    }

    /**
     * 在HDFS上创建一个文件
     * newFilePath 创建的文件在HDFS的相对路径（带文件名和后缀名）
     * @throws Exception
     */
    public static void createFileOnHDFS(String newFilePath)throws Exception{
        FileSystem fs=FileSystem.get(conf);
        Path p =new Path(newFilePath);
        fs.createNewFile(p);
        //fs.create(p);
        fs.close();//释放资源
        System.out.println("创建文件成功["+newFilePath+"]");
    }

    /**
     * 在HDFS上删除一个文件
     * @param destPath 上传到HDFS的相对路径（带文件名和后缀名）
     * @throws Exception
     */
    public static void deleteFileOnHDFS(String destPath)throws Exception{
        FileSystem fs=FileSystem.get(conf);
        Path p =new Path(destPath);
        fs.deleteOnExit(p);
        fs.close();//释放资源
        System.out.println("删除文件成功["+destPath+"]");
    }

    /**
     * 上传本地文件到HDFS上
     * @param sourcePath 本地文件的绝对路径
     * @param destPath 上传到HDFS的相对路径（带文件名和后缀名）
     * @throws Exception
     */
    public static void uploadFile(String sourcePath, String destPath)throws Exception{
        //加载默认配置
        FileSystem fs=FileSystem.get(conf);
        //本地文件
        Path src =new Path(sourcePath);
        //HDFS为止
        Path dst =new Path(destPath);
        try {
            fs.copyFromLocalFile(src, dst);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("上传文件成功["+destPath+"]");
        fs.close();//释放资源
    }

    public static void main(String[] args) throws Exception {
        /**
         *    示例：  "/hadoop/myfile"
         */
//        createDirectoryOnHDFS("/hadoop/myfile/test");

        /**
         *    示例：  "/hadoop/myfile"
         */
//        deleteDirectoryOnHDFS("/hadoop/myfile/test2");

        /**
         *     示例：  "E:\图片视频\1.flv"     "/hadoop/myfile/1.flv"
         */
//        uploadFile("E:\\图片视频\\大话设计模式.rar", "/hadoop/myfile/test2/sheji1.rar");

        /**
         *     示例：  "/hadoop/myfile/1.flv"
         */
//        deleteFileOnHDFS("/hadoop/myfile/test1/1.jpg");

        /**
         *    示例：  "/hadoop/myfile/test1"         "/hadoop/myfile/test2"
         */
//        renameFileOrDirectoryOnHDFS("/hadoop/myfile/test1", "/hadoop/myfile/test2");

        /**
         *     示例：  "/hadoop/myfile/test2"
         */
//        readHDFSListAll("/hadoop/myfile/test2");

        /**
         *    示例：  "/hadoop/myfile/test1"         "E:\\ftpServer"
         */
//        downloadFileorDirectoryOnHDFS("/hadoop/myfile/test2", "E:\\ftpServer");

        /**
         *     示例：  "/hadoop/myfile/test2/a.txt"
         */
//        createFileOnHDFS("/hadoop/myfile/test2/a.txt");
    }
}
