package main.java;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.simple.parser.ParseException;

import com.opencsv.CSVWriter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class JsonToCsv 
{

	public static void main(String[] args) 
	{
		JSONParser prsr=new JSONParser();
		
		FileWriter outputFile=null;
        File file = new File(".//sample1.csv");
		
        try 
        {
			outputFile = new FileWriter(file);
		} 
        
        catch (IOException e1) 
        {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		CSVWriter writer = new CSVWriter(outputFile);
		//Read json files in loop from the folder. Then construct a map or a set with a collection of tuples.
        String[] csvheader = {"rh","R","A","Q"};
        writer.writeNext(csvheader);
        
		//retrieve the files from data sets folder
		File dir = new File(".//datasets");
		  File[] directoryListing = dir.listFiles();
		  if (directoryListing != null) 
		  {
		    for (File child : directoryListing) 
		    {
		      System.out.println("info appears here..." + child);
		
				try(FileReader reader = new FileReader(child))
				{
					//parse the JSON files
					Object obj=prsr.parse(reader);
					
					//storing the details of each file in array
					JSONObject jo = (JSONObject) obj;
					//display table details
					System.out.println(jo.get("relation"));
					JSONArray relation =(JSONArray) jo.get("relation");
					//display table size
					int ts=relation.size();
					System.out.println(ts);
				
					//declaring the data structures to store each entity
					HashSet<String> headerRow = new HashSet<String>();
					HashSet<String> metadata = new HashSet<String>();
					List<List<String>> datarows = new ArrayList<List<String>>();
		            List<String> queries = new ArrayList<String>();
					
		            //get information for queries
		            String pageTitle =(String) jo.get("pageTitle");
		            String title =(String) jo.get("title");
		            String textBeforeTable =(String) jo.get("textBeforeTable");
		            String textAfterTable =(String) jo.get("textAfterTable");
				//create query and metadata for each table
		           String q1 =pageTitle+"---"+title+"---"+textBeforeTable+"---"+textAfterTable;
		           String m = pageTitle+"---"+title;
		            queries.add(q1);
		            metadata.add(m);
		            //display metadata and queries
		            System.out.println("Queries-->"+queries);
		            System.out.println("Metadata-->"+metadata);
		            
		            //retrieve dataRows and headerRow
					//for (int i = 0; i < ts; i++) 
					//{
					//	List<String> datarowslist = new ArrayList<String>();
		            //	HashMap<String,String> temp = (HashMap<String, String>) jo.get(i);
		            //	int l=relation[i].size(); //to get size of elements inside table
		            //	for(int j=0;j<l;j++)
		            //	{
					//			headerRow.add());
		            //	}
						
					//}
		            
		            System.out.println(queries+"---"+metadata);
		            String[] data1 = {String.join(":::",metadata),String.join(":::",queries)};
		            
		            writer.writeNext(data1);
						
				}	
		    
		
				catch(FileNotFoundException e) 
				{e.printStackTrace(); }
				catch(ParseException e) 
				{e.printStackTrace(); }
				catch(IOException e) 
				{e.printStackTrace(); }
				catch(Exception e) 
				{e.printStackTrace(); }
				
		    
		  }
		  }		
	}
}
