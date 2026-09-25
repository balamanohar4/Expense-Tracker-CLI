import java.util.ArrayList;
import java.util.Scanner;
import java.util.InputMismatchException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.time.format.DateTimeParseException;
import java.io.EOFException;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {

    private static ArrayList<Expense> expenses = new ArrayList<>();
    private static double monthlyBudget = 0;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        loadExpenses();

        System.out.println("=================================");
        System.out.println("       EXPENSE TRACKER CLI");
        System.out.println("=================================");
        System.out.println("Welcome to your Expense Tracker!");

        while (true) {

            System.out.println();
            System.out.println("1. Add Expense");
            System.out.println("2. View All Expenses");
            System.out.println("3. Delete Expense");
            System.out.println("4. Search Expenses");
            System.out.println("5. Monthly Report");
            System.out.println("6. Statistics");
            System.out.println("7. Export to CSV");
            System.out.println("8. Sort Expenses");
            System.out.println("9. Monthly Budget");
            System.out.println("10. Category-wise Spending");
            System.out.println("11. Date-Range Report");
            System.out.println("12. Budget Status");
            System.out.println("13. Expense Dashboard");
            System.out.println("14. Exit");
            System.out.print("Enter your choice: ");
            int choice;

            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
                continue;
            }

            if (choice == 1) {
                addExpense();
            }

            if (choice == 2) {
                viewExpenses();
            }

            if (choice == 3) {
                deleteExpense();
            }

            if (choice == 4) {
                searchExpenses();
            }

            if (choice == 5) {
                monthlyReport();
            }

            if (choice == 6) {
                showStatistics();
            }

            if (choice == 7) {
                exportToCSV();
            }
            if (choice == 8) {
                sortExpenses();
            }

            if (choice == 9) {
                setMonthlyBudget();
            }

            if (choice == 10) {

                showCategoryWiseSpending();
            }

            if (choice == 11) {

                dateRangeReport();

            }

            if (choice == 12) {

                showBudgetStatus();

            }

            if (choice == 13) {

                showExpenseDashboard();

            }

            if (choice == 14) {
                saveExpenses();
                System.out.println("Thank you for using Expense Tracker!");
                break;
            }

        }

    }

    private static void addExpense() {

        System.out.println();
        System.out.println("===== ADD EXPENSE =====");

        System.out.print("Enter amount: ₹");

        double amount;

        try {
            amount = scanner.nextDouble();
        } catch (InputMismatchException e) {
            System.out.println("Invalid amount. Please enter a number.");
            scanner.nextLine();
            return;
        }

        scanner.nextLine();

        if (amount <= 0) {
            System.out.println("Amount must be greater than 0.");
            return;
        }

        System.out.print("Enter category: ");
        String category = scanner.nextLine();

        System.out.print("Enter description: ");
        String description = scanner.nextLine();

        System.out.print("Enter date (YYYY-MM-DD): ");
        String dateInput = scanner.nextLine();

        LocalDate date;

        try {
            date = LocalDate.parse(dateInput);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date. Please use YYYY-MM-DD.");
            return;
        }

        Expense expense = new Expense(amount, category, description, date);

        expenses.add(expense);
        saveExpenses();

        System.out.println();
        System.out.println("Expense added successfully!");
    }

    private static void viewExpenses() {

        System.out.println();
        System.out.println("===== ALL EXPENSES =====");

        if (expenses.isEmpty()) {
            System.out.println("No expenses found.");
            return;
        }

        for (Expense expense : expenses) {
            System.out.println(expense);
        }
    }

    private static void deleteExpense() {

        if (expenses.isEmpty()) {
            System.out.println("No expenses found.");
            return;
        }

        System.out.println("Your Expenses:");

        for (int i = 0; i < expenses.size(); i++) {
            Expense expense = expenses.get(i);

            System.out.println(
                    (i + 1) + ". ₹" + expense.getAmount()
                            + " | " + expense.getCategory()
                            + " | " + expense.getDescription()
                            + " | " + expense.getDate());
        }

        System.out.print("Enter expense number to delete: ");

        int expenseNumber;

        try {
            expenseNumber = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.nextLine();
            return;
        }

        if (expenseNumber < 1 || expenseNumber > expenses.size()) {
            System.out.println("Invalid expense number.");
            return;
        }

        expenses.remove(expenseNumber - 1);
        saveExpenses();

        System.out.println("Expense deleted successfully.");
    }

    private static void searchExpenses() {

        if (expenses.isEmpty()) {
            System.out.println("No expenses found.");
            return;
        }

        scanner.nextLine();

        System.out.print("Enter category or description to search: ");
        String keyword = scanner.nextLine().toLowerCase();

        boolean found = false;

        for (Expense expense : expenses) {

            if (expense.getCategory().toLowerCase().contains(keyword)
                    || expense.getDescription().toLowerCase().contains(keyword)) {

                System.out.println(
                        "₹" + expense.getAmount()
                                + " | " + expense.getCategory()
                                + " | " + expense.getDescription()
                                + " | " + expense.getDate());

                found = true;
            }
        }

        if (!found) {
            System.out.println("No matching expenses found.");
        }
    }

    private static void monthlyReport() {

        if (expenses.isEmpty()) {
            System.out.println("No expenses found.");
            return;
        }

        System.out.print("Enter year (example: 2026): ");
        int year = scanner.nextInt();

        System.out.print("Enter month (1-12): ");
        int month = scanner.nextInt();

        if (month < 1 || month > 12) {
            System.out.println("Invalid month.");
            return;
        }

        double total = 0;

        System.out.println();
        System.out.println("Monthly Report: " + year + "-" + String.format("%02d", month));

        for (Expense expense : expenses) {

            LocalDate date = expense.getDate();

            if (date.getYear() == year && date.getMonthValue() == month) {

                System.out.println(
                        "₹" + expense.getAmount()
                                + " | " + expense.getCategory()
                                + " | " + expense.getDescription()
                                + " | " + expense.getDate());

                total += expense.getAmount();
            }
        }

        System.out.println("-----------------------------");
        System.out.println("Total for month: ₹" + total);

        if (monthlyBudget > 0) {

            double remaining = monthlyBudget - total;

            System.out.println("Monthly budget: ₹" + monthlyBudget);

            if (remaining >= 0) {
                System.out.println("Remaining budget: ₹" + remaining);
            } else {
                System.out.println("WARNING: Budget exceeded by ₹" + Math.abs(remaining));
            }
        }
    }

    private static void showStatistics() {

        if (expenses.isEmpty()) {
            System.out.println("No expenses found.");
            return;
        }

        double total = 0;
        double highest = expenses.get(0).getAmount();
        double lowest = expenses.get(0).getAmount();

        HashMap<String, Double> categoryTotals = new HashMap<>();

        for (Expense expense : expenses) {

            double amount = expense.getAmount();
            total += amount;

            if (amount > highest) {
                highest = amount;
            }

            if (amount < lowest) {
                lowest = amount;
            }

            categoryTotals.put(
                    expense.getCategory(),
                    categoryTotals.getOrDefault(expense.getCategory(), 0.0) + amount);
        }

        double average = total / expenses.size();

        System.out.println();
        System.out.println("===== EXPENSE STATISTICS =====");
        System.out.println("Total expenses: " + expenses.size());
        System.out.println("Total amount: ₹" + total);
        System.out.println("Average expense: ₹" + average);
        System.out.println("Highest expense: ₹" + highest);
        System.out.println("Lowest expense: ₹" + lowest);

        System.out.println();
        System.out.println("Category Totals:");

        for (String category : categoryTotals.keySet()) {
            System.out.println(
                    category + ": ₹" + categoryTotals.get(category));
        }
    }

    private static void exportToCSV() {

        if (expenses.isEmpty()) {
            System.out.println("No expenses to export.");
            return;
        }

        try (FileWriter writer = new FileWriter("expenses.csv")) {

            writer.write("Amount,Category,Description,Date\n");

            for (Expense expense : expenses) {

                writer.write(
                        expense.getAmount() + ","
                                + expense.getCategory() + ","
                                + expense.getDescription() + ","
                                + expense.getDate() + "\n");
            }

            System.out.println("Expenses exported successfully to expenses.csv.");

        } catch (IOException e) {

            System.out.println("Error exporting expenses: " + e.getMessage());
        }
    }

    private static void sortExpenses() {

        if (expenses.isEmpty()) {
            System.out.println("No expenses found.");
            return;
        }

        System.out.println();
        System.out.println("===== SORT EXPENSES =====");
        System.out.println("1. Sort by Date");
        System.out.println("2. Sort by Amount (Low to High)");
        System.out.println("3. Sort by Amount (High to Low)");

        System.out.print("Enter your choice: ");
        int sortChoice = scanner.nextInt();

        if (sortChoice == 1) {

            expenses.sort(
                    Comparator.comparing(Expense::getDate));

            System.out.println("Expenses sorted by date.");

        } else if (sortChoice == 2) {

            expenses.sort(
                    Comparator.comparingDouble(Expense::getAmount));

            System.out.println("Expenses sorted by amount (low to high).");

        } else if (sortChoice == 3) {

            expenses.sort(
                    Comparator.comparingDouble(Expense::getAmount).reversed());

            System.out.println("Expenses sorted by amount (high to low).");

        } else {

            System.out.println("Invalid choice.");
            return;
        }

        System.out.println();
        System.out.println("===== SORTED EXPENSES =====");

        for (Expense expense : expenses) {
            System.out.println(expense);
        }
    }

    private static void saveExpenses() {

        try (ObjectOutputStream output = new ObjectOutputStream(
                new FileOutputStream("expenses.dat"))) {

            output.writeObject(expenses);
            output.writeDouble(monthlyBudget);

        } catch (IOException e) {

            System.out.println("Error saving expenses: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private static void loadExpenses() {

        try (ObjectInputStream input = new ObjectInputStream(
                new FileInputStream("expenses.dat"))) {

            expenses = (ArrayList<Expense>) input.readObject();

            try {
                monthlyBudget = input.readDouble();
            } catch (EOFException e) {
                monthlyBudget = 0;
            }

            System.out.println("Previous expenses loaded successfully.");

        } catch (IOException | ClassNotFoundException e) {

            // No saved data yet. This is normal for the first run.
        }
    }

    private static void setMonthlyBudget() {

        System.out.println();
        System.out.println("===== SET MONTHLY BUDGET =====");

        System.out.print("Enter monthly budget: ₹");
        double budget = scanner.nextDouble();

        if (budget <= 0) {
            System.out.println("Budget must be greater than 0.");
            return;
        }

        monthlyBudget = budget;
        saveExpenses();

        System.out.println("Monthly budget set to ₹" + monthlyBudget);
    }

    private static void showCategoryWiseSpending() {

        if (expenses.isEmpty()) {
            System.out.println("No expenses available.");
            return;
        }

        Map<String, Double> categoryTotals = expenses.stream()
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.summingDouble(Expense::getAmount)));

        System.out.println("\n===== CATEGORY-WISE SPENDING =====");

        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {

            System.out.printf(
                    "%-20s : ₹%.2f%n",
                    entry.getKey(),
                    entry.getValue());
        }

        System.out.println("==================================");
    }

    private static void dateRangeReport() {

        scanner.nextLine();

        System.out.print("Enter start date (YYYY-MM-DD): ");
        String startDateInput = scanner.nextLine();

        System.out.print("Enter end date (YYYY-MM-DD): ");
        String endDateInput = scanner.nextLine();

        LocalDate startDate;
        LocalDate endDate;

        try {
            startDate = LocalDate.parse(startDateInput);
            endDate = LocalDate.parse(endDateInput);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date. Please use YYYY-MM-DD.");
            return;
        }

        if (startDate.isAfter(endDate)) {
            System.out.println("Start date cannot be after end date.");
            return;
        }

        double total = 0;

        System.out.println("\n===== DATE-RANGE EXPENSE REPORT =====");
        System.out.println("From: " + startDate);
        System.out.println("To:   " + endDate);
        System.out.println("-------------------------------------");

        boolean found = false;

        for (Expense expense : expenses) {

            LocalDate expenseDate = expense.getDate();

            if (!expenseDate.isBefore(startDate)
                    && !expenseDate.isAfter(endDate)) {

                System.out.printf(
                        "%s | %-15s | ₹%.2f%n",
                        expense.getDate(),
                        expense.getCategory(),
                        expense.getAmount());

                total += expense.getAmount();
                found = true;
            }
        }

        if (!found) {

            System.out.println("No expenses found in this date range.");

        } else {

            System.out.println("-------------------------------------");
            System.out.printf("Total: ₹%.2f%n", total);

        }

        System.out.println("=====================================");
    }

    private static void showBudgetStatus() {

        if (monthlyBudget <= 0) {

            System.out.println("Monthly budget is not set.");
            System.out.println("Please set your monthly budget first.");

            return;
        }

        LocalDate today = LocalDate.now();

        int currentYear = today.getYear();
        int currentMonth = today.getMonthValue();

        double monthlySpent = 0;

        for (Expense expense : expenses) {

            LocalDate expenseDate = expense.getDate();

            if (expenseDate.getYear() == currentYear
                    && expenseDate.getMonthValue() == currentMonth) {

                monthlySpent += expense.getAmount();
            }
        }

        double remaining = monthlyBudget - monthlySpent;

        double percentageUsed = (monthlySpent / monthlyBudget) * 100;

        System.out.println("\n===== BUDGET STATUS =====");

        System.out.printf(
                "Monthly Budget : ₹%.2f%n",
                monthlyBudget);

        System.out.printf(
                "Spent          : ₹%.2f%n",
                monthlySpent);

        System.out.printf(
                "Remaining      : ₹%.2f%n",
                remaining);

        System.out.printf(
                "Used           : %.1f%%%n",
                percentageUsed);

        System.out.println("-------------------------");

        if (monthlySpent > monthlyBudget) {

            System.out.println("⚠ BUDGET EXCEEDED!");

        } else if (percentageUsed >= 80) {

            System.out.println(
                    "⚠ Warning: You have used 80% or more of your budget.");

        } else {

            System.out.println(
                    "✓ You are within your monthly budget.");
        }

        System.out.println("=========================");
    }

    private static void showExpenseDashboard() {

        if (expenses.isEmpty()) {

            System.out.println("\nNo expenses available.");
            return;
        }

        double totalExpenses = 0;
        double highestExpense = expenses.get(0).getAmount();
        double lowestExpense = expenses.get(0).getAmount();

        for (Expense expense : expenses) {

            double amount = expense.getAmount();

            totalExpenses += amount;

            if (amount > highestExpense) {
                highestExpense = amount;
            }

            if (amount < lowestExpense) {
                lowestExpense = amount;
            }
        }

        int numberOfExpenses = expenses.size();

        double averageExpense = totalExpenses / numberOfExpenses;

        // Current month calculation
        LocalDate today = LocalDate.now();

        int currentYear = today.getYear();
        int currentMonth = today.getMonthValue();

        double monthlySpent = 0;

        for (Expense expense : expenses) {

            LocalDate expenseDate = expense.getDate();

            if (expenseDate.getYear() == currentYear
                    && expenseDate.getMonthValue() == currentMonth) {

                monthlySpent += expense.getAmount();
            }
        }

        System.out.println("\n========== EXPENSE DASHBOARD ==========");

        System.out.printf(
                "Total Expenses       : ₹%.2f%n",
                totalExpenses);

        System.out.printf(
                "Number of Expenses   : %d%n",
                numberOfExpenses);

        System.out.printf(
                "Average Expense      : ₹%.2f%n",
                averageExpense);

        System.out.printf(
                "Highest Expense      : ₹%.2f%n",
                highestExpense);

        System.out.printf(
                "Lowest Expense       : ₹%.2f%n",
                lowestExpense);

        System.out.println("\nCurrent Month");
        System.out.println("----------------------------------------");

        System.out.printf(
                "Monthly Spending     : ₹%.2f%n",
                monthlySpent);

        if (monthlyBudget > 0) {

            double remainingBudget = monthlyBudget - monthlySpent;
            double budgetUsed = (monthlySpent / monthlyBudget) * 100;

            System.out.printf(
                    "Monthly Budget       : ₹%.2f%n",
                    monthlyBudget);

            System.out.printf(
                    "Remaining Budget     : ₹%.2f%n",
                    remainingBudget);

            System.out.printf(
                    "Budget Used          : %.1f%%%n",
                    budgetUsed);

        } else {

            System.out.println(
                    "Monthly Budget       : Not Set");
        }

        System.out.println("========================================");
    }

}