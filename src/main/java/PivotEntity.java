package main.java;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import main.java.util.WorkBookUtil;

/**
 * This class identifies the pivot entities for both Carousels and saves the
 * facts, subject column information for each carousel.
 * 
 * @author NETUSER
 *
 */
public class PivotEntity {

	public static StringBuffer entityForDownwardCarousel(String[] contexts, HashMap<String, String> queryMap) {

		HashSet<String> querytokens = new HashSet<String>();
		HashSet<String> contextTokens = new HashSet<String>();
		for (String query : queryMap.keySet()) {
			String[] tokens = query.split(" ");
			for (String t1 : tokens) {
				querytokens.add(t1);
			}
		}

		for (String c1 : contexts) {
			String[] tokens = c1.split(" ");
			for (String t1 : tokens) {
				contextTokens.add(t1);
			}
		}
		// System.out.println(querytokens);
		// System.out.println(contextTokens);
		List<String> contextList = new ArrayList<String>();
		for (String context : contextTokens) {
			for (String query : querytokens) {
				if (context.equalsIgnoreCase(query)) {
					// System.out.println(context);
					contextList.add(context);
				}
			}
		}
		TreeMap<String, Integer> sortedMap = calculateTokenFreq(contextList);
		// System.out.println("============================");
		// System.out.println(sortedMap);

		int i = 0;
		StringBuffer pivotEntity = new StringBuffer();
		for (Entry<String, Integer> entry : sortedMap.entrySet()) {
			i++;
			if (i > 2)
				continue;

			pivotEntity.append(entry.getKey() + " ");
		}
		// System.out.println(pivotEntity);
		// System.out.println("=========================================");
		return pivotEntity;
	}

	public static String pivotEntityForSideways(HashMap<String, String> subjectMap, String header, String dataRows) {
		String[] headerCols = header.split(":::");
		String[] dataRowsCol = dataRows.split("],");
		List<String> dataList = new ArrayList<String>();
		List<String> potentialPE = new ArrayList<String>();
		String subject = subjectMap.get(header);

		int i = 0;
		int index = 0;
		int counter = 0;

		for (String data : dataRowsCol) {
			counter++;
			if (counter == dataRowsCol.length)
				dataList.add(data.replace("]]", ""));
			else
				dataList.add(data.replace("[", ""));

		}

		for (String h1 : headerCols) {
			i++;
			if (subject != null && subject.equalsIgnoreCase(h1)) {
				index = i;
				// System.out.println(subject+" "+index);
			}
		}
		// System.out.println(dataRows);
		// System.out.println(dataList);

		for (String data : dataList) {
			// System.out.println(data);
			String[] temp = data.trim().split(",");
			/*
			 * for(String t : temp) { System.out.println(t); }
			 */
			try {
				if (temp != null)
					potentialPE.add(temp[index - 1]);
			} catch (ArrayIndexOutOfBoundsException e) {
				// System.out.println(" Exception here for "+data);
				return null;
			}

		}
		return potentialPE.get(1);
		// System.out.println("========================");
		// System.out.println(dataRowsCol[index-1]);

	}

	public static TreeMap<String, Integer> calculateTokenFreq(List<String> contextList) {

		HashMap<String, Integer> freqMap = new HashMap<String, Integer>();

		HashSet<String> temp = new HashSet<String>();
		for (String context : contextList) {
			if (temp.contains(context.toLowerCase())) {
				int value = freqMap.get(context.toLowerCase());
				freqMap.put(context, value + 1);
			} else {
				freqMap.put(context.toLowerCase(), 1);
				temp.add(context.toLowerCase());
			}
		}
		TreeMap<String, Integer> sortedMap = new TreeMap<String, Integer>(freqMap);
		return sortedMap;
	}

