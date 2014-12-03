import java.util.Random;
import java.io.*;
import akka.actor.*;

// -- MISC ------------------------------------------------------
interface IPrintable {
    void print();
}
// -- MESSAGES --------------------------------------------------
class StartTransferMessage implements Serializable {
    public final ActorRef bank;
    public final ActorRef from;
    public final ActorRef to;

    StartTransferMessage(ActorRef bank, ActorRef from, ActorRef to) {
        this.bank = bank;
        this.from = from;
        this.to = to;
    }
}
class TransferMessage implements Serializable {
    public final int amount;
    public final ActorRef from;
    public final ActorRef to;
    TransferMessage(int amount, ActorRef from, ActorRef to) {
        this.amount = amount;
        this.from = from;
        this.to = to;
    }

}
class DepositMessage implements Serializable {
    public final int amount;
    DepositMessage(int amount) {
        this.amount = amount;
    }
}
class PrintBalanceMessage implements Serializable {

}

// -- ACTORS --------------------------------------------------
class AccountActor extends UntypedActor implements IPrintable {
    private int _balance;

    AccountActor(int balance) {
        _balance = balance;
    }
    @Override
    public void onReceive(Object o) throws Exception {
        if(o instanceof DepositMessage) {
            DepositMessage msg = (DepositMessage)o;
            _balance += msg.amount;
        }
        else if(o instanceof PrintBalanceMessage) {
            this.print();
        }
        else {
            throw new IllegalArgumentException("Could not parse message type: " + o.getClass());
        }
    }

    @Override
    public void print() {
        System.out.println("Balance: " + _balance);
    }
}
class BankActor extends UntypedActor {
    @Override
    public void onReceive(Object o) throws Exception {
        if(o instanceof TransferMessage) {
            TransferMessage msg = (TransferMessage)o;
            msg.from.tell(new DepositMessage(-msg.amount), this.getSelf());
            msg.to.tell(new DepositMessage(msg.amount), this.getSelf());
        }
        else {
            throw new IllegalArgumentException("Could not parse message type: " + o.getClass());
        }
    }
}
class ClerkActor extends UntypedActor {
    private Random _random;

    ClerkActor() {
        _random = new Random();
    }
    @Override
    public void onReceive(Object o) throws Exception {
        if(o instanceof StartTransferMessage) {
            StartTransferMessage msg = (StartTransferMessage)o;
            start(msg.bank, msg.from, msg.to);
        }
        else {
            throw new IllegalArgumentException("Could not parse message type: " + o.getClass());
        }
    }

    public void start(ActorRef bank, ActorRef from, ActorRef to) {
        for(int i = 0; i < 100; i++) {
            int value = _random.nextInt(10);
            bank.tell(new TransferMessage(value, from, to), this.getSelf());
        }
    }
}

// -- MAIN --------------------------------------------------
public class ABC { // Demo showing how things work:
    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("ABCSystem");
        final ActorRef A1 = system.actorOf(Props.create(AccountActor.class, 0), "A1");
        final ActorRef A2 = system.actorOf(Props.create(AccountActor.class, 0), "A2");
        final ActorRef B1 = system.actorOf(Props.create(BankActor.class),       "B1");
        final ActorRef B2 = system.actorOf(Props.create(BankActor.class),       "B2");
        final ActorRef C1 = system.actorOf(Props.create(ClerkActor.class),      "C1");
        final ActorRef C2 = system.actorOf(Props.create(ClerkActor.class),      "C2");

        C1.tell(new StartTransferMessage(B1, A1, A2), ActorRef.noSender());
        C2.tell(new StartTransferMessage(B2, A2, A1), ActorRef.noSender());

        try {
            System.out.println("Press return to inspect...");
            System.in.read();

            A1.tell(new PrintBalanceMessage(), ActorRef.noSender());
            A2.tell(new PrintBalanceMessage(), ActorRef.noSender());

            System.out.println("Press return to terminate...");
            System.in.read();
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            system.shutdown();
        }
    }
}