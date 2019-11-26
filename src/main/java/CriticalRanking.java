package main.java;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.opencsv.CSVWriter;


public class CriticalRanking {
    /**
     * @param doc  list of strings
     * @param term String represents a term
     * @return term frequency of term in document
     */
    public double tf(List<String> doc, String term) {
        double result = 0;
        for (String word : doc) {
            if (term.equalsIgnoreCase(word))
                result++;
        }
        return result / doc.size();
    }

    /**
     * @param docs list of list of strings represents the dataset
     * @param term String represents a term
     * @return the inverse term frequency of term in documents
     */
    public double idf(List<List<String>> docs, String term) {
        double n = 0;
        for (List<String> doc : docs) {
            for (String word : doc) {
                if (term.equalsIgnoreCase(word)) {
                    n++;
                    break;
                }
            }
        }
        return Math.log(docs.size() / n);
    }

    /**
     * @param doc  a text document
     * @param docs all documents
     * @param term term
     * @return the TF-IDF of term
     */
    public double tfIdf(List<String> doc, List<List<String>> docs, String term) {
        return tf(doc, term) * idf(docs, term);
    }   
        /**
         * Method to create termVector according to its tfidf score.
         */
       /* public void tfIdfCalculator() {
            double tf; //term frequency
            double idf; //inverse document frequency
            double tfidf; //term requency inverse document frequency        
            for (String[] docTermsArray : termsDocsArray) {
                double[] tfidfvectors = new double[allTerms.size()];
                int count = 0;
                for (String terms : allTerms) {
                    tf = new TfIdf().tfCalculator(docTermsArray, terms);
                    idf = new TfIdf().idfCalculator(termsDocsArray, terms);
                    tfidf = tf * idf;
                    tfidfvectors[count] = tfidf;
                    count++;
                }
                tfidfDocsVector.add(tfidfvectors);  //storing document vectors;            
            }
        }
        
        /**
         * Method to calculate cosine similarity between all the documents.
         */
        public double getCosineSimilarity(List<List<Double>> tfidfDocsVector) {
        	double cosinesim = 0;
            for (int i = 0; i < tfidfDocsVector.size(); i++) {
                for (int j = 0; j < tfidfDocsVector.size(); j++) {
                	if(tfidfDocsVector.get(i).size() != tfidfDocsVector.get(j).size()) {
                		if(tfidfDocsVector.get(i).size() > tfidfDocsVector.get(j).size()) {
                			for(int k = tfidfDocsVector.get(j).size(); k < tfidfDocsVector.get(i).size(); k++) {
                				tfidfDocsVector.get(j).add(0.0);
                			}
                		}else {
                			for(int k = tfidfDocsVector.get(i).size(); k < tfidfDocsVector.get(j).size(); k++) {
                				//System.out.println(tfidfDocsVector.get(i));
                				tfidfDocsVector.get(i).add(0.0);
                			}
                		}
                		cosinesim = new CosineSimilarity().cosineSimilarity(tfidfDocsVector.get(i),tfidfDocsVector.get(j));
                		//System.out.println("between " + i + " and " + j + "  =  "+ cosinesim);
                	}else {
                		cosinesim = new CosineSimilarity().cosineSimilarity(tfidfDocsVector.get(i),tfidfDocsVector.get(j));
                		//System.out.println("between " + i + " and " + j + "  =  "+ cosinesim); 
                	}
                }
            }
            return cosinesim;
        }
        
        public static HashMap<String,Double> getTdIdfVectors(List<List<String>> documents, CriticalRanking calculator) {
        	HashMap<String,Double> tfidfScores = new HashMap<String,Double>();
        	for(List<String>  doclist : documents) {
            	//List<Double> tfidfDocsList = new ArrayList<Double>();
            	for(String docs : doclist) {
            		double score = calculator.tfIdf(doclist, documents, docs);
            		//System.out.println(docs+" ---- "+score);
            		tfidfScores.put(docs, score);
            		
            	}
        	}
        	return tfidfScores;
        }
        
