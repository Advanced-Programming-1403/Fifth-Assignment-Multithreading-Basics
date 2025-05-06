import java.io.*;
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
        public void run()
        {
            try (InputStream is = ReportGenerator.class.getClassLoader().getResourceAsStream(path);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

                if (is == null) {
                    System.err.println("File not found: " + path);
                    return;
                }

                String line;
                while ((line = reader.readLine()) != null) {
                    totalLines++;
                    String[] parts = line.split(",");
                    int productId = Integer.parseInt(parts[0]);
                    int amount = Integer.parseInt(parts[1]);
                    int discount = Integer.parseInt(parts[2]);

                    Product product = findProductById(productId);
                    if (product == null) continue;

                    double originalCost = product.getPrice() * amount;
                    double discountedCost = originalCost * (1 - discount / 100.0);

                    totalAmount += amount;
                    totalCost += discountedCost;
                    totalDiscountSum += discount;

                    if (discountedCost > highestCostAfterDiscount) {
                        highestCostAfterDiscount = discountedCost;
                        mostExpensiveProduct = product;
                    }
                }
            } catch (IOException e) {
                System.err.println("Error reading " + path + ": " + e.getMessage());
            }
        }

        private Product findProductById(int id) {
            for (Product p : productCatalog) {
                if (p != null && p.getProductID() == id)
                    return p;
            }
            return null;
        }

        public void makeReport() {
            System.out.println("Report for file: " + path);
            System.out.println("Total cost: $" + totalCost);
            System.out.println("Total items bought: " + totalAmount);
            System.out.println("Average discount: " + (totalLines == 0 ? 0 : (double) totalDiscountSum / totalLines) + "%");
            if (mostExpensiveProduct != null) {
                System.out.println("Most expensive purchase after discount: " + mostExpensiveProduct.getProductName() +
                        " ($" + highestCostAfterDiscount + ")");
            }
            System.out.println("----------------------------------------------------");
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
    }


    private static final String[] ORDER_FILES = {
            "2021_order_details.txt",
            "2022_order_details.txt",
            "2023_order_details.txt",
            "2024_order_details.txt"
    };

    static List<Product> productCatalog = new ArrayList<>();

    public static void loadProducts() {
        try (InputStream is = ReportGenerator.class.getClassLoader().getResourceAsStream("Products.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0]);
                String name = parts[1];
                double price = Double.parseDouble(parts[2]);

                productCatalog.add(new Product(id, name, price));
            }
        } catch (IOException e) {
            System.err.println("Error reading Products.txt: " + e.getMessage());
        }
    }



    public static void main(String[] args) throws InterruptedException, IOException {

        loadProducts();

        List<Thread> threads = new ArrayList<>();
        List<TaskRunnable> tasks = new ArrayList<>();


        for (String file : ORDER_FILES) {
            TaskRunnable task = new TaskRunnable(file);
            Thread t = new Thread(task);
            threads.add(t);
            tasks.add(task);
            t.start();
        }


        for (Thread t : threads) {
            t.join();
        }


        for (TaskRunnable task : tasks) {
            task.makeReport();
        }
    }
}
