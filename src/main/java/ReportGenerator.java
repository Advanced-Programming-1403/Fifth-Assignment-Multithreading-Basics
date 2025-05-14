import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ReportGenerator {

    static class TaskRunnable implements Runnable {
        private final String fileName;
        private double totalCost;
        private int totalAmount;
        private int totalDiscountSum;
        private int totalLines;
        private Product mostExpensiveProduct;
        private double highestCostAfterDiscount;

        public TaskRunnable(String fileName) {
            this.fileName = fileName;
            this.totalCost = 0;
            this.totalAmount = 0;
            this.totalDiscountSum = 0;
            this.totalLines = 0;
            this.highestCostAfterDiscount = 0;
            this.mostExpensiveProduct = null;
        }

        @Override
        public void run() {
            try (
                    InputStream is = ReportGenerator.class.getClassLoader().getResourceAsStream(fileName);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is))
            ) {
                String line;
                while ((line = reader.readLine()) != null) {
                    totalLines++;
                    String[] parts = line.split(",");
                    int productId = Integer.parseInt(parts[0]);
                    int amount = Integer.parseInt(parts[1]);
                    int discount = Integer.parseInt(parts[2]);

                    Product product = findProductById(productId);
                    if (product == null) continue;

                    double cost = product.getPrice() * amount;
                    double discountedCost = cost - discount;

                    totalAmount += amount;
                    totalCost += discountedCost;
                    totalDiscountSum += discount;

                    if (discountedCost > highestCostAfterDiscount) {
                        highestCostAfterDiscount = discountedCost;
                        mostExpensiveProduct = product;
                    }
                }
            } catch (Exception e) {
                System.out.println("خطا در پردازش فایل: " + fileName);
                e.printStackTrace();
            }
        }

        private Product findProductById(int id) {
            for (Product p : productCatalog) {
                if (p != null && p.getProductID() == id) {
                    return p;
                }
            }
            return null;
        }

        public void makeReport() {
            System.out.println("File reports: " + fileName);
            System.out.printf("Total cost: %.2f\n", totalCost);
            System.out.println("Total number of purchased items: " + totalAmount);
            double avgDiscount = totalLines > 0 ? (double) totalDiscountSum / totalLines : 0;
            System.out.printf("Average discount: %.2f\n", avgDiscount);
            if (mostExpensiveProduct != null) {
                System.out.println("Most expensive purchase after discount: " +
                        mostExpensiveProduct.getProductName() + " (Cost: " +
                        String.format("%.2f", highestCostAfterDiscount) + ")");
            }
            System.out.println("--------------");
        }
    }

    static class Product {
        private final int productID;
        private final String productName;
        private final double price;

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
    }


    private static final String[] ORDER_FILES = {
            "2021_order_details.txt",
            "2022_order_details.txt",
            "2023_order_details.txt",
            "2024_order_details.txt"
    };

    static Product[] productCatalog = new Product[10];

    public static void loadProducts() throws IOException {
        InputStream inputStream = ReportGenerator.class.getClassLoader().getResourceAsStream("Products.txt");
        if (inputStream == null) {
            throw new IOException("Products.txt not found in resources folder");
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        int index = 0;
        while ((line = reader.readLine()) != null && index < productCatalog.length) {
            String[] parts = line.split(",");
            int id = Integer.parseInt(parts[0]);
            String name = parts[1];
            double price = Double.parseDouble(parts[2]);
            productCatalog[index++] = new Product(id, name, price);
        }
        reader.close();
    }

    public static void main(String[] args) throws InterruptedException {
        try {
            loadProducts();
        } catch (IOException e) {
            return;
        }

        List<TaskRunnable> tasks = new ArrayList<>();
        List<Thread> threads = new ArrayList<>();

        for (String fileName : ORDER_FILES) {
            TaskRunnable task = new TaskRunnable(fileName);
            Thread thread = new Thread(task);
            tasks.add(task);
            threads.add(thread);
            thread.start();
        }

        for (Thread t : threads) {
            t.join();
        }

        for (TaskRunnable task : tasks) {
            task.makeReport();
        }
    }
}