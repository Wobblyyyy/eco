package eco;

import java.sql.*;
import java.util.function.Consumer;

public class Database {
    private static final String DATABASE_URL = "jdbc:h2:/data/database";

    private static final String DATABASE_USER = "sa";

    private static final String DATABASE_PASSWORD = "sa";

    private static Database DEFAULT_DATABASE;

    private Connection connection;

    public static Database getDefaultDatabase() {
        if (DEFAULT_DATABASE == null) {
            throw new RuntimeException("You need to set the default database before attempting to use it!");
        }

        return DEFAULT_DATABASE;
    }

    public static void setDefaultDatabase(Database database) {
        DEFAULT_DATABASE = database;
    }

    public void start() {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException exception) {
            throw new RuntimeException("Failed to start local database!");
        }

        try {
            connection = DriverManager.getConnection(
                    DATABASE_URL,
                    DATABASE_USER,
                    DATABASE_PASSWORD
            );
            Logger.log("Successful database connection: " + connection.isValid(0));
        } catch (SQLException exception) {
            connection = null;
            throw new RuntimeException("Failed to connect to local database!");
        }

        try {
            createEconomyTable();
            createTransactionsTable();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("An exception occurred while attempting to create tables.");
        }

        executeSql("DELETE FROM Economy WHERE Username = 'test'");
        executeSql("DELETE FROM Economy WHERE (" +
                "Username = 'User 1' OR " +
                "Username = 'User 2' OR " +
                "Username = 'User 3' OR " +
                "Username = 'User 4')");
        executeSql("DELETE FROM EconomyTransactions WHERE (" +
                "Sender = 'User 1' OR " +
                "Sender = 'User 2' OR " +
                "Sender = 'User 3')"
        );

        setDefaultDatabase(this);
    }

    public void createEconomyTable() {
        try (Statement statement = connection.createStatement()) {
            String sql =
                    "CREATE TABLE IF NOT EXISTS Economy (" +
                            "Username VARCHAR(50)," +
                            "Balance DOUBLE(16)," +
                            ")";

            statement.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.log("An exception occurred while attempting to create the economy table.");
        }
    }

    public void createTransactionsTable() {
        try (Statement statement = connection.createStatement()) {
            String sql =
                    "CREATE TABLE IF NOT EXISTS EconomyTransactions (" +
                            "Sender VARCHAR(50), " +
                            "Receiver VARCHAR(50), " +
                            "TaxAccount VARCHAR(50), " +
                            "TotalPaid DOUBLE(16), " +
                            "TaxPaid DOUBLE(16), " +
                            "TotalReceived DOUBLE(16)" +
                            ")";

            statement.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.log("An exception occurred while attempting to create the economy table.");
        }
    }

    public void executeSql(String sql) {
        executeSql(sql, (resultSet) -> {
        });
    }

    public void executeSql(String sql, Consumer<ResultSet> consumer) {
        try (Statement statement = connection.createStatement()) {
            if (sql.length() < 1 || consumer == null) {
                Logger.log("Invalid SQL command or result acceptor.");
            }

            statement.execute(sql);

            assert consumer != null;
            consumer.accept(statement.getResultSet());
        } catch (Exception e) {
            e.printStackTrace();

            Logger.log("An exception occurred while attempting to execute a SQL command.");
        }
    }
}
