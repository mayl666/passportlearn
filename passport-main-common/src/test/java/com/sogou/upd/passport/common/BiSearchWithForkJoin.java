package com.sogou.upd.passport.common;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-8-6
 * Time: 下午9:24
 * To change this template use File | Settings | File Templates.
 */
public class BiSearchWithForkJoin extends RecursiveAction {
    private final int threshold;
    private final BinarySearchProblem problem;
    public int result;
    private final int numberToSearch;

    public BiSearchWithForkJoin(BinarySearchProblem problem, int threshold, int numberToSearch) {
        this.problem = problem;
        this.threshold = threshold;
        this.numberToSearch = numberToSearch;
    }

    @Override
    protected void compute() {
        if (problem.size < threshold) { //小于阀值，就直接用普通的二分查找
            result = problem.searchSequentially(numberToSearch);
        } else {
            //分解子任务
            int midPoint = problem.size / 2;
            BiSearchWithForkJoin left = new BiSearchWithForkJoin(problem.subProblem(0, midPoint), threshold, numberToSearch);
            BiSearchWithForkJoin right = new BiSearchWithForkJoin(problem.subProblem(midPoint + 1, problem.size), threshold, numberToSearch);
            invokeAll(left, right);
            result = Math.max(left.result, right.result);
        }
//        result = problem.searchSequentially(numberToSearch);
    }

    //构造数据
    private static final int[] data = new int[1000_0000];

    static {
        for (int i = 0; i < 1000_0000; i++) {
            data[i] = i;
        }
    }

    public static void main(String[] args) {
        BinarySearchProblem problem = new BinarySearchProblem(data, 0, data.length);
        int threshold = 100;
        int nThreads = 10;
        //查找100_0000所在的下标
        BiSearchWithForkJoin bswfj = new BiSearchWithForkJoin(problem, threshold, 100_0000);
        ForkJoinPool fjPool = new ForkJoinPool(nThreads);
        long start = System.currentTimeMillis();
        fjPool.invoke(bswfj);
        long end = System.currentTimeMillis();
        System.out.printf("Result is:%d%n", bswfj.result);
        System.out.println("cost time:" + (end - start) + "ms");
    }
}

class BinarySearchProblem {
    private final int[] numbers;
    private final int start;
    private final int end;
    public final int size;

    public BinarySearchProblem(int[] numbers, int start, int end) {
        this.numbers = numbers;
        this.start = start;
        this.end = end;
        this.size = end - start;
    }

    public int searchSequentially(int numberToSearch) {
        //偷懒，不自己写二分查找了
        return Arrays.binarySearch(numbers, start, end, numberToSearch);
    }

    public BinarySearchProblem subProblem(int subStart, int subEnd) {
        return new BinarySearchProblem(numbers, start + subStart, start + subEnd);
    }
}
