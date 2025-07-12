package com.joysky.ms.ct.aitest;

import java.util.Optional;

public class Npe {

    public static void main(String[] args) {
        Integer i = null;
        
        String result = Optional.ofNullable(i)
                .map(Object::toString)
                .orElse("值为 null");
                
        System.out.println(result);
    }

}