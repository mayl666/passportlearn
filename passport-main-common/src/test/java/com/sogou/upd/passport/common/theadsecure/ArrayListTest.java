package com.sogou.upd.passport.common.theadsecure;

import org.junit.Ignore;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 测试使用静态变量List导致的越界异常
 * 初始化List时候只给Size赋值，还未对list赋值时就访问get，产生了异常
 * User: shipengzhi
 * Date: 13-11-14
 * Time: 下午9:04
 * To change this template use File | Settings | File Templates.
 */
@Ignore
public class ArrayListTest {
    public static ExecutorService pool = Executors.newFixedThreadPool(200);

    static String random;
    static ArrayList<String> list;

    public static void addValue() {
        list.add("a");
        list.add("b");
        list.add("c");
    }

    static String getValue() {
        try {
            random = list.get(0) + list.get(1) + list.get(2);
//                random = list.get(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return random;
    }

    static class NewTask implements Runnable {
        @Override
        public void run() {
            list = new ArrayList<String>(3);
            addValue();
        }
    }

    static class AddTask implements Runnable {
        @Override
        public void run() {
            addValue();
        }
    }

    static class GetTask implements Runnable {
        @Override
        public void run() {
            getValue();
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100000000; i++) {
            pool.execute(new NewTask());
            pool.execute(new GetTask());
        }
    }
}
