package zabdulre_CSCI201L_Assignment4;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.sound.midi.Soundbank;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;


@WebServlet("/LoginHandler")
public class LoginHandler extends HttpServlet{
	/**
	 * 
	 */
	Gson gson = new Gson();
	SqlUtil conn = new SqlUtil();
	private static final long serialVersionUID = 1L;
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		/*
		 * 
		 * 
		 * return 1 if the user is logged in, 2 if not
		 * 
		 * 
		 * 
		 * */
		
		JsonObject message = new JsonObject();
		message.add("loggedIn", new JsonPrimitive((conn.isLoggedIn(req) == true)? 1: 2));
		Util.writeMessage(resp, gson.toJson(message));
		System.out.println("sent" + message.toString());
		/*
		System.out.println("received a request");
		System.out.println("Done Sleeping");
		PrintWriter response = resp.getWriter();
		JsonObject message = new JsonObject();
		message.addProperty("status", 1);//say that the user is logged in
		response.print(gson.toJson(message));
		response.flush();
		response.close();
		System.out.println("sent" + message.toString());
		*/
		
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//HttpSession y = req.getSession();
		//y.getId();
		/*
		 * 
		 * 
		 * The user tries to login, will create new session and return true if successful, otherwise return false
		 * 
		 * */
		try {
			InputStream p = req.getInputStream();
			JsonObject j = gson.fromJson(new String(p.readAllBytes()), JsonObject.class);
			System.out.println(j.get("username").getAsString());
			System.out.println(j.get("password").getAsString());
			Util.compareHash(j.get("password").getAsString(), conn.getHashByUsername(j.get("username").getAsString()));
			conn.createSession(Integer.valueOf(conn.idByUsername(j.get("username").getAsString())), req);
		}
		catch(Exception e) {
			JsonObject message = new JsonObject();
			message.add("loggedIn", new JsonPrimitive(2));
			Util.writeMessage(resp, gson.toJson(message));
			System.out.println("a google user may have tried to sign in/a user with this username does not exist");
			System.out.println(message);
			return;
		}
		JsonObject message = new JsonObject();
		message.add("loggedIn", new JsonPrimitive(1));
		Util.writeMessage(resp, gson.toJson(message));
		return;
	}
}