        public static HashMap<String, Double> sortByValue(HashMap<String, Double> hm) 
        { 
            // Create a list from elements of HashMap 
            List<Map.Entry<String, Double> > list = 
                   new LinkedList<Map.Entry<String, Double> >(hm.entrySet()); 
      
            // Sort the list 
            Collections.sort(list, new Comparator<Map.Entry<String, Double> >() { 
                public int compare(Map.Entry<String, Double> o1,  
                                   Map.Entry<String, Double> o2) 
                { 
                    return (o1.getValue()).compareTo(o2.getValue()); 
                } 
            }); 
              
            // put data from sorted list to hashmap  
            HashMap<String, Double> temp = new LinkedHashMap<String, Double>(); 
            for (Map.Entry<String, Double> aa : list) { 
                temp.put(aa.getKey(), aa.getValue()); 
            } 
            return temp; 
        } 

        public static HashMap<String,Double> calculateTopicalScore(HashMap<String,Double> attrTfIdfScores, 
        		HashMap<String,Double> contextTfIdfScores,CriticalRanking calculator) {
        	HashMap<String,Double> topicalScoreForAttr = new HashMap<String,Double>();
        	
        	for(Map.Entry<String, Double> attrEntry : attrTfIdfScores.entrySet()) {
        		double cosinescore = 0; 
        		List<Double> attrList = new ArrayList<Double>();
        		attrList.add(attrEntry.getValue());
        				
        		List<Double> contextList = new ArrayList<Double>();
        		for(Map.Entry<String, Double> contextEntry : contextTfIdfScores.entrySet()) {
        			contextList.add(contextEntry.getValue());
        		}
        		List<List<Double>> tfidfDocsVector = Arrays.asList(attrList,contextList);
        		cosinescore = calculator.getCosineSimilarity(tfidfDocsVector);
        		topicalScoreForAttr.put(attrEntry.getKey(), cosinescore);
        	}
        	return topicalScoreForAttr;
        }
        
        public static HashMap<String,Double> calculatePopularityScore(HashMap<String,String> queryMap, HashMap<String,Double> attrTfIdfScores,
        		HashMap<String,Double> queryTfIdfScores, CriticalRanking calculator){
        	HashMap<String,Double> popScoreForAttr = new HashMap<String,Double>();
        	
        	for(Map.Entry<String, Double> attrEntry : attrTfIdfScores.entrySet()) {
        		double cosinescore = 0; double sum = 0; double psum = 0;
        		List<Double> attrList = new ArrayList<Double>();
        		attrList.add(attrEntry.getValue());
        				
        		
        		for(Map.Entry<String, Double> queryEntry : queryTfIdfScores.entrySet()) {
        			List<Double> queryList = new ArrayList<Double>();
        			queryList.add(queryEntry.getValue());
        			List<List<Double>> tfidfDocsVector = Arrays.asList(attrList,queryList);
            		cosinescore = calculator.getCosineSimilarity(tfidfDocsVector) * Integer.parseInt(queryMap.get(queryEntry.getKey()));
            		sum = sum + cosinescore;
            		psum = psum + Integer.parseInt(queryMap.get(queryEntry.getKey()));
            		
        		}
        		
        		//System.out.println(sum+"  "+psum);
        		popScoreForAttr.put(attrEntry.getKey(), (sum/psum));
        	}
        	return popScoreForAttr;
        }
        
        public static HashMap<String,Double> getCriticalScore(HashMap<String,Double> topicalScores, HashMap<String,Double> popScoreForAttr){
        	HashMap<String,Double> criticalScoreMap = new HashMap<String,Double>();
        	for(Map.Entry<String, Double> scoreEntry : topicalScores.entrySet()) {
        		double score = scoreEntry.getValue() + popScoreForAttr.get(scoreEntry.getKey());
        		criticalScoreMap.put(scoreEntry.getKey(), score);
        	}        	
        	return criticalScoreMap;
        }
        
