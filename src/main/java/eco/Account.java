package eco;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Account {
    private final Database database;

    private final String username;

    private static final String SERVER_ACCOUNT = "SERVER";

    public Account(String username) {
        this(
                Database.getDefaultDatabase(),
                username
        );
    }

    public Account(Database database,
                   String username) {
        this.database = database;
        this.username = username;

        // if there's no account, insert it into the database
        double balance = -1;
        try {
            balance = getBalance();

        } catch (Exception ignored) {
        }

        if (balance == -1) {
            database.executeSql("INSERT INTO Economy VALUES ('" + username + "', 0.0)");
        }
    }

    public static Account getAccount(Player player) {
        String username = player.getUniqueId().toString();

        return new Account(
                Database.getDefaultDatabase(),
                username
        );
    }

    public static Account getServerAccount(Database database) {
        return new Account(
                database,
                SERVER_ACCOUNT
        );
    }

    public static List<Account> getAccountLeaderboard(int howMany) {
        try {
            List<Account> list = new ArrayList<>(howMany);

            Database.getDefaultDatabase().executeSql(
                    "SELECT TOP " + howMany + " * FROM Economy ORDER BY Balance DESC",
                    (resultSet -> {
                        try {
                            while (resultSet.next()) {
                                String username = resultSet.getString("Username");
                                list.add(new Account(username));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    })
            );

            return list;
        } catch (Exception e) {
            e.printStackTrace();

            Logger.log("An issue occurred while attempting to get the account leaderboard!");

            return new ArrayList<>();
        }
    }

    public double getBalance() {
        AtomicReference<Double> balance = new AtomicReference<>(-1d);

        database.executeSql("SELECT Balance FROM Economy WHERE Username = '" + username + "'",
                (resultSet -> {
                    try {
                        while (resultSet.next()) {
                            balance.set(resultSet.getDouble("Balance"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }));

        return balance.get();
    }

    public void setBalance(double balance) {
        database.executeSql("UPDATE Economy " +
                "SET Balance = '" + balance + "' " +
                "WHERE Username='" + username + "'");
    }

    public void subtractBalance(double subtract) {
        double currentBalance = getBalance();
        double newBalance = currentBalance - subtract;

        setBalance(newBalance);
    }

    public void addBalance(double add) {
        double currentBalance = getBalance();
        double newBalance = currentBalance + add;

        setBalance(newBalance);
    }

    public void multiplyBalance(double multiplier) {
        double currentBalance = getBalance();
        double newBalance = currentBalance * multiplier;

        setBalance(newBalance);
    }

    public void divideBalance(double divisor) {
        double currentBalance = getBalance();
        double newBalance = currentBalance / divisor;

        setBalance(newBalance);
    }

    public String getUsername() {
        return this.username;
    }
}
