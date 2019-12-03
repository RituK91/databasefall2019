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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.opencsv.CSVWriter;

/**
 * This class identifies the pivot entities for both Carousels and 
 * saves the facts, subject column information for each carousel.
 * 
 * @author NETUSER
 *
 */
public class PivotEntity {
	
	public static StringBuffer entityForDownwardCarousel(String[] contexts, HashMap<String,String> queryMap) {
		
		HashSet<String> querytokens = new HashSet<String>();
		HashSet<String> contextTokens = new HashSet<String>();
		for(String query : queryMap.keySet()) {
			String[] tokens= query.split(" ");
			for(String t1 : tokens) {
				querytokens.add(t1);
			}
		}
		
		for(String c1 : contexts) {
			String[] tokens = c1.split(" ");
			for(String t1 : tokens) {
				contextTokens.add(t1);
			}
		}
		//System.out.println(querytokens);
		//System.out.println(contextTokens);
		List<String> contextList = new ArrayList<String>();
		for(String context : contextTokens) {
			for(String query : querytokens) {
				if(context.equalsIgnoreCase(query)) {
					//System.out.println(context);
					contextList.add(context);
				}
			}
		}
		TreeMap<String,Integer> sortedMap = calculateTokenFreq(contextList);
		//System.out.println("============================");
		//System.out.println(sortedMap);
		
		int i = 0; StringBuffer pivotEntity = new StringBuffer();
		for(Entry<String, Integer> entry : sortedMap.entrySet()) {
			i++;
			if(i > 2)
				continue;
			
			pivotEntity.append(entry.getKey()+" ");
		}
		//System.out.println(pivotEntity);
		//System.out.println("=========================================");
		return pivotEntity;
	}
	
	public static String pivotEntityForSideways(HashMap<String,String> subjectMap, String header, String dataRows) {
		String[] headerCols = header.split(":::");
		String[] dataRowsCol = dataRows.split("],");
		List<String> dataList = new ArrayList<String>();
		List<String> potentialPE = new ArrayList<String>();
		String subject = subjectMap.get(header);
		
		int i = 0; int index = 0; int counter = 0;
		
		for(String data : dataRowsCol) {
			counter++;
			if(counter == dataRowsCol.length)
				dataList.add(data.replace("]]", ""));
			else
				dataList.add(data.replace("[", ""));
			
		}
				
		for(String h1 : headerCols) {
			i++;
			if(subject != null && subject.equalsIgnoreCase(h1)) {
				index = i;
				//System.out.println(subject+" "+index);
			}
		}
		//System.out.println(dataRows);
		//System.out.println(dataList);
		
		for(String data : dataList) {
			//System.out.println(data);
			String[] temp = data.trim().split(",");
			/*for(String t : temp) {
				System.out.println(t);
			}*/
			try {
				if(temp != null)
					potentialPE.add(temp[index-1]);
			}catch(ArrayIndexOutOfBoundsException e) {
				//System.out.println(" Exception here for "+data);
				return null;
			}
			
		}
		return potentialPE.get(1);
		//System.out.println("========================");
		//System.out.println(dataRowsCol[index-1]);
		
	}
	
	public static TreeMap<String,Integer> calculateTokenFreq(List<String> contextList){
		
		HashMap<String,Integer> freqMap = new HashMap<String,Integer>();
		
		HashSet<String> temp = new HashSet<String>();
		for(String context : contextList) {			
			if(temp.contains(context.toLowerCase())) {
				int value = freqMap.get(context.toLowerCase());
				freqMap.put(context, value+1);
			}else {
				freqMap.put(context.toLowerCase(), 1);
				temp.add(context.toLowerCase());
			}		
		}
		TreeMap<String,Integer> sortedMap = new TreeMap<String,Integer>(freqMap);
		return sortedMap;
	}
	
