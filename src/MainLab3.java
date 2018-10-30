
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class MainLab3 {
    public static int[][] matrix_a = {
            {1, 2, 4, 5},
            {3, 2, 2, 11},
            {11, 12, 5, 4},
            {11, 10, 17, 12}
    };
    public static int[][] matrix_b = {
            {1, 4, 10, 11},
            {1, 2, 11, 19},
            {23, 2, 8, 3},
            {2, 1, 8, 31}
    };
    public static int[][] matrix_sum = new int[matrix_a.length][matrix_a.length];
    public static int[][] matrix_prod = new int[matrix_a[0].length][matrix_b.length];

    public static int[][] matrix_sum_future = new int[matrix_a.length][matrix_a.length];
    public static int[][] matrix_prod_future = new int[matrix_a[0].length][matrix_b.length];

    public static void main(String[] args) throws InterruptedException {

        //start timer
        long startTime = System.currentTimeMillis();

        //Create all threads for sum
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        for (int rows = 0; rows < matrix_a.length; rows++) {
            MainLab3.MatrixSum thr = new MainLab3.MatrixSum(rows);
            executorService.submit(thr);
        }

        executorService.shutdown();
        if (!executorService.awaitTermination(10000, TimeUnit.MICROSECONDS)) {
            System.out.println("Still waiting after 10000ms: calling System.exit(0)...");
            System.exit(0);
        }
        System.out.println("Sum threads shut down!");

        // print Sum matrix
        for (int i = 0; i < matrix_sum.length; i++) {
            for (int j = 0; j < matrix_sum[0].length; j++) {
                System.out.print(matrix_sum[i][j]+"\t");
            }
            System.out.println();
        }

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("Elapsed time for sum computation: " + elapsedTime);


        startTime = System.currentTimeMillis();
        //Create all threads for product
        ExecutorService executorService2 = Executors.newFixedThreadPool(6);
        for (int rows = 0; rows < matrix_a.length; rows++) {
            MainLab3.MatrixProd thr = new MainLab3.MatrixProd(rows);
            executorService2.submit(thr);
        }

        executorService2.shutdown();
        if (!executorService2.awaitTermination(10000, TimeUnit.MICROSECONDS)) {
            System.out.println("Still waiting after 10000ms: calling System.exit(0)...");
            System.exit(0);
        }
        System.out.println("Multiplication threads shut down!");

        // print prod matrix
        for (int i = 0; i < matrix_prod.length; i++) {
            for (int j = 0; j < matrix_prod[0].length; j++) {
                System.out.print(matrix_prod[i][j]+"\t");
            }
            System.out.println();
        }

        stopTime = System.currentTimeMillis();
        elapsedTime = stopTime - startTime;
        System.out.println("Elapsed time for product computation: " + elapsedTime);


        //start timer
        startTime = System.currentTimeMillis();

        ExecutorService executorServiceSum = Executors.newFixedThreadPool(4);

        for (int rows = 0; rows < matrix_a.length; rows++) {
            Future<ArrayList<Integer>> future = executorServiceSum.submit(new SumTask(rows));
            try {
                ArrayList<Integer> row = future.get();
                copyRows(matrix_sum_future, rows, row);
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        
        executorServiceSum.shutdown();
        if (!executorServiceSum.awaitTermination(10000, TimeUnit.MICROSECONDS)) {
            System.out.println("Still waiting after 10000ms: calling System.exit(0)...");
            System.exit(0);
        }
        System.out.println("Sum future callable tasks shut down!");

        // print Sum matrix
        for (int i = 0; i < matrix_sum_future.length; i++) {
            for (int j = 0; j < matrix_sum_future[0].length; j++) {
                System.out.print(matrix_sum_future[i][j]+"\t");
            }
            System.out.println();
        }

        stopTime = System.currentTimeMillis();
        elapsedTime = stopTime - startTime;
        System.out.println("Elapsed time for future sum computation: " + elapsedTime);



        startTime = System.currentTimeMillis();
        //Create all threads for product
        ExecutorService executorServiceMul = Executors.newFixedThreadPool(6);
        for (int rows = 0; rows < matrix_a.length; rows++) {
            Future<ArrayList<Integer>> future = executorServiceMul.submit(new MulTask(rows));
            try {
                ArrayList<Integer> row = future.get();
                copyRows(matrix_prod_future, rows, row);
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        executorServiceMul.shutdown();
        if (!executorServiceMul.awaitTermination(10000, TimeUnit.MICROSECONDS)) {
            System.out.println("Still waiting after 10000ms: calling System.exit(0)...");
            System.exit(0);
        }
        System.out.println("Multiplication future threads shut down!");

        // print prod matrix
        for (int i = 0; i < matrix_prod_future.length; i++) {
            for (int j = 0; j < matrix_prod_future[0].length; j++) {
                System.out.print(matrix_prod_future[i][j]+"\t");
            }
            System.out.println();
        }

        stopTime = System.currentTimeMillis();
        elapsedTime = stopTime - startTime;
        System.out.println("Elapsed time for product future  computation: " + elapsedTime);

    }

    static class SumTask implements Callable{
        int row;

        public SumTask(int row){
            this.row = row;
        }

        @Override
        public ArrayList<Integer> call() throws Exception {
            ArrayList<Integer> integers = new ArrayList<>();
            for (int i = 0; i < matrix_a[row].length; i++) {
                integers.add(matrix_a[row][i] + matrix_b[row][i]);
            }
            return integers;
        }
    }

    static class MulTask implements Callable{
        int row;

        public MulTask(int row){
            this.row = row;
        }

        @Override
        public List<Integer> call() throws Exception {
            int[] integers = new int[matrix_b.length];
            for (int j = 0; j < matrix_b[row].length; j++) { // bColumn
                for (int k = 0; k < matrix_a[row].length; k++) { // aColumn
                    integers[j] += matrix_a[row][k] * matrix_b[k][j];
                }
            }
            return Arrays.stream(integers).boxed().collect(Collectors.toList());
        }
    }

    static class MatrixSum extends Thread {
        int row;

        public MatrixSum(int row) {
            this.row = row;
        }

        public void run() {
            for (int i = 0; i < matrix_a[row].length; i++) {
                matrix_sum[row][i] = matrix_a[row][i] + matrix_b[row][i];
            }
        }
    }

    static class MatrixProd extends Thread {
        int row;

        public MatrixProd(int row) {
            this.row = row;
        }

        public void run() {
            //System.out.println("running thread:" + this.getName());
            //for (int i = 0; i < aRows; i++) { // aRow
            for (int j = 0; j < matrix_b[row].length; j++) { // bColumn
                for (int k = 0; k < matrix_a[row].length; k++) { // aColumn
                    matrix_prod[row][j] += matrix_a[row][k] * matrix_b[k][j];
                }
            }
        }
    }

    static public void copyRows(int[][] matrix, int row, List<Integer> l) {
        for (int i=0; i < l.size(); i++) {
            matrix[row][i] = l.get(i);
        }
    }


}
