package main;

import browser.NgordnetQuery;
import browser.NgordnetQueryHandler;
import graphs.WordNet;
import ngrams.NGramMap;
import ngrams.TimeSeries;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/** Handler for hyponym queries: returns the hyponyms common to all queried
 *  words. When k > 0, only the k most popular hyponyms (by total count in
 *  the requested time range) are returned, in alphabetical order. */
public class HyponymsHandler extends NgordnetQueryHandler {
    private final WordNet wordNet;
    private final NGramMap ngm;

    public HyponymsHandler(WordNet wordNet, NGramMap ngm) {
        this.wordNet = wordNet;
        this.ngm = ngm;
    }

    @Override
    public String handle(NgordnetQuery q) {
        List<String> words = q.words();
        int startYear = q.startYear();
        int endYear = q.endYear();
        int k = q.k();

        Set<String> hyponymWords = wordNet.hyponymsOf(words);

        if (k == 0) {
            List<String> result = new ArrayList<>(hyponymWords);
            return result.toString();
        }

        // k > 0: keep the k hyponyms that occurred the most times over the
        // time range, then return them in alphabetical order.
        List<String> sortedByCount = new ArrayList<>(hyponymWords);
        sortedByCount.sort(Comparator.comparingDouble(
                (String word) -> -totalCount(word, startYear, endYear)));

        List<String> topK = new ArrayList<>(sortedByCount.subList(0, Math.min(k, sortedByCount.size())));
        topK.sort(String::compareTo);
        return topK.toString();
    }

    /** Returns the total number of occurrences of word between startYear and
     *  endYear, inclusive. Words missing from the data contribute 0. */
    private double totalCount(String word, int startYear, int endYear) {
        TimeSeries history = ngm.countHistory(word, startYear, endYear);
        double total = 0;
        for (double count : history.data()) {
            total += count;
        }
        return total;
    }
}
