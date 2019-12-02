package main.java;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReadWriteExcel {

	public static void main(String[] args) {
		try {
			FileInputStream file = new FileInputStream(new File(".//RituExcel4.xlsx"));

			// Create Workbook instance holding reference to .xlsx file
			XSSFWorkbook workbook = new XSSFWorkbook(file);

			// Get first/desired sheet from the workbook
			XSSFSheet sheet = workbook.getSheetAt(0);

			FileInputStream fileToWrite = new FileInputStream(new File(".//demoResult4.xlsx"));
			XSSFWorkbook workbookToWrite = new XSSFWorkbook(fileToWrite); 
	         
	        //Create a blank sheet
	        XSSFSheet sheetToWrite = workbookToWrite.getSheetAt(0);
	        sheetToWrite.getSheetName();
	        FileOutputStream outFile = new FileOutputStream(new File(".//demoResult4.xlsx"));

			// Iterate through each rows one by one
			Iterator<Row> rowIterator = sheet.iterator();
			int rownum = 0;
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				// For each row, iterate through all the columns
				Iterator<Cell> cellIterator = row.cellIterator();
				Row rowToWrite = sheetToWrite.createRow(rownum++);

				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					// Check the cell type and format accordingly

					String cellContent = cell.toString();
					StringBuffer newCellContent = new StringBuffer();
					char[] ch = new char[cellContent.length()];

					for (int i = 0; i < cellContent.length(); i++) {
						ch[i] = cellContent.charAt(i);
						String stringToBeChecked = Character.toString(ch[i]);
						if (stringToBeChecked.matches("[\\p{Graph}\\s]+")) {
							newCellContent.append(stringToBeChecked);
						}
					}

					System.out.println(newCellContent);

					// wrtite out the content in the new workbook
					Cell cellToWrite = rowToWrite.createCell(0);
					cellToWrite.setCellValue(String.valueOf(newCellContent));
					
					
					workbookToWrite.write(outFile);
				}
			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
