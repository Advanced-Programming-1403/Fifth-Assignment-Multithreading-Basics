import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TypingTest {
    static int correct = 0;
    static int incorrect = 0;
    private static String lastInput = "";
    private static Scanner scanner = new Scanner(System.in);
    private static List<Long> reactionTimes = new ArrayList<>();
    private static long totalTime = 0;

    public static class InputRunnable implements Runnable {
        private long startTime;

        public InputRunnable(long startTime) {
            this.startTime = startTime;
        }

        @Override
        public void run() {
            try {
                lastInput = scanner.nextLine();
                long endTime = System.currentTimeMillis();
                long reactionTime = endTime - startTime;
                reactionTimes.add(reactionTime);
                totalTime += reactionTime;
            } catch (IllegalStateException | IndexOutOfBoundsException e) {
                scanner = new Scanner(System.in);
                lastInput = scanner.nextLine();
                long endTime = System.currentTimeMillis();
                long reactionTime = endTime - startTime;
                reactionTimes.add(reactionTime);
                totalTime += reactionTime;
            }
        }
    }

    public static void testWord(String wordToTest) {
        try {
            System.out.println(wordToTest);
            long startTime = System.currentTimeMillis();
            Thread inputThread = new Thread(new InputRunnable(startTime));
            inputThread.start();
            int timeout = wordToTest.length() * 5000;
            inputThread.join(timeout);
            System.out.println();
            System.out.println("You typed: " + lastInput);
            if(lastInput == null){
                System.out.println("Incorrect");
                incorrect++;
            }
            if (lastInput.equals(wordToTest)) {
                System.out.println("Correct");
                correct++;
            } else {
                System.out.println("Incorrect");
                incorrect++;
            }

            double lastReactionTimeInSeconds = reactionTimes.get(reactionTimes.size() - 1) / 1000.0;
            System.out.println("Time: " + lastReactionTimeInSeconds + " s");

        } catch (Exception e) {
            scanner.nextLine();
            e.printStackTrace();
        }
    }

    public static void typingTest(List<String> inputList) throws InterruptedException {
        correct = 0;
        incorrect = 0;
        reactionTimes.clear();
        totalTime = 0;
        System.out.println("Enter the number of words : (0 - 100) ");
        int number = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < number ; i++) {
            String wordToTest = inputList.get(i);
            testWord(wordToTest);
            Thread.sleep(2000);
        }

        System.out.println();
        System.out.println("Correct: " + correct);
        System.out.println("Incorrect: " + incorrect);

        double totalTimeInSeconds = totalTime / 1000.0;
        System.out.println("Total time: " + totalTimeInSeconds + " s");

        if (correct > 0) {
            double averageTimeInSeconds = (totalTime / 1000.0) / correct;
            System.out.println("Average time per correct word: " + averageTimeInSeconds + " s");
        }

        System.out.println("\nReaction times for each word:");
        for (int i = 0; i < reactionTimes.size(); i++) {
            double timeInSeconds = reactionTimes.get(i) / 1000.0;
            System.out.println(inputList.get(i) + ": " + timeInSeconds + " s");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        try {
            List<String> words = Files.readAllLines(Paths.get("D:\\AP\\Fifth-Assignment-Multithreading-Basics\\src\\main\\resources\\Words.txt"));
            typingTest(words);
        } catch (IOException e) {
            System.out.println("file not found");
            throw new RuntimeException(e);
        }
        System.out.println("\nPress any key to exit.");
        scanner.nextLine();
    }
}