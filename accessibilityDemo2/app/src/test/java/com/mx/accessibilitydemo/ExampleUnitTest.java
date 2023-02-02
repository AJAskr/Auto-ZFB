package com.mx.accessibilitydemo;

import com.mx.accessibilitydemo.utils.ExcelUtli.ExcelToInfo;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void test() {
        boolean b = ExcelToInfo.XlsxExcelA("C:\\Users\\18584\\Desktop\\祝在旭3.xlsx", "C:\\Users\\18584\\Desktop\\info.txt", 1, ";", "法人", "电话", "");
        if (b){
            System.out.println("ok");
        }else {
            System.out.println("error");
        }
    }

    @Test
    public void test2(){

    }
}