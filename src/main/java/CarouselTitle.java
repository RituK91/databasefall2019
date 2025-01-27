package main.java;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import main.java.util.WorkBookUtil;
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

/*
 * This class generates the carousel title and 
 * writes to a excel file with details of the carousel.
 */

public class CarouselTitle {

	private static List<String> findHypernyms(IndexWord word) throws JWNLException {
		// Get all of the hypernyms (parents) of the first sense of word
		PointerTargetNodeList hypernyms = PointerUtils.getDirectHypernyms(word.getSenses().get(0));
		List<String> hypernymsForSubj = new ArrayList<String>();

		for (PointerTargetNode h : hypernyms) {
			List<Word> w1 = h.getPointerTarget().getSynset().getWords();
			for (Word w : w1) {
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
			// load chunking model
			modelInParse = new FileInputStream(".//en-parser-chunking.bin");
			ParserModel model = new ParserModel(modelInParse);

			// create parse tree
			Parser parser = ParserFactory.create(model);
			Parse topParses[] = ParserTool.parseLine(sentence, parser, 1);

			Set<String> nounPhrases = new HashSet<String>();
			// call subroutine to extract noun phrases
			for (Parse p : topParses)
				getNounPhrases(p, nounPhrases);

			for (String s : nounPhrases) {
				nouns.add(s);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (modelInParse != null) {
				try {
					modelInParse.close();
				} catch (IOException e) {
				}
			}
		}
		return nouns;
	}

//recursively loop through tree, extracting noun phrases
	public static void getNounPhrases(Parse p, Set<String> nounPhrases) {

		if (p.getType().equals("NP")) { // NP=noun phrase
			nounPhrases.add(p.getCoveredText());
		}
		for (Parse child : p.getChildren())
			getNounPhrases(child, nounPhrases);

	}

	private static void generateTitlesForDownwardCarousel(XSSFSheet sheet1, HashMap<Integer, String[]> contextMap,
			HashMap<Integer, String[]> queryMap, HashMap<Integer, String> headerMap, Dictionary dictionary,
			XSSFSheet sheetToWrite) throws JWNLException, IOException {

		Iterator<Row> rowIterator = sheet1.iterator();
		int rowNumber = 0;

		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			if (row.getRowNum() == 0) {
				rowNumber++;
				continue;
			}
			Row rowToWrite = sheetToWrite.createRow(row.getRowNum() - 1);
			String[] contextArr = contextMap.get(row.getRowNum());

			String[] queries = queryMap.get(row.getRowNum());
			String attributes = headerMap.get(row.getRowNum());

			LinkedHashMap<String, String> queryMap1 = new LinkedHashMap<String, String>();

			for (String query : queries) {
				int index = query.indexOf("===");
				queryMap1.put(query.substring(0, index), query.substring(index + 3));
			}

			Iterator<Cell> cellIterator = row.cellIterator();
			String penitity = null, fact1 = null, fact2 = null, member = null, context = null, queryStr = null;
			int cellNumber = 0;

			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				if (cellNumber == 0) {
					penitity = cell.toString();
				} else if (cellNumber == 1) {
					fact1 = cell.toString();
				} else if (cellNumber == 2) {
					fact2 = cell.toString();
				} else if (cellNumber == 3) {
					member = cell.toString();
				} else if (cellNumber == 4) {
					context = cell.toString();
				} else if (cellNumber == 5) {
					queryStr = cell.toString();
				}
				cellNumber++;
			}
			String[] members = member.split(":::");

			HashSet<String> hypernymForSub = new HashSet<String>();

			for (String m : members) {
				IndexWord indexWord = dictionary.getIndexWord(POS.NOUN, m);
				if (indexWord != null) {
					List<String> hypernym = findHypernyms(indexWord);
					hypernymForSub.addAll(hypernym);
				}
			}
			String title = getScores(contextArr, queryMap1, hypernymForSub);

			Cell entityCell = rowToWrite.createCell(0);
			entityCell.setCellValue(penitity);

			Cell titleCell = rowToWrite.createCell(1);
			titleCell.setCellValue(title);

			Cell fact1Cell = rowToWrite.createCell(2);
			fact1Cell.setCellValue(fact1);

			Cell fact2Cell = rowToWrite.createCell(3);
			fact2Cell.setCellValue(fact2);

			Cell SubCell = rowToWrite.createCell(4);
			SubCell.setCellValue(member);

			Cell contxtCell = rowToWrite.createCell(5);
			contxtCell.setCellValue(context);

			Cell queryCell = rowToWrite.createCell(6);
			queryCell.setCellValue(queryStr);

			Cell attrCell = rowToWrite.createCell(7);
			attrCell.setCellValue(attributes);

		}
	}

