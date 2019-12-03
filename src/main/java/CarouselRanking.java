package main.java;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import main.java.util.WorkBookUtil;

/**
 * 
 * Ranks the carousel based on ranking function and writes to an excel with the
 * ranking function for each pivot entity.
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
		try {

			// Output file For Downward Carousel Ranking
			XSSFSheet downwardCarouselRankingSheetToWrite = WorkBookUtil
					.getWorkbookSheet("./testFolder/DownwardCarouselRanking.xlsx");
			FileOutputStream downwardCarouselRankingOutputFile = new FileOutputStream(
					new File("./testFolder/DownwardCarouselRanking.xlsx"));

			// Output file For Sideward Carousel Ranking
			XSSFSheet sidewardCarouselRankingSheetToWrite = WorkBookUtil
					.getWorkbookSheet("./testFolder/SidewardCarouselRanking.xlsx");
			FileOutputStream sidewardCarouselRankingOutputFile = new FileOutputStream(
					new File("./testFolder/SidewardCarouselRanking.xlsx"));

			// Read the carousels
			XSSFSheet downwardCarouselSheet = WorkBookUtil.getWorkbookSheet("./testFolder/DownwardCarousel.xlsx");
			XSSFSheet sidewardCarouselSheet = WorkBookUtil.getWorkbookSheet("./testFolder/SidewardCarousel.xlsx");

			Iterator<Row> downwardCarouselSheetRowIterator = downwardCarouselSheet.iterator();
			Iterator<Row> sidewardCarouselSheetRowIterator = sidewardCarouselSheet.iterator();

			while (downwardCarouselSheetRowIterator.hasNext()) {
				Row row = downwardCarouselSheetRowIterator.next();
				Row rowToWrite = downwardCarouselRankingSheetToWrite.createRow(row.getRowNum());
				String context = null, queries = null, pivotEntity = null, header = null;

				Iterator<Cell> cellIterator = row.cellIterator();
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

				Cell numberCell = rowToWrite.createCell(0);
				numberCell.setCellValue(row.getRowNum());

				Cell entityCell = rowToWrite.createCell(1);
				entityCell.setCellValue(pivotEntity);

				Cell scoreCell = rowToWrite.createCell(2);
				scoreCell.setCellValue(String.valueOf(rankingScore));
			}

			while (sidewardCarouselSheetRowIterator.hasNext()) {
				Row row = sidewardCarouselSheetRowIterator.next();
				Row rowToWrite = sidewardCarouselRankingSheetToWrite.createRow(row.getRowNum());
				String context = null, queries = null, pivotEntity = null, header = null;

				Iterator<Cell> cellIterator = row.cellIterator();
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
			downwardCarouselRankingSheetToWrite.getWorkbook().write(downwardCarouselRankingOutputFile);
			downwardCarouselRankingSheetToWrite.getWorkbook().close();
			sidewardCarouselRankingSheetToWrite.getWorkbook().write(sidewardCarouselRankingOutputFile);
			sidewardCarouselRankingSheetToWrite.getWorkbook().close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
