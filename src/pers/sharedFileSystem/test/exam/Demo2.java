package pers.sharedFileSystem.test.exam;

/**
 * Created by dell on 2016/8/30.
 */
public class Demo2 extends Demo1{
    public Demo2(){
        System.out.println("demo2");
    }
    public void m1(){
        System.out.println(this.age);
        System.out.println(this.str1);
    }
    public void testThread(){
        Thread thread =new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("pang");
            }
        });
        thread.start();
        System.out.println("ping");
    }
    public int add(int a){
        return a+1;
    }
    public static void main(String[] args) {
        Demo1 demo1=new Demo1();
        Demo2 demo2=new Demo2();
        demo2.testThread();
        System.out.println(demo2.add(1,2));
        String s;
        System.out.println("s=");
    }
}
