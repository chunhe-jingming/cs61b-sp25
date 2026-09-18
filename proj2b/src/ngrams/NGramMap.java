package ngrams;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static ngrams.TimeSeries.MAX_YEAR;
import static ngrams.TimeSeries.MIN_YEAR;

/**
 * An object that provides utility methods for making queries on the
 * Google NGrams dataset (or a subset thereof).
 *
 * An NGramMap stores pertinent data from a "words file" and a "counts
 * file". It is not a map in the strict sense, but it does provide additional
 * functionality.
 *
 * @author Josh Hug
 */
public class NGramMap {

    /** Maps each word to a TimeSeries of its per-year counts. */
    private final Map<String, TimeSeries> wordCounts = new HashMap<>();
    /** Total number of words recorded per year in all volumes. */
    private final TimeSeries totalCounts = new TimeSeries();

    /**
     * Constructs an NGramMap from WORDSFILENAME and COUNTSFILENAME.
     */
    public NGramMap(String wordsFilename, String countsFilename) {
        java.io.File wordsFile = new java.io.File(wordsFilename);
        java.io.File countsFile = new java.io.File(countsFilename);

        // Words file format: word \t year \t count \t volumeCount
        try (java.util.Scanner scanner = new java.util.Scanner(wordsFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("\t");
                if (parts.length < 3) {
                    continue;
                }
                String word = parts[0];
                int year;
                double count;
                try {
                    year = Integer.parseInt(parts[1]);
                    count = Double.parseDouble(parts[2]);
                } catch (NumberFormatException e) {
                    continue;
                }
                TimeSeries series = wordCounts.computeIfAbsent(word, k -> new TimeSeries());
                series.put(year, series.getOrDefault(year, 0.0) + count);
            }
        } catch (java.io.FileNotFoundException e) {
            throw new RuntimeException("Could not find words file: " + wordsFilename, e);
        }

        // Counts file format: year,totalCount,pageCount,volumeCount
        try (java.util.Scanner scanner = new java.util.Scanner(countsFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length < 2) {
                    continue;
                }
                int year;
                double count;
                try {
                    year = Integer.parseInt(parts[0]);
                    count = Double.parseDouble(parts[1]);
                } catch (NumberFormatException e) {
                    continue;
                }
                totalCounts.put(year, totalCounts.getOrDefault(year, 0.0) + count);
            }
        } catch (java.io.FileNotFoundException e) {
            throw new RuntimeException("Could not find counts file: " + countsFilename, e);
        }
    }

    /**
     * Provides the history of WORD between STARTYEAR and ENDYEAR, inclusive of both ends. The
     * returned TimeSeries should be a copy, not a link to this NGramMap's TimeSeries. In other
     * words, changes made to the object returned by this function should not also affect the
     * NGramMap. This is also known as a "defensive copy". If the word is not in the data files,
     * returns an empty TimeSeries.
     */
    public TimeSeries countHistory(String word, int startYear, int endYear) {
        TimeSeries series = wordCounts.get(word);
        if (series == null) {
            return new TimeSeries();
        }
        return new TimeSeries(series, startYear, endYear);
    }

    /**
     * Provides the history of WORD. The returned TimeSeries should be a copy, not a link to this
     * NGramMap's TimeSeries. In other words, changes made to the object returned by this function
     * should not also affect the NGramMap. This is also known as a "defensive copy". If the word
     * is not in the data files, returns an empty TimeSeries.
     */
    public TimeSeries countHistory(String word) {
        return countHistory(word, MIN_YEAR, MAX_YEAR);
    }

    /**
     * Returns a defensive copy of the total number of words recorded per year in all volumes.
     */
    public TimeSeries totalCountHistory() {
        return new TimeSeries(totalCounts, MIN_YEAR, MAX_YEAR);
    }

    /**
     * Provides a TimeSeries containing the relative frequency per year of WORD between STARTYEAR
     * and ENDYEAR, inclusive of both ends. If the word is not in the data files, returns an empty
     * TimeSeries.
     */
    public TimeSeries weightHistory(String word, int startYear, int endYear) {
        TimeSeries counts = countHistory(word, startYear, endYear);
        if (counts.isEmpty()) {
            return counts;
        }
        TimeSeries totals = new TimeSeries(totalCounts, startYear, endYear);
        return counts.dividedBy(totals);
    }

    /**
     * Provides a TimeSeries containing the relative frequency per year of WORD compared to all
     * words recorded in that year. If the word is not in the data files, returns an empty
     * TimeSeries.
     */
    public TimeSeries weightHistory(String word) {
        return weightHistory(word, MIN_YEAR, MAX_YEAR);
    }

    /**
     * Provides the summed relative frequency per year of all words in WORDS between STARTYEAR and
     * ENDYEAR, inclusive of both ends. If a word does not exist in this time frame, ignore it
     * rather than throwing an exception.
     */
    public TimeSeries summedWeightHistory(Collection<String> words,
                                          int startYear, int endYear) {
        TimeSeries summed = new TimeSeries();
        for (String word : words) {
            TimeSeries counts = countHistory(word, startYear, endYear);
            summed = summed.plus(counts);
        }
        if (summed.isEmpty()) {
            return summed;
        }
        TimeSeries totals = new TimeSeries(totalCounts, startYear, endYear);
        return summed.dividedBy(totals);
    }

    /**
     * Returns the summed relative frequency per year of all words in WORDS. If a word does not
     * exist in this time frame, ignore it rather than throwing an exception.
     */
    public TimeSeries summedWeightHistory(Collection<String> words) {
        return summedWeightHistory(words, MIN_YEAR, MAX_YEAR);
    }

}
