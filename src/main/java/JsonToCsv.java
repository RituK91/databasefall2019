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
		File dir = new File(".//newdatasets");
		  File[] directoryListing = dir.listFiles();
		  System.out.println(directoryListing.length);
		  int count = 0;
		  if (directoryListing != null) 
		  {
		    for (File child : directoryListing) 
		    {
		      //System.out.println("info appears here..." + child);
		    	/*count++;
		        if(count > 2)
		        	continue; */
				try(FileReader reader = new FileReader(child))
				{
					//parse the JSON files
					Object obj=prsr.parse(reader);
					
					//storing the details of each file in array
					JSONObject jo = (JSONObject) obj;
					//display table details
					//System.out.println(jo.get("relation"));
					JSONArray relation =(JSONArray) jo.get("relation");
					//display table size
					int ts=relation.size();
					//System.out.println("Table Size"+ts);
				
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
		            String tableType = (String) jo.get("tableType");
		            /*if(tableType.equalsIgnoreCase("relation"))
		            	System.out.println("True");*/
				//create query and metadata for each table
		           int k=1;
		           String q1 =pageTitle+"===1"+title+"===2"+textBeforeTable+"===3"+textAfterTable+"===4";
		           String m = pageTitle+"---"+title;
		            queries.add(q1);
		            metadata.add(m);
		            //display metadata and queries
		            //System.out.println("Queries--->"+queries);
		            //System.out.println("Metadata--->"+metadata);
		            
		            //retrieve dataRows and headerRow
					for (int i = 0; i < ts; i++) 
					{
						//System.out.println(relation.get(i));
		            	int l=((ArrayList) relation.get(i)).size();//to get size of elements inside table
		            	ArrayList<String> relationList = (ArrayList<String>) relation.get(i);
		            	//System.out.println("Column size"+l);
		            	for(int j=0;j< relationList.size() ;j++)
		            	{
		            		//System.out.println(relationList.get(0));
		            		//System.out.println(relationList.get(j));
		            		//JSONArray tem=(JSONArray)(relation.get(j));
		            		//int lt=tem.size();
		            		//String e=(String)tem.get(0);
		            		
		            		headerRow.add(relationList.get(0));
		            		
		            		//for(int n=1;n<lt;n++)
		            		//{
		            		//	String t=(String)tem.get(n);
		            			//System.out.println(t);
		            		//	datarows.add(t);
		            		//	System.out.println(datarows);
		            		//}
		            	}
		            	
		            }
					System.out.println(headerRow);	
		            //System.out.println(headerRow+"---"+queries+"---"+metadata);
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
	


