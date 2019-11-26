package main.java;

import java.io.File;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

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
			if(subject.equalsIgnoreCase(h1)) {
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
			HashMap<String,String> factsColumn, String datarows, CSVWriter writer) {
		
		String subjectCol = subjectColumn.get(header); //String[] facts = factsColumn.get(header).split(":::");
		String[] datarow = datarows.split("],"); String[] headerList = header.split(":::");
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
		
		System.out.println("Subject List ===== "+subList);
		System.out.println("Fact List1 ===== "+factList1);
		System.out.println("Fact List1 ====="+factList2);
		System.out.println("================================");
		
		if(entity != null) {
			String data[] = {entity.toString(), String.join(":::", factList1), String.join(":::", factList2), String.join(":::", subList)};
			writer.writeNext(data);
		}
		
	}
	
	public static void main(String args[]) {
		
		Reader reader; Reader reader1;
		
		try {
			
	        FileWriter outputFile1=null; FileWriter outputFile2=null;
	        File file1 = new File(".//dataForDownwardC.csv");
	        File file2 = new File(".//dataForSidewardC.csv");
			
	        try {
				outputFile1 = new FileWriter(file1); outputFile2 = new FileWriter(file2);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        CSVWriter writer1 = new CSVWriter(outputFile1); CSVWriter writer2 = new CSVWriter(outputFile2);
	        
			reader = Files.newBufferedReader(Paths.get(".//sample.csv"),Charset.forName("ISO-8859-1"));
			reader1 = Files.newBufferedReader(Paths.get(".//subjectAndFact.csv"),Charset.forName("ISO-8859-1"));
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
				
				if(csvRecord.getRecordNumber() == 1 /*|| csvRecord.getRecordNumber() > 2*/)
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
				dataForCarousels(pivotEntityForDownward, csvRecord.get(0), subjectColumn, factsColumn, csvRecord.get(1), writer1);
				dataForCarousels(pivotEntityForSideway, csvRecord.get(0), subjectColumn, factsColumn, csvRecord.get(1), writer2);
			}
			writer1.close();
			writer2.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}

}
