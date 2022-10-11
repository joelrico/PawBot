package me.joel;
import java.sql.*;

public class Database {

    private static Connection conn = null;

    /**
     * Initializes DB
     */
    public static void connect() {
        String url = "jdbc:sqlite:avalon.sqlite";
        try {
            Connection conn = DriverManager.getConnection(url);

            conn.createStatement().execute("CREATE TABLE IF NOT EXISTS currency(user_id string UNIQUE, wallet int)");
            conn.createStatement().execute("CREATE TABLE IF NOT EXISTS guild_settings(guild_id string UNIQUE,  confession_ch int, join_ch int, leave_ch int)");
            conn.createStatement().execute("CREATE TABLE IF NOT EXISTS starboard_settings(guild_id string UNIQUE,  starboard_ch int, star_limit int, star_self int)");
            conn.close();

            Console.info("Successfully initialized DB");
        } catch (SQLException e) {
            Console.warn("Failed to initialize DB");
            throw new RuntimeException(e);
        }
    }

    /**
     * @return Connection to DB
     */
    public static Connection getConnect() {
        String url = "jdbc:sqlite:avalon.sqlite";

        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            Console.warn("Failed to connect to DB");
            e.printStackTrace();
        }

        return conn;
    }

    public static int getWallet(String user_id) throws SQLException {
        String url = "jdbc:sqlite:avalon.sqlite";
        try {
            conn = DriverManager.getConnection(url);

            String sql = ("SELECT wallet FROM currency WHERE user_id=" + user_id);
            ResultSet rs = conn.createStatement().executeQuery(sql);

            int bal = rs.getInt(1);

            if (bal < 100) {
                String reset = "REPLACE INTO currency(user_id, wallet) values (" + user_id + ", 500)";
                conn.createStatement().execute(reset);
                return 0;
            }

            conn.close();
            return bal;
        } catch (SQLException e) {
            Console.warn("Failed to connect to DB");
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Perform addition/subtraction on user bank balance
     * @param user_id Discord user ID
     * @param amt Amount that bank balance is to be modified by
     * @throws SQLException Error attempting to communicate with database
     */
    public static void modifyWallet(String user_id, int amt) throws SQLException {
        // Get balance
        int bal = 0;
        try {
            bal = Database.getWallet(user_id);
        } catch (SQLException ignore) {}

        // Reset balance to 500 if < 100
        int new_bal = bal + amt;
        if (new_bal < 100) new_bal = 500;

        String sql = "REPLACE INTO currency(user_id, wallet) values (" + user_id + ", " + new_bal + ")";

        Connection conn = getConnect();
        conn.createStatement().execute(sql);
        conn.close();
    }

}
