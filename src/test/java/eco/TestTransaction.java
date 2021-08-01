package eco;

import org.junit.jupiter.api.Test;

public class TestTransaction {
    @Test
    public void testTransactionDatabase() {
        Database database = new Database();

        try {
            database.start();

            Account sender = new Account("User 1");
            Account receiver = new Account("User 2");
            Account taxAccount = new Account("User 3");
            double amount = 600;
            double tax = 0.1;

            Transaction transaction = new Transaction(
                    sender,
                    receiver,
                    taxAccount,
                    amount,
                    tax
            );

            transaction.execute();

            database.executeSql(
                    "SELECT * FROM EconomyTransactions",
                    (resultSet -> {
                        try {
                            while (resultSet.next()) {
                                String _sender = resultSet.getString("Sender");
                                String _receiver = resultSet.getString("Receiver");
                                String _taxAccount = resultSet.getString("TaxAccount");
                                double _totalPaid = resultSet.getDouble("TotalPaid");
                                double _taxPaid = resultSet.getDouble("TaxPaid");
                                double _totalReceived = resultSet.getDouble("TotalReceived");

                                Logger.log("Sender: " + _sender);
                                Logger.log("Receiver: " + _receiver);
                                Logger.log("Tax account: " + _taxAccount);
                                Logger.log("Total paid: " + _totalPaid);
                                Logger.log("Tax paid: " + _taxPaid);
                                Logger.log("Total received: " + _totalReceived);
                                Logger.log("");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    })
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
