import java.util.*;
import java.util.stream.Collectors;

public class Main {
    
    static String[] RAW_COMMENTS = {
        "Java est génial! J'adore Java...",
        "java, JAVA, JaVa — trop de versions ?",
        "Les listes en Java sont puissantes; les Sets aussi.",
        "Map<Map, Map> ? Non merci ; mais les Map simples oui.",
        "java & python: amour/haine, mais Java reste top."
    };
    
    static String STOPWORDS = "est,les,la,le,de,des,en,et,mais,oui,non,trop";
    

    static String normalize(String s) {

        String normalized = s.toLowerCase();
        

        normalized = normalized.replaceAll("[^a-zàâçéèêëîïôûùüÿñæœ0-9]", " ");
        

        normalized = normalized.replaceAll(" +", " ");
        

        normalized = normalized.strip();
        
        return normalized;
    }
    

    static List<String> tokens(String s) {

        String normalized = normalize(s);
        return Arrays.asList(normalized.split(" "));
    }
    
    
    static String reconstructSentence(List<String> toks) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < toks.size(); i++) {
            sb.append(toks.get(i));
            if (i < toks.size() - 1) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }
    

    static Set<String> createStopset() {
        String[] stopArray = STOPWORDS.split(",");
        Set<String> stopset = new HashSet<>(Arrays.asList(stopArray));
        return stopset;
    }
    

    static List<String> filterStopwords(List<String> toks, Set<String> stopset) {
        List<String> filtered = new ArrayList<>();
        for (String tok : toks) {
            // Vérifier si le token n'est pas dans les stopwords
            if (!stopset.contains(tok)) {
                filtered.add(tok);
            }
        }
        return filtered;
    }
    
    /**
     * Q4) Démontrer les différents types de Set
     */
    static void demonstrateSetTypes() {
        System.out.println("\n--- Q4 : Types de Set (HashSet, TreeSet, LinkedHashSet) ---");
        
        // Collecter tous les tokens de tous les commentaires
        List<String> allTokens = new ArrayList<>();
        Set<String> stopset = createStopset();
        
        for (String comment : RAW_COMMENTS) {
            List<String> toks = tokens(comment);
            List<String> filtered = filterStopwords(toks, stopset);
            allTokens.addAll(filtered);
        }
        
        // HashSet : ordre non garanti, recherche O(1)
        // Utilité : stockage efficace, pas d'ordre d'apparition important
        Set<String> hashSet = new HashSet<>(allTokens);
        System.out.println("HashSet (ordre aléatoire) : " + hashSet);
        
        // TreeSet : ordonné alphabétiquement, recherche O(log n)
        // Utilité : si on veut les mots triés alphabétiquement
        Set<String> treeSet = new TreeSet<>(allTokens);
        System.out.println("TreeSet (ordre alphabétique) : " + treeSet);
        
        // LinkedHashSet : ordre d'insertion préservé, recherche O(1)
        // Utilité : garder l'ordre d'apparition dans le texte original
        Set<String> linkedHashSet = new LinkedHashSet<>(allTokens);
        System.out.println("LinkedHashSet (ordre d'apparition) : " + linkedHashSet);
    }
    

    static Map<String, Integer> calculateFrequencies() {
        Map<String, Integer> freq = new HashMap<>();
        Set<String> stopset = createStopset();
        
        for (String comment : RAW_COMMENTS) {
            List<String> toks = tokens(comment);
            List<String> filtered = filterStopwords(toks, stopset);
            
            for (String tok : filtered) {
                freq.put(tok, freq.getOrDefault(tok, 0) + 1);
            }
        }
        
        return freq;
    }
    

    static void displayTopKWords(int k) {
        System.out.println("\n--- Q5 : Top " + k + " mots les plus fréquents ---");
        
        Map<String, Integer> freq = calculateFrequencies();
        
        List<Map.Entry<String, Integer>> entryList = new ArrayList<>(freq.entrySet());
        
        entryList.sort((e1, e2) -> {
            int freqCompare = e2.getValue().compareTo(e1.getValue());
            return freqCompare != 0 ? freqCompare : e1.getKey().compareTo(e2.getKey());
        });
        
        // Afficher les K premiers
        int count = 0;
        for (Map.Entry<String, Integer> entry : entryList) {
            if (count >= k) break;
            System.out.println(entry.getKey() + " : " + entry.getValue() + " fois");
            count++;
        }
    }
    

    static Map<String, Set<Integer>> buildInverseIndex() {
        Map<String, Set<Integer>> inverseIndex = new HashMap<>();
        Set<String> stopset = createStopset();
        
        // Parcourir chaque commentaire
        for (int commentIdx = 0; commentIdx < RAW_COMMENTS.length; commentIdx++) {
            String comment = RAW_COMMENTS[commentIdx];
            List<String> toks = tokens(comment);
            List<String> filtered = filterStopwords(toks, stopset);
            
            // Pour chaque mot du commentaire
            for (String tok : filtered) {
                inverseIndex.computeIfAbsent(tok, k -> new TreeSet<>()).add(commentIdx);
            }
        }
        
        return inverseIndex;
    }
    

    static void displayInverseIndex() {
        System.out.println("\n--- Q6 : Index Inverse (mots → indices des commentaires) ---");
        
        Map<String, Set<Integer>> inverseIndex = buildInverseIndex();
        
        List<String> selectedWords = inverseIndex.keySet().stream()
            .limit(5)
            .collect(Collectors.toList());
        
        for (String word : selectedWords) {
            System.out.println(word + " aparaît dans commentaires : " + inverseIndex.get(word));
        }
    }
    
    
    public static void main(String[] args) {
        System.out.println("═══ CleanText Analytics ═══\n");
        
        System.out.println("--- Q1 & Q2 : Normalisation et Tokenization ---");
        for (String comment : RAW_COMMENTS) {
            System.out.println("Brut : " + comment);
            System.out.println("Normalisé : " + normalize(comment));
            System.out.println("Tokens : " + tokens(comment));
            System.out.println();
        }
        
        System.out.println("Reconstruction (StringBuilder) du 1er commentaire :");
        String reconstructed = reconstructSentence(tokens(RAW_COMMENTS[0]));
        System.out.println(reconstructed + "\n");
        
        
        System.out.println("--- Q3 : Stopwords ---");
        Set<String> stopset = createStopset();
        System.out.println("Stopwords set : " + stopset);
        List<String> filtered = filterStopwords(tokens(RAW_COMMENTS[0]), stopset);
        System.out.println("Après filtrage du 1er commentaire : " + filtered + "\n");
        
        demonstrateSetTypes();
        
        displayTopKWords(10);
        
        displayInverseIndex();
        
        System.out.println("\n═══ Fin du programme ═══");
    }
}