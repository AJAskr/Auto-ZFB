package com.mx.accessibilitydemo.utils.ExcelUtli;

import java.io.BufferedReader;
import java.io.FileReader;

public class InfoLen {
    public static int getLen(String path){
        int cnt = 0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            while (reader.readLine() !=null){
                cnt++;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return cnt;
    }

    public static int getNumberSum(String path){
        int cnt = 0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String tempLine;
            while ((tempLine = reader.readLine())!=null){
                cnt+=tempLine.split(";").length-2;
            }
        }catch (Exception e){
            e.printStackTrace();;
        }
        return cnt;
    }
}
