package eco;

import org.junit.jupiter.api.Test;

public class TestDatabase {
    @Test
    public void testDatabaseStartup() {
        Database database = new Database();

        database.start();
    }

    @Test
    public void testEconomyTableCreation() {
        Database database = new Database();

        database.start();

        database.createEconomyTable();
    }

    @Test
    public void testEconomyUserCreation() {
        Database database = new Database();

        try {
            database.start();

            database.executeSql("INSERT INTO Economy VALUES ('test', 100.0)");
            database.executeSql("SELECT * FROM Economy", (resultSet) -> {
                try {
                    while (resultSet.next()) {
                        String username = resultSet.getString("Username");
                        double balance = resultSet.getDouble("Balance");
                        Logger.log(username + ", " + balance);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            database.executeSql("DELETE FROM Economy WHERE Username = 'test'");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
