package zabdulre_CSCI201L_Assignment4;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/PortfolioHandler")
public class PortfolioHandler extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Gson gson = new Gson();
	SqlUtil conn = new SqlUtil();
	enum Mode {buy, sell, invalid, get};//user info returned with all modes except get
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		JsonObject transactionOutcome = new JsonObject();
		JsonArray arr = new JsonArray();
		if (conn.isLoggedIn(req)) {
		try {
			JsonObject j = gson.fromJson(new String(req.getInputStream().readAllBytes()), JsonObject.class);
			Mode mode = getMode(j);
			switch(mode) {
				case sell:{
					transactionOutcome = doSell(j.get("ticker").getAsString(), j.get("quantity").getAsInt(), (Integer) req.getSession().getAttribute("userID"));
					break;
				}
				case buy:{
					transactionOutcome = doBuy(j.get("ticker").getAsString(), j.get("quantity").getAsInt(), (Integer) req.getSession().getAttribute("userID"));
					break;
				}
				case get:{
					arr = getAll(((Integer) req.getSession().getAttribute("userID")));
					Util.writeMessage(resp, arr.toString());
					return;
				}
				default:{
					break;
				}
			}
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			sendFailMessage(resp);
			return;
		}
		}
		else {
			Util.writeMessage(resp, "must be logged in");
		}
		try {
		addUserBalance(transactionOutcome, ((Integer) req.getSession().getAttribute("userID")));
		addAccountValue(transactionOutcome, ((Integer) req.getSession().getAttribute("userID")));
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
			sendFailMessage(resp);
		}
		System.out.println(transactionOutcome.toString());
		Util.writeMessage(resp, transactionOutcome.toString());
	}
	
	Mode getMode(JsonObject j) {//set buyOrSell to 3 when getting stocks
		if (j.get("mode").getAsString().equals("sell")) {
			return Mode.sell;
		}
		else if (j.get("mode").getAsString().equals("buy")) return Mode.buy;
		else if (j.get("mode").getAsString().equals("get")) return Mode.get;
		else return Mode.invalid;//will be used for getting user info
	}
	
	
	void addUserBalance(JsonObject transactionOutcome, int userID) throws SQLException {
		transactionOutcome.add("cashBalance", new JsonPrimitive(conn.getUserBalance(userID)));
	}
	
	void addAccountValue(JsonObject transactionOutcome, int userID) throws SQLException{
		transactionOutcome.add("accountValue", new JsonPrimitive(accumulate(SqlUtil.getListFromSetNoPurchaseID(conn.getPortfolio(userID))) + conn.getUserBalance(userID)));
	}
	
	JsonArray getAll(int userID) throws SQLException {
		ResultSet rs = conn.getPortfolio(userID);
		Map<String, ArrayList<Stock>> collection = groupResults(rs);
		ArrayList<JsonObject> stocks = new ArrayList<JsonObject>();
		for (Map.Entry<String, ArrayList<Stock>> i: collection.entrySet()) {
			stocks.add(makeJsonForArray(i.getValue()));
		}
		return gson.fromJson(gson.toJson(stocks), JsonArray.class);//might have to debug
	}
	
	JsonObject makeJsonForArray(ArrayList<Stock> stocks){
		JsonObject message = new JsonObject();
		JsonObject company = SearchHandler.getCompanyLatestPrice(stocks.get(0).ticker.toUpperCase());
		JsonObject company2 = SearchHandler.getCompanyTicker(stocks.get(0).ticker.toUpperCase());
		message.add("quantity", new JsonPrimitive(getQuantity(stocks)));
		message.add("totalCost", new JsonPrimitive(getTotalCost(stocks)));
		message.add("ticker", new JsonPrimitive(stocks.get(0).ticker.toUpperCase()));
		message.add("currentPrice", new JsonPrimitive(company.get("last").getAsDouble()));
		message.add("name", new JsonPrimitive(company2.get("name").getAsString()));
		message.add("isMarketOpen", new JsonPrimitive((company.get("bidPrice") == JsonNull.INSTANCE)? false : true));
		return message;
	}
	
	Map<String, ArrayList<Stock>> groupResults(ResultSet rs) throws SQLException{
		Map<String, ArrayList<Stock>> grouping = new HashMap<String, ArrayList<Stock>>();
		while (rs.next()) {
			if (grouping.containsKey(rs.getString("ticker").toUpperCase())) {
				grouping.get(rs.getString("ticker").toUpperCase()).add(new Stock(rs.getInt("quantity"), rs.getString("ticker").toUpperCase(), rs.getDouble("price"), rs.getDouble("purchasePrice")));
			}
			else {
				grouping.put(rs.getString("ticker").toUpperCase(), new ArrayList<Stock>());
				grouping.get(rs.getString("ticker").toUpperCase()).add(new Stock(rs.getInt("quantity"), rs.getString("ticker").toUpperCase(), rs.getDouble("price"), rs.getDouble("purchasePrice")));
			}
		}
		return grouping;
	}
	
	JsonObject doBuy(String ticker, int quantity, int userID) throws Exception {
		JsonObject company = getCompanyPrice(ticker);
		Double price = company.get("askPrice").getAsDouble();
		Double priceForSale = company.get("askPrice").getAsDouble();
		Double userBalance = conn.getUserBalance(userID);
		if (price == null) {
			Exception e = new Exception("Invalid company");
			throw e;
		}
		if (userBalance == null) {
			Exception e = new Exception("Invalid userID");
			throw e;
			}
		if ((price * quantity) <= userBalance) {
			double cost = ((-1) * (price * quantity));
			conn.addPurchase(ticker, priceForSale, quantity, userID);
			conn.updateUserBalance(cost, userID);
			JsonObject success = new JsonObject();
			success.add("status", new JsonPrimitive(1));
			success.add("quantityBought", new JsonPrimitive(quantity));
			success.add("price", new JsonPrimitive(price));
			success.add("ticker", new JsonPrimitive(ticker.toUpperCase()));
			return success;
		}
		else {
			Exception e = new Exception("Insufficient balance");
			throw e;
		}
	}
	
	JsonObject doSell(String ticker, int quantity, int userID) throws Exception{
		ArrayList<Stock> allTickers = conn.getAllBought(ticker, userID);//make sure its sorted
		getTotalCost(allTickers);//set prices to ask price
		if (quantity > getQuantity(allTickers)) {throw new Exception("Not enough stocks owned");}
		double profitSoFar = 0;
		int i = 0;
		while(quantity != 0) {//while you still have stocks to sell
			
			int sold = Integer.min(allTickers.get(i).quantity, quantity);//never let stock go negative
			quantity -= sold;
			allTickers.get(i).quantity -= sold;
			profitSoFar += (sold * allTickers.get(i).price);
			i++;//go to the next stock
		
		}
		conn.updateStocks(new ArrayList<Stock>(allTickers.subList(0, i)));//might have to debug
		conn.updateUserBalance(profitSoFar, userID);
		JsonObject message = new JsonObject();
		message.add("status", new JsonPrimitive(1));
		message.add("profit", new JsonPrimitive(profitSoFar));//add new total cost
		message.add("newQuantity", new JsonPrimitive(getQuantity(new ArrayList<Stock>(allTickers.subList((i - 1), allTickers.size())))));
		message.add("newTotalCost", new JsonPrimitive(accumulate(new ArrayList<Stock>(allTickers.subList((i - 1), allTickers.size())))));
		message.add("quantity", new JsonPrimitive(quantity));
		message.add("ticker", new JsonPrimitive(ticker.toUpperCase()));
		return message;
	}
	
	/*
	if (i >= allTickers.size()) return null;//if there are still stocks left to sell
	
	while ((allTickers.get(i).quantity != 0) && (quantity != 0)) {//sell until you are out of stock or have sold all you need to
		quantity -=1;
		allTickers.get(i).quantity -= 1;
		profitSoFar += allTickers.get(i).price;
		allTickers.get(i).altered = true;
	}
	*/
	
	JsonObject getCompanyPrice(String ticker) {
		try {
		JsonObject company = SearchHandler.getCompany(ticker, true);
		System.out.println(company.toString());
		return company;
		}
		catch (Exception e) {
			return null;
		}
	}
	
	double accumulate(ArrayList<Stock> stocks) {
		double p = 0;
		for (Stock i: stocks) {
			p += (i.purchasePrice*i.quantity);
		}
		return p;
	}
	
	void sendFailMessage(HttpServletResponse resp) throws IOException {
		JsonObject response = new JsonObject();
		response.add("status", new JsonPrimitive(2));
		Util.writeMessage(resp, response.toString());
	}
	
	double getTotalCost(ArrayList<Stock> stocks) {
		if (stocks.size() == 0) return 0;
		double cost = 0;
		Stock prev = stocks.get(0);
		double price;
		try {
		price = getCompanyPrice(prev.ticker).get("askPrice").getAsDouble();
		}
		catch (Exception e) {
			price = 0;// TODO: handle exception
		}
		for (Stock i : stocks) {
			if (prev.ticker.toUpperCase().equals(i.ticker.toUpperCase())) {
				if (price == 0) {//do nothing just use buy price
				}
				else {
					i.price = price;
					try {
						conn.alterStockPrice(i.stockId, price);
					} catch (Exception e) {
						// TODO: handle exception
						System.out.println("messed up trying to change stock price to latest ask");
					}
				}
			}
			else {
				try {
				price = getCompanyPrice(i.ticker).get("askPrice").getAsDouble();
				i.price = price;
				try {
					conn.alterStockPrice(i.stockId, price);
				} catch (Exception e) {
					// TODO: handle exception
					System.out.println("messed up trying to change stock price to latest ask");
				}
				}
				catch(Exception e) {
					//do nothing, market is closed
				}
			}
			cost += (i.price * i.quantity);
			prev = i;
		}
		return cost;
	}
	
	int getQuantity(ArrayList<Stock> allTickers) {
		int c = 0;
		for (Stock i: allTickers) {
			c += i.quantity;
		}
		return c;
	}
	
	//write response function with new balance status new quantity with ticker to update frontend
}
