import java.util.ArrayList;

public class Main {
    public static int[][] matrix_a = {
            {1, 2, 4, 5},
            {3, 2, 2, 11},
            {11, 12, 5, 4},
            {11, 10, 17, 12}
    };
    public static int[][] matrix_b = {
            {5, 4, 10, 11},
            {1, 2, 11, 19},
            {23, 2, 8, 3},
            {2, 1, 8, 31}
    };
    public static int[][] matrix_sum = new int[matrix_a.length][matrix_a.length];
    public static int[][] matrix_prod = new int[matrix_a[0].length][matrix_b.length];

//    public static void main(String[] args) {
//
//        //start timer
//        long startTime = System.currentTimeMillis();
//
//        //Create all threads for sum
//        ArrayList<Thread> threads = new ArrayList<>();
//        for (int rows = 0; rows < matrix_a.length; rows++) {
//            MatrixSum thr = new MatrixSum(rows);
//            threads.add(thr);
//            thr.start();
//        }
//
//        //Wait for all the threads to finish
//        for (Thread t: threads) {
//            try {
//                t.join();
//                System.out.println("thread " + t.getName() + "joined!");
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//
//        // print Sum matrix
//        for (int i = 0; i < matrix_sum.length; i++) {
//            for (int j = 0; j < matrix_sum[0].length; j++) {
//                System.out.print(matrix_sum[i][j]+"\t");
//            }
//            System.out.println();
//        }
//
//        long stopTime = System.currentTimeMillis();
//        long elapsedTime = stopTime - startTime;
//        System.out.println("Elapsed time for sum computation: " + elapsedTime);
//
//
//        startTime = System.currentTimeMillis();
//        //Create all threads for product
//        ArrayList<Thread> prodThreads = new ArrayList<>();
//        for (int rows = 0; rows < matrix_a.length; rows++) {
//            MatrixProd thr = new MatrixProd(rows);
//            prodThreads.add(thr);
//            thr.start();
//        }
//
//        //Wait for all the threads to finish
//        for (Thread t: prodThreads) {
//            try {
//                t.join();
//                System.out.println("thread " + t.getName() + "joined!");
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//
//        // print prod matrix
//        for (int i = 0; i < matrix_prod.length; i++) {
//            for (int j = 0; j < matrix_prod[0].length; j++) {
//                System.out.print(matrix_prod[i][j]+"\t");
//            }
//            System.out.println();
//        }
//
//        stopTime = System.currentTimeMillis();
//        elapsedTime = stopTime - startTime;
//        System.out.println("Elapsed time for product computation: " + elapsedTime);
//    }

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

            //for (int i = 0; i < aRows; i++) { // aRow
            for (int j = 0; j < matrix_b[row].length; j++) { // bColumn
                for (int k = 0; k < matrix_a[row].length; k++) { // aColumn
                    matrix_prod[row][j] += matrix_a[row][k] * matrix_b[k][j];
                }
            }
        }
    }
}
