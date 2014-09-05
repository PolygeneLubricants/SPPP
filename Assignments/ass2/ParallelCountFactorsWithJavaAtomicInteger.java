import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ParallelCountFactorsWithJavaAtomicInteger {
    private static final int THREAD_COUNT = 10;
    private static final int range = 5_000_000;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        List<Thread> threads = new ArrayList<>();

        AtomicInteger counter = new AtomicInteger();
        for (int i = 0; i < THREAD_COUNT; i++) {
            threads.add(createWorker(i, counter));
        }

        for (Thread t : threads) {
            t.join();
        }

        System.out.printf("Total number of factors is %9d%n", counter.get());
    }

    public static Thread createWorker(final int from, final AtomicInteger counter) {
        Thread t = new Thread(new Runnable() {
            public void run() {
                for (int i = from; i < range; i += THREAD_COUNT) {
                    counter.addAndGet(countFactors(i));
                }
            }
        });

        t.start();
        return t;
    }

    public static int countFactors(int p) {
        if (p < 2)
            return 0;
        int factorCount = 1, k = 2;
        while (p >= k * k) {
            if (p % k == 0) {
                factorCount++;
                p /= k;
            } else
                k++;
        }
        return factorCount;
    }
}