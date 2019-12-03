package main.java.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class WorkBookUtil {

	public static XSSFSheet getWorkbookSheet(String fileName) throws IOException {
		FileInputStream infile = new FileInputStream(new File(fileName));
		XSSFWorkbook testSampleWorkbook = new XSSFWorkbook(infile);
		XSSFSheet testSampleSheet = testSampleWorkbook.getSheetAt(0);

		return testSampleSheet;
	}
}
