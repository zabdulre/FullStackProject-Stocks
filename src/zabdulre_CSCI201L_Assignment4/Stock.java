package zabdulre_CSCI201L_Assignment4;

public class Stock {
	public int quantity = 0;
	public int stockId = 0;
	public double price = 0;
	public double purchasePrice = 0;
	String ticker;
	public volatile boolean altered = false;
	Stock(int q, int s, double p, double pp){
		quantity = q;
		stockId = s;
		price = p;
		purchasePrice = pp;
	}
	Stock(int q, int s, double p, String t, double pp){
		quantity = q;
		stockId = s;
		price = p;
		ticker = t;
		purchasePrice = pp;
	}
	Stock(int q, String s, double p, double pp){
		quantity = q;
		ticker = new String(s);
		price = p;
		purchasePrice = pp;
	}
}
