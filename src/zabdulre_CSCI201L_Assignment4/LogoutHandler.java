package zabdulre_CSCI201L_Assignment4;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/LogoutHandler")
public class LogoutHandler extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	SqlUtil conn = new SqlUtil();
	Gson gson = new Gson();
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		try {
		conn.endSession(req);
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.println("exception in sign out handler, maybe the user was never signed in");
		}
		finally {
			JsonObject message = new JsonObject();
			message.add("loggedOut", new JsonPrimitive((conn.isLoggedIn(req) == true)? 1: 2));
			Util.writeMessage(resp, gson.toJson(message));
		}
	}
	
}
