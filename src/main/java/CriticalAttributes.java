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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.opencsv.CSVWriter;

public class CriticalAttributes {

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
        
        try (FileReader reader = new FileReader(".//motogp_riders.json"))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
 
            JSONArray jsonList = (JSONArray) obj;
            System.out.println(jsonList.size());
            HashSet<String> headerRow = new HashSet<String>(); // attributes
            HashSet<String> metadata = new HashSet<String>(); // auxiliary metadata
            List<List<String>> datarows = new ArrayList<List<String>>();
            HashSet<List<String>> queries = createQueryList();
            
            //Iterate and create the tuples. And if it is last iteration then it is a table context.
            //jsonList.forEach( jsonobj -> parseJsonObject( (JSONObject) jsonobj , jsonList.size()) );
            
            for (int i = 0; i < jsonList.size(); i++) {
            	//System.out.println(jsonList.get(i));
            	List<String> datarowslist = new ArrayList<String>();
            	HashMap<String,String> temp = (HashMap<String, String>) jsonList.get(i);
            	if(i == jsonList.size() - 1)
            		metadata.addAll(temp.values());
            	else {
            		headerRow.addAll(temp.keySet());
            		//System.out.println(temp.values());
            		for(String header : temp.keySet()) {
            			datarowslist.add(temp.get(header));
            		}
            		
            		//System.out.println(datarowslist);
                	//datarows.addAll(temp.values());
            		datarows.add(datarowslist);
            	}
            	
            	//System.out.println(temp.keySet());
            }
            System.out.println(headerRow+"----"+datarows+"----"+metadata+"----"+queries);
            
            String[] data1 = {String.join(":",headerRow),String.join(":",datarows.toString()),
            		String.join(":",metadata),String.join(":",queries.toString())};
            writer.writeNext(data1);
            /*for(List<String> dataList : datarows) {
            	for(String str : dataList) {
            		System.out.println(str);
            	}
            }*/
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //writer.close();
	}
	
	private static HashSet<List<String>> createQueryList() {
		
		HashSet<List<String>> queries = new HashSet<List<String>>();
		List<String> q1 = new ArrayList<String>();
		List<String> q2 = new ArrayList<String>();
		List<String> q3 = new ArrayList<String>();
		
		q1.add("motogp riders"); q1.add("1");
		q2.add("wikipedia motogp records"); q2.add("2");
		q3.add("motogp champions history"); q3.add("3");
		
		queries.add(q1); queries.add(q2); queries.add(q3);
		return queries;
		
	}
	
	 private static String parseJsonObject(JSONObject jsonobj, int jsonlength) 
	    {
	        //Get employee object within list
	        //JSONObject employeeObject = (JSONObject) employee.get("9");
		 int count = 0;
	         System.out.println(jsonobj.keySet());
	        //Get employee first name
	        String firstName = (String) jsonobj.get("Rider");    
	        //System.out.println("First name .... "+firstName);
	         
	        //Get employee last name
	        String lastName = (String) jsonobj.get("Titles");  
	        //System.out.println("Last name .... "+lastName);
	         
	        //Get employee website name
	        String website = (String) jsonobj.get("Page Title");    
	        //System.out.println("Page Title .... "+website);
	        return null;
	    }

}
