
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class MainLab4 {
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
    public static int[][] matrix_c = {
            {10, 4, 10, 3},
            {12, 2, 1, 1},
            {2, 2, 8, 3},
            {2, 10, 18, 3}
    };
    public static int[][] matrix_prod_ab = new int[matrix_a[0].length][matrix_b.length];
    public static int[][] matrix_prod_abc = new int[matrix_a[0].length][matrix_b.length];

    public static int[][] matrix_prod_future = new int[matrix_a[0].length][matrix_b.length];
    public static int[][] matrix_prod_for = new int[matrix_a[0].length][matrix_b.length];
    public static int[][] matrix_prod_for_aux = new int[matrix_a[0].length][matrix_b.length];

    private static final Lock lock = new ReentrantLock();

    private static final Condition rowDone = lock.newCondition();

    public static void main(String[] args) throws InterruptedException {

        //start timer
        long startTime;
        long stopTime;
        long elapsedTime;


        startTime = System.currentTimeMillis();
        for (int i = 0; i < 4; i++) { // aRow
            for (int j = 0; j < 4; j++) { // bColumn
                for (int k = 0; k < 4; k++) { // aColumn
                    matrix_prod_for_aux[i][j] += matrix_a[i][k] * matrix_b[k][j];
                }
            }
        }
        for (int i = 0; i < 4; i++) { // aRow
            for (int j = 0; j < 4; j++) { // bColumn
                for (int k = 0; k < 4; k++) { // aColumn
                    matrix_prod_for[i][j] += matrix_prod_for_aux[i][k] * matrix_c[k][j];
                }
            }
        }

        stopTime = System.currentTimeMillis();
        elapsedTime = stopTime - startTime;
        System.out.println("Elapsed time for product sequential computation: " + elapsedTime);


        startTime = System.currentTimeMillis();
        //Create all threads for product
        ExecutorService executorServiceAB = Executors.newFixedThreadPool(6);
        for (int rows = 0; rows < matrix_a.length; rows++) {
            MainLab4.MatrixProdab thr = new MatrixProdab(rows);
            executorServiceAB.submit(thr);
        }

        executorServiceAB.shutdown();
        /*if (!executorServiceAB.awaitTermination(10000, TimeUnit.MICROSECONDS)) {
            System.out.println("Still waiting after 10000ms: calling System.exit(0)...");
            System.exit(0);
        }
        System.out.println("Multiplication AB threads shut down!");
*/
        ExecutorService executorServiceABC = Executors.newFixedThreadPool(6);
        for (int rows = 0; rows < matrix_a.length; rows++) {
            MainLab4.MatrixProdabc thr = new MatrixProdabc(rows);
            executorServiceABC.submit(thr);
        }

        executorServiceABC.shutdown();
/*        if (!executorServiceABC.awaitTermination(10000, TimeUnit.MICROSECONDS)) {
            System.out.println("Still waiting after 10000ms: calling System.exit(0)...");
            System.exit(0);
        }
        System.out.println("Multiplication ABC threads shut down!");*/

        stopTime = System.currentTimeMillis();
        elapsedTime = stopTime - startTime;
        System.out.println("Elapsed time for product computation: " + elapsedTime);



        // print prod matrix
        for (int i = 0; i < matrix_prod_ab.length; i++) {
            for (int j = 0; j < matrix_prod_ab[0].length; j++) {
                System.out.print(matrix_prod_ab[i][j]+"\t");
            }
            System.out.println();
        }

        // print prod matrix
        for (int i = 0; i < matrix_prod_abc.length; i++) {
            for (int j = 0; j < matrix_prod_abc[0].length; j++) {
                System.out.print(matrix_prod_abc[i][j]+"\t");
            }
            System.out.println();
        }

        // print prod matrix
        for (int i = 0; i < matrix_prod_for.length; i++) {
            for (int j = 0; j < matrix_prod_for[0].length; j++) {
                System.out.print(matrix_prod_for[i][j]+"\t");
            }
            System.out.println();
        }


/*
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
        */

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

    static class MatrixProdab extends Thread {
        int row;

        public MatrixProdab(int row) {
            this.row = row;
        }

        public void run() {
            lock.lock();
            //System.out.println("running thread:" + this.getName());
            //for (int i = 0; i < aRows; i++) { // aRow

            for (int j = 0; j < matrix_b[row].length; j++) { // bColumn
                for (int k = 0; k < matrix_a[row].length; k++) { // aColumn
                    matrix_prod_ab[row][j] += matrix_a[row][k] * matrix_b[k][j];
                }
            }
            rowDone.signal();
            lock.unlock();
        }
    }
    static class MatrixProdabc extends Thread {
        int row;

        public MatrixProdabc(int row) {
            this.row = row;
        }

        public void run() {
            //System.out.println("running thread:" + this.getName());
            //for (int i = 0; i < aRows; i++) { // aRow
            lock.lock();

            try {
                while (!isFilledRow(matrix_prod_ab, row)) {
                    rowDone.await();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (int j = 0; j < matrix_prod_ab[row].length; j++) { // bColumn
                for (int k = 0; k < matrix_c[row].length; k++) { // aColumn
                    matrix_prod_abc[row][j] += matrix_prod_ab[row][k] * matrix_c[k][j];
                }
            }
            lock.unlock();
        }
    }

    static public boolean isFilledRow(int[][] mat, int row) {
        for (int i = 0; i < mat.length; i++) {
            if (mat[row][i] == 0) return false;
        }
        return true;
    }


}
