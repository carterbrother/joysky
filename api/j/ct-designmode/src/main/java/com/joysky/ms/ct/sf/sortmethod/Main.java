package com.joysky.ms.ct.sf.sortmethod;

public class Main {


    /**
     * 冒泡排序 每一次冒出一个最大的数，多次冒泡排好序  t= n2
     *
     * @param ar
     * @return
     */
    public int[] bubbleSort(int[] ar) {

        for (int i = 0; i < ar.length; i++) {
            for (int j = 0; j < ar.length - 1 - i; j++) {
                if (ar[j] > ar[j + 1]) {
                    int t = ar[j];
                    ar[j] = ar[j + 1];
                    ar[j + 1] = t;
                }
            }
        }
        return ar;
    }


    /**
     * 快速排序  t= nlogn
     *
     * @param ar
     * @return
     */
    public int[] fastSort(int[] ar) {

        
        return ar;
    }


}
