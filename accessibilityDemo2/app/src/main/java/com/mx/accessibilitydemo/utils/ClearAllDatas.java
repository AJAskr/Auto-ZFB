package com.mx.accessibilitydemo.utils;

import java.io.File;

public class ClearAllDatas {
    public static void clear(String rootPath){
        String path1 = rootPath+"/advInfo.txt";
        String path2 = rootPath+"/info.txt";
        String path3 = rootPath+"/info3.txt";
        String path5 = rootPath+"result.txt";
        String path4 = rootPath+"/结果表格.xlsx";
        String[] allPath = new String[]{path1,path2,path3,path4,path5};
        for (String s : allPath) {
            File file = new File(s);
            if (file.exists()) file.delete();
        }
    }
}
