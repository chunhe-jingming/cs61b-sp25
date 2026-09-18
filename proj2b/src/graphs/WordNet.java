package graphs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/** A graph representation of the WordNet dataset: synsets (nodes) and
 *  hyponym relations (directed edges from hypernym to hyponym). */
public class WordNet {

    /** Maps a synset id to the words in that synset. */
    private final Map<Integer, List<String>> synsets = new HashMap<>();
    /** Maps a synset id to its direct hyponym synset ids. */
    private final Map<Integer, List<Integer>> hyponyms = new HashMap<>();
    /** Maps a word to the ids of all synsets that contain it. */
    private final Map<String, Set<Integer>> wordToSynsetIds = new HashMap<>();

    /**
     * Constructs a WordNet from the given synset and hyponym files.
     * @param synsetFilename  synsets file: one synset per line,
     *                        "id,word1 word2 ...,definition"
     * @param hyponymFilename hyponyms file: "id,id1,id2,..."
     */
    public WordNet(String synsetFilename, String hyponymFilename) {
        try (java.util.Scanner scanner = new java.util.Scanner(new java.io.File(synsetFilename))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                int firstComma = line.indexOf(',');
                int secondComma = line.indexOf(',', firstComma + 1);
                if (firstComma == -1 || secondComma == -1) {
                    continue;
                }
                int id = Integer.parseInt(line.substring(0, firstComma));
                String[] words = line.substring(firstComma + 1, secondComma).split(" ");
                List<String> wordList = new ArrayList<>();
                for (String word : words) {
                    if (!word.isEmpty()) {
                        wordList.add(word);
                    }
                }
                synsets.put(id, wordList);
                for (String word : wordList) {
                    wordToSynsetIds.computeIfAbsent(word, k -> new HashSet<>()).add(id);
                }
            }
        } catch (java.io.FileNotFoundException e) {
            throw new RuntimeException("Could not find synsets file: " + synsetFilename, e);
        }

        try (java.util.Scanner scanner = new java.util.Scanner(new java.io.File(hyponymFilename))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0]);
                // A synset id may appear on multiple lines; accumulate its
                // hyponyms rather than overwriting them.
                List<Integer> childIds = hyponyms.computeIfAbsent(id, k -> new ArrayList<>());
                for (int i = 1; i < parts.length; i++) {
                    childIds.add(Integer.parseInt(parts[i]));
                }
            }
        } catch (java.io.FileNotFoundException e) {
            throw new RuntimeException("Could not find hyponyms file: " + hyponymFilename, e);
        }
    }

    /** Returns the ids of all synsets that contain the given word. */
    public Set<Integer> synsetIdsOf(String word) {
        return wordToSynsetIds.getOrDefault(word, Set.of());
    }

    /** Returns the set of all words that are hyponyms of the given word
     *  (including the word itself if it appears in the reachable synsets). */
    public Set<String> hyponymsOf(String word) {
        Set<String> result = new TreeSet<>();
        for (int id : synsetIdsOf(word)) {
            collectReachableWords(id, result);
        }
        return result;
    }

    /** Returns the set of all words that are hyponyms of every word in
     *  words: the intersection of the per-word hyponym word sets. */
    public Set<String> hyponymsOf(List<String> words) {
        if (words.isEmpty()) {
            return new TreeSet<>();
        }
        Set<String> common = new TreeSet<>(hyponymsOf(words.get(0)));
        for (int i = 1; i < words.size(); i++) {
            common.retainAll(hyponymsOf(words.get(i)));
        }
        return common;
    }

    /** Adds the words of synset id and all of its hyponym descendants to
     *  result. */
    private void collectReachableWords(int id, Set<String> result) {
        for (String word : synsets.getOrDefault(id, List.of())) {
            result.add(word);
        }
        for (int child : hyponyms.getOrDefault(id, List.of())) {
            collectReachableWords(child, result);
        }
    }
}
