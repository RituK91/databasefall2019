package src;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
 
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CriticalAttributes {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Hello World");

		JSONParser jsonParser = new JSONParser();
		
		//Read json files in loop from the folder. Then construct a map or a set with a collection of tuples.
        
        try (FileReader reader = new FileReader(".\\motogp_riders.json"))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
 
            JSONArray jsonList = (JSONArray) obj;
            System.out.println(jsonList);
             
            //Iterate and create the tuples. And if it is last iteration then it is a table context.
            jsonList.forEach( jsonobj -> parseJsonObject( (JSONObject) jsonobj ) );
 
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
	}
	
	 private static void parseJsonObject(JSONObject jsonobj) 
	    {
	        //Get employee object within list
	        //JSONObject employeeObject = (JSONObject) employee.get("9");
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
	    }

}
