package main.java;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

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
				System.out.println(" Exception here for "+data);
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
	
	public static void main(String args[]) {
		
		Reader reader; Reader reader1;
		
		try {
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
				//System.out.println("Pivot Entity "+pivotEntityForDownward);
				String pivotEntityForSideway = pivotEntityForSideways(subjectColumn, csvRecord.get(0), csvRecord.get(1));
				System.out.println(csvRecord.get(0)+" ---- "+pivotEntityForSideway);
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
	}

}
