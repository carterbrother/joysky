package com.joysky.ms.ct.sf.sortmethod;


import java.util.Arrays;

public class TestMain {
    int[] arr = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    int[] arr2 = {5, 6, 7, 8, 9, 1, 2, 3, 4, 10};
    int[] arr3 = {4, 5, 6, 7, 8, 9, 1, 2, 3, 10};
    int[] arr4 = {9, 100, 27, 73, 555, 41, 68, 237, 9518, 10};

    
    public void testBubbleSort() {

        Arrays.stream(new Main().bubbleSort(arr)).forEach(System.out::println);
        System.out.println("============");
        Arrays.stream(new Main().bubbleSort(arr2)).forEach(System.out::println);
        System.out.println("============");

        Arrays.stream(new Main().bubbleSort(arr3)).forEach(System.out::println);
        System.out.println("============");

        Arrays.stream(new Main().bubbleSort(arr4)).forEach(System.out::println);


    }

}
