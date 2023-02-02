package com.mx.accessibilitydemo.utils.ExcelUtli;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExcelToInfo {
    //获取表格总数据
    public static int DataLen(int headRow, String path) {
        File file = new File(path);
        if (!file.exists() || !checkIfExcelFile(file)) {
            return 0;
        }
        InputStream in;
        Workbook wk;
        try {
            in = new FileInputStream(file);
            wk = WorkbookFactory.create(in);
        } catch (Exception e) {
            return 0;
        }
        Workbook sheets = wk;
        Sheet sheetAt = sheets.getSheetAt(0);
        return sheetAt.getPhysicalNumberOfRows() - headRow;
    }

    //判断是否为想xlsx xls文件
    public static boolean checkIfExcelFile(File file) {
        if (file == null) {
            return false;
        }
        String name = file.getName();
        String[] list = name.split("\\.");
        if (list.length < 2) {
            return false;
        }
        String typeName = list[list.length - 1];
        return "xls".equals(typeName) || "xlsx".equals(typeName);
    }

    public static boolean checkIfExcelFile2(String path) {
        if (path == null || path.equals("")) {
            return false;
        }
        String hz = path.split("\\.")[1];
        return hz.equals("xlsx") || hz.equals("xls");
    }

    //xlsx以及xsl的解决方案 让调用者不再写多余代码
    public static boolean XlsxExcelA(String ExcelPath, String InfoSavePath, int headRow, String splitStr, String col1, String col2, String col3) {
        headRow -= 1;
        File file = new File(ExcelPath);
        if (!file.exists() && !checkIfExcelFile(file)) {
            return false;
        }
        InputStream in;
        Workbook sheets;

        try {
            in = new FileInputStream(file);
            sheets = WorkbookFactory.create(in);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        //1-;-法定代表人-电话-其他电话
        Sheet sheetAt = sheets.getSheetAt(0);
        Row TitleRow = sheetAt.getRow(headRow);
        if (checkIfExcelFile2(ExcelPath)) {
            if (col3.length() > 0) {
                int[] resIndex = new int[3];
                for (int i = 0; i < TitleRow.getPhysicalNumberOfCells(); i++) {
                    Cell cell = TitleRow.getCell(i);
                    if (cell.getStringCellValue().equals(""))
                        cell.setCellType(Cell.CELL_TYPE_STRING);
                    String stringCellValue = cell.getStringCellValue();
                    if (stringCellValue.equals(col1)) {
                        resIndex[0] = i;
                    }
                    if (stringCellValue.equals(col2)) {
                        resIndex[1] = i;
                    }
                    if (stringCellValue.equals(col3)) {
                        resIndex[2] = i;
                        break;
                    }
                }
                //判断电话 其他电话的列名是否正常
                if (resIndex[1] == 0 || resIndex[2] == 0) return false;
                //获取总行数
                int AllRowCnt = sheetAt.getPhysicalNumberOfRows();
                //结果保存
                List<String> resList = new ArrayList<>();
                String[] tempStr = new String[4];
                for (int i = headRow + 1; i < AllRowCnt; i++) {
                    Row row = sheetAt.getRow(i);
                    for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {
                        Cell cell = row.getCell(j);
                        if (cell.getStringCellValue().equals("")) {
                            cell.setCellType(Cell.CELL_TYPE_STRING);
                        }
                        String stringCellValue = cell.getStringCellValue();
                        if (j == resIndex[0]) {
                            tempStr[1] = stringCellValue;
                        }
                        if (j == resIndex[1]) {
                            tempStr[2] = stringCellValue;
                        }
                        if (j == resIndex[2]) {
                            tempStr[3] = stringCellValue;
                        }
                    }
                    if (!tempStr[1].equals("")) {
                        tempStr[0] = i - headRow + "";
                        String resStr = tempStr[0] + ";" + tempStr[1] + ";";
                        if (!tempStr[2].contains("-") && tempStr[2].length()==11){
                            resStr += tempStr[2]+";";
                        }
                        String[] splitT = tempStr[3].split(splitStr);
                        for (String s : splitT){
                            if (!s.contains("-") && s.length() == 11 && !s.equals(tempStr[2])) {
                                resStr += s + ";";
                            }
                        }
                        resList.add(resStr);
                    }
                    Arrays.fill(tempStr,"");
                }
                return save_Info(resList,InfoSavePath) && save_advInfo(resIndex,headRow,InfoSavePath,ExcelPath,0);
            } else {
                int[] resIndex = new int[2];
                for (int i = 0; i < TitleRow.getPhysicalNumberOfCells(); i++) {
                    Cell cell = TitleRow.getCell(i);
                    try {
                        if (cell.getStringCellValue().equals("") && cell.getStringCellValue().length()==0){
                            cell.setCellType(Cell.CELL_TYPE_STRING);
                        }
                    }catch (Exception e){
                        cell.setCellType(Cell.CELL_TYPE_STRING);
                    }
                    String stringCellValue = cell.getStringCellValue();
                    if (stringCellValue.equals(col1)) {
                        resIndex[0] = i;
                    }
                    if (stringCellValue.equals(col2)) {
                        resIndex[1] = i;
                    }
                    if (stringCellValue.equals(col3)) {
                        resIndex[2] = i;
                        break;
                    }
                }
                //判断电话 其他电话的列名是否正常
                if (resIndex[1] == 0) return false;
                //获取总行数
                int AllRowCnt = sheetAt.getPhysicalNumberOfRows();
                //结果保存
                List<String> resList = new ArrayList<>();
                String[] tempStr = new String[4];
                for (int i = headRow + 1; i < AllRowCnt; i++) {
                    Row row = sheetAt.getRow(i);
                    for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {
                        Cell cell = row.getCell(j);
                        try {
                            if (cell.getStringCellValue().equals("")){
                                cell.setCellType(Cell.CELL_TYPE_STRING);
                            }
                        }catch (Exception e){
                            cell.setCellType(Cell.CELL_TYPE_STRING);
                        }
                        String stringCellValue = cell.getStringCellValue();
                        if (j == resIndex[0]) {
                            tempStr[1] = stringCellValue;
                        }
                        if (j == resIndex[1]) {
                            tempStr[2] = stringCellValue;
                        }
                    }
                    if (!tempStr[1].equals("")) {
                        tempStr[0] = i - headRow + "";
                        String resStr = tempStr[0] + ";" + tempStr[1] + ";";
                        String[] splitT = tempStr[2].split(splitStr);
                        for (String s : splitT){
                            if (!s.contains("-") && s.length() == 11) {
                                resStr += s + ";";
                            }
                        }
                        resList.add(resStr);
                    }
                    Arrays.fill(tempStr,"");
                }
                return save_Info(resList,InfoSavePath) && save_advInfo(resIndex,headRow,InfoSavePath,ExcelPath,1);
            }
        } else {
            return false;
        }
    }

    /**
     * 保存为info.txt
     */
    public static boolean save_Info(List<String> list, String savePath) {
        File file = new File(savePath);
        File file1 = new File(savePath.replace("info","info3"));
        if (file.exists()){
            file.delete();
        }
        if (file1.exists()){
            file1.delete();
        }
        try {
            boolean t = file.createNewFile();
            if (!t) return false;
            FileWriter fw = new FileWriter(file);
            FileWriter fw2 = new FileWriter(file1);
            for (String i : list) {
                fw.write(i + "\n");
                fw2.write(i + "\n");
                fw.flush();
                fw2.flush();
            }
            fw.close();
            fw2.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * advInfo.txt
     */
    public static boolean save_advInfo(int[] resIndex,int headRow,String path,String xlsxPath,int tnum){
        try {
            path = path.replace("info","advInfo");
            File file = new File(path);
            if (file.exists()){
                boolean yz = file.delete();
                if (!yz) return false;
            }
            boolean t = file.createNewFile();
            if (!t) return false;
            FileWriter fw = new FileWriter(file);
            if (tnum==0){
                fw.write(headRow+"\n");
                fw.write(resIndex[0]+";"+resIndex[1]+";"+resIndex[2]+";"+"\n");
                fw.write(xlsxPath);
            }else if (tnum == 1){
                fw.write(headRow+"\n");
                fw.write(resIndex[0]+";"+resIndex[1]+";"+"\n");
                fw.write(xlsxPath);
            }else {
                fw.write("error");
            }
            fw.flush();
            fw.close();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 返回列头数据
     */
    public static List<String> headList(String advInfoPath){
        List<String> list = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(advInfoPath));
            String line;
            String temp="";
            int headRow = 0;
            int cnt = 0;
            while ((line = br.readLine())!=null){
                if (cnt==0){
                    headRow = Integer.parseInt(line);
                    cnt++;
                }
                temp = line;
            }
            br.close();
            File file = new File(temp);
            if (!file.exists() || !checkIfExcelFile(file)) {
                return list;
            }
            InputStream in;
            Workbook sheets;
            in = new FileInputStream(file);
            sheets = WorkbookFactory.create(in);
            // 读取整个Excel
            // 获取第一个表单Sheet
            Sheet sheetAt = sheets.getSheetAt(0);
//        List<Map<String, String>> list = new ArrayList<>();
            //获取标题行
            Row titleRow = sheetAt.getRow(headRow);
            for (int i = 0;i<titleRow.getPhysicalNumberOfCells();i++){
                Cell cell = titleRow.getCell(i);
                try {
                    if (cell.getStringCellValue().equals("")){
                        cell.setCellType(Cell.CELL_TYPE_STRING);
                    }
                }catch (Exception e){
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                }
                String stringCellValue = cell.getStringCellValue();
                list.add(stringCellValue);
            }
            in.close();
        }catch (Exception e){
            return list;
        }
        return list;
    }

    /**
     * 返回列内数据
     */
    public static List<List<String>> DataList1(String advInfoPath){
        List<List<String>> resList = new ArrayList<>();
        List<String> list = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(advInfoPath));
            String line;
            String temp="";
            int headRow = 0;
            int cnt = 0;
            int xm=0;
            while ((line = br.readLine())!=null){
                if (cnt==0){
                    headRow = Integer.parseInt(line);
                }else if (cnt==1){
                    xm = Integer.parseInt(line.split(";")[1]);
                }
                cnt++;
                temp = line;
            }
            br.close();
            File file = new File(temp);
            if (!file.exists() || !checkIfExcelFile(file)) {
                return resList;
            }
            InputStream in;
            Workbook wk;
            in = new FileInputStream(file);
            wk = WorkbookFactory.create(in);
            // 读取整个Excel
            Workbook sheets = wk;
            // 获取第一个表单Sheet
            Sheet sheetAt = sheets.getSheetAt(0);
            for (int i = headRow+1;i<sheetAt.getPhysicalNumberOfRows();i++){
                Row row = sheetAt.getRow(i);
                for (int index = 0;index<row.getPhysicalNumberOfCells();index++){
                    Cell cell = row.getCell(index);
                    try {
                        if (cell.getStringCellValue().equals("")){
                            cell.setCellType(Cell.CELL_TYPE_STRING);
                        }
                    }catch (Exception e){
                        cell.setCellType(Cell.CELL_TYPE_STRING);
                    }
                    String stringCellValue = cell.getStringCellValue();
                    list.add(stringCellValue);
                }
                if (!list.get(xm).equals("")){
                    resList.add(list);
                    list = new ArrayList<>();
                }
            }
            in.close();
        }catch (Exception e){
            return resList;
        }
        return resList;
    }
}

