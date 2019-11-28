package main.java;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import net.sf.extjwnl.JWNL;
import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.PointerUtils;
import net.sf.extjwnl.data.Word;
import net.sf.extjwnl.data.list.PointerTargetNode;
import net.sf.extjwnl.data.list.PointerTargetNodeList;
import net.sf.extjwnl.dictionary.Dictionary;

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
	
	private static void generateTitlesForDownward(CSVParser dccsvParser, Dictionary dictionary) throws JWNLException {
		
		for(CSVRecord dc : dccsvParser) {
			//System.out.println(dc.get(1));
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
			System.out.println(hypernymForSub);
		}
		System.out.println("========================");
		
	}
	
	private static void generateTitlesForSideward(CSVParser sccsvParser, Dictionary dictionary) throws JWNLException {
		
		for(CSVRecord dc : sccsvParser) {
			//System.out.println(dc.get(1));
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
			System.out.println(hypernymForSub);
		}
		System.out.println("========================");
	}
	
	public static void main(String args[]) throws FileNotFoundException, JWNLException {
		
		Reader dcreader; Reader screader;
		
		try {
			dcreader = Files.newBufferedReader(Paths.get(".//dataForDownwardC.csv"),Charset.forName("ISO-8859-1"));
			screader = Files.newBufferedReader(Paths.get(".//dataForSidewardC.csv"),Charset.forName("ISO-8859-1"));
			CSVParser dccsvParser = new CSVParser(dcreader, CSVFormat.DEFAULT);
			CSVParser sccsvParser = new CSVParser(screader, CSVFormat.DEFAULT);
			
			JWNL.initialize(new FileInputStream(".//properties.xml")); 
			final Dictionary dictionary = Dictionary.getInstance();
			
			generateTitlesForDownward(dccsvParser, dictionary); generateTitlesForSideward(sccsvParser, dictionary);
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
