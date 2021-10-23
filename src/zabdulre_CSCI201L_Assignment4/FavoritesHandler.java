package zabdulre_CSCI201L_Assignment4;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/FavoritesHandler")
public class FavoritesHandler extends HttpServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	SqlUtil conn = new SqlUtil();
	Gson gson = new Gson();
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	// TODO Auto-generated method stub
		boolean wasFavorite = false;
		JsonObject message = new JsonObject();
		try {
		String ticker = req.getParameter("ticker");
		Integer userID =  (Integer) req.getSession().getAttribute("userID");
		if (userID == null || !conn.isLoggedIn(req)) return;
		if (req.getParameter("mode").equals("toggle")) {
			if(conn.isFavorite(ticker, userID)) {
				wasFavorite = true;
				conn.deleteFavorite(ticker, userID);
			}
			else {
				conn.addFavorite(ticker, userID);
			}
			message.add("wasFavorite", new JsonPrimitive(wasFavorite));
			Util.writeMessage(resp, message.toString());
		}
		else if (req.getParameter("mode").equals("all")) {
			ArrayList<String> tickers = getAllTickers(userID);
			if (tickers == null) sendError(resp);
			else {
			Util.writeMessage(resp, gson.toJson(createTickerJson(tickers)));
			}
		}
		else {
			message.add("isFavorite", new JsonPrimitive(conn.isFavorite(ticker, userID)));
			Util.writeMessage(resp, message.toString());
		}
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.println("error in getting a favorite, user is probably not logged in");
			
		}
		finally {
		}
}
	private ArrayList<String> getAllTickers(Integer userID) throws SQLException {
		// TODO Auto-generated method stub
		ArrayList<String> tickers = new ArrayList<String>();
		ResultSet rs = conn.getAllFavorites(userID);
		if (!rs.next()) return null;
		do {
			tickers.add(rs.getString("ticker"));
		}
		while(rs.next());
		return tickers;
	}
	
	void sendError(HttpServletResponse resp) throws IOException {
		JsonObject error = new JsonObject();
		error.add("error", new JsonPrimitive(true));
		Util.writeMessage(resp, error.toString());
	}
	
	JsonArray createTickerJson(ArrayList<String> tickers) {
		ArrayList<JsonObject> jsons = new ArrayList<JsonObject>();
		String result = new String();
		for (String i: tickers) {
			jsons.add(SearchHandler.getCompany(i, true));
		}
		return gson.fromJson(gson.toJson(jsons), JsonArray.class);
	}
}
