// For week 6
// sestoft@itu.dk * 2014-09-29

// The Dining Philosophers problem, due to E.W. Dijkstra 1965.  Five
// philosophers (threads) sit at a round table on which there are five
// forks (shared resources), placed between the philosophers.  A
// philosopher alternatingly thinks and eats spaghetti.  To eat, the
// philosopher needs exclusive use of the two forks placed to his left
// and right, so he tries to lock them.  

// Both the places and the forks are numbered 0 to 5.  The fork to the
// left of place p has number p, and the fork to the right has number
// (p+1)%5.

// This solution is wrong; it will deadlock after a while.

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class TestPhilosophers {
  public static void main(String[] args) {
      List<Philosopher> philosophers = new LinkedList<>();
    Fork[] forks = { new Fork(), new Fork(), new Fork(), new Fork(), new Fork() };
    for (int place=0; place<forks.length; place++) {
        Philosopher philosopher = new Philosopher(forks, place);
        philosophers.add(philosopher);
      Thread phil = new Thread(philosopher);
      phil.start();
    }

      while(true) {
          for(Philosopher philosopher : philosophers) {
              System.out.println(philosopher.place + ": " + philosopher.eatCounter.get());
          }

          try {
              Thread.sleep(10_000);
          } catch (InterruptedException ignored) {
          }
      }
  }
}

class Philosopher implements Runnable {
  private final Fork[] forks;
  public final int place;

    public final AtomicInteger eatCounter = new AtomicInteger();

  public Philosopher(Fork[] forks, int place) {
    this.forks = forks;
    this.place = place;
  }
// 6.2.4
//    public void run() {
//        while (true) {
//            // Take the two forks to the left and the right
//            int left = place, right = (place+1) % forks.length;
//            if(left <= right) {
//                synchronized (forks[left]) {
//                    synchronized (forks[right]) {
//                        // Eat
//                        System.out.print(place + " ");
//                    }
//                }
//            }
//            else {
//                synchronized (forks[right]) {
//                    synchronized (forks[left]) {
//                        // Eat
//                        System.out.print(place + " ");
//                    }
//                }
//            }
//            // Think
//            try { Thread.sleep(10); }
//            catch (InterruptedException exn) { }
//        }
//    }

// 6.2.5
    public void run() {
        while (true) {
            // Take the two forks to the left and the right
            int left = place, right = (place+1) % forks.length;

            if(!forks[left].tryLock()) {
                continue;
            }

            if(!forks[right].tryLock()){
                forks[left].unlock();
                continue;
            }

            // Eat
            System.out.print(place + " ");
            eatCounter.incrementAndGet();

            forks[right].unlock();
            forks[left].unlock();

            // Think
            try { Thread.sleep(10); }
            catch (InterruptedException exn) { }
        }
    }
}

class Fork extends ReentrantLock { }

