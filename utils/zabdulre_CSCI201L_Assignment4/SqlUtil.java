package zabdulre_CSCI201L_Assignment4;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import jakarta.servlet.http.HttpServletRequest;





public class SqlUtil {
	Connection conn;
	
	
	SqlUtil(){
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO: handle exception
			System.out.println("Please check the JDBC driver and its build path");
		}
		try {
		conn = DriverManager.getConnection("jdbc:mysql://localhost/ZSTOCKS?user=root&password=root&serverTimezone=America/Los_Angeles");
		}
		catch (SQLException e) {
			// TODO: handle exception
		}
		if(conn == null) System.out.println("Could not connect to database");
	}
	
	boolean isExistingEmail(String email) throws SQLException {
		return (getByEmail(email).next());
	}
	
	boolean isExistingUsername(String username) throws SQLException{
		return (getByUsername(username).next());//should never be more than one
	}
	
	boolean isGoogleUser(String username) throws SQLException{
		ResultSet rs = getByUsername(username);
		rs.next();
		return rs.getBoolean("isGoogle");
	}
	
	int idByEmail(String email) throws SQLException{
		if (!isExistingEmail(email)) throw new SQLException();
		Statement st = conn.createStatement();
		PreparedStatement ps = conn.prepareStatement("SELECT userID from user s WHERE s.email=?");
		ps.setString(1, email);
		ResultSet rs = ps.executeQuery();
		rs.next();
		return rs.getInt("userID");
	}
	
	int idByUsername(String username) throws SQLException{
		if (!isExistingUsername(username)) throw new SQLException();
		Statement st = conn.createStatement();
		PreparedStatement ps = conn.prepareStatement("SELECT userID from user s WHERE s.username=?");
		ps.setString(1, username);
		ResultSet rs = ps.executeQuery();
		rs.next();
		return rs.getInt("userID");
	}
	
	ResultSet getByEmail(String email) throws SQLException{
		Statement st = conn.createStatement();
		PreparedStatement ps = conn.prepareStatement("SELECT * from user s WHERE s.email=?");
		ps.setString(1, email);
		return ps.executeQuery();
	}
	
	ResultSet getByUsername(String username) throws SQLException{
		Statement st = conn.createStatement();
		PreparedStatement ps = conn.prepareStatement("SELECT * from user s WHERE s.username=?");
		ps.setString(1, username);
		return ps.executeQuery();
	}
	
	void createSession(int userID, HttpServletRequest req) throws SQLException{
		String sessionId = req.getSession().getId();
		req.getSession().setAttribute("userID", userID);
		Statement st = conn.createStatement();
		PreparedStatement ps = conn.prepareStatement("UPDATE user SET sessionID = ? WHERE userID = ?");
		ps.setString(1, sessionId);
		ps.setInt(2, userID);
		ps.executeUpdate();
	}
	
	void endSession(HttpServletRequest req) throws SQLException {
		if (!isLoggedIn(req)) {return;}
		else {
			Statement st = conn.createStatement();
			PreparedStatement ps = conn.prepareStatement("UPDATE user SET sessionID = ? WHERE userID = ?");
			ps.setString(1, "null");
			ps.setInt(2, (Integer)req.getSession().getAttribute("userID"));
			ps.execute();
		}
	}
	
	void createNewGoogleUser(String email, String username,float balance) throws SQLException{
		Statement st = conn.createStatement();
		PreparedStatement ps = conn.prepareStatement("INSERT INTO user (email, username, balance, isGoogle) VALUES (?, ?, ?, ?)");
		ps.setString(1, email);
		ps.setString(2, username);
		ps.setFloat(3, balance);
		ps.setBoolean(4, true);
		ps.execute();
	}
	
	void createNewUser(String email, String username, String password, float balance) throws SQLException, NoSuchAlgorithmException{
		Statement st = conn.createStatement();
		PreparedStatement ps = conn.prepareStatement("INSERT INTO user (email, username, balance, isGoogle, hashes) VALUES (?, ?, ?, ?, ?)");
		ps.setString(1, email);
		ps.setString(2, username);
		ps.setFloat(3, balance);
		ps.setBoolean(4, false);
		ps.setString(5, Util.hash(password));
		ps.execute();
	}
	
	boolean isLoggedIn(HttpServletRequest req) {
		try {
		if (req.getSession().getAttribute("userID") == null) return false;
		Integer userID = (Integer) (req.getSession().getAttribute("userID"));
		String sessionId = req.getSession().getId();
		if (sessionId.toLowerCase().equals("null")) return false;
		Statement st = conn.createStatement();
		PreparedStatement ps = conn.prepareStatement("SELECT * from user s WHERE s.sessionID = ? AND s.userID = ?");
		ps.setString(1, sessionId);
		ps.setInt(2, userID);
		return ps.executeQuery().next();
		}
		catch (SQLException e) {
			System.out.println("Sql error in isLogged in");// TODO: handle exception
			return false;
		}
	}
	
	
	String getHashByUsername(String username) throws Exception{//might have to comeback to this one
		Statement st = conn.createStatement();
		PreparedStatement ps = conn.prepareStatement("SELECT * from user s WHERE s.username = ? AND s.isGoogle = 0");
		ps.setString(1, username);
		ResultSet rs = ps.executeQuery();
		if (!rs.next()) throw new Exception();
		if (isGoogleUser(rs.getString("username"))) throw new Exception();
		return rs.getString("hashes");
	}
	
	boolean isFavorite(String ticker, int userID) throws SQLException {
		Statement st = conn.createStatement();
		PreparedStatement ps = conn.prepareStatement("SELECT * from favorite s WHERE s.userID = ? AND s.ticker = ?");
		ps.setInt(1, userID);
		ps.setString(2, ticker);
		ResultSet rs = ps.executeQuery();
		return rs.next();
	}
	
	void addFavorite(String ticker, int userID) throws SQLException{
		Statement st = conn.createStatement();
		PreparedStatement ps = conn.prepareStatement("INSERT INTO favorite (userID, ticker) VALUES (?, ?)");
		ps.setInt(1, userID);
		ps.setString(2, ticker);
		ps.execute();
	}
	
	void deleteFavorite(String ticker, int userID) throws SQLException{
		Statement st = conn.createStatement();
		PreparedStatement ps = conn.prepareStatement("DELETE from favorite s WHERE s.userID = ? AND s.ticker = ?");
		ps.setInt(1, userID);
		ps.setString(2, ticker);
		ps.execute();
	}

	public ResultSet getAllFavorites(Integer userID) throws SQLException {
		// TODO Auto-generated method stub
		Statement st = conn.createStatement();
		PreparedStatement ps = conn.prepareStatement("SELECT * from favorite s WHERE s.userID = ?");
		ps.setInt(1, userID);
		return ps.executeQuery();
	}
	
	Double getUserBalance(int userID) throws SQLException{
		Statement st = conn.createStatement();
		PreparedStatement ps = conn.prepareStatement("SELECT balance from user s WHERE s.userID = ?");
		ps.setInt(1, userID);
		ResultSet rs = ps.executeQuery();
		if(!rs.next()) return null;
		else {
			return (double) rs.getFloat("balance");
		}
		}
	
	void addPurchase(String ticker, Double price, int quantity, int userID) throws SQLException{
		Statement st = conn.createStatement();
		PreparedStatement ps = conn.prepareStatement("INSERT INTO portfolio (ticker, price, quantity, userID, purchasePrice) VALUES (?, ?, ?, ?, ?)");
		ps.setString(1, ticker.toUpperCase());
		ps.setDouble(2, price);
		ps.setInt(3, quantity);
		ps.setInt(4, userID);
		ps.setDouble(5, price);
		ps.execute();
	}
	
	void updateUserBalance(double cost, int userID) throws SQLException{
		Statement st = conn.createStatement();
		PreparedStatement ps = conn.prepareStatement("UPDATE user SET balance = (balance + ?) WHERE userID = ?");
		ps.setDouble(1, cost);
		ps.setInt(2, userID);
		ps.execute();
	}
	
	ArrayList<Stock> getAllBought(String ticker, int userID) throws SQLException{
		Statement st = conn.createStatement();
		PreparedStatement ps = conn.prepareStatement("SELECT quantity, price, purchaseID, purchasePrice ticker from portfolio s WHERE s.ticker = ? AND s.userID = ? ORDER BY purchaseID ASC");
		ps.setString(1, ticker.toUpperCase());
		ps.setInt(2, userID);
		ResultSet rs = ps.executeQuery();
		return getListFromSet(rs);
	}
	
	static ArrayList<Stock> getListFromSet(ResultSet rs) throws SQLException{
		ArrayList<Stock> result = new ArrayList<Stock>();
		while(rs.next()) {
			int q = rs.getInt("quantity");
			int id = rs.getInt("purchaseID");
			double p = rs.getDouble("price");
			String t = rs.getString("ticker").toUpperCase();
			double pp = rs.getDouble("purchasePrice");
			result.add(new Stock(q, id, p, t, pp));
		}
		return result;
	}
	
	static ArrayList<Stock> getListFromSetNoPurchaseID(ResultSet rs) throws SQLException{
		ArrayList<Stock> result = new ArrayList<Stock>();
		while(rs.next()) {
			int q = rs.getInt("quantity");
			String id = rs.getString("ticker").toUpperCase();
			double p = rs.getDouble("price");
			double pp = rs.getDouble("purchasePrice");
			result.add(new Stock(q, id, p, pp));
		}
		return result;
	}
	
	void updateStocks(ArrayList<Stock> allStocks) throws SQLException{
		for (int i = 0; i < (allStocks.size() - 1); i++) {
			deleteStock(allStocks.get(i).stockId);
		}
		if (allStocks.get((allStocks.size() - 1)).quantity == 0) deleteStock(allStocks.get((allStocks.size() - 1)).stockId);
		else alterStock(allStocks.get((allStocks.size() - 1)).stockId , allStocks.get((allStocks.size() - 1)).quantity);
	}
	
	void deleteStock(int stockId) throws SQLException {
		Statement st = conn.createStatement();
		PreparedStatement ps = conn.prepareStatement("DELETE from portfolio s WHERE s.purchaseID = ? LIMIT 1");
		ps.setInt(1, stockId);
		ps.execute();
	}
	
	void alterStock(int stockId, int quantity) throws SQLException {
		Statement st = conn.createStatement();
		PreparedStatement ps = conn.prepareStatement("UPDATE portfolio SET quantity = ? WHERE purchaseID = ?");
		ps.setInt(1, quantity);
		ps.setInt(2, stockId);
		ps.execute();
	}
	
	void alterStockPrice(int stockId, double price) throws SQLException {
		Statement st = conn.createStatement();
		PreparedStatement ps = conn.prepareStatement("UPDATE portfolio SET price = ? WHERE purchaseID = ?");
		ps.setDouble(1, price);
		ps.setInt(2, stockId);
		ps.execute();
	}
	
	ResultSet getPortfolio(int userID) throws SQLException{
		Statement st = conn.createStatement();
		PreparedStatement ps = conn.prepareStatement("SELECT quantity, price, ticker, purchaseID from portfolio s WHERE s.userID = ? ORDER BY ticker;");
		ps.setInt(1, userID);
		return ps.executeQuery();
	}
}