        public static void getMembersAndFacts(HashMap<String,Double> criticalScore, String header, CSVWriter writer) throws IOException {
        	HashMap<String,Double> sortedCrtiticalScores = sortByValue(criticalScore);
        	
        	System.out.println("Max score is .... "+sortedCrtiticalScores);
        	int mapSize = sortedCrtiticalScores.size(); int counter = 0;
        	List<String> facts = new ArrayList<String>(); String subjectCol = null; 
        	for(Map.Entry<String, Double> scoreEntry : sortedCrtiticalScores.entrySet()) {
        		
        		counter++;
        		if((counter) == mapSize) {
        			System.out.println("Subject column "+scoreEntry.getKey());
        			subjectCol = scoreEntry.getKey();
        		}else if((counter+1) == mapSize || (counter+2) == mapSize) {
        			System.out.println("Facts column "+scoreEntry.getKey());
        			facts.add(scoreEntry.getKey());
        		}
        		
        	}
        	String data[] = {header,subjectCol, String.join(":::", facts)};
    		writer.writeNext(data);
        	//writer.close();
        }
    
    public static void main(String[] args) {

        List<List<Double>> tfidfDocsVector = new ArrayList<List<Double>>();
        CriticalRanking calculator = new CriticalRanking();
        
        Reader reader; 
        FileWriter outputFile=null;
        File file = new File(".//subjectAndFact.csv");
		
        try {
			outputFile = new FileWriter(file);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        CSVWriter writer = new CSVWriter(outputFile);
    	
		try {
			reader = Files.newBufferedReader(Paths.get(".//sample.csv"),Charset.forName("ISO-8859-1"));
			CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
			for (CSVRecord csvRecord : csvParser) {
				if(csvRecord.getRecordNumber() == 1 /*|| csvRecord.getRecordNumber() > 2*/)
					continue;
				
				List<List<String>> attrDoc = new ArrayList<List<String>>();
				List<List<String>> contextDoc = new ArrayList<List<String>>();
				List<List<String>> queryDoc = new ArrayList<List<String>>();
				
				String[] allAttr = csvRecord.get(0).split(":::");
				//System.out.println(allAttr.length);
				String[] contexts = csvRecord.get(2).split(":::");
				String[] queries = csvRecord.get(3).split(":::");
				for(String attr : allAttr) {
					List<String> attrList = new ArrayList<String>();
					if(attr != null) {
					   attrList.add(attr);
					}
					attrDoc.add(attrList);
				}
				HashMap<String,Double> attrTfIdfScores = getTdIdfVectors(attrDoc,calculator);
				//System.out.println(attrTfIdfScores);
				
				for(String contx : contexts) {
					List<String> contextList = new ArrayList<String>();
					if(contx != null) {
					   contextList.add(contx);
					}
					contextDoc.add(contextList);
				}
				HashMap<String,Double> contextTfIdfScores = getTdIdfVectors(contextDoc,calculator);
				HashMap<String,String> queryMap = new HashMap<String,String>();
				for(String query : queries) {
					List<String> queryList = new ArrayList<String>();
					int index = query.indexOf("===");
					//System.out.println(query.substring(index+3));
					queryMap.put(query.substring(0, index), query.substring(index+3));
					queryList.add(query.substring(0, index));
					queryDoc.add(queryList);
				}
				
				HashMap<String,Double> queryTfIdfScores = getTdIdfVectors(queryDoc,calculator);
				
				//System.out.println(queryTfIdfScores);
				HashMap<String,Double> topicalScores = calculateTopicalScore(attrTfIdfScores,contextTfIdfScores,calculator);
				HashMap<String,Double> popScoreForAttr = calculatePopularityScore(queryMap, attrTfIdfScores, queryTfIdfScores, calculator);
				HashMap<String,Double> criticalScore = getCriticalScore(topicalScores,popScoreForAttr);
				//System.out.println(criticalScore);	
				
				getMembersAndFacts(criticalScore, csvRecord.get(0), writer);
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        		
        calculator.getCosineSimilarity(tfidfDocsVector);
        
    }
}