package main.java;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.opencsv.CSVWriter;

public class DataPreProcessing {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//System.out.println("Hello World");
		
		JSONParser jsonParser = new JSONParser();
		
		FileWriter outputFile=null;
        File file = new File(".//sample.csv");
		
        try {
			outputFile = new FileWriter(file);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        CSVWriter writer = new CSVWriter(outputFile);
		//Read json files in loop from the folder. Then construct a map or a set with a collection of tuples.
        String[] csvheader = {"rh","R","A","Q"};
        writer.writeNext(csvheader);
        
        File dir = new File(".//datasets");
		  File[] directoryListing = dir.listFiles();
		  if (directoryListing != null) {
		    for (File child : directoryListing) {
		      System.out.println("Data appears here ... "+child);
		      try (FileReader reader = new FileReader(child))
		        {
		            //Read JSON file
		            Object obj = jsonParser.parse(reader);
		 
		            JSONArray jsonList = (JSONArray) obj;
		            System.out.println(jsonList.size());
		            HashSet<String> headerRow = new HashSet<String>(); // attributes
		            HashSet<String> metadata = new HashSet<String>(); // auxiliary metadata
		            List<List<String>> datarows = new ArrayList<List<String>>();
		            List<String> queries = new ArrayList<String>();
		            
		            //Iterate and create the tuples. And if it is last iteration then it is a query.
		            //jsonList.forEach( jsonobj -> parseJsonObject( (JSONObject) jsonobj , jsonList.size()) );
		            
		            for (int i = 0; i < jsonList.size(); i++) {
		            	//System.out.println(jsonList.get(i));
		            	List<String> datarowslist = new ArrayList<String>();
		            	HashMap<String,String> temp = (HashMap<String, String>) jsonList.get(i);
		            	if(i == jsonList.size() - 2) // Getting all table context
		            		metadata.addAll(temp.values());
		            	else if(i == jsonList.size() - 1) { // Getting all queries
		            		Set<String> keys = temp.keySet();
		            		for(String key : keys) {
		            			String q1 = key + "===" + temp.get(key);
		            			/*List<String> q1 = new ArrayList<String>();
		            			q1.add(key); q1.add(temp.get(key));*/
		            			queries.add(q1);
		            		}
		            		
		            	}
		            	else {
		            		headerRow.addAll(temp.keySet()); // Getting all table headers
		            		//System.out.println(temp.values());
		            		for(String header : temp.keySet()) {
		            			datarowslist.add(temp.get(header));
		            		}
		            		
		            		datarows.add(datarowslist);
		            	}
		            	
		            	//System.out.println(temp.keySet());
		            }
		            System.out.println(headerRow+"----"+datarows+"----"+metadata+"----"+queries);
		            
		            String[] data1 = {String.join(":::",headerRow),String.join(":::",datarows.toString()),
		            		String.join(":::",metadata),String.join(":::",queries)};
		            writer.writeNext(data1);
		            /*for(List<String> dataList : datarows) {
		            	for(String str : dataList) {
		            		System.out.println(str);
		            	}
		            }*/
		           // writer.close();
		        } catch (FileNotFoundException e) {
		            e.printStackTrace();
		        } catch (IOException e) {
		            e.printStackTrace();
		        } catch (ParseException e) {
		            e.printStackTrace();
		        }
		    }
		  }
       
        try {
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	 

}
