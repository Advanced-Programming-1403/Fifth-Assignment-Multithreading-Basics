import java.io.IOException;

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
            try {
                BufferedReader reader = new BufferedReader(new FileReader(path));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    int productId = Integer.parseInt(parts[0].trim());
                    int amount = Integer.parseInt(parts[1].trim());
                    int discount = Integer.parseInt(parts[2].trim());

                    Product product = findProductById(productId);
                    if (product == null) continue;

                    double totalPrice = product.getPrice() * amount;
                    double discountedPrice = totalPrice - discount;

                    totalLines++;
                    totalAmount += amount;
                    totalCost += discountedPrice;
                    totalDiscountSum += discount;

                    if (discountedPrice > highestCostAfterDiscount) {
                        highestCostAfterDiscount = discountedPrice;
                        mostExpensiveProduct = product;
                    }
                }
                reader.close();
            } catch (IOException e) {
                System.err.println("Error reading file " + path + ": " + e.getMessage());
            }
        }

        // Helper method to find product by ID
        private Product findProductById(int id) {
            for (Product product : productCatalog) {
                if (product != null && product.getProductID() == id) {
                    return product;
                }
            }
            return null;
        }


        public void makeReport() {
            System.out.println("Report for file: " + path);
            System.out.println("Total items bought: " + totalAmount);
            System.out.printf("Total cost after discount: %.2f\n", totalCost);
            if (totalLines > 0) {
                double avgDiscount = (double) totalDiscountSum / totalLines;
                System.out.printf("Average discount: %.2f\n", avgDiscount);
            } else {
                System.out.println("Average discount: N/A");
            }

            if (mostExpensiveProduct != null) {
                System.out.println("Most expensive purchase (after discount):");
                System.out.println(" - Product name: " + mostExpensiveProduct.getProductName());
                System.out.printf(" - Cost: %.2f\n", highestCostAfterDiscount);
            } else {
                System.out.println("No valid purchases found.");
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
            // TODO: Define the paths to the order detail text files in the resources folder
    };

    static Product[] productCatalog = new Product[10];

    public static void loadProducts() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/Products.txt"));
        String line;
        int index = 0;
        while ((line = reader.readLine()) != null && index < productCatalog.length) {
            String[] parts = line.split(",");
            int id = Integer.parseInt(parts[0].trim());
            String name = parts[1].trim();
            double price = Double.parseDouble(parts[2].trim());
            productCatalog[index++] = new Product(id, name, price);
        }
        reader.close();
    }


    public static void main(String[] args) throws InterruptedException {
        try {
            loadProducts();
        } catch (IOException e) {
            System.err.println("Failed to load products: " + e.getMessage());
            return;
        }

        String[] orderFiles = {
                "src/main/resources/2021_order_details.txt",
                "src/main/resources/2022_order_details.txt",
                "src/main/resources/2023_order_details.txt",
                "src/main/resources/2024_order_details.txt"
        };

        TaskRunnable[] tasks = new TaskRunnable[orderFiles.length];
        Thread[] threads = new Thread[orderFiles.length];

        for (int i = 0; i < orderFiles.length; i++) {
            tasks[i] = new TaskRunnable(orderFiles[i]);
            threads[i] = new Thread(tasks[i]);
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join(); // Wait for all threads to finish
        }

        // After all threads are done, generate reports
        for (TaskRunnable task : tasks) {
            task.makeReport();
        }
    }

}