package main.java;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;


public class TFIDFCalculator {
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
        public void getCosineSimilarity(List<List<Double>> tfidfDocsVector) {
            for (int i = 0; i < tfidfDocsVector.size(); i++) {
                for (int j = 0; j < tfidfDocsVector.size(); j++) {
                	double cosinesim = new CosineSimilarity().cosineSimilarity(tfidfDocsVector.get(i),tfidfDocsVector.get(j));
                    System.out.println("between " + i + " and " + j + "  =  "+ cosinesim);                                      
                }
            }
        }

    

    public static void main(String[] args) {

        List<String> doc1 = Arrays.asList("Lorem", "ipsum", "dolor", "ipsum", "sit", "ipsum");
        List<String> doc2 = Arrays.asList("Vituperata", "incorrupte", "at", "ipsum", "pro", "quo");
        List<String> doc3 = Arrays.asList("Has", "persius", "disputationi", "id", "simul","id");
        List<List<String>> documents = Arrays.asList(doc1, doc2, doc3);
        List<String> attrList = null;
        
        Reader reader; 
		try {
			reader = Files.newBufferedReader(Paths.get(".//sample.csv"));
			CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
			for (CSVRecord csvRecord : csvParser) {
				//System.out.println(csvRecord.get(0));
				String[] allAttr = csvRecord.get(0).split(":::");
				for(String attr : allAttr) {
					attrList.add(attr);
				}
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        

        List<List<Double>> tfidfDocsVector = new ArrayList<List<Double>>();
        TFIDFCalculator calculator = new TFIDFCalculator();
        double tfidfscore = 0;
        for(List<String>  doclist : documents) {
        	List<Double> tfidfDocsList = new ArrayList<Double>();
        	for(String docs : doclist) {
        		tfidfscore = calculator.tfIdf(doclist, documents, docs);
        		//System.out.println(docs+" ---- "+tfidfscore);
        		tfidfDocsList.add(tfidfscore);
        		
        	}
        	tfidfDocsVector.add(tfidfDocsList);
        }
        System.out.println(tfidfDocsVector);
        calculator.getCosineSimilarity(tfidfDocsVector);
        //double tfidf = calculator.tfIdf(doc1, documents, "ipsum");
        //System.out.println("TF-IDF (ipsum) = " + tfidf);
    }
}