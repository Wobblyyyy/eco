package eco;

import java.util.concurrent.atomic.AtomicBoolean;

public class Transaction {
    private final AtomicBoolean hasExecuted = new AtomicBoolean(false);
    private final Account sender;
    private final Account receiver;
    private Account taxReceiver;
    private double amount;
    private double tax;

    private static Account DEFAULT_TAX_ACCOUNT = null;
    private static double DEFAULT_TAX = 0.0;

    public Transaction(Account sender,
                       Account receiver,
                       double amount) {
        this(
                sender,
                receiver,
                null,
                amount,
                0.0
        );
    }

    public Transaction(Account sender,
                       Account receiver,
                       Account taxReceiver,
                       double amount,
                       double tax) {
        this.sender = sender;
        this.receiver = receiver;
        this.taxReceiver = taxReceiver;
        this.amount = amount;
        this.tax = tax;

        if (taxReceiver == null && DEFAULT_TAX_ACCOUNT != null) {
            this.taxReceiver = DEFAULT_TAX_ACCOUNT;
        }
        if (tax == 0d && DEFAULT_TAX != 0d) {
            this.tax = DEFAULT_TAX;
        }
    }

    public static Account getDefaultTaxAccount() {
        if (DEFAULT_TAX_ACCOUNT == null) {
            throw new RuntimeException("You need to set the default tax account before attempting to use it!");
        }

        return DEFAULT_TAX_ACCOUNT;
    }

    public static double getDefaultTax() {
        return DEFAULT_TAX;
    }

    public static void setDefaultTaxAccount(Account defaultTaxAccount) {
        DEFAULT_TAX_ACCOUNT = defaultTaxAccount;
    }

    public static void setDefaultTax(double tax) {
        DEFAULT_TAX = tax;
    }

    public Account getTaxReceiver() {
        return this.taxReceiver;
    }

    public void setTaxReceiver(Account account) {
        this.taxReceiver = account;
    }

    public double getAmount() {
        return this.amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void execute() {
        if (!hasExecuted.get()) {
            if (this.tax != 0) {
                double toSend = amount;
                double toTax = amount * tax;
                double toReceive = toSend - toTax;

                sender.subtractBalance(amount);
                receiver.addBalance(toReceive);
                taxReceiver.addBalance(toTax);
            } else {
                sender.subtractBalance(amount);
                receiver.addBalance(amount);
                hasExecuted.set(true);
            }

            String sql =
                    "INSERT INTO EconomyTransactions (" +
                            "Sender, " +
                            "Receiver, " +
                            "TaxAccount, " +
                            "TotalPaid, " +
                            "TaxPaid, " +
                            "TotalReceived) VALUES (" +
                            "'" + sender.getUsername() + "', " +
                            "'" + receiver.getUsername() + "', " +
                            "'" + taxReceiver.getUsername() + "', " +
                            amount + ", " +
                            amount * tax + ", " +
                            (amount - (amount * tax)) +
                            ")";

            Database.getDefaultDatabase().executeSql(sql);
        } else {
            throw new RuntimeException("Attempted to execute a transaction more than once!");
        }
    }

    public boolean hasExecuted() {
        return this.hasExecuted.get();
    }
}