	private static void generateTitlesForSidewardCarousel(XSSFSheet sheet2, HashMap<Integer, String[]> contextMap,
			HashMap<Integer, String[]> queryMap, HashMap<Integer, String> headerMap, Dictionary dictionary,
			XSSFSheet sheetToWrite1) throws JWNLException, IOException {
		Iterator<Row> rowIterator = sheet2.iterator();
		int rowNumber = 0;

		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			if (row.getRowNum() == 0) {
				rowNumber++;
				continue;
			}
			Row rowToWrite = sheetToWrite1.createRow(row.getRowNum() - 1);
			String[] contextArr = contextMap.get(row.getRowNum());

			String[] queries = queryMap.get(row.getRowNum());
			String attributes = headerMap.get(row.getRowNum());

			LinkedHashMap<String, String> queryMap1 = new LinkedHashMap<String, String>();

			for (String query : queries) {
				int index = query.indexOf("===");
				queryMap1.put(query.substring(0, index), query.substring(index + 3));
			}

			Iterator<Cell> cellIterator = row.cellIterator();
			String penitity = null, fact1 = null, fact2 = null, member = null, context = null, queryStr = null;
			int cellNumber = 0;

			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				if (cellNumber == 0) {
					penitity = cell.toString();
				} else if (cellNumber == 1) {
					fact1 = cell.toString();
				} else if (cellNumber == 2) {
					fact2 = cell.toString();
				} else if (cellNumber == 3) {
					member = cell.toString();
				} else if (cellNumber == 4) {
					context = cell.toString();
				} else if (cellNumber == 5) {
					queryStr = cell.toString();
				}
				cellNumber++;
			}
			String[] subjectMembers = member.split(":::");

			HashSet<String> hypernymForSub = new HashSet<String>();

			for (String subjectMember : subjectMembers) {
				IndexWord indexWord = dictionary.getIndexWord(POS.NOUN, subjectMember);
				if (indexWord != null) {
					List<String> hypernym = findHypernyms(indexWord);
					hypernymForSub.addAll(hypernym);
				}
			}
			String title = getScores(contextArr, queryMap1, hypernymForSub);

			Cell entityCell = rowToWrite.createCell(0);
			entityCell.setCellValue(penitity);

			Cell titleCell = rowToWrite.createCell(1);
			titleCell.setCellValue(title);

			Cell fact1Cell = rowToWrite.createCell(2);
			fact1Cell.setCellValue(fact1);

			Cell fact2Cell = rowToWrite.createCell(3);
			fact2Cell.setCellValue(fact2);

			Cell SubCell = rowToWrite.createCell(4);
			SubCell.setCellValue(member);

			Cell contxtCell = rowToWrite.createCell(5);
			contxtCell.setCellValue(context);

			Cell queryCell = rowToWrite.createCell(6);
			queryCell.setCellValue(queryStr);

			Cell attrCell = rowToWrite.createCell(7);
			attrCell.setCellValue(attributes);
		}

	}

	public static String getScores(String[] context, LinkedHashMap<String, String> queryMap,
			HashSet<String> hypernymForSub) {

		HashSet<String> contextSet = new HashSet<String>();
		for (String c : context)
			contextSet.add(c);

		boolean bofw_hardConstraint = false;
		boolean noun_hardConstraint = false;
		boolean hardconstraint = false;
		LinkedHashSet<String> query = new LinkedHashSet<String>(queryMap.keySet());
		HashMap<String, List<String>> Wq = generateBagOfWords(query);
		HashMap<String, List<String>> Wa = generateBagOfWords(contextSet);
		HashMap<String, List<String>> Ws = generateBagOfWords(hypernymForSub);

		String contextStr = String.join(" ", contextSet);
		String queryStr = String.join(" ", queryMap.values());
		String subHypernymStr = String.join(" ", hypernymForSub);

		/*
		 * List<String> Nq = extractNounPhrases(queryStr); List<String> Na =
		 * extractNounPhrases(contextStr); List<String> Ns =
		 * extractNounPhrases(subHypernymStr);
		 */

		/*
		 * System.out.println(Wq+" "+Nq);
		 * System.out.println("-------------------------");
		 * System.out.println(Wa+" "+Na);
		 * System.out.println("-------------------------------");
		 * System.out.println(Ws+" "+Ns);
		 */

		/* Checking the hard constraints */
		List<String> contxtHrdConst = new ArrayList<String>();
		List<String> hypernymHrdConst = new ArrayList<String>();
		for (Entry<String, List<String>> cEntry : Wa.entrySet()) {
			for (String c1 : cEntry.getValue()) {
				contxtHrdConst.add(c1);
			}
		}

		for (Entry<String, List<String>> hEntry : Ws.entrySet()) {
			for (String c1 : hEntry.getValue()) {
				hypernymHrdConst.add(c1);
			}
		}

		for (Entry<String, List<String>> queryEntry : Wq.entrySet()) {
			for (String q : queryEntry.getValue()) {

				boolean context_value = contxtHrdConst.contains(q);
				boolean sub_value = hypernymHrdConst.contains(q);

				if (context_value == true || sub_value == true) {
					bofw_hardConstraint = true;
				}
				// System.out.println(bofw_hardConstraint);
				if (bofw_hardConstraint == true)
					break;
			}

		}

		/*
		 * for(String n : Nq) {
		 * 
		 * boolean context_value = Na.contains(n); boolean sub_value = Ns.contains(n);
		 * 
		 * if(context_value == true || sub_value == true) { noun_hardConstraint = true;
		 * } if(noun_hardConstraint == true) break; }
		 */

		if (bofw_hardConstraint == true || noun_hardConstraint == true)
			hardconstraint = true;

		System.out.println(bofw_hardConstraint);
		// System.out.println(noun_hardConstraint);
		System.out.println(hardconstraint);
		// checking hard constraint ends here
		System.out.println(Wq.size());

		int[][] scores = new int[Wq.size()][2];
		int k = 0;
		for (Entry<String, List<String>> queryEntry : Wq.entrySet()) {

			if (hardconstraint == false)
				continue;
			List<Integer> i1 = new ArrayList<Integer>();
			int contextQuery = 0;
			int hypernymQuery = 0;
			int commonContextWords = 0;
			int commonHypernymWords = 0;
			for (int i = 0; i < queryEntry.getValue().size(); i++) {

				for (Entry<String, List<String>> contextEntry : Wa.entrySet()) {
					int temp = 0;
					for (String c : contextEntry.getValue()) {
						if (c.equalsIgnoreCase(queryEntry.getValue().get(i)))
							commonContextWords = commonContextWords + 1;
					}
					temp = (commonContextWords) / (contextEntry.getValue().size());
					contextQuery = contextQuery + temp;
				}
				for (Entry<String, List<String>> hypernymEntry : Ws.entrySet()) {
					int temp = 0;
					for (String s : hypernymEntry.getValue()) {
						if (s.equalsIgnoreCase(queryEntry.getValue().get(i)))
							commonHypernymWords = commonHypernymWords + 1;
					}
					temp = (commonHypernymWords) / (hypernymEntry.getValue().size());
					i1.add(temp);
				}

			}
			Collections.sort(i1, Collections.reverseOrder());
			if (!i1.isEmpty() || i1.size() != 0)
				hypernymQuery = i1.get(0);
			scores[k][0] = contextQuery + hypernymQuery; // Saves the descriptive score
			k++;
		}
		int queryIndex = findIndexWithMaxPair(scores);
		Set<String> queries = Wq.keySet();
		List<String> queryList = new ArrayList<String>(queries);
		return queryList.get(queryIndex);
	}

	public static int findIndexWithMaxPair(int[][] scores) {
		int temp = 0;
		int maxValue = 0;
		int index = 0;
		int tempindex = 0;
		for (int i = 0; i < scores.length; i++) {
			temp = maxValue;
			tempindex = index;
			maxValue = scores[i][0];
			index = i;
			if (temp > maxValue) {
				maxValue = temp;
				index = tempindex;
			}
		}
		return tempindex;
	}

	public static HashMap<String, List<String>> generateBagOfWords(HashSet<String> objectToGenerateToken) {

		HashMap<String, List<String>> bagOfWordsMap = new HashMap<String, List<String>>();

		for (String c1 : objectToGenerateToken) {
			List<String> bagOfWords = new ArrayList<String>();
			String[] tokens = c1.split(" ");
			for (String t1 : tokens) {
				bagOfWords.add(t1);
			}
			bagOfWordsMap.put(c1, bagOfWords);
		}

		return bagOfWordsMap;
	}

	public static void main(String args[]) throws FileNotFoundException, JWNLException {
		XSSFSheet downwardCarouselSheetToWrite = null, sidewardCarouselSheetToWrite = null;
		try {
			// Output file for downward carousel
			downwardCarouselSheetToWrite = WorkBookUtil.getWorkbookSheet("./testFolder/DownwardCarousel.xlsx");
			FileOutputStream downwardCarouselOutputFile = new FileOutputStream(
					new File("./testFolder/DownwardCarousel.xlsx"));

			// Output file for Sideward Carousel
			sidewardCarouselSheetToWrite = WorkBookUtil.getWorkbookSheet("./testFolder/SidewardCarousel.xlsx");
			FileOutputStream sidewardCarouselOutFile = new FileOutputStream(
					new File("./testFolder/SidewardCarousel.xlsx"));

			XSSFSheet testSampleSheet = WorkBookUtil.getWorkbookSheet(".//TestSample.xlsx");
			XSSFSheet dataDCSheet = WorkBookUtil.getWorkbookSheet("./testFolder/dataDC.xlsx");
			XSSFSheet dataSCsheet = WorkBookUtil.getWorkbookSheet("./testFolder/dataSC.xlsx");

			Iterator<Row> rowIterator = testSampleSheet.iterator();
			int rowNumber = 0;

			JWNL.initialize(new FileInputStream(".//properties.xml"));
			final Dictionary dictionary = Dictionary.getInstance();

			HashMap<Integer, String[]> contextMap = new HashMap<Integer, String[]>();
			HashMap<Integer, String[]> queryMap = new HashMap<Integer, String[]>();
			HashMap<Integer, String> headerMap = new HashMap<Integer, String>();

			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				if (row.getRowNum() == 0) {
					rowNumber++;
					continue;
				}

				String context = null;
				String queries = null;

				Iterator<Cell> cellIterator = row.cellIterator();
				String header = null;
				int cellNumber = 0;

				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					if (cellNumber == 2) {
						context = cell.toString();
					} else if (cellNumber == 3) {
						queries = cell.toString();
					} else if (cellNumber == 0) {
						header = cell.toString();
					}
					cellNumber++;
				}
				contextMap.put(row.getRowNum(), context.split(":::"));
				queryMap.put(row.getRowNum(), queries.split(":::"));
				headerMap.put(row.getRowNum(), header);
				rowNumber++;

			}

			generateTitlesForDownwardCarousel(dataDCSheet, contextMap, queryMap, headerMap, dictionary,
					downwardCarouselSheetToWrite);
			generateTitlesForSidewardCarousel(dataSCsheet, contextMap, queryMap, headerMap, dictionary,
					sidewardCarouselSheetToWrite);

			downwardCarouselSheetToWrite.getWorkbook().write(downwardCarouselOutputFile);
			sidewardCarouselSheetToWrite.getWorkbook().write(sidewardCarouselOutFile);
			downwardCarouselSheetToWrite.getWorkbook().close();
			sidewardCarouselSheetToWrite.getWorkbook().close();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				downwardCarouselSheetToWrite.getWorkbook().close();
				sidewardCarouselSheetToWrite.getWorkbook().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
