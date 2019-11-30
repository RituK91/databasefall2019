package main.java;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.opencsv.CSVWriter;

import net.sf.extjwnl.JWNL;
import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.PointerUtils;
import net.sf.extjwnl.data.Word;
import net.sf.extjwnl.data.list.PointerTargetNode;
import net.sf.extjwnl.data.list.PointerTargetNodeList;
import net.sf.extjwnl.dictionary.Dictionary;
import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;


public class CarouselTitle {
		
	private static List<String> findHypernyms(IndexWord word) throws JWNLException {
		// Get all of the hypernyms (parents) of the first sense of word
		PointerTargetNodeList hypernyms = PointerUtils.getDirectHypernyms(word.getSenses().get(0));
		//System.out.println("Direct hypernyms of \"" + word.getLemma() + "\":");
		List<String> hypernymsForSubj = new ArrayList<String>();
		
		//hypernyms.print();
		for(PointerTargetNode h : hypernyms) {
			//System.out.println(h.getPointerTarget().getSynset().getWords());
			List<Word> w1 = h.getPointerTarget().getSynset().getWords();
			for(Word w : w1) {
				//System.out.println(w.getLemma());
				hypernymsForSubj.add(w.getLemma());
			}
		}
		return hypernymsForSubj;
	}
	
	
	// method to getBagOfWords and noun phrases
	public static List<String> extractNounPhrases(String sentence) {
		
		InputStream modelInParse = null;
		List<String> nouns = new ArrayList<String>();
		try {
			//load chunking model
			modelInParse = new FileInputStream(".//en-parser-chunking.bin"); //from http://opennlp.sourceforge.net/models-1.5/
			ParserModel model = new ParserModel(modelInParse);
			
			//create parse tree
			Parser parser = ParserFactory.create(model);
			Parse topParses[] = ParserTool.parseLine(sentence, parser, 1);
			
			Set<String> nounPhrases = new HashSet<String>();
			//call subroutine to extract noun phrases
			for (Parse p : topParses)
				getNounPhrases(p, nounPhrases);
			
			//print noun phrases
			for (String s : nounPhrases) {
			   // System.out.println(s);
				nouns.add(s);
			}
			
		}catch (IOException e) {
			  e.printStackTrace();
			}
			finally {
			  if (modelInParse != null) {
			    try {
			    	modelInParse.close();
			    }
			    catch (IOException e) {
			    }
			  }
			}
		return nouns;
		}
	

//recursively loop through tree, extracting noun phrases
	public static void getNounPhrases(Parse p, Set<String> nounPhrases) {
				
	    if (p.getType().equals("NP")) { //NP=noun phrase
	         nounPhrases.add(p.getCoveredText());
	    }
	    for (Parse child : p.getChildren())
	         getNounPhrases(child, nounPhrases);
	    
	}
	
    private static void generateTitlesForDownward(CSVParser dccsvParser, HashMap<Integer,String[]> contextMap,
    		HashMap<Integer,String[]> queryMap, HashMap<Integer,String> headerMap, Dictionary dictionary, CSVWriter writer1) 
    				throws JWNLException, IOException {
		
    	String header[] = {"Pivot Entity", "Carousel Title", "Facts Values", "Members", "Context", "Queries", "Header"};
		writer1.writeNext(header);
		
		for(CSVRecord dc : dccsvParser) {
			//System.out.println("DC RN "+dc.getRecordNumber());
			
			String[] contextArr = contextMap.get((int)dc.getRecordNumber());
			//System.out.println(contextArr[1]);
			
			String[] queries = queryMap.get((int)dc.getRecordNumber());
			String attributes = headerMap.get((int)dc.getRecordNumber());
			
			LinkedHashMap<String,String> queryMap1 = new LinkedHashMap<String,String>();
			
			for(String query : queries) {
				//List<String> queryList = new ArrayList<String>();
				int index = query.indexOf("===");
				
				queryMap1.put(query.substring(0, index), query.substring(index+3));
			}
			
			//System.out.println(queryMap1);
			String[] members = dc.get(1).split(":::");
			HashSet<String> hypernymForSub = new HashSet<String>();
			
			for(String m : members) {
				IndexWord indexWord = dictionary.getIndexWord(POS.NOUN, m);
				//System.out.println(indexWord);
				if(indexWord != null) {
					List<String> hypernym = findHypernyms(indexWord);
					hypernymForSub.addAll(hypernym);
				}				
			}
			System.out.println("Downward -----");
			String title = getScores(contextArr, queryMap1, hypernymForSub);
			System.out.println(dc.get(0)+"--------"+title);
			String data[] = {dc.get(0), title, dc.get(1), dc.get(2), dc.get(4), dc.get(5), attributes};
			writer1.writeNext(data);
		}
		
		System.out.println("========================");
		
	}

