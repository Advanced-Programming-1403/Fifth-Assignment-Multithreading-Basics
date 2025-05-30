import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class TypingTest {

    private static String lastInput = "";
    private static Scanner scanner = new Scanner(System.in);
    private static int totalCorrectWords = 0;
    private static int totalIncorrectWords = 0;

    public static class InputRunnable implements Runnable {

        @Override
        public void run() {
            while (true) {
                lastInput = scanner.nextLine();
            }
        }
    }

    public static void testWord(String wordToTest) {
        try {
            System.out.println(wordToTest);
            lastInput = "";

            long timeout = wordToTest.length() * 500L; // 500 ms per character
            long startTime = System.currentTimeMillis();

            while (System.currentTimeMillis() - startTime < timeout) {
                if (!lastInput.isEmpty()) {
                    break; // User typed something
                }
                try {
                    Thread.sleep(50); // Small delay to reduce CPU usage
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("You typed: " + lastInput);
            if (lastInput.equals(wordToTest)) {
                System.out.println("Correct");
                totalCorrectWords++;
            } else {
                System.out.println("Incorrect");
                totalIncorrectWords++;
            }
            System.out.println();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void typingTest(List<String> inputList) throws InterruptedException {

        long totalStart = System.currentTimeMillis();

        for (int i = 0; i < inputList.size(); i++) {
            String wordToTest = inputList.get(i);
            testWord(wordToTest);
            Thread.sleep(200); // Pause briefly before showing the next word
        }

        long totalEnd = System.currentTimeMillis();
        long totalTimeMillis = totalEnd - totalStart;
        double totalTimeSeconds = totalTimeMillis / 1000.0;

        double averageTimePerWord = totalTimeMillis / (double) inputList.size();

        System.out.println("Total correct words: " + totalCorrectWords);
        System.out.println("Total incorrect words: " + totalIncorrectWords);
        System.out.println("Total time taken: " + totalTimeSeconds + " seconds");
        System.out.println("Average time per word: " + averageTimePerWord + " ms");
    }

    public static List<String> readWordsFromFile() {
        List<String> words = new ArrayList<>();
        String fileName = "C:\\Users\\Notebook\\Ap-course\\Fifth-Assignment-Multithreading-Basics\\src\\main\\resources\\Words.txt";

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    words.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return words;
    }

    public static void main(String[] args) throws InterruptedException {
        List<String> words = new ArrayList<>();

        // read the words from the Word.txt file
        words = readWordsFromFile();

        // Shuffle the list randomly
        Collections.shuffle(words);

        InputRunnable inputRunnable = new InputRunnable();
        Thread inputThread = new Thread(inputRunnable);
        inputThread.setDaemon(true);
        inputThread.start();

        typingTest(words);
    }
}