package main;

import browser.NgordnetQuery;
import browser.NgordnetQueryHandler;
import ngrams.NGramMap;
import ngrams.TimeSeries;
import plotting.Plotter;

import java.util.ArrayList;
import java.util.List;

/** Handler for the "history" endpoint: returns a base64-encoded chart of
 *  each word's weight history. */
public class HistoryHandler extends NgordnetQueryHandler {
    private final NGramMap ngm;

    public HistoryHandler(NGramMap ngm) {
        this.ngm = ngm;
    }

    @Override
    public String handle(NgordnetQuery q) {
        List<String> words = q.words();
        int startYear = q.startYear();
        int endYear = q.endYear();

        List<TimeSeries> seriesList = new ArrayList<>();
        for (String word : words) {
            seriesList.add(ngm.weightHistory(word, startYear, endYear));
        }
        return Plotter.encodeChartAsString(Plotter.generateTimeSeriesChart(words, seriesList));
    }
}
