package zabdulre_CSCI201L_Assignment4;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/SignUpHandler")
public class SignUpHandler extends HttpServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	SqlUtil conn = new SqlUtil();
	Gson gson = new Gson();
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		InputStream p = req.getInputStream();
		JsonObject j = gson.fromJson(new String(p.readAllBytes()), JsonObject.class);
		System.out.println(gson.fromJson(j, JsonObject.class) + "by sign up handler");
		PrintWriter w = resp.getWriter();
		JsonObject message = new JsonObject();
		try {
			if (conn.isExistingEmail(j.get("email").getAsString())) {
				message.addProperty("status", 2);
				message.addProperty("message", "This email is in use");
			}
			else if (conn.isExistingUsername(j.get("username").getAsString())) {
				message.addProperty("status", 2);
				message.addProperty("message", "This username is in use");
			}
			else {
				conn.createNewUser(j.get("email").getAsString(),j.get("username").getAsString(), j.get("password").getAsString(), 50000);
				conn.createSession(conn.idByEmail(j.get("email").getAsString()), req);
				message.addProperty("status", 1);
				message.addProperty("message", "Account created");
			}
			System.out.println((gson.toJson(message)));
			Util.writeMessage(resp, gson.toJson(message));
			return;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
