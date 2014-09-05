import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ParallelCountFactorsUsingFutures {
    private static final int THREAD_COUNT = 10;
    private static ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
    private static final int range = 5_000_000;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        List<Future<Integer>> futures = new ArrayList<>();

        for (int i = 0; i < THREAD_COUNT; i ++) {
            futures.add(createWorker(i));
        }

        int count = 0;
        for(Future<Integer> f : futures) {
            count += f.get();
        }

        System.out.printf("Total number of factors is %9d%n", count);
    }

    public static Future<Integer> createWorker(final int from) {
        return executor.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int count = 0;
                for(int i = from; i < range; i += THREAD_COUNT) {
                    count += countFactors(i);
                }

                return count;
            }
        });
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

