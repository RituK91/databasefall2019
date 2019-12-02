package main.java;

import java.util.List;

public class CosineSimilarity {

    /**
     * Method to calculate cosine similarity between two documents.
     * @param tfidfVector1 : tfidf vector 1 (a)
     * @param tfidfVector2 : tfidf vector 2 (b)
     * @return 
     */
    public double cosineSimilarity(List<Double> tfidfVector1, List<Double> tfidfVector2) {
        double dotProduct = 0.0;
        double magnitude1 = 0.0;
        double magnitude2 = 0.0;
        double cosineSimilarity = 0.0;

        for (int i = 0; i < tfidfVector1.size(); i++) //docVector1 and docVector2 must be of same length
        {
            dotProduct += tfidfVector1.get(i) * tfidfVector2.get(i);  //a.b
            magnitude1 += Math.pow(tfidfVector1.get(i), 2);  //(a^2)
            magnitude2 += Math.pow(tfidfVector2.get(i), 2); //(b^2)
        }

        magnitude1 = Math.sqrt(magnitude1);//sqrt(a^2)
        magnitude2 = Math.sqrt(magnitude2);//sqrt(b^2)
        //System.out.println(docVector1+" "+docVector2);

        if (magnitude1 != 0.0 | magnitude2 != 0.0) {
            cosineSimilarity = dotProduct / (magnitude1 * magnitude2);
            //System.out.println(dotProduct +"------" +(magnitude1 * magnitude2));
        } else {
            return 0.0;
        }
        return cosineSimilarity;
    }
}
