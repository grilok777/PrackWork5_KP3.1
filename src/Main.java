import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Зчитування параметрів від користувача


        int rows = getPositiveInput(scanner, "Введіть кількість рядків масиву: ");
        int cols = getPositiveInput(scanner, "Введіть кількість стовпців масиву: ");
        int min = getInput(scanner, "Введіть мінімальне значення для генерації: ");
        int max = getInput(scanner, "Введіть максимальне значення для генерації: ");

        while (max < min) {
            System.out.println("Максимальне значення має бути більше чи рівне мінімальному значенню");
            max = getInput(scanner, "Введіть максимальне значення для генерації: ");
        }
        int[][] array = generateArray(rows, cols, min, max);
        System.out.println("Згенерований масив:");
        printArray(array);

        // Виконання Work Stealing підходу
        long startTime = System.currentTimeMillis();
        Integer result1 = findWithWorkStealing(array);
        long endTime = System.currentTimeMillis();
        System.out.println("Work Stealing результат: " + (result1 != null ? result1 : "Не знайдено"));
        System.out.println("Час виконання Work Stealing: " + (endTime - startTime) + " мс");

        // Виконання Work Dealing підходу
        startTime = System.currentTimeMillis();
        Integer result2 = findWithWorkDealing(array);
        endTime = System.currentTimeMillis();
        System.out.println("Work Dealing результат: " + (result2 != null ? result2 : "Не знайдено"));
        System.out.println("Час виконання Work Dealing: " + (endTime - startTime) + " мс");
    }

    // Генерація випадкового двовимірного масиву
    private static int[][] generateArray(int rows, int cols, int min, int max) {
        int[][] array = new int[rows][cols];
        Random random = new Random();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                array[i][j] = random.nextInt(max - min + 1) + min;
            }
        }
        return array;
    }

    // Друк масиву на екран
    private static void printArray(int[][] array) {
        for (int[] row : array) {
            for (int value : row) {
                System.out.print(value + "\t");
            }
            System.out.println();
        }
    }

    // Work Stealing реалізація
    private static Integer findWithWorkStealing(int[][] array) {
        ForkJoinPool pool = new ForkJoinPool();
        return pool.invoke(new SearchTask(array, 0, array.length));
    }



    // Work Dealing реалізація
    private static Integer findWithWorkDealing(int[][] array) {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        int numRows = array.length;
        Future<Integer>[] futures = new Future[numRows];
        for (int i = 0; i < numRows; i++) {
            final int row = i;
            futures[i] = executor.submit(() -> findInRow(array, row));
        }
        executor.shutdown();

        try {
            for (Future<Integer> future : futures) {
                Integer result = future.get();
                if (result != null) {
                    return result;
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static Integer findInRow(int[][] array, int row) {
        for (int col = 0; col < array[row].length; col++) {
            if (array[row][col] == row + col) {
                //System.out.println("Deal:"+array[row][col]+" row - "+ row+" col - "+ col);
                return array[row][col];
            }
        }
        return null;
    }

    private static int getPositiveInput(Scanner scanner, String message) {
        int input;
        do {
            System.out.print(message);
            while (!scanner.hasNextInt()) {
                System.out.print("Введіть правильне позитивне [:)] значення: ");
                scanner.next();
            }
            input = scanner.nextInt();
            if (input <= 0) {
                System.out.println("Число повинно бути позитивним :)");
            }
        } while (input <= 0);
        return input;
    }

    private static int getInput(Scanner scanner, String message) {
        System.out.print(message);
        while (!scanner.hasNextInt()) {
            System.out.print("Введіть правильне значення: ");
            scanner.next();
        }
        return scanner.nextInt();
    }

}