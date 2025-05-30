import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReportGenerator {
    static class TaskRunnable implements Runnable {
        private final String path;
        private double totalCost;
        private int totalAmount;
        private int totalDiscountSum;
        private int totalLines;
        private Product mostExpensiveProduct;
        private double highestCostAfterDiscount;

        public TaskRunnable(String path) {
            this.path = path;
            this.totalCost = 0;
            this.totalAmount = 0;
            this.totalDiscountSum = 0;
            this.totalLines = 0;
            this.highestCostAfterDiscount = 0;
            this.mostExpensiveProduct = null;
        }

        @Override
        public void run() {
            try (BufferedReader br = new BufferedReader(new FileReader(path))) {
                String line;
                while((line = br.readLine()) != null) {
                    String[] parts = line.split(",", 3);
                    if (parts.length != 3) continue;

                    int productId = Integer.parseInt(parts[0].trim());
                    int amount = Integer.parseInt(parts[1].trim());
                    double discount = Double.parseDouble(parts[2].trim());

                    Product product = productCatalog[productId - 1];
                    if (product == null) continue;

                    double price = product.getPrice();
                    double discountedPrice = price - discount;
                    double totalLineCost = amount * discountedPrice;

                    totalCost += totalLineCost;
                    totalAmount += amount;
                    totalDiscountSum += amount * discount;
                    totalLines++;

                    // Check for most expensive purchase
                    if (totalLineCost > highestCostAfterDiscount) {
                        highestCostAfterDiscount = totalLineCost;
                        mostExpensiveProduct = product;
                    }
                }
            } catch (IOException e) {
                System.err.println("Error processing file " + path + ": " + e.getMessage());
            }
        }

        public void makeReport() {
            System.out.println("Report for file: " + path);

            System.out.printf("Total items bought: %d%n", totalAmount);
            System.out.printf("Total cost: %.2f%n", totalCost);

            // Avoid division by zero
            double averageDiscount = totalLines > 0 ? totalDiscountSum / totalLines : 0.0;
            System.out.printf("Average discount: %.2f%n", averageDiscount);

            if (mostExpensiveProduct != null) {
                System.out.printf("Most expensive purchase after discount: %s (%.2f)%n",
                        mostExpensiveProduct.getProductName(), highestCostAfterDiscount);
            } else {
                System.out.println("No purchases were made in this file.");
            }

            System.out.println("--------------------------------------------------");
        }
    }

    static class Product {
        private int productID;
        private String productName;
        private double price;

        public Product(int productID, String productName, double price) {
            this.productID = productID;
            this.productName = productName;
            this.price = price;
        }

        public int getProductID() {
            return productID;
        }

        public String getProductName() {
            return productName;
        }

        public double getPrice() {
            return price;
        }
        @Override
        public String toString() {
            return productID + "," + productName + "," + price;
        }

    }

    private static final String[] ORDER_FILES = {
            "src\\main\\resources\\2021_order_details.txt",
            "src\\main\\resources\\2022_order_details.txt",
            "src\\main\\resources\\2023_order_details.txt",
            "src\\main\\resources\\2024_order_details.txt"
    };

    static Product[] productCatalog = new Product[10];

    public static void loadProducts() throws IOException {
        String fileName = "src\\main\\resources\\Products.txt";

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            int nextIndex = 0;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 3); // ID, Name, Price
                if (parts.length == 3) {
                    int id = Integer.parseInt(parts[0].trim());
                    String name = parts[1].trim();
                    double price = Double.parseDouble(parts[2].trim());

                    Product product = new Product(id, name, price);
                    productCatalog[nextIndex++] = product;
                }
            }

        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // Load the product catalog first
        try {
            loadProducts();
        } catch (IOException e) {
            System.err.println("Error loading products: " + e.getMessage());
            return;
        }

        // Create list to keep track of TaskRunnable and Thread instances
        List<TaskRunnable> tasks = new ArrayList<>();
        List<Thread> threads = new ArrayList<>();

        // For each order file, create a TaskRunnable and a Thread
        for (String filePath : ORDER_FILES) {
            TaskRunnable task = new TaskRunnable(filePath); // Pass file path to TaskRunnable
            Thread thread = new Thread(task);
            tasks.add(task);
            threads.add(thread);
            thread.start(); // Start the thread
        }

        // Wait for all threads to finish
        for (Thread thread : threads) {
            thread.join(); // Wait for thread to finish
        }

        // After all threads are done, generate a report from each task
        for (TaskRunnable task : tasks) {
            task.makeReport();
        }
    }
}