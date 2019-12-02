package main.java;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.opencsv.CSVWriter;

/**
 * 
 * Ranks the carousel based on ranking function and
 * writes to an excel with the ranking function for
 * each pivot entity.
 *
 */

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
				if (tfidfDocsVector.get(i).size() != tfidfDocsVector.get(j).size()) {
					if (tfidfDocsVector.get(i).size() > tfidfDocsVector.get(j).size()) {
						for (int k = tfidfDocsVector.get(j).size(); k < tfidfDocsVector.get(i).size(); k++) {
							tfidfDocsVector.get(j).add(0.0);
						}
					} else {
						for (int k = tfidfDocsVector.get(i).size(); k < tfidfDocsVector.get(j).size(); k++) {
							// System.out.println(tfidfDocsVector.get(i));
							tfidfDocsVector.get(i).add(0.0);
						}
					}
					cosinesim = new CosineSimilarity().cosineSimilarity(tfidfDocsVector.get(i), tfidfDocsVector.get(j));
					// System.out.println("between " + i + " and " + j + " = "+ cosinesim);
				} else {
					cosinesim = new CosineSimilarity().cosineSimilarity(tfidfDocsVector.get(i), tfidfDocsVector.get(j));
					// System.out.println("between " + i + " and " + j + " = "+ cosinesim);
				}
			}
		}
		return cosinesim;
	}

	public static HashMap<String, Double> getTdIdfVectors(List<List<String>> documents, CarouselRanking calculator) {
		HashMap<String, Double> tfidfScores = new HashMap<String, Double>();
		for (List<String> doclist : documents) {
			// List<Double> tfidfDocsList = new ArrayList<Double>();
			for (String docs : doclist) {
				double score = calculator.tfIdf(doclist, documents, docs);
				// System.out.println(docs+" ---- "+score);
				tfidfScores.put(docs, score);

			}
		}
		return tfidfScores;
	}

	public static double calculatePopularityScore(String[] contexts, String[] header, HashMap<String, String> queryMap,
			HashMap<String, Double> queryTfIdfScores, CarouselRanking calculator) {
		List<String> contextList = new ArrayList<String>();
		List<String> headerList = new ArrayList<String>();
		List<List<String>> contextAndHeaderDoc = new ArrayList<List<String>>();
		for (String contxt : contexts) {
			contextList.add(contxt);
		}
		contextAndHeaderDoc.add(contextList);
		for (String h : header) {
			headerList.add(h);
		}
		contextAndHeaderDoc.add(headerList);
		HashMap<String, Double> tfIdfScoresForH = getTdIdfVectors(contextAndHeaderDoc, calculator);
		System.out.println(tfIdfScoresForH);
		double sum = 0;
		for (Map.Entry<String, Double> HEntry : tfIdfScoresForH.entrySet()) {
			double cosinescore = 0;
			;
			List<Double> HList = new ArrayList<Double>();
			HList.add(HEntry.getValue());

			for (Map.Entry<String, Double> queryEntry : queryTfIdfScores.entrySet()) {
				List<Double> queryList = new ArrayList<Double>();
				queryList.add(queryEntry.getValue());
				List<List<Double>> tfidfDocsVector = Arrays.asList(HList, queryList);
				cosinescore = calculator.getCosineSimilarity(tfidfDocsVector)
						* Integer.parseInt(queryMap.get(queryEntry.getKey()));
				sum = sum + cosinescore;

			}
		}
		System.out.println(" Pop score :::: " + sum);
		return sum;
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

	public static double calculateRelatedScore(String pivotEntity, HashMap<String, String> queryMap) {
		HashSet<String> entity = new HashSet<String>();
		entity.add(pivotEntity);
		HashMap<String, List<String>> peBOW = generateBagOfWords(entity);
		List<String> peBOWList = new ArrayList<String>();

		for (Map.Entry<String, List<String>> peEntry : peBOW.entrySet()) {
			for (String pe : peEntry.getValue()) {
				peBOWList.add(pe);
			}
		}
		Set<String> querySet = queryMap.keySet();
		HashSet<String> queriesSet = new HashSet<String>(querySet);
		HashMap<String, List<String>> queryBOW = generateBagOfWords(queriesSet);

		double relScore = 0;
		for (Map.Entry<String, List<String>> queryEntry : queryBOW.entrySet()) {
			for (String pe : peBOWList) {
				if (queryEntry.getValue().contains(pe))
					relScore++;
			}

		}
		System.out.println("Related Score " + relScore);
		return relScore;
	}

	public static void main(String[] args) {

		CarouselRanking calculator = new CarouselRanking();
		/*
		 * Reader dcreader; Reader screader;
		 * 
		 * FileWriter outputFile1=null; FileWriter outputFile2=null; File file1 = new
		 * File(".//DownwardCarouselRanking.csv"); File file2 = new
		 * File(".//SidewardCarouselRanking.csv");
		 * 
		 * try { outputFile1 = new FileWriter(file1); outputFile2 = new
		 * FileWriter(file2); } catch (IOException e1) { // TODO Auto-generated catch
		 * block e1.printStackTrace(); } CSVWriter writer1 = new CSVWriter(outputFile1);
		 * CSVWriter writer2 = new CSVWriter(outputFile2);
		 */

		// ===========================================================================================================
		try {

			// ===============Data For Downward Carousel=================
			FileInputStream fileToWrite = new FileInputStream(new File(".//DownwardCarouselRanking.xlsx"));
			XSSFWorkbook workbookToWrite = new XSSFWorkbook(fileToWrite);
			XSSFSheet sheetToWrite = workbookToWrite.getSheetAt(0);
			FileOutputStream outFile = new FileOutputStream(new File(".//DownwardCarouselRanking.xlsx"));

			// ===============Data For Sideward Carousel=================
			FileInputStream fileToWrite1 = new FileInputStream(new File(".//SidewardCarouselRanking.xlsx"));
			XSSFWorkbook workbookToWrite1 = new XSSFWorkbook(fileToWrite1);
			XSSFSheet sheetToWrite1 = workbookToWrite1.getSheetAt(0);
			FileOutputStream outFile1 = new FileOutputStream(new File(".//SidewardCarouselRanking.xlsx"));

			FileInputStream dcfile = new FileInputStream(new File(".//DownwardCarousel.xlsx"));
			FileInputStream scfile = new FileInputStream(new File(".//SidewardCarousel.xlsx"));

			// Create Workbook instance holding reference to .xlsx file
			XSSFWorkbook workbook1 = new XSSFWorkbook(dcfile);
			XSSFWorkbook workbook2 = new XSSFWorkbook(scfile);

			// Get first/desired sheet from the workbook
			XSSFSheet sheet1 = workbook1.getSheetAt(0);
			XSSFSheet sheet2 = workbook2.getSheetAt(0);

			Iterator<Row> rowIterator1 = sheet1.iterator();
			Iterator<Row> rowIterator2 = sheet2.iterator();

			while (rowIterator1.hasNext()) {
				Row row = rowIterator1.next();
				Row rowToWrite = sheetToWrite.createRow(row.getRowNum());
				String context = null;
				String queries = null;
				String pivotEntity = null;

				Iterator<Cell> cellIterator = row.cellIterator();
				String header = null;
				int cellNumber = 0;

				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					if (cellNumber == 0) {
						pivotEntity = cell.toString();
					} else if (cellNumber == 5) {
						context = cell.toString();
					} else if (cellNumber == 6) {
						queries = cell.toString();
					} else if (cellNumber == 7) {
						header = cell.toString();
					}
					cellNumber++;
				}
				String[] queriesList = queries.split(":::");
				HashMap<String, String> queryMap = new HashMap<String, String>();
				List<List<String>> queryDoc = new ArrayList<List<String>>();
				for (String query : queriesList) {
					List<String> queryList = new ArrayList<String>();
					int index = query.indexOf("===");
					if (index != -1) {
						queryMap.put(query.substring(0, index), query.substring(index + 3));

						queryList.add(query.substring(0, index));
						queryDoc.add(queryList);
					}

				}

				HashMap<String, Double> queryTfIdfScores = getTdIdfVectors(queryDoc, calculator);
				double popScore = calculatePopularityScore(context.split(":::"), header.split(":::"), queryMap,
						queryTfIdfScores, calculator);
				double relScore = calculateRelatedScore(pivotEntity, queryMap);
				double rankingScore = 0;
				if (popScore == 0) {
					rankingScore = relScore;
				} else if (relScore == 0) {
					rankingScore = popScore;
				} else {
					rankingScore = popScore * relScore;
				}
				Cell noCell = rowToWrite.createCell(0);
				noCell.setCellValue(row.getRowNum());

				Cell entityCell = rowToWrite.createCell(1);
				entityCell.setCellValue(pivotEntity);

				Cell scoreCell = rowToWrite.createCell(2);
				scoreCell.setCellValue(String.valueOf(rankingScore));
			}

			while (rowIterator2.hasNext()) {
				Row row = rowIterator2.next();
				Row rowToWrite = sheetToWrite1.createRow(row.getRowNum());
				String context = null;
				String queries = null;
				String pivotEntity = null;

				Iterator<Cell> cellIterator = row.cellIterator();
				String header = null;
				int cellNumber = 0;

				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					if (cellNumber == 0) {
						pivotEntity = cell.toString();
					} else if (cellNumber == 5) {
						context = cell.toString();
					} else if (cellNumber == 6) {
						queries = cell.toString();
					} else if (cellNumber == 7) {
						header = cell.toString();
					}
					cellNumber++;
				}
				String[] queriesList = queries.split(":::");
				HashMap<String, String> queryMap = new HashMap<String, String>();
				List<List<String>> queryDoc = new ArrayList<List<String>>();
				for (String query : queriesList) {
					List<String> queryList = new ArrayList<String>();
					int index = query.indexOf("===");

					if (index != -1) {
						queryMap.put(query.substring(0, index), query.substring(index + 3));

						queryList.add(query.substring(0, index));
						queryDoc.add(queryList);
					}

				}

				HashMap<String, Double> queryTfIdfScores = getTdIdfVectors(queryDoc, calculator);
				double popScore = calculatePopularityScore(context.split(":::"), header.split(":::"), queryMap,
						queryTfIdfScores, calculator);
				double relScore = calculateRelatedScore(pivotEntity, queryMap);
				double rankingScore = 0;
				if (popScore == 0) {
					rankingScore = relScore;
				} else if (relScore == 0) {
					rankingScore = popScore;
				} else {
					rankingScore = popScore * relScore;
				}

				Cell noCell = rowToWrite.createCell(0);
				noCell.setCellValue(row.getRowNum());

				Cell entityCell = rowToWrite.createCell(1);
				entityCell.setCellValue(pivotEntity);

				Cell scoreCell = rowToWrite.createCell(2);
				scoreCell.setCellValue(String.valueOf(rankingScore));

			}
			workbookToWrite.write(outFile);
			workbookToWrite1.write(outFile1);

			/*
			 * dcreader =
			 * Files.newBufferedReader(Paths.get(".//DownwardCarousel.csv"),Charset.forName(
			 * "ISO-8859-1")); screader =
			 * Files.newBufferedReader(Paths.get(".//SidewardCarousel.csv"),Charset.forName(
			 * "ISO-8859-1")); CSVParser dccsvParser = new CSVParser(dcreader,
			 * CSVFormat.DEFAULT); CSVParser sccsvParser = new CSVParser(screader,
			 * CSVFormat.DEFAULT);
			 * 
			 * for(CSVRecord dcrecord : dccsvParser) { if(dcrecord.getRecordNumber() == 1)
			 * continue;
			 * 
			 * String[] contexts = dcrecord.get(5).split(":::"); String[] header =
			 * dcrecord.get(7).split(":::"); String[] queries =
			 * dcrecord.get(6).split(":::");
			 * 
			 * HashMap<String,String> queryMap = new HashMap<String,String>();
			 * List<List<String>> queryDoc = new ArrayList<List<String>>(); for(String query
			 * : queries) { List<String> queryList = new ArrayList<String>(); int index =
			 * query.indexOf("===");
			 * 
			 * queryMap.put(query.substring(0, index), query.substring(index+3));
			 * 
			 * queryList.add(query.substring(0, index)); queryDoc.add(queryList); }
			 * 
			 * HashMap<String,Double> queryTfIdfScores =
			 * getTdIdfVectors(queryDoc,calculator); double popScore =
			 * calculatePopularityScore(contexts, header, queryMap,
			 * queryTfIdfScores,calculator); double relScore =
			 * calculateRelatedScore(dcrecord.get(0), queryMap); double rankingScore = 0;
			 * if(popScore == 0 ) { rankingScore = relScore; }else if(relScore == 0) {
			 * rankingScore = popScore; }else { rankingScore = popScore * relScore; }
			 * 
			 * String[] data = {String.valueOf(dcrecord.getRecordNumber()), dcrecord.get(0),
			 * String.valueOf(rankingScore)}; writer1.writeNext(data); }
			 * 
			 * for(CSVRecord screcord : sccsvParser) { if(screcord.getRecordNumber() == 1)
			 * continue;
			 * 
			 * String[] contexts = screcord.get(5).split(":::"); String[] header =
			 * screcord.get(7).split(":::"); String[] queries =
			 * screcord.get(6).split(":::");
			 * 
			 * HashMap<String,String> queryMap = new HashMap<String,String>();
			 * List<List<String>> queryDoc = new ArrayList<List<String>>(); for(String query
			 * : queries) { List<String> queryList = new ArrayList<String>(); int index =
			 * query.indexOf("===");
			 * 
			 * queryMap.put(query.substring(0, index), query.substring(index+3));
			 * 
			 * queryList.add(query.substring(0, index)); queryDoc.add(queryList); }
			 * 
			 * HashMap<String,Double> queryTfIdfScores =
			 * getTdIdfVectors(queryDoc,calculator); double popScore =
			 * calculatePopularityScore(contexts, header, queryMap,
			 * queryTfIdfScores,calculator); double relScore =
			 * calculateRelatedScore(screcord.get(0), queryMap); double rankingScore = 0;
			 * if(popScore == 0 ) { rankingScore = relScore; }else if(relScore == 0) {
			 * rankingScore = popScore; }else { rankingScore = popScore * relScore; }
			 * String[] data = {String.valueOf(screcord.getRecordNumber()), screcord.get(0),
			 * String.valueOf(rankingScore)}; writer2.writeNext(data); }
			 * 
			 * writer1.close(); writer2.close();
			 */
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