	private static void generateTitlesForSideward(CSVParser sccsvParser, HashMap<Integer,String[]> contextMap, 
			HashMap<Integer,String[]> queryMap, HashMap<Integer,String> headerMap, Dictionary dictionary, CSVWriter writer2) 
					throws JWNLException, IOException {
		
		String header[] = {"Pivot Entity", "Carousel Title", "Facts Values", "Members", "Context", "Queries", "Header"};
		writer2.writeNext(header);
		
		for(CSVRecord dc : sccsvParser) {
			
			String[] contextArr = contextMap.get((int)dc.getRecordNumber());
			//System.out.println(contextArr[1]);
			
			String[] queries = queryMap.get((int)dc.getRecordNumber());
			String attributes = headerMap.get((int)dc.getRecordNumber());
			
			LinkedHashMap<String,String> queryMap1 = new LinkedHashMap<String,String>();
			
			for(String query : queries) {
				//List<String> queryList = new ArrayList<String>();
				int index = query.indexOf("===");
				
				queryMap1.put(query.substring(0, index), query.substring(index+3));
			}
			
			//System.out.println(queryMap1);
			
			String[] members = dc.get(1).split(":::");
			HashSet<String> hypernymForSub = new HashSet<String>();
			
			for(String m : members) {
				IndexWord indexWord = dictionary.getIndexWord(POS.NOUN, m);
				//System.out.println(indexWord);
				if(indexWord != null) {
					List<String> hypernym = findHypernyms(indexWord);
					hypernymForSub.addAll(hypernym);
				}
				
			}
			System.out.println("Sideway -------");
			String title = getScores(contextArr, queryMap1, hypernymForSub);
			System.out.println(dc.get(0)+"--------"+title);
			String data[] = {dc.get(0), title, dc.get(1), dc.get(2), dc.get(4), dc.get(5), attributes};
			writer2.writeNext(data);
		}
		
		System.out.println("========================");
	}
	