	public static void dataForCarousels(Object entity, String header, HashMap<String, String> subjectColumn,
			HashMap<String, String> factsColumn, String datarows, String contexts, String queries, Row rowToWrite) {

		String subjectCol = subjectColumn.get(header); // String[] facts = factsColumn.get(header).split(":::");
		String[] datarow = datarows.split("],");
		String[] headerList = header.split(":::");
		if (factsColumn.get(header) == null)
			factsColumn.put(header, "Test:::Test");
		List<String> facts = new ArrayList<String>(Arrays.asList(factsColumn.get(header).split(":::")));
		List<String> dataList = new ArrayList<String>();
		List<String> subList = new ArrayList<String>();
		List<String> factList1 = new ArrayList<String>();
		List<String> factList2 = new ArrayList<String>();

		int subIndex = 0;
		int fact1 = 0;
		int fact2 = 0;

		for (String data : datarow) {
			String result = null;
			if (data.contains("[")) {
				result = data.replace("[", "");
				// dataList.remove(data);
				dataList.add(result);
			} else {
				dataList.add(data);
			}

		}

		int counter = 0;
		for (int i = 0; i < headerList.length; i++) {
			counter++;
			if (headerList[i].equals(subjectCol))
				subIndex = i;
			else if (headerList[i].equals(facts.get(0)))
				fact1 = i;
			else if (headerList[i].equals(facts.get(1)))
				fact2 = i;
		}
		// System.out.println(subIndex+" "+fact1+" "+fact2);
		for (String data : dataList) {
			String[] data1 = data.split(",");
			for (int i = 0; i < data1.length; i++) {
				if (i == subIndex) {
					subList.add(data1[i]);
				} else if (i == fact1) {
					factList1.add(data1[i]);
				} else if (i == fact2) {
					factList2.add(data1[i]);
				}
			}
		}

		// System.out.println("Subject List ===== "+subList);
		// System.out.println("Fact List1 ===== "+factList1);
		// System.out.println("Fact List1 ====="+factList2);
		// System.out.println("================================");

		if (entity == null)
			entity = "Test";

		if (entity != null) {
			Cell entityCell = rowToWrite.createCell(0);
			entityCell.setCellValue(entity.toString());

			Cell factCell1 = rowToWrite.createCell(1);
			factCell1.setCellValue(String.join(":::", factList1));

			Cell factCell2 = rowToWrite.createCell(2);
			factCell2.setCellValue(String.join(":::", factList2));

			Cell subCell = rowToWrite.createCell(3);
			subCell.setCellValue(String.join(":::", subList));

			Cell contextCell = rowToWrite.createCell(4);
			contextCell.setCellValue(contexts);

			Cell queryCell = rowToWrite.createCell(5);
			queryCell.setCellValue(queries);

		}

	}

	public static void main(String args[]) {

		XSSFSheet testSamplesheet = null, subFactSheet = null, dataDCsheet = null, dataSCsheet = null;
		try {
			// Reading the file TestSample.xlsx
			testSamplesheet = WorkBookUtil.getWorkbookSheet(".//TestSample.xlsx");

			// Reading the file subFact.xlsx
			subFactSheet = WorkBookUtil.getWorkbookSheet("./testFolder/subFact.xlsx");

			// Output file For Downward Carousel
			dataDCsheet = WorkBookUtil.getWorkbookSheet("./testFolder/dataDC.xlsx");
			FileOutputStream dataDCOutputFile = new FileOutputStream(new File("./testFolder/dataDC.xlsx"));

			// Output file For Sideward Carousel
			dataSCsheet = WorkBookUtil.getWorkbookSheet("./testFolder/dataSC.xlsx");
			FileOutputStream dataSCOutputFile = new FileOutputStream(new File("./testFolder/dataSC.xlsx"));

			Iterator<Row> rowIterator = testSamplesheet.iterator();
			Iterator<Row> rowIterator1 = subFactSheet.iterator();
			int rowNumber = 0;

			HashMap<String, String> subjectColumn = new HashMap<String, String>();
			HashMap<String, String> factsColumn = new HashMap<String, String>();

			while (rowIterator1.hasNext()) {
				Row row = rowIterator1.next();
				Iterator<Cell> cellIterator = row.cellIterator();
				int cellNumber = 0;
				String header = null;
				String subj = null;
				String fact = null;

				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();

					if (cellNumber == 0) {
						header = cell.toString();
					} else if (cellNumber == 1) {
						subj = cell.toString();
					} else if (cellNumber == 2) {
						fact = cell.toString();
					}
					cellNumber++;
				}
				subjectColumn.put(header, subj);
				factsColumn.put(header, fact);
			}

			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				if (row.getRowNum() == 0) {
					rowNumber++;
					continue;
				}

				Row rowToWrite = dataDCsheet.createRow(row.getRowNum() - 1);
				Row rowToWrite1 = dataSCsheet.createRow(row.getRowNum() - 1);

				HashMap<String, String> queryMap = new HashMap<String, String>();

				Iterator<Cell> cellIterator = row.cellIterator();
				String header = null, dataRows = null, context = null, querystr = null;
				int cellNumber = 0;

				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();

					// Check the cell type and format accordingly
					if (cellNumber == 0) {
						header = cell.toString();
					} else if (cellNumber == 1) {
						dataRows = cell.toString();
					} else if (cellNumber == 2) {
						context = cell.toString();
					} else if (cellNumber == 3) {
						querystr = cell.toString();
						String[] queries = cell.toString().split(":::");
						for (String query : queries) {
							int index = query.indexOf("===");
							if (index != -1) {
								queryMap.put(query.substring(0, index), query.substring(index + 3));
							}
						}
					}

					cellNumber++;
				}
				StringBuffer pivotEntityForDownward = entityForDownwardCarousel(context.toString().split(":::"),
						queryMap);
				String pivotEntityForSideway = pivotEntityForSideways(subjectColumn, header, dataRows);
				dataForCarousels(pivotEntityForDownward, header, subjectColumn, factsColumn, dataRows, context,
						querystr, rowToWrite);
				dataForCarousels(pivotEntityForSideway, header, subjectColumn, factsColumn, dataRows, context, querystr,
						rowToWrite1);
				dataDCsheet.getWorkbook().write(dataDCOutputFile);
				dataSCsheet.getWorkbook().write(dataSCOutputFile);

				rowNumber++;
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				dataDCsheet.getWorkbook().close();
				dataSCsheet.getWorkbook().close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

}
