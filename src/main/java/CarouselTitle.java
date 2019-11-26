package main.java;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.extjwnl.JWNL;
import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.PointerType;
import net.sf.extjwnl.data.PointerUtils;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.Word;
import net.sf.extjwnl.data.list.PointerTargetNode;
import net.sf.extjwnl.data.list.PointerTargetNodeList;
import net.sf.extjwnl.data.list.PointerTargetTreeNodeList;
import net.sf.extjwnl.dictionary.Dictionary;

public class CarouselTitle {
	
	
	private static void demonstrateListOperation(IndexWord word) throws JWNLException {
		// Get all of the hypernyms (parents) of the first sense of word
		PointerTargetNodeList hypernyms = PointerUtils.getDirectHypernyms(word.getSenses().get(1));
		System.out.println("Direct hypernyms of \"" + word.getLemma() + "\":");
		hypernyms.print();
		for(PointerTargetNode h : hypernyms) {
			System.out.println(h.getPointerTarget().getSynset().getWords());
		}
	}
	
	public static void main(String args[]) throws FileNotFoundException, JWNLException {
		
		JWNL.initialize(new FileInputStream(".//properties.xml")); 
		final Dictionary dictionary = Dictionary.getInstance();	
		
		IndexWord indexWord = dictionary.getIndexWord(POS.NOUN, "rain");
		demonstrateListOperation(indexWord);
		List<Synset> senses = indexWord.getSenses();
		 
		for (Synset set : senses) {
		 System.out.println(indexWord + ": " + set.getGloss());

		 }
	}

}
