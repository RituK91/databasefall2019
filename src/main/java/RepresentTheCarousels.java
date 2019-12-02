package main.java;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class RepresentTheCarousels {
	
	public static void representCarousel(CSVParser csvparser, HashMap<Integer,String> subjectMap, 
			HashMap<Integer,String> fact1Map, HashMap<Integer,String> fact2Map){
		
		for(CSVRecord csvrecord : csvparser) {
			if(csvrecord.getRecordNumber() == 1 || csvrecord.getRecordNumber() >= 10)
				continue;
			
			
			System.out.println("Pivot Entity ::: "+csvrecord.get(0));
			System.out.println("Title ::: "+csvrecord.get(1));
			String[] members = csvrecord.get(4).split(":::");
			String[] fact1 = csvrecord.get(2).split(":::");
			String[] fact2 = csvrecord.get(3).split(":::");
			int recordNo = (int)csvrecord.getRecordNumber();
			if(members.length < recordNo)
				System.out.println("Subject And Members ------ "+subjectMap.get(recordNo));
			else
				System.out.println("Subject And Members ------ "+subjectMap.get(recordNo)+" : "+members[(int) csvrecord.getRecordNumber()]);
			if(fact1Map.containsKey(recordNo))
				System.out.println("Facts ------ "+fact1Map.get(recordNo)+" : "+fact1[recordNo]);
			if(fact2Map.containsKey(recordNo))
				System.out.println("Facts ------ "+fact2Map.get(recordNo)+" : "+fact2[recordNo]);
			System.out.println("============================================================");
		}
		
		
	}

	public static void main(String[] args) {
		
		Reader dcreader; Reader screader; Reader subFactReader;
		
		System.out.println("===============================");
		try {
			dcreader = Files.newBufferedReader(Paths.get(".//DownwardCarousel.csv"),Charset.forName("ISO-8859-1"));
			screader = Files.newBufferedReader(Paths.get(".//SidewardCarousel.csv"),Charset.forName("ISO-8859-1"));
			subFactReader = Files.newBufferedReader(Paths.get(".//subjectAndFact.csv"),Charset.forName("ISO-8859-1"));
			CSVParser dccsvParser = new CSVParser(dcreader, CSVFormat.DEFAULT);
			CSVParser sccsvParser = new CSVParser(screader, CSVFormat.DEFAULT);
			CSVParser subfactcsvParser = new CSVParser(subFactReader, CSVFormat.DEFAULT);
			
			HashMap<Integer,String> subjectMap = new HashMap<Integer,String>();
			HashMap<Integer,String> fact1Map = new HashMap<Integer,String>();
			HashMap<Integer,String> fact2Map = new HashMap<Integer,String>();
			
			for(CSVRecord subfactcsvRecord : subfactcsvParser) {
				
				String subjects = subfactcsvRecord.get(1);
				String[] factList = subfactcsvRecord.get(2).split(":::");
				
				subjectMap.put((int) (subfactcsvRecord.getRecordNumber()+1), subjects);
				fact1Map.put((int) (subfactcsvRecord.getRecordNumber()+1), factList[0]);
				if(factList.length > 1)
					fact2Map.put((int) (subfactcsvRecord.getRecordNumber()+1), factList[1]);
				
			}
			
			//List<String> subject = fillTheDataStructure(subfactcsvParser, 0);
			//System.out.println(subjectMap);
			//System.out.println(fact1Map);
			//System.out.println(fact2Map);
			System.out.println("Downward Carousels ===============================");
			representCarousel(dccsvParser, subjectMap, fact1Map, fact2Map);
			System.out.println("Sideward ===============================");
			representCarousel(sccsvParser, subjectMap, fact1Map, fact2Map);

		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
