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
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.json.simple.parser.ParseException;

import com.opencsv.CSVWriter;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class JsonToCsv 
{
	public static List<String> getDataRows(HashMap<Integer,ArrayList<String>> relationMap, int index) {
		String relationData = null; List<String> relationList = new ArrayList<String>();
		for(Entry<Integer, ArrayList<String>> hEntry : relationMap.entrySet()) {
			relationList.add(hEntry.getValue().get(index));
			//relationData = hEntry.getValue().get(index);
			//System.out.println(relationData);
		}
		return relationList;
	}

	public static void main(String[] args) 
	{
		JSONParser prsr=new JSONParser();
		
		FileWriter outputFile=null;
        File file = new File(".//sample2.csv");
		
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
		  //System.out.println(directoryListing.length);
		  int count = 0;
		  if (directoryListing != null) 
		  {
		    for (File child : directoryListing) 
		    {
		      //System.out.println("info appears here..." + child);
		    /*	count++;
		        if(count > 4)
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
					//System.out.println("Table Size:"+ts);
				
					//declaring the data structures to store each entity
					List<String> headerRow = new ArrayList<String>();
					HashSet<String> metadata = new HashSet<String>();
					List<String> dataRow = new ArrayList<String>();
					List<List<String>> allDatarows = new ArrayList<List<String>>();
					HashSet<String> tempRow=new HashSet<String>();
		            HashSet<String> queries = new HashSet<String>();
					
		            //get information for queries
		            String pageTitle =(String) jo.get("pageTitle");
		            String title =(String) jo.get("title");
		            String textBeforeTable =(String) jo.get("textBeforeTable");
		            String textAfterTable =(String) jo.get("textAfterTable");
		            String[] textAfterTableArr = textAfterTable.split(" ");
		            String tableType = (String) jo.get("tableType");
		            /*if(tableType.equalsIgnoreCase("relation"))
		            	System.out.println("True");*/
				//create query and metadata for each table
		           
		            if(pageTitle != null && !pageTitle.isEmpty() ) {
		            	queries.add(pageTitle+"===1"); 
		            	metadata.add(pageTitle);
		            }
		            	
		            if(title != null && !title.isEmpty()) {
		            	queries.add(title+"===2");
		            	metadata.add(title);
		            }
		            	
		            System.out.println("Inspected Value ------"+textAfterTableArr[0].length());
		            if(textAfterTableArr[0] != null && !StringUtils.isBlank(textAfterTableArr[0].trim()))
		            	queries.add(textAfterTableArr[0]+"===3"); 
		            if(textAfterTableArr[1] != null && !textAfterTableArr[1].isEmpty() && !textAfterTableArr[1].contains(" "))
		            	queries.add(textAfterTableArr[1]+"===4");
		            		             
		            //display metadata and queries
		            //System.out.println("Queries--->"+queries);
		            //System.out.println("Metadata--->"+metadata);
		            HashMap<Integer,ArrayList<String>> relationMap = new HashMap<Integer,ArrayList<String>>();
		            int innerListSize = 0;
		            //retrieve dataRows and headerRow
					for (int i = 0; i < ts; i++) 
					{
						//System.out.println(relation.get(i));
		            	ArrayList<String> relationList = (ArrayList<String>) relation.get(i);
		            	innerListSize = relationList.size();
		            	//System.out.println("Column size:"+lt);
		            	relationMap.put(i, relationList);
		            	
		            	/*for(int j=0;j< relationList.size() ;j++)
		            	{		            		
		            		headerRow.add(relationList.get(0));
		            		
		            		//to get first row of data for test
		            		//System.out.println(relationList.get(1));
		            		//tempRow.add(relationList.get(1));
		            		//to get each row of data
		            		if(j!=0) {
		            			//System.out.println(relationList.get(j));
		            			//tempRow.add(relationList.get(j)); 
		            		}
		            		//System.out.println(relationList.get(j));
		            			//adding data in HashSet
		            		
		            	} */
		            	
		            }
					
					//List<String> dataRow = new ArrayList<String>();
					for(int j = 0; j < innerListSize ; j++) {
						if(j == 0)
							headerRow = getDataRows(relationMap, j);
						else {
							dataRow = getDataRows(relationMap, j); allDatarows.add(dataRow);
						}
						//System.out.println(headerRow);
					}
					//System.out.println(relationMap);
					//System.out.println(headerRow.size());
					//System.out.println(allDatarows);
					
					//datarows.add(tempRow);
					//System.out.println(headerRow);
					//System.out.println(tempRow);
					//System.out.println("All data rowise:\n");
					//System.out.println(datarows);
					
		            //System.out.println(headerRow+"---"+queries+"---"+metadata);
		            String[] data1 = {String.join(":::",headerRow),String.join(":::",allDatarows.toString()),
		            		String.join(":::",metadata),String.join(":::",queries)};
		            
		            writer.writeNext(data1);
		            System.out.println("==================================");					
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
		  try {
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
	}
	
		  
}
	


