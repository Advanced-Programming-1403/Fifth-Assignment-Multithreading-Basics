import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TypingTest {

    private static String lastInput = "";
    private static Scanner scanner = new Scanner(System.in);

    public static class InputRunnable implements Runnable {
        @Override
        public void run() {
            if (scanner.hasNextLine()) {
                lastInput = scanner.nextLine();
            }
        }
    }

    public static void testWord(String wordToTest) {
        try {
            System.out.println(wordToTest);
            lastInput = "";

            Thread inputThread = new Thread(new InputRunnable());
            inputThread.start();

            int waitTime = 5000; // 5 seconds to type
            inputThread.join(waitTime); // Wait, but only up to 5 seconds

            System.out.println();
            System.out.println("You typed: " + lastInput);
            if (lastInput.equals(wordToTest)) {
                System.out.println("Correct");
            } else {
                System.out.println("Incorrect");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void typingTest(List<String> inputList) throws InterruptedException {
        int correctCount = 0;

        for (String wordToTest : inputList) {
            testWord(wordToTest);
            if (wordToTest.equals(lastInput)) {
                correctCount++;
            }
            Thread.sleep(2000); // Pause briefly before showing the next word
        }

        // Summary
        System.out.println("\nTest Summary:");
        System.out.println("Total words: " + inputList.size());
        System.out.println("Correct entries: " + correctCount);
        System.out.println("Accuracy: " + (100.0 * correctCount / inputList.size()) + "%");
    }

    public static void main(String[] args) throws InterruptedException {
        List<String> words = new ArrayList<>();

        try (Scanner fileScanner = new Scanner(new java.io.File("src/main/resources/Words.txt"))) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                if (!line.isEmpty()) {
                    words.add(line);
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading words from file: " + e.getMessage());
        }

        typingTest(words);

        System.out.println("Press enter to exit.");
    }
}
