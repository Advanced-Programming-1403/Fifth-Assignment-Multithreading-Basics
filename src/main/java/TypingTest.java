import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class TypingTest {

    private static String lastInput = "";
    private static Scanner scanner = new Scanner(System.in);
    private static int correct = 0;
    private static int incorrect = 0;
    private static long totalTime = 0;
    //private  static int count = 10;
    public static class InputRunnable implements Runnable {

        //TODO: Implement a thread to get user input without blocking the main thread
        @Override
        public void run() {
            try {
                lastInput = scanner.nextLine().trim();//Trim: ignore spaces
            }
            catch (Exception e)
            {
                lastInput = "";
            }

        }
    }

    public static List<String> readWordsFromFile(String filename) {
        List<String> words = new ArrayList<>();
        try (InputStream is = TypingTest.class.getClassLoader().getResourceAsStream("Words.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    words.add(line.trim());
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return words;
    }

    public static List<String> getRandomWords(List<String> wordList, int count) {
        Collections.shuffle(wordList);
        return wordList.subList(0, Math.min(count, wordList.size()));
    }

    public static long getTimeoutForWord(String word) {
        return word.length() * 2000L;
    }
    public static void testWord(String wordToTest) {
        try {
            System.out.println("Type this word : "+wordToTest);
            lastInput = "";

            // TODO

            long timeout = getTimeoutForWord(wordToTest);
            long start = System.currentTimeMillis();

            Thread inputThread = new Thread(new InputRunnable());
            inputThread.start();
            inputThread.join(timeout);

            long End = System.currentTimeMillis();
            long duration = End - start;
            totalTime += duration;


            System.out.println();
            System.out.println("You typed: " + lastInput);
            if (lastInput.equalsIgnoreCase(wordToTest)) {
                System.out.println("Correct");
                correct++;
            } else {
                System.out.println("Incorrect");
                incorrect++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void typingTest(List<String> inputList) throws InterruptedException {

        for (int i = 0; i < inputList.size(); i++) {
            String wordToTest = inputList.get(i);
            testWord(wordToTest);
            Thread.sleep(2000); // Pause briefly before showing the next word
        }

        System.out.println("Typing Test Summery");
        System.out.println("Correct Words : " + correct);
        System.out.println("Incorrect Words : " +incorrect);
        System.out.println("Total time : "+totalTime/1000.00);
        System.out.println("Average foe each word :"+totalTime/((correct+incorrect)*1000.00));
    }

    public static void main(String[] args) throws InterruptedException {
        List<String> allWords = readWordsFromFile("resources/Words.txt");
        if (allWords.isEmpty()) {
            System.out.println("No words found in file.");
            return;
        }

        List<String> selectedWords = getRandomWords(allWords, 10);
        typingTest(selectedWords);

        System.out.println("\nPress Enter to exit.");
        scanner.nextLine();
    }
}