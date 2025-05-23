package com.jinyu.utils;

import java.util.Scanner;

public class Utility {
    public static String readString(int length){
        Scanner sc = new Scanner(System.in);
        String input = sc.next();
        if(input.length() > length){
            input = input.substring(0,length);
        }
        return input;
    }
}