	public static void dataForCarousels(Object entity, String header, HashMap<String,String> subjectColumn, 
			HashMap<String,String> factsColumn, String datarows, String contexts, String queries, Row rowToWrite) {
		
		String subjectCol = subjectColumn.get(header); //String[] facts = factsColumn.get(header).split(":::");
		String[] datarow = datarows.split("],"); String[] headerList = header.split(":::");
		if(factsColumn.get(header) == null)
			factsColumn.put(header, "Test:::Test");
		List<String> facts = new ArrayList<String>(Arrays.asList(factsColumn.get(header).split(":::")));
		List<String> dataList = new ArrayList<String>();
		List<String> subList = new ArrayList<String>(); List<String> factList1 = new ArrayList<String>();
		List<String> factList2 = new ArrayList<String>();
		
		int subIndex = 0; int fact1 = 0; int fact2 = 0;
		
		for(String data : datarow) {
			String result = null;
			if (data.contains("[")) {
				result = data.replace("[", "");
				//dataList.remove(data);
				dataList.add(result);
			}else {
				dataList.add(data);
			}
				
		}
		
		int counter = 0;
		for(int i = 0; i < headerList.length; i++ ) {
			counter++; 
			if (headerList[i].equals(subjectCol))
				subIndex = i;
			else if(headerList[i].equals(facts.get(0)))
				fact1 = i;
			else if(headerList[i].equals(facts.get(1)))
				fact2 = i;
		}
		//System.out.println(subIndex+" "+fact1+" "+fact2);
		for(String data : dataList) {
			String[] data1 = data.split(",");
			for(int i = 0; i < data1.length; i++) {
				if(i == subIndex) {
					subList.add(data1[i]);
				}else if(i == fact1) {
					factList1.add(data1[i]);
				}else if(i == fact2) {
					factList2.add(data1[i]);
				}
			}
		}
		
		//System.out.println("Subject List ===== "+subList);
		//System.out.println("Fact List1 ===== "+factList1);
		//System.out.println("Fact List1 ====="+factList2);
		//System.out.println("================================");
		
		if(entity == null)
			entity = "Test";
		
		if(entity != null) {
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
			
			//String data[] = {entity.toString(), String.join(":::", factList1), String.join(":::", factList2), 
			//		String.join(":::", subList), contexts, queries};
			//writer.writeNext(data);
		}
		
	}
	
