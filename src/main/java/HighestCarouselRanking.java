package main.java;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.opencsv.CSVWriter;
/*
 * Choose the carousel with higest rank for an entity
 * and writes to a final excel the carousels.
 */
public class HighestCarouselRanking {

	public static void finalCarousels(HashMap<String, Integer> downwardRId, HashMap<String, Integer> sidewardRId)
			throws IOException {
		// Reader dcreader; Reader screader;

		// ===============Data For Downward Carousel=================
		FileInputStream fileToWrite = new FileInputStream(new File("./testFolder/FinalDownwardCarousel.xlsx"));
		XSSFWorkbook workbookToWrite = new XSSFWorkbook(fileToWrite);
		XSSFSheet sheetToWrite = workbookToWrite.getSheetAt(0);
		FileOutputStream outFile = new FileOutputStream(new File("./testFolder/FinalDownwardCarousel.xlsx"));

		// ===============Data For Sideward Carousel=================
		FileInputStream fileToWrite1 = new FileInputStream(new File("./testFolder/FinalSidewardCarousel.xlsx"));
		XSSFWorkbook workbookToWrite1 = new XSSFWorkbook(fileToWrite1);
		XSSFSheet sheetToWrite1 = workbookToWrite1.getSheetAt(0);
		FileOutputStream outFile1 = new FileOutputStream(new File("./testFolder/FinalSidewardCarousel.xlsx"));

		FileInputStream dcfile = new FileInputStream(new File("./testFolder/DownwardCarousel.xlsx"));
		FileInputStream scfile = new FileInputStream(new File("./testFolder/SidewardCarousel.xlsx"));

		// Create Workbook instance holding reference to .xlsx file
		XSSFWorkbook workbook1 = new XSSFWorkbook(dcfile);
		XSSFWorkbook workbook2 = new XSSFWorkbook(scfile);

		// Get first/desired sheet from the workbook
		XSSFSheet sheet1 = workbook1.getSheetAt(0);
		XSSFSheet sheet2 = workbook2.getSheetAt(0);

		Iterator<Row> rowIterator1 = sheet1.iterator();
		Iterator<Row> rowIterator2 = sheet2.iterator();

		while (rowIterator1.hasNext()) {
			Row row = rowIterator1.next();
			Row rowToWrite = sheetToWrite.createRow(row.getRowNum());
			String title = null;
			String fact1 = null;
			String fact2 = null;
			String pivotEntity = null;

			Iterator<Cell> cellIterator = row.cellIterator();
			String members = null;
			int cellNumber = 0;

			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				if (cellNumber == 0) {
					pivotEntity = cell.toString();
				} else if (cellNumber == 1) {
					title = cell.toString();
				} else if (cellNumber == 2) {
					fact1 = cell.toString();
				} else if (cellNumber == 3) {
					fact2 = cell.toString();
				}else if (cellNumber == 4) {
					members = cell.toString();
				}
				cellNumber++;
			}
			for (int i : downwardRId.values()) {
				if (row.getRowNum() == i) {

					Cell entityCell = rowToWrite.createCell(0);
					entityCell.setCellValue(pivotEntity);

					Cell titleCell = rowToWrite.createCell(1);
					titleCell.setCellValue(title);

					Cell fact1Cell = rowToWrite.createCell(2);
					fact1Cell.setCellValue(fact1);
					
					Cell fact2Cell = rowToWrite.createCell(3);
					fact2Cell.setCellValue(fact2);

					Cell memberCell = rowToWrite.createCell(4);
					memberCell.setCellValue(members);

				}
			}

		}

