package pers.sharedFileSystem.test.exam;

/**
 * Created by dell on 2016/8/30.
 */
public class Demo2 extends Demo1{
    public void m1(){
        System.out.println(this.age);
        System.out.println(this.str1);
    }
    public static void main(String[] args) {
        Demo2 demo2=new Demo2();
        demo2.m1();
        Demo1 demo1=new Demo1();
        System.out.println(demo1.age);
        System.out.println(demo1.str1);
    }
}
