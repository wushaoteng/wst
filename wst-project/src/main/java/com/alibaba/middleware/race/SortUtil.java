package com.alibaba.middleware.race;

import java.util.List;

/**
 * Created by tingshu.zb on 17/4/8.
 */
public class SortUtil {

    public static void quickSort(List<Long> arr) {
        long start = System.currentTimeMillis();
        qsort(arr, 0, arr.size() - 1);
        System.out.println("QuickSort cost:" + (System.currentTimeMillis() - start));

    }

    private static void qsort(List<Long> arr, int low, int high) {
        if (low < high) {
            int pivot = partition(arr, low, high);        //将数组分为两部分
            qsort(arr, low, pivot - 1);                   //递归排序左子数组
            qsort(arr, pivot + 1, high);                  //递归排序右子数组
        }
    }

    private static int partition(List<Long> arr, int low, int high) {
        Long pivot = arr.get(low);     //枢轴记录
        while (low < high) {
            while (low < high && arr.get(high) >= pivot) {
                --high;
            }
            arr.set(low, arr.get(high));            //交换比枢轴小的记录到左端
            while (low < high && arr.get(low) <= pivot) {
                ++low;
            }
            arr.set(high, arr.get(low));           //交换比枢轴小的记录到右端
        }
        //扫描完成，枢轴到位
        arr.set(low, pivot);
        //返回的是枢轴的位置
        return low;
    }

}
