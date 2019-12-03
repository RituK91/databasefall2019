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

import main.java.util.WorkBookUtil;

/*
 * Choose the carousel with higest rank for an entity
 * and writes to a final excel the carousels.
 */
public class HighestCarouselRanking {

	public static void finalCarousels(HashMap<String, Integer> downwardRId, HashMap<String, Integer> sidewardRId)
			throws IOException {

		// Output for final downward carousel
		XSSFSheet finalDownwardCarouselSheetToWrite = WorkBookUtil
				.getWorkbookSheet("./testFolder/FinalDownwardCarousel.xlsx");
		FileOutputStream downwardCarouselOutputFile = new FileOutputStream(
				new File("./testFolder/FinalDownwardCarousel.xlsx"));

		// Output for final Sideward Carousel
		XSSFSheet finalSidewardCarouselSheetToWrite = WorkBookUtil
				.getWorkbookSheet("./testFolder/FinalSidewardCarousel.xlsx");
		FileOutputStream sidewardCarouselOutputFile = new FileOutputStream(
				new File("./testFolder/FinalSidewardCarousel.xlsx"));

		FileInputStream dcfile = new FileInputStream(new File("./testFolder/DownwardCarousel.xlsx"));
		FileInputStream scfile = new FileInputStream(new File("./testFolder/SidewardCarousel.xlsx"));

		// Create Workbook instance holding reference to .xlsx file
		XSSFWorkbook workbook1 = new XSSFWorkbook(dcfile);
		XSSFWorkbook workbook2 = new XSSFWorkbook(scfile);

		// Read the carousel files
		XSSFSheet downwardCarouselSheet = WorkBookUtil.getWorkbookSheet("./testFolder/DownwardCarousel.xlsx");
		XSSFSheet sidewardCarouselSheet = WorkBookUtil.getWorkbookSheet("./testFolder/SidewardCarousel.xlsx");

		Iterator<Row> downwardCarouselRowIterator = downwardCarouselSheet.iterator();
		Iterator<Row> sidewardCarouselRowIterator = sidewardCarouselSheet.iterator();

		while (downwardCarouselRowIterator.hasNext()) {
			Row row = downwardCarouselRowIterator.next();
			Row rowToWrite = finalDownwardCarouselSheetToWrite.createRow(row.getRowNum());
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
				} else if (cellNumber == 4) {
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

		while (sidewardCarouselRowIterator.hasNext()) {
			Row row = sidewardCarouselRowIterator.next();
			Row rowToWrite = finalSidewardCarouselSheetToWrite.createRow(row.getRowNum());
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
				} else if (cellNumber == 4) {
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
		finalDownwardCarouselSheetToWrite.getWorkbook().write(downwardCarouselOutputFile);
		finalDownwardCarouselSheetToWrite.getWorkbook().close();
		finalSidewardCarouselSheetToWrite.getWorkbook().write(sidewardCarouselOutputFile);
		finalSidewardCarouselSheetToWrite.getWorkbook().close();
	}

	public static void main(String[] args) throws IOException {

		// Get first/desired sheet from the workbook
		XSSFSheet downwardCarouselRankingSheet = WorkBookUtil
				.getWorkbookSheet("./testFolder/DownwardCarouselRanking.xlsx");
		XSSFSheet sidewardCarouselRankingSheet = WorkBookUtil
				.getWorkbookSheet("./testFolder/SidewardCarouselRanking.xlsx");

		Iterator<Row> downwardCarouselRankingRowIterator = downwardCarouselRankingSheet.iterator();
		Iterator<Row> sidewardCarouselRankingRowIterator = sidewardCarouselRankingSheet.iterator();

		HashMap<String, Double> peDownward = new HashMap<String, Double>();
		HashMap<String, Integer> downwardRId = new HashMap<String, Integer>();
		HashMap<String, Double> peSideward = new HashMap<String, Double>();
		HashMap<String, Integer> sidewardRId = new HashMap<String, Integer>();

		while (downwardCarouselRankingRowIterator.hasNext()) {
			Row row = downwardCarouselRankingRowIterator.next();
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

		while (sidewardCarouselRankingRowIterator.hasNext()) {
			Row row = sidewardCarouselRankingRowIterator.next();
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

	}
}
