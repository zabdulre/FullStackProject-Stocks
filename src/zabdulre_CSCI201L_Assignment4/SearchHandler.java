package zabdulre_CSCI201L_Assignment4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.http.HttpConnectTimeoutException;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/SearchHandler")
public class SearchHandler extends HttpServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static Gson gson = new Gson();
	SqlUtil conn = new SqlUtil();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String ticker = req.getParameter("ticker");
		JsonObject j = getCompany(ticker, conn.isLoggedIn(req));
		j.addProperty("loggedIn", (conn.isLoggedIn(req)? 1:2));
		System.out.println(j.toString());
		Util.writeMessage(resp, j.toString());
		System.out.println("did a post");
	}
	
	@SuppressWarnings("deprecation")
	static JsonObject getCompanyDailyQuote(String ticker) {
		Date date = new Date();
		JsonParser response = null;
		JsonObject h = new JsonObject();
		String url = "https://api.tiingo.com/tiingo/daily/";
		String token = "7a926eef4b934df46d4f532d8cdccbc241e2670b";
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		url = url + ticker;
		url = url + "/prices?&token=";
		url = url + token;
		try {
		URL toOpen = new URL(url);
		HttpsURLConnection connector = (HttpsURLConnection) toOpen.openConnection();
		connector.setRequestMethod("GET");
		response = new JsonParser();
		String buffer = new String();
		String msg = new String();
		BufferedReader reading = new BufferedReader(new InputStreamReader(connector.getInputStream()));
		while ((buffer = reading.readLine()) != null) msg += buffer;
		//InputStreamReader stream = new InputStreamReader(connector.getInputStream());
		//h = response.parse(stream).getAsJsonObject();
		JsonElement y = response.parse(msg);
		if (y.isJsonArray())
		h = y.getAsJsonArray().get(0).getAsJsonObject();
		else {
			h = y.getAsJsonObject();
		}
		}
		catch(HttpConnectTimeoutException s) {
			System.out.println("Could not connect to Tiingo, please check your internet connection or refresh your API token");
			return null;
		}
		catch (Exception e) {
			System.out.println("Error in retrieving company " + ticker + " from Tiingo");
			return null;
		}
		return h;
	}
	
	
	static JsonObject getCompanyTicker(String ticker) {
		Date date = new Date();
		JsonParser response = null;
		JsonObject h = new JsonObject();
		String url = "https://api.tiingo.com/tiingo/daily/";
		String token = "7a926eef4b934df46d4f532d8cdccbc241e2670b";
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		url = url + ticker;
		url = url + "?token=";
		url = url + token;
		try {
		URL toOpen = new URL(url);
		HttpsURLConnection connector = (HttpsURLConnection) toOpen.openConnection();
		connector.setRequestMethod("GET");
		response = new JsonParser();
		String buffer = new String();
		String msg = new String();
		BufferedReader reading = new BufferedReader(new InputStreamReader(connector.getInputStream()));
		while ((buffer = reading.readLine()) != null) msg += buffer;
		//InputStreamReader stream = new InputStreamReader(connector.getInputStream());
		//h = response.parse(stream).getAsJsonObject();
		JsonElement y = response.parse(msg);
		System.out.println(y.toString());
		if (y.isJsonArray())
		h = y.getAsJsonArray().get(0).getAsJsonObject();
		else {
			h = y.getAsJsonObject();
		}
		}
		catch(HttpConnectTimeoutException s) {
			System.out.println("Could not connect to Tiingo, please check your internet connection or refresh your API token");
			return null;
		}
		catch (Exception e) {
			System.out.println("Error in retrieving company " + ticker + " from Tiingo");
			return null;
		}
		return h;
	}
	
	static JsonObject getCompanyLatestPrice(String ticker) {
		Date date = new Date();
		JsonParser response = null;
		JsonObject h = new JsonObject();
		String url = "https://api.tiingo.com/iex/";
		String token = "7a926eef4b934df46d4f532d8cdccbc241e2670b";
		//DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		url = url + ticker;
		url = url + "?token=";
		url = url + token;
		try {
		URL toOpen = new URL(url);
		HttpsURLConnection connector = (HttpsURLConnection) toOpen.openConnection();
		connector.setRequestMethod("GET");
		response = new JsonParser();
		String buffer = new String();
		String msg = new String();
		BufferedReader reading = new BufferedReader(new InputStreamReader(connector.getInputStream()));
		while ((buffer = reading.readLine()) != null) msg += buffer;
		//InputStreamReader stream = new InputStreamReader(connector.getInputStream());
		//h = response.parse(stream).getAsJsonObject();
		JsonElement y = response.parse(msg);
		if (y.isJsonArray())
		h = y.getAsJsonArray().get(0).getAsJsonObject();
		else {
			h = y.getAsJsonObject();
		}
		}
		catch(HttpConnectTimeoutException s) {
			System.out.println("Could not connect to Tiingo, please check your internet connection or refresh your API token");
			return null;
		}
		catch (Exception e) {
			System.out.println("Error in retrieving company " + ticker + " from Tiingo");
			return null;
		}
		return h;
	}
	
	static JsonObject getCompany(String ticker, boolean isLoggedIn) {
		String a = new String(); 
		String b = new String();
		String c = new String();
		String holder = new String();
		JsonObject aye = null;
		boolean exceptionFlag = false;
		try {
		if (!isLoggedIn) a = getCompanyDailyQuote(ticker).toString();
		else {
			a = getCompanyLatestPrice(ticker).toString();
		}
		c = getCompanyTicker(ticker).toString();
		holder = addTwoJsons(a, c);
		System.out.println(holder);
		}
		catch(Exception e) {
			exceptionFlag = true;
			System.out.println("stock market trouble getting stock");
		}
		aye = gson.fromJson(holder, JsonObject.class);
		if (isLoggedIn) {
		if ((aye.get("askSize") == JsonNull.INSTANCE) || (aye.get("askPrice").getAsDouble() == 0 ) || (aye.get("bidPrice").getAsDouble() == 0 ) || (aye.get("bidSize") == JsonNull.INSTANCE)) aye.addProperty("isMarketOpen", false);
		else {
			aye.addProperty("isMarketOpen", true);
		}
		}
		return aye;
	}
	
	static String addTwoJsons(String a, String b) {
		b = b.substring(1, b.length() - 1);//take out brackets
		a = a.substring(0, a.length() - 1);//take out end bracket
		a += ", ";
		a += b;
		a += "}";
		return a;
	}
}
