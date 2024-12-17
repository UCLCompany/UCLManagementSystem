package com.example.excel;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@SpringBootApplication
public class ExcelApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExcelApplication.class, args);
    }
    FileInputStream templateFile = new FileInputStream();
    Workbook workbook = new XSSFWorkbook(templateFile);
}