		while (rowIterator2.hasNext()) {
			Row row = rowIterator2.next();
			Row rowToWrite = sheetToWrite1.createRow(row.getRowNum());
			String title = null;
			String fact1 = null;
			String fact2 = null;
			String pivotEntity = null;

			Iterator<Cell> cellIterator = row.cellIterator();
			String members = null;
			int cellNumber = 0;

			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				if (cellNumber == 0) {
					pivotEntity = cell.toString();
				} else if (cellNumber == 1) {
					title = cell.toString();
				} else if (cellNumber == 2) {
					fact1 = cell.toString();
				} else if (cellNumber == 3) {
					fact2 = cell.toString();
				}else if (cellNumber == 4) {
					members = cell.toString();
				}
				cellNumber++;
			}
			for (int i : sidewardRId.values()) {
				if (row.getRowNum() == i) {

					Cell entityCell = rowToWrite.createCell(0);
					entityCell.setCellValue(pivotEntity);

					Cell titleCell = rowToWrite.createCell(1);
					titleCell.setCellValue(title);

					Cell fact1Cell = rowToWrite.createCell(2);
					fact1Cell.setCellValue(fact1);
					
					Cell fact2Cell = rowToWrite.createCell(2);
					fact2Cell.setCellValue(fact2);

					Cell memberCell = rowToWrite.createCell(3);
					memberCell.setCellValue(members);

				}
			}

		}
		workbookToWrite.write(outFile); 
		workbookToWrite.close();
		workbookToWrite1.write(outFile1);
		workbookToWrite1.close();

		// ============================================
		/*
		 * dcreader =
		 * Files.newBufferedReader(Paths.get(".//DownwardCarousel.csv"),Charset.forName(
		 * "ISO-8859-1")); screader =
		 * Files.newBufferedReader(Paths.get(".//SidewardCarousel.csv"),Charset.forName(
		 * "ISO-8859-1")); CSVParser dccsvParser = new CSVParser(dcreader,
		 * CSVFormat.DEFAULT); CSVParser sccsvParser = new CSVParser(screader,
		 * CSVFormat.DEFAULT);
		 * 
		 * File file1 = new File(".//FinalDownwardCarousel.csv"); File file2 = new
		 * File(".//FinalSidewardCarousel.csv"); FileWriter outputFile1 = new
		 * FileWriter(file1); FileWriter outputFile2 = new FileWriter(file2); CSVWriter
		 * writer1 = new CSVWriter(outputFile1); CSVWriter writer2 = new
		 * CSVWriter(outputFile2);
		 * 
		 * for(CSVRecord dccsvrecord : dccsvParser) { for(int i : downwardRId.values())
		 * { if(dccsvrecord.getRecordNumber() == i) { String data[] =
		 * {dccsvrecord.get(0), dccsvrecord.get(1), dccsvrecord.get(2),
		 * dccsvrecord.get(3)}; writer1.writeNext(data); } } }
		 * 
		 * for(CSVRecord sccsvrecord : sccsvParser) { for(int i : sidewardRId.values())
		 * { if(sccsvrecord.getRecordNumber() == i) { String data[] =
		 * {sccsvrecord.get(0), sccsvrecord.get(1), sccsvrecord.get(2),
		 * sccsvrecord.get(3)}; writer2.writeNext(data); } } } writer1.close();
		 * writer2.close();
		 */
	}

	public static void main(String[] args) throws IOException {
		// Reader dcreader; Reader screader;

		FileInputStream dcfile = new FileInputStream(new File("./testFolder/DownwardCarouselRanking.xlsx"));
		FileInputStream scfile = new FileInputStream(new File("./testFolder/SidewardCarouselRanking.xlsx"));

		// Create Workbook instance holding reference to .xlsx file
		XSSFWorkbook workbook1 = new XSSFWorkbook(dcfile);
		XSSFWorkbook workbook2 = new XSSFWorkbook(scfile);

		// Get first/desired sheet from the workbook
		XSSFSheet sheet1 = workbook1.getSheetAt(0);
		XSSFSheet sheet2 = workbook2.getSheetAt(0);

		Iterator<Row> rowIterator1 = sheet1.iterator();
		Iterator<Row> rowIterator2 = sheet2.iterator();

		HashMap<String, Double> peDownward = new HashMap<String, Double>();
		HashMap<String, Integer> downwardRId = new HashMap<String, Integer>();
		HashMap<String, Double> peSideward = new HashMap<String, Double>();
		HashMap<String, Integer> sidewardRId = new HashMap<String, Integer>();

		while (rowIterator1.hasNext()) {
			Row row = rowIterator1.next();
			double score = 0;
			String pivotEntity = null;

			Iterator<Cell> cellIterator = row.cellIterator();
			int cellNumber = 0;

			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				if (cellNumber == 1) {
					pivotEntity = cell.toString();
				} else if (cellNumber == 2) {
					score = Double.parseDouble(cell.toString());
				}
				cellNumber++;
			}

			if (peDownward.keySet().isEmpty()) {
				peDownward.put(pivotEntity, score);
				downwardRId.put(pivotEntity, row.getRowNum());
			}
			if (!(peDownward.keySet().contains(pivotEntity))) {
				peDownward.put(pivotEntity, score);
				downwardRId.put(pivotEntity, row.getRowNum());
			}

			for (Map.Entry<String, Double> peEntry : peDownward.entrySet()) {
				if (peEntry.getKey().equals(pivotEntity)) {
					if (peEntry.getValue() < score) {
						peDownward.put(pivotEntity, score);
						downwardRId.put(pivotEntity, row.getRowNum());
					}
				}

			}

		}

		while (rowIterator2.hasNext()) {
			Row row = rowIterator2.next();
			double score = 0;
			String pivotEntity = null;

			Iterator<Cell> cellIterator = row.cellIterator();
			int cellNumber = 0;

			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				if (cellNumber == 1) {
					pivotEntity = cell.toString();
				} else if (cellNumber == 2) {
					score = Double.parseDouble(cell.toString());
				}
				cellNumber++;
			}

			if (peSideward.keySet().isEmpty()) {
				peSideward.put(pivotEntity, score);
				sidewardRId.put(pivotEntity, row.getRowNum());
			}
			if (!(peSideward.keySet().contains(pivotEntity))) {
				peSideward.put(pivotEntity, score);
				sidewardRId.put(pivotEntity, row.getRowNum());
			}

			for (Map.Entry<String, Double> peEntry : peSideward.entrySet()) {
				if (peEntry.getKey().equals(pivotEntity)) {
					if (peEntry.getValue() < score) {
						peSideward.put(pivotEntity, score);
						sidewardRId.put(pivotEntity, row.getRowNum());
					}
				}

			}

		}
		finalCarousels(downwardRId, sidewardRId);

		System.out.println("===============================");
		/*
		 * dcreader =
		 * Files.newBufferedReader(Paths.get(".//DownwardCarouselRanking.csv"),Charset.
		 * forName("ISO-8859-1")); screader =
		 * Files.newBufferedReader(Paths.get(".//SidewardCarouselRanking.csv"),Charset.
		 * forName("ISO-8859-1")); CSVParser dccsvParser = new CSVParser(dcreader,
		 * CSVFormat.DEFAULT); CSVParser sccsvParser = new CSVParser(screader,
		 * CSVFormat.DEFAULT); HashMap<String, Double> peDownward = new HashMap<String,
		 * Double>(); HashMap<String, Integer> downwardRId = new HashMap<String,
		 * Integer>(); HashMap<String, Double> peSideward = new HashMap<String,
		 * Double>(); HashMap<String, Integer> sidewardRId = new HashMap<String,
		 * Integer>();
		 * 
		 * for(CSVRecord dccsvrecord : dccsvParser) {
		 * //System.out.println(dccsvrecord.get(1)); if(peDownward.keySet().isEmpty()) {
		 * peDownward.put(dccsvrecord.get(1),Double.parseDouble((dccsvrecord.get(2))));
		 * downwardRId.put(dccsvrecord.get(1), (int) dccsvrecord.getRecordNumber()+1); }
		 * if(!(peDownward.keySet().contains(dccsvrecord.get(1)))) {
		 * peDownward.put(dccsvrecord.get(1),Double.parseDouble(dccsvrecord.get(2)));
		 * downwardRId.put(dccsvrecord.get(1), (int) dccsvrecord.getRecordNumber()+1); }
		 * 
		 * for(Map.Entry<String, Double> peEntry : peDownward.entrySet()) {
		 * if(peEntry.getKey().equals(dccsvrecord.get(1))) { if(peEntry.getValue() <
		 * Double.parseDouble(dccsvrecord.get(2))) { peDownward.put(peEntry.getKey(),
		 * Double.parseDouble(dccsvrecord.get(2))); downwardRId.put(dccsvrecord.get(1),
		 * (int) dccsvrecord.getRecordNumber()+1); } }
		 * 
		 * } }
		 * 
		 * for(CSVRecord sccsvrecord : sccsvParser) {
		 * //System.out.println(dccsvrecord.get(1)); if(peSideward.keySet().isEmpty()) {
		 * peSideward.put(sccsvrecord.get(1),Double.parseDouble((sccsvrecord.get(2))));
		 * sidewardRId.put(sccsvrecord.get(1), (int) sccsvrecord.getRecordNumber()+1); }
		 * if(!(peSideward.keySet().contains(sccsvrecord.get(1)))) {
		 * peSideward.put(sccsvrecord.get(1),Double.parseDouble(sccsvrecord.get(2)));
		 * sidewardRId.put(sccsvrecord.get(1), (int) sccsvrecord.getRecordNumber()+1); }
		 * 
		 * for(Map.Entry<String, Double> peEntry : peSideward.entrySet()) {
		 * if(peEntry.getKey().equals(sccsvrecord.get(1))) { if(peEntry.getValue() <
		 * Double.parseDouble(sccsvrecord.get(2))) { peSideward.put(peEntry.getKey(),
		 * Double.parseDouble(sccsvrecord.get(2))); sidewardRId.put(sccsvrecord.get(1),
		 * (int) sccsvrecord.getRecordNumber()+1); } }
		 * 
		 * } }
		 */
		System.out.println(peDownward);
		System.out.println(downwardRId);
		System.out.println("==========================================");
		System.out.println(peSideward);
		System.out.println(sidewardRId);
		// finalCarousels(downwardRId, sidewardRId);
	}

}