	public static String getScores(String[] context, LinkedHashMap<String,String> queryMap, HashSet<String> hypernymForSub) {
		
		HashSet<String> contextSet = new HashSet<String>();
		for(String c : context)
			contextSet.add(c);
		
		boolean bofw_hardConstraint = false; boolean noun_hardConstraint = false;
		boolean hardconstraint = false;
		LinkedHashSet<String> query = new LinkedHashSet<String>(queryMap.keySet());
		System.out.println(query);
		HashMap<String,List<String>> Wq = generateBagOfWords(query);
		HashMap<String,List<String>> Wa = generateBagOfWords(contextSet);
		HashMap<String,List<String>> Ws = generateBagOfWords(hypernymForSub);
		
		String contextStr = String.join(" ", contextSet); String queryStr = String.join(" ", queryMap.values());
		String subHypernymStr = String.join(" ", hypernymForSub);
		
		/*List<String> Nq = extractNounPhrases(queryStr); 
		List<String> Na = extractNounPhrases(contextStr);
		List<String> Ns = extractNounPhrases(subHypernymStr);*/
		
		/*System.out.println(Wq+" "+Nq); 
		System.out.println("-------------------------");
		System.out.println(Wa+" "+Na); 
		System.out.println("-------------------------------");
		System.out.println(Ws+" "+Ns);*/
		
		/* Checking the hard constraints */
		List<String> contxtHrdConst = new ArrayList<String>(); List<String> hypernymHrdConst = new ArrayList<String>();
		for(Entry<String, List<String>> cEntry : Wa.entrySet()) {
			for(String c1 : cEntry.getValue()) {
				contxtHrdConst.add(c1);
			}
		}
		
		for(Entry<String, List<String>> hEntry : Ws.entrySet()) {
			for(String c1 : hEntry.getValue()) {
				hypernymHrdConst.add(c1);
			}
		}
		
		for(Entry<String, List<String>> queryEntry : Wq.entrySet()) {
			for(String q : queryEntry.getValue()) {
				
				boolean context_value = contxtHrdConst.contains(q);
				boolean sub_value = hypernymHrdConst.contains(q);
				
				if(context_value == true || sub_value == true) {
					bofw_hardConstraint = true;
				}
				//System.out.println(bofw_hardConstraint);
				if(bofw_hardConstraint == true)
					break;
			}
									
		}
		
		/*for(String n : Nq) {
			
			boolean context_value = Na.contains(n);
			boolean sub_value = Ns.contains(n);
			
			if(context_value == true || sub_value == true) {
				noun_hardConstraint = true;
			}
			if(noun_hardConstraint == true)
				break;			
		}*/
		
		if(bofw_hardConstraint == true || noun_hardConstraint == true)
			hardconstraint = true;
		
		System.out.println(bofw_hardConstraint);
		//System.out.println(noun_hardConstraint);
		System.out.println(hardconstraint);
		// checking hard constraint ends here
		System.out.println(Wq.size());
		
		int[][] scores = new int[Wq.size()][2];
		System.out.println(scores.length);
		int k = 0;
		for(Entry<String, List<String>> queryEntry : Wq.entrySet()) {
			
			if(hardconstraint == false)
				continue;
			List<Integer> i1 = new ArrayList<Integer>();
			int contextQuery = 0;int hypernymQuery = 0;
			int commonContextWords = 0; int commonHypernymWords = 0;
			for(int i = 0; i < queryEntry.getValue().size(); i++) {	
				
				for(Entry<String, List<String>> contextEntry : Wa.entrySet()) {
					int temp = 0; 
					for(String c : contextEntry.getValue()) {
						if(c.equalsIgnoreCase(queryEntry.getValue().get(i)))
							commonContextWords = commonContextWords + 1;
					}
					temp = (commonContextWords) / (contextEntry.getValue().size());
					contextQuery = contextQuery + temp;
				}
				for(Entry<String, List<String>> hypernymEntry : Ws.entrySet()) {
					int temp = 0; 
					for(String s : hypernymEntry.getValue()) {
						if(s.equalsIgnoreCase(queryEntry.getValue().get(i)))
							commonHypernymWords = commonHypernymWords + 1;
					}
					temp = (commonHypernymWords) / (hypernymEntry.getValue().size());
					i1.add(temp);
				}
											
			}
			Collections.sort(i1, Collections.reverseOrder());
			if(!i1.isEmpty() || i1.size() != 0)
				hypernymQuery = i1.get(0);	
			System.out.println("Values of k ----"+k);
			scores[k][0] = contextQuery + hypernymQuery; // Saves the descriptive score
			//System.out.println(queryEntry.getKey()+" "+(contextQuery + hypernymQuery));
			k++;
		}
		int queryIndex = findIndexWithMaxPair(scores); Set<String> queries = Wq.keySet();
		List<String> queryList = new ArrayList<String>(queries);
		//System.out.println("Query "+queryList.get(queryIndex));
		return queryList.get(queryIndex);
	}
	
