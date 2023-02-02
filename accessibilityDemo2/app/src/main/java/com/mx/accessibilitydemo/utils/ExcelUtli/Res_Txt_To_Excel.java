package com.mx.accessibilitydemo.utils.ExcelUtli;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.List;


public class Res_Txt_To_Excel {

    public static boolean savaResXlsx(String advPath, String resPath, String savepath) {
        List<String> list = ExcelToInfo.headList(advPath);
        List<List<String>> dataList = ExcelToInfo.DataList1(advPath);
        int numIndex = 0;
        int qtnumIndex = 0;
        int headRow = 0;
        int flag = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(advPath));
            String line;
            int cnt = 0;
            while ((line = br.readLine()) != null) {
                if (cnt == 1) {
                    numIndex = Integer.parseInt(line.split(";")[1]);
                    if (line.split(";").length == 3) {
                        flag = 0;
                        qtnumIndex = Integer.parseInt(line.split(";")[2]);
                    } else {
                        flag = 1;
                    }
                }
                cnt++;
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet1 = workbook.createSheet("sheet1");
        XSSFRow row;
        XSSFCell cell;
        row = sheet1.createRow(0);
        sheet1.setDefaultColumnWidth(4);
        int tempIndex = 0;
        //创建标题
        if (list == null) return false;
        for (String i : list) {
            sheet1.setColumnWidth(tempIndex, 18 * 256);
            cell = row.createCell(tempIndex);
            cell.setCellValue(i);
            tempIndex++;
        }
        //创建数据
        try {
            if (flag == 0) {
                headRow++;
                BufferedReader br = new BufferedReader(new FileReader(resPath));
                String line;
                int cnt = 0;
                while ((line = br.readLine()) != null) {
                    if (!endStr(line.split(";")).equals("false") && !endStr(line.split(";")).equals("false\n")) {
                        row = sheet1.createRow(headRow);
                        String tempStr = endStr(line.split(";"));
                        if (tempStr.contains("\n")) {
                            tempStr = tempStr.replace("\n", "");
                        }
                        int indexNum = Integer.parseInt(tempStr);

                        for (int j = 0; j < dataList.get(headRow).size(); j++) {
                            cell = row.createCell(j);
                            String number = line.split(";")[indexNum + 2];
                            if (j == numIndex) {
                                cell.setCellValue(number);
                            } else if (j == qtnumIndex) {
                                cell.setCellValue("");
                            } else {
                                try {
                                    cell.setCellValue(dataList.get(cnt).get(j));
                                }catch (Exception e){
                                    continue;
                                }
                            }
                        }
                        headRow++;
                    }
                    cnt++;
                }
            } else {
                headRow++;
                BufferedReader br = new BufferedReader(new FileReader(resPath));
                String line;
                int cnt = 0;
                while ((line = br.readLine()) != null) {
                    if (!endStr(line.split(";")).equals("false") && !endStr(line.split(";")).equals("false\n")) {
                        row = sheet1.createRow(headRow);
                        String tempStr = endStr(line.split(";"));
                        if (tempStr.contains("\n")) {
                            tempStr = tempStr.replace("\n", "");
                        }
                        int indexNum = Integer.parseInt(tempStr);

                        for (int j = 0; j < dataList.get(headRow).size(); j++) {
                            cell = row.createCell(j);
                            String number = line.split(";")[indexNum + 2];
                            if (j == numIndex) {
                                cell.setCellValue(number);
                            } else {
                                cell.setCellValue(dataList.get(cnt).get(j));
                            }
                        }
                        headRow++;
                    }
                    cnt++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        //创建结果表格
        try {
            File file = new File(savepath);
            if (file.exists()) {
                boolean yz = file.delete();
                if (!yz) {
                    return false;
                }
            }
            FileOutputStream out = new FileOutputStream(file);
            workbook.write(out);
            out.close();
            File file1 = new File(resPath);
            if (file1.exists()) {
                boolean yz = file1.delete();
                if (!yz) {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static String endStr(String[] a) {
        String res = "";
        for (String i : a) {
            res = i;
        }
        return res;
    }
}
