public class MyAtomicInteger {
    private int counter;

    public synchronized int addAndGet(int amount) {
        counter += amount;
        return counter;
    }

    public synchronized int get() {
        return counter;
    }
}