	public static int findIndexWithMaxPair(int[][] scores) {
		int temp = 0; int maxValue = 0; int index = 0; int tempindex = 0;
		for(int i = 0; i < scores.length; i++) {
			temp = maxValue; tempindex = index;
			maxValue = scores[i][0]; index = i;		
			if(temp > maxValue) {
				maxValue = temp;
				index = tempindex;
			}
		}
		//System.out.println("Max Value ==== "+maxValue);
		return tempindex;
	}
	
	public static HashMap<String,List<String>> generateBagOfWords(HashSet<String> objectToGenerateToken) {
		
		HashMap<String,List<String>> bagOfWordsMap = new HashMap<String,List<String>>();
		
		for(String c1 : objectToGenerateToken) {
			List<String> bagOfWords = new ArrayList<String>();
			String[] tokens = c1.split(" ");
			for(String t1 : tokens) {
				bagOfWords.add(t1);
			}
			bagOfWordsMap.put(c1, bagOfWords);
		}
		
		return bagOfWordsMap;
	}
	
	public static void main(String args[]) throws FileNotFoundException, JWNLException {
		
		Reader dcreader; Reader screader; Reader mainreader;
		//String sentence = "Who is the author of The Call of the Wild?";
		
		try {
			
			FileWriter outputFile1=null; FileWriter outputFile2=null;
	        File file1 = new File(".//DownwardCarousel.csv");
	        File file2 = new File(".//SidewardCarousel.csv");
			
	        try {
				outputFile1 = new FileWriter(file1); outputFile2 = new FileWriter(file2);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        CSVWriter writer1 = new CSVWriter(outputFile1); CSVWriter writer2 = new CSVWriter(outputFile2);
			dcreader = Files.newBufferedReader(Paths.get(".//dataForDownwardC.csv"),Charset.forName("ISO-8859-1"));
			screader = Files.newBufferedReader(Paths.get(".//dataForSidewardC.csv"),Charset.forName("ISO-8859-1"));
			mainreader = Files.newBufferedReader(Paths.get(".//sample.csv"),Charset.forName("ISO-8859-1"));
			CSVParser dccsvParser = new CSVParser(dcreader, CSVFormat.DEFAULT);
			CSVParser sccsvParser = new CSVParser(screader, CSVFormat.DEFAULT);
			CSVParser maincsvParser = new CSVParser(mainreader, CSVFormat.DEFAULT);
			
			JWNL.initialize(new FileInputStream(".//properties.xml")); 
			final Dictionary dictionary = Dictionary.getInstance();
			HashMap<Integer,String[]> contextMap = new HashMap<Integer,String[]>();
			HashMap<Integer,String[]> queryMap = new HashMap<Integer,String[]>();
			HashMap<Integer,String> headerMap = new HashMap<Integer,String>();
			
			for(CSVRecord mcsv : maincsvParser) {
				String context[] = mcsv.get(2).split(":::");
				String queries[] = mcsv.get(3).split(":::");
				contextMap.put((int) (mcsv.getRecordNumber() - 1), context);
				queryMap.put((int) (mcsv.getRecordNumber() - 1), queries);
				headerMap.put((int) (mcsv.getRecordNumber() - 1), mcsv.get(0));
			}
			
			generateTitlesForDownward(dccsvParser, contextMap, queryMap, headerMap, dictionary, writer1);
			generateTitlesForSideward(sccsvParser, contextMap, queryMap, headerMap, dictionary, writer2);
			
			writer1.close(); writer2.close();
			//extractNounPhrases(sentence);
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		
		
		/*IndexWord indexWord = dictionary.getIndexWord(POS.NOUN, "rain");
		findHypernyms(indexWord);*/
		
		/*List<Synset> senses = indexWord.getSenses(); 
		for (Synset set : senses) {
		 System.out.println(indexWord + ": " + set.getGloss());
		 }*/
	}

}
