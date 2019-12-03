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

/*
 * Represent few carousels to see the results
 * in an appropriate manner. These csv files contain 
 * some of the good carousels.
 */

public class RepresentTheCarousels {

	public static void representCarousel(CSVParser csvparser, HashMap<Integer, String> subjectMap,
			HashMap<Integer, String> fact1Map, HashMap<Integer, String> fact2Map) {

		for (CSVRecord csvrecord : csvparser) {
			if (csvrecord.getRecordNumber() == 1 || csvrecord.getRecordNumber() >= 10)
				continue;

			System.out.println("Pivot Entity ::: " + csvrecord.get(0));
			System.out.println("Title ::: " + csvrecord.get(1));
			String[] members = csvrecord.get(4).split(":::");
			String[] fact1 = csvrecord.get(2).split(":::");
			String[] fact2 = csvrecord.get(3).split(":::");

			//System.out.println(members.length+" "+fact1.length+" "+fact2.length);
			int recordNo = (int)csvrecord.getRecordNumber();
			if(members.length < recordNo)
				System.out.println("Subject And Members ------ "+subjectMap.get(recordNo));

			else
				System.out.println("Subject And Members ------ "+subjectMap.get(recordNo)+" : "+members[(int) csvrecord.getRecordNumber()]);
			if(fact1Map.containsKey(recordNo) && fact1.length >= recordNo)
				System.out.println("Facts ------ "+fact1Map.get(recordNo)+" : "+fact1[0]);
			if(fact2Map.containsKey(recordNo) && fact1.length >= recordNo)
				System.out.println("Facts ------ "+fact2Map.get(recordNo)+" : "+fact2[0]);

			System.out.println("============================================================");
		}

	}
	
	public static void showOneFullCarousel(CSVParser csvparser, HashMap<Integer,String> subjectMap, 
			HashMap<Integer,String> fact1Map, HashMap<Integer,String> fact2Map) {
		
		for(CSVRecord csvrecord : csvparser) {
			System.out.println(csvrecord.getRecordNumber());
			if(csvrecord.getRecordNumber() != 2 )
				continue;
			
			
			System.out.println("Pivot Entity ::: "+csvrecord.get(0));
			System.out.println("Title ::: "+csvrecord.get(1));
			String[] members = csvrecord.get(4).split(":::");
			String[] fact1 = csvrecord.get(2).split(":::");
			String[] fact2 = csvrecord.get(3).split(":::");
			int recordNo = (int)csvrecord.getRecordNumber();
			for(int i = 0; i < members.length; i++) {
				if(members.length < recordNo)
					System.out.println("Subject And Members ------ "+subjectMap.get(recordNo));
				else
					System.out.println("Subject And Members ------ "+subjectMap.get(recordNo)+" : "+members[i]);
				if(fact1Map.containsKey(recordNo) && fact1.length >= recordNo)
					System.out.println("Facts ------ "+fact1Map.get(recordNo)+" : "+fact1[i]);
				if(fact2Map.containsKey(recordNo) && fact1.length >= recordNo)
					System.out.println("Facts ------ "+fact2Map.get(recordNo)+" : "+fact2[i]);
			}
			//System.out.println(members.length+" "+fact1.length+" "+fact2.length);
			
			
			System.out.println("============================================================");
		}
	}
	public static void main(String[] args) {

		
		Reader dcreader; Reader screader; Reader subFactDownReader; Reader subFactSideReader;
		
		try {

			dcreader = Files.newBufferedReader(Paths.get(".//DownwardCarousel.csv"),Charset.forName("ISO-8859-1"));
			screader = Files.newBufferedReader(Paths.get(".//SidewardCarousel.csv"),Charset.forName("ISO-8859-1"));
			subFactDownReader = Files.newBufferedReader(Paths.get(".//subjectAndFact.csv"),Charset.forName("ISO-8859-1"));
			subFactSideReader = Files.newBufferedReader(Paths.get(".//subjectAndFact.csv"),Charset.forName("ISO-8859-1"));

			CSVParser dccsvParser = new CSVParser(dcreader, CSVFormat.DEFAULT);
			CSVParser sccsvParser = new CSVParser(screader, CSVFormat.DEFAULT);

			CSVParser subfactdowncsvParser = new CSVParser(subFactDownReader, CSVFormat.DEFAULT);
			CSVParser subfactsidecsvParser = new CSVParser(subFactSideReader, CSVFormat.DEFAULT);
			
			HashMap<Integer,String> subjectDownMap = new HashMap<Integer,String>();
			HashMap<Integer,String> fact1DownMap = new HashMap<Integer,String>();
			HashMap<Integer,String> fact2DownMap = new HashMap<Integer,String>();
			
			HashMap<Integer,String> subjectSideMap = new HashMap<Integer,String>();
			HashMap<Integer,String> fact1SideMap = new HashMap<Integer,String>();
			HashMap<Integer,String> fact2SideMap = new HashMap<Integer,String>();
			
			for(CSVRecord subfactcsvRecord : subfactdowncsvParser) {
				
				String subjects = subfactcsvRecord.get(1);
				String[] factList = subfactcsvRecord.get(2).split(":::");
				
				subjectDownMap.put((int) (subfactcsvRecord.getRecordNumber()+1), subjects);
				fact1DownMap.put((int) (subfactcsvRecord.getRecordNumber()+1), factList[0]);
				if(factList.length > 1)
					fact2DownMap.put((int) (subfactcsvRecord.getRecordNumber()+1), factList[1]);
			
			}
			
			for(CSVRecord subfactcsvRecord : subfactsidecsvParser) {
				
				String subjects = subfactcsvRecord.get(1);
				String[] factList = subfactcsvRecord.get(2).split(":::");
				
				subjectSideMap.put((int) (subfactcsvRecord.getRecordNumber()+1), subjects);
				fact1SideMap.put((int) (subfactcsvRecord.getRecordNumber()+1), factList[0]);
				if(factList.length > 1)
					fact2SideMap.put((int) (subfactcsvRecord.getRecordNumber()+1), factList[1]);
				
			}
			//List<String> subject = fillTheDataStructure(subfactcsvParser, 0);
			//System.out.println(subjectMap);
			//System.out.println(fact1Map);
			//System.out.println(fact2Map);
			//showOneFullCarousel(dccsvParser, subjectDownMap, fact1DownMap, fact2DownMap);

			System.out.println("Downward Carousels ===============================");
			representCarousel(dccsvParser, subjectDownMap, fact1DownMap, fact2DownMap);
			System.out.println("Sideward ===============================");
			representCarousel(sccsvParser, subjectSideMap, fact1SideMap, fact2SideMap);
			System.out.println("=====================================================");
			//showOneFullCarousel(dccsvParser, subjectDownMap, fact1DownMap, fact2DownMap);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