	public static void main(String args[]) {
		
		Reader reader; Reader reader1;
		
		try {
			
	       /* FileWriter outputFile1=null; FileWriter outputFile2=null;
	        File file1 = new File(".//dataForDownwardC_1.csv");
	        File file2 = new File(".//dataForSidewardC_1.csv");
			
	        try {
				outputFile1 = new FileWriter(file1); outputFile2 = new FileWriter(file2);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        CSVWriter writer1 = new CSVWriter(outputFile1); CSVWriter writer2 = new CSVWriter(outputFile2);*/
	        
	        //=============================================================================================
	        
	        FileInputStream infile = new FileInputStream(new File(".//TestSample.xlsx"));
	        FileInputStream subFactfile = new FileInputStream(new File("./testFolder/subFact.xlsx"));

			// Create Workbook instance holding reference to .xlsx file
			XSSFWorkbook workbook = new XSSFWorkbook(infile);
			XSSFWorkbook workbook1 = new XSSFWorkbook(subFactfile);

			// Get first/desired sheet from the workbook
			XSSFSheet sheet = workbook.getSheetAt(0);
			XSSFSheet sheet1 = workbook1.getSheetAt(0);
			
			//===============Data For Downward Carousel=================
			FileInputStream fileToWrite = new FileInputStream(new File("./testFolder/dataDC.xlsx"));
			XSSFWorkbook workbookToWrite = new XSSFWorkbook(fileToWrite); 
			XSSFSheet sheetToWrite = workbookToWrite.getSheetAt(0);
	        FileOutputStream outFile = new FileOutputStream(new File("./testFolder/dataDC.xlsx"));
	        
	      //===============Data For Sideward Carousel=================
	        FileInputStream fileToWrite1 = new FileInputStream(new File("./testFolder/dataSC.xlsx"));
			XSSFWorkbook workbookToWrite1 = new XSSFWorkbook(fileToWrite1); 
			XSSFSheet sheetToWrite1 = workbookToWrite1.getSheetAt(0);
	        FileOutputStream outFile1 = new FileOutputStream(new File("./testFolder/dataSC.xlsx"));
	        
			Iterator<Row> rowIterator = sheet.iterator();
			Iterator<Row> rowIterator1 = sheet1.iterator();
			int rowNumber = 0;
			
			HashMap<String,String> subjectColumn = new HashMap<String,String>();
			HashMap<String,String> factsColumn = new HashMap<String,String>();
			while(rowIterator1.hasNext()) {
				Row row = rowIterator1.next();
				Iterator<Cell> cellIterator = row.cellIterator();
				int cellNumber = 0;
				String header = null; String subj = null; String fact = null;
				
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					
					if(cellNumber == 0) {
						header = cell.toString();
					}else if(cellNumber == 1) {
						subj = cell.toString();
					}else if(cellNumber == 2) {
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
				
				Row rowToWrite = sheetToWrite.createRow(row.getRowNum()-1);
				Row rowToWrite1 = sheetToWrite1.createRow(row.getRowNum()-1);
				//System.out.println("Row Number ---- "+row.getRowNum());
				
				HashMap<String, String> queryMap = new HashMap<String, String>();
				// For each row, iterate through all the columns
				Iterator<Cell> cellIterator = row.cellIterator();
				String header = null; String dataRows = null; String context = null; String querystr = null;
				int cellNumber = 0;

				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					
					// Check the cell type and format accordingly
					if(cellNumber == 0) {
						header = cell.toString();
					}else if(cellNumber == 1) {
						dataRows = cell.toString();
					}else if (cellNumber == 2) {
						context = cell.toString();						
					} else if (cellNumber == 3) {
						querystr = cell.toString();
						String[] queries = cell.toString().split(":::");
						for (String query : queries) {
							List<String> queryList = new ArrayList<String>();
							int index = query.indexOf("===");
							//System.out.println(query);
							if (index != -1 ) {
								queryMap.put(query.substring(0, index), query.substring(index + 3));								
							}

						}
					}

					cellNumber++;

				}
				StringBuffer pivotEntityForDownward = entityForDownwardCarousel(context.toString().split(":::"), queryMap);
				//System.out.println("Downward Carousel Pivot Entity ---- "+pivotEntityForDownward);
				System.out.println("Subject Column "+subjectColumn);
				System.out.println("Facts Column "+factsColumn);
				String pivotEntityForSideway = pivotEntityForSideways(subjectColumn, header, dataRows);
				dataForCarousels(pivotEntityForDownward, header, subjectColumn, factsColumn, 
						dataRows, context, querystr, rowToWrite);
				dataForCarousels(pivotEntityForSideway, header, subjectColumn, factsColumn, 
						dataRows, context, querystr, rowToWrite1);
				workbookToWrite.write(outFile);
				workbookToWrite1.write(outFile1);
				
				rowNumber++;
			}
	        
	        
			workbookToWrite.close();
			workbookToWrite1.close();
	        
			/*reader = Files.newBufferedReader(Paths.get(".//FinalTestSample.csv"),Charset.forName("ISO-8859-1"));
			reader1 = Files.newBufferedReader(Paths.get(".//subjectAndFact_2.csv"),Charset.forName("ISO-8859-1"));
			CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
			CSVParser csvParser1 = new CSVParser(reader1, CSVFormat.DEFAULT);
			
			HashMap<String,String> subjectColumn = new HashMap<String,String>();
			HashMap<String,String> factsColumn = new HashMap<String,String>();
			
			for (CSVRecord csvRecord1 : csvParser1) {
				subjectColumn.put(csvRecord1.get(0), csvRecord1.get(1));
				factsColumn.put(csvRecord1.get(0), csvRecord1.get(2));
			}
			
			//System.out.println(subjectColumn);
			//System.out.println("=======================");
			//System.out.println(factsColumn);
			
			for (CSVRecord csvRecord : csvParser) {
				
				if(csvRecord.getRecordNumber() == 1 || csvRecord.getRecordNumber() > 2)
					continue;
				String[] contexts = csvRecord.get(2).split(":::");
				String[] queries = csvRecord.get(3).split(":::");
				
				HashMap<String,String> queryMap = new HashMap<String,String>();
				
				for(String query : queries) {
					List<String> queryList = new ArrayList<String>();
					int index = query.indexOf("===");
					
					queryMap.put(query.substring(0, index), query.substring(index+3));
				}
				//System.out.println(contexts); 
				//System.out.println("==========");
				//System.out.println(queryMap.keySet());
				
				StringBuffer pivotEntityForDownward = entityForDownwardCarousel(contexts, queryMap);
				//System.out.println("Downward Carousel Pivot Entity ---- "+pivotEntityForDownward);
				String pivotEntityForSideway = pivotEntityForSideways(subjectColumn, csvRecord.get(0), csvRecord.get(1));
				//System.out.println("Sideward Carousel Pivot Entity ---- "+pivotEntityForSideway);
				dataForCarousels(pivotEntityForDownward, csvRecord.get(0), subjectColumn, factsColumn, 
						csvRecord.get(1), writer1, csvRecord.get(2), csvRecord.get(3));
				dataForCarousels(pivotEntityForSideway, csvRecord.get(0), subjectColumn, factsColumn, 
						csvRecord.get(1), writer2, csvRecord.get(2), csvRecord.get(3));
			}
			writer1.close();
			writer2.close();*/
		}catch(IOException e) {
			e.printStackTrace();
		}
	}

}
