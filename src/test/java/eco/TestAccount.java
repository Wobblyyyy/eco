package eco;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestAccount {
    @Test
    public void testGetBalance() {
        Database database = new Database();

        try {
            database.start();

            database.executeSql("INSERT INTO Economy VALUES ('test', 100.0)");
            Account account = new Account(database, "test");
            Logger.log("test account balance: " + account.getBalance());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSetBalance() {
        Database database = new Database();

        try {
            database.start();

            database.executeSql("INSERT INTO Economy VALUES ('test', 100.0)");
            Account account = new Account(database, "test");
            Logger.log("test account balance: " + account.getBalance());
            account.setBalance(50d);
            Logger.log("test account new balance: " + account.getBalance());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testBalanceManipulation() {
        Database database = new Database();

        try {
            database.start();

            database.executeSql("INSERT INTO Economy VALUES ('test', 100.0)");
            Account account = new Account(database, "test");
            Logger.log("test account balance: " + account.getBalance());
            account.setBalance(50d);
            Logger.log("test account new balance: " + account.getBalance());
            account.addBalance(50d);
            Logger.log("test account new balance: " + account.getBalance());
            account.multiplyBalance(2d);
            Logger.log("test account new balance: " + account.getBalance());
            account.subtractBalance(50d);
            Logger.log("test account new balance: " + account.getBalance());
            account.divideBalance(2d);
            Logger.log("test account new balance: " + account.getBalance());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getAllBalances() {
        Map<String, Double> balances = new HashMap<>();

        Database database = new Database();
        Database.setDefaultDatabase(database);

        try {
            database.start();

            database.executeSql(
                    "SELECT * FROM Economy",
                    (resultSet -> {
                        try {
                            while (resultSet.next()) {
                                balances.put(
                                        resultSet.getString("Username"),
                                        resultSet.getDouble("Balance")
                                );
                            }
                        } catch (Exception ignored) {

                        }
                    })
            );

            for (Map.Entry<String, Double> entry : balances.entrySet()) {
                Logger.log(
                        entry.getKey() + ": " + entry.getValue()
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testBalanceLeaderboard() {
        Database database = new Database();
        Database.setDefaultDatabase(database);

        try {
            database.start();

            (new Account("User 1")).setBalance(100d);
            (new Account("User 2")).setBalance(200d);
            (new Account("User 3")).setBalance(500d);
            (new Account("User 4")).setBalance(300d);

            List<Account> balanceTop = Account.getAccountLeaderboard(3);

            for (Account account : balanceTop) {
                Logger.log(account.getUsername());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
