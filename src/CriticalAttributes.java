package src;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CriticalAttributes {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//System.out.println("Hello World");

		JSONParser jsonParser = new JSONParser();
		
		//Read json files in loop from the folder. Then construct a map or a set with a collection of tuples.
        
        try (FileReader reader = new FileReader(".//motogp_riders.json"))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
 
            JSONArray jsonList = (JSONArray) obj;
            System.out.println(jsonList.size());
            HashSet<String> headerRow = new HashSet<String>(); // attributes
            HashSet<String> metadata = new HashSet<String>(); // auxiliary metadata
            List<String> datarows = new ArrayList<String>();
            HashSet<String> queries = new HashSet<String>();
            
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
            		for(String header : temp.keySet())
            			datarowslist.add(temp.get(header));
            		
            		System.out.println(datarowslist);
                	datarows.addAll(temp.values());
            	}
            	
            	//System.out.println(temp.keySet());
            }
            //System.out.println(headerRow+"----"+datarows+"----"+metadata);
             
 
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
