package main;

import browser.NgordnetQuery;
import browser.NgordnetQueryHandler;
import ngrams.NGramMap;
import ngrams.TimeSeries;

import java.util.List;

/** Handler for the "historytext" endpoint: returns each word's weight
 *  history as text. */
public class HistoryTextHandler extends NgordnetQueryHandler {
    private final NGramMap ngm;

    public HistoryTextHandler(NGramMap ngm) {
        this.ngm = ngm;
    }

    @Override
    public String handle(NgordnetQuery q) {
        List<String> words = q.words();
        int startYear = q.startYear();
        int endYear = q.endYear();

        StringBuilder response = new StringBuilder();
        for (String word : words) {
            TimeSeries weights = ngm.weightHistory(word, startYear, endYear);
            response.append(word).append(": ").append(weights.toString()).append("\n");
        }
        return response.toString();
    }
}
