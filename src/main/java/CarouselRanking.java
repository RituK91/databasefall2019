package main.java;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class CarouselRanking {
	
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
    
    public static HashMap<String,Double> getTdIdfVectors(List<List<String>> documents, CarouselRanking calculator) {
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
	
	public static double calculatePopularityScore(String[] contexts, String[] header, HashMap<String,String> queryMap,
			HashMap<String,Double> queryTfIdfScores, CarouselRanking calculator) {
		List<String> contextList = new ArrayList<String>(); List<String> headerList = new ArrayList<String>();
		List<List<String>> contextAndHeaderDoc = new ArrayList<List<String>>();
		for(String contxt : contexts) {
			contextList.add(contxt);
		}
		contextAndHeaderDoc.add(contextList);
		for(String h : header) {
			headerList.add(h);
		}
		contextAndHeaderDoc.add(headerList);
		HashMap<String,Double> tfIdfScoresForH = getTdIdfVectors(contextAndHeaderDoc, calculator);
		System.out.println(tfIdfScoresForH);
		double sum = 0;
		for(Map.Entry<String, Double> HEntry : tfIdfScoresForH.entrySet()) {
    		double cosinescore = 0; ; 
    		List<Double> HList = new ArrayList<Double>();
    		HList.add(HEntry.getValue());
    				        		
    		for(Map.Entry<String, Double> queryEntry : queryTfIdfScores.entrySet()) {
    			List<Double> queryList = new ArrayList<Double>();
    			queryList.add(queryEntry.getValue());
    			List<List<Double>> tfidfDocsVector = Arrays.asList(HList,queryList);
        		cosinescore = calculator.getCosineSimilarity(tfidfDocsVector) * Integer.parseInt(queryMap.get(queryEntry.getKey()));
        		sum = sum + cosinescore;
        		
    		}
    	}
		System.out.println(" Pop score :::: "+sum);
		return sum;
	}
	
	public static void calculateRelatedScore() {
		
	}

	public static void main(String[] args) {
		
		Reader dcreader; Reader screader;
		CarouselRanking calculator = new CarouselRanking();
		try {
			dcreader = Files.newBufferedReader(Paths.get(".//DownwardCarousel.csv"),Charset.forName("ISO-8859-1"));
			screader = Files.newBufferedReader(Paths.get(".//SidewardCarousel.csv"),Charset.forName("ISO-8859-1"));
			CSVParser dccsvParser = new CSVParser(dcreader, CSVFormat.DEFAULT);
			CSVParser sccsvParser = new CSVParser(screader, CSVFormat.DEFAULT);
			
			for(CSVRecord dcrecord : dccsvParser) {
				if(dcrecord.getRecordNumber() == 1)
					continue;
				
				String[] contexts = dcrecord.get(4).split(":::");
				String[] header = dcrecord.get(6).split(":::");
				String[] queries = dcrecord.get(5).split(":::");
				
				HashMap<String,String> queryMap = new HashMap<String,String>();
				List<List<String>> queryDoc = new ArrayList<List<String>>();
				for(String query : queries) {
					List<String> queryList = new ArrayList<String>();
					int index = query.indexOf("===");
					
					queryMap.put(query.substring(0, index), query.substring(index+3));
					
					queryList.add(query.substring(0, index));
					queryDoc.add(queryList);
				}		
				
				HashMap<String,Double> queryTfIdfScores = getTdIdfVectors(queryDoc,calculator);
				double popScore = calculatePopularityScore(contexts, header, queryMap, queryTfIdfScores,calculator);
			}
			
			for(CSVRecord screcord : sccsvParser) {
				if(screcord.getRecordNumber() == 1)
					continue;
				
				screcord.get(0);
			}
		}catch(IOException e) {
			e.printStackTrace();
		}

	}

}
