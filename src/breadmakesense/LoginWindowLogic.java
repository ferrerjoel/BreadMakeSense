package breadmakesense;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

/**
 * @author jpg, ferrerjoel
 * This class is the one with the logic used to log in.
 */
public class LoginWindowLogic {
	//Global variables declaration
	static Connection con;
	static Statement stmt;
	static ResultSet getUname;
	static FileHandler handler;
	static Logger logger;
	static FileReader fr;
	static BufferedReader br;
	static HashMap<String, String> hm;

	static String clientVersion = "1.0";
	static boolean incPass;
	static String username;
	static String loginInfo;
	/**
	 * Starts all the logic so we can use it anywhere.
	 */
	public static void startLogic() {
		startLog();
		getConfig();
		initializeBD();
		checkVersions();
	}
	/**
	 *	Read the configuration file so we can use it later
	 */
	public static void getConfig() {
		try {
			fr = new FileReader(System.getProperty("user.dir") + "/src/files/config.conf");
			br = new BufferedReader(fr);

			String res = "";
			String[] config;

			hm = new HashMap<String, String>();
			//Separates the lines for the symbol. Links the rigth side with the left side
			while ((res = br.readLine()) != null) {

				config = res.split("=");
				hm.put(config[0], config[1]);
			}
		} catch (IOException ioe) {
			logger.warning("Can't acces to configuration file");
		}
	}
	/**
	 * Initialize the DataBase
	 */
	public static void initializeBD() {
		try {
			//Uses the configuration file to connect to the database
			con = DriverManager.getConnection(
					"jdbc:mysql://" + hm.get("ip") + ":" + hm.get("port") + "/" + hm.get("bd"), hm.get("user"),
					hm.get("passwd"));
			//Also creates a statement for later
			stmt = con.createStatement();
		} catch (Exception e) {
			logger.severe("Error: Can't connect to database");
			e.printStackTrace();
		}
	}
	/**
	 * Check if the client version is up-to-date
	 */
	public static void checkVersions() {
		try {

			ResultSet getVersion = stmt.executeQuery("select vers from server limit 1");

			getVersion.next();
			if (!clientVersion.equals(getVersion.getString(1))) {
				logger.severe("The client version isn't up-to-date");
				System.exit(-1);
			}
		} catch (SQLException e) {
			logger.warning("Can't get version from the server");
			e.printStackTrace();
		}
	}
	/**
	 * Start the Log file (all the info will go there)
	 */
	public static void startLog() {
		try {
			handler = new FileHandler(System.getProperty("user.dir") + "/src/logs/default.log");
			logger = Logger.getLogger("p1");
			logger.addHandler(handler);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Tries to see if the username and password are in the database and if there is no user, creates one.
	 * Used to Log in
	 * @param u The username
	 * @param p The password
	 */
	public static void attempLogin(String u, String p) {

		username = u;
		boolean usFound = false, end = false;
		//This while is in case the user have to be created it log in automatically
		while (!end) {
			try {
				getUname = stmt.executeQuery("select username, id from users");
				//Search for the username
				while (getUname.next()) {
					if (u.equals(getUname.getString(1))) {
						usFound = true;
						PreparedStatement pstmt = con.prepareStatement("select passwd from users where username = ?");
						pstmt.setString(1, u);

						ResultSet getPass = pstmt.executeQuery();
						//Checks if the password coincide
						if (getPass.next()) {
							if (p.equals(getPass.getString(1))) {
								logger.info("Connection established");
								incPass = false;
							} else {
								logger.warning("Error: Incorrect password");
								incPass = true;
							}
							end = true;
						}
					}
				}
				//If the user is not found it creates it
				if (!usFound) {
					PreparedStatement pstmt = con.prepareStatement("insert into users(username, passwd) values(?, ?)");
					pstmt.setString(1, u);
					pstmt.setString(2, p);

					pstmt.executeUpdate();
					logger.info("User created succesfully");
					end = true;
				}

			} catch (Exception e) {
				logger.severe("Error: Can't connect to database");
				e.printStackTrace();
			}
		}
		System.out.println("A");
		loginInfo = (!usFound) ? "User created successfully" : "User logged successfully";
	}
	/**
	 * Gets the sum of all the breads generated
	 * @return
	 */
	public static double serverPuntuation() {
		try {
			ResultSet totalPunt = stmt.executeQuery("select sum(legacy_bread) from users");
			if (totalPunt.next()) {
				return totalPunt.getDouble(1);
			}
		} catch (SQLException e) {
			logger.info("Can't get total breads");
			e.printStackTrace();
		}
		return 0;

	}
	/**
	 * Checks if the username or the password have the requeriments
	 * @param u The username
	 * @param p The password
	 * @return
	 */
	public static boolean loginInCheck(String u, String p) {
		if (u.trim().equals("") || p.trim().equals("") || u.contains(" ") || p.contains(" ") || p.length() >= 16 || p.length() < 4|| u.length() < 4) {
			return true;
		}
		return false;
	}
}
