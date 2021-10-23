package zabdulre_CSCI201L_Assignment4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.google.api.client.auth.openidconnect.IdToken;
import com.google.api.client.auth.openidconnect.IdTokenVerifier;
import com.google.api.client.googleapis.apache.v2.GoogleApacheHttpTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.Json;
import com.google.api.client.json.gson.GsonFactory;
import com.google.gson.Gson;
import com.google.gson.JsonObject;


@WebServlet("/GoogleHandler")
public class GoogleHandler extends HttpServlet{
	/**
	 * 
	 */
	IdTokenVerifier verify = new IdTokenVerifier();
	Gson gson = new Gson();
	String clienId = "635096690334-jm439htspa4rq6vlc1gbivahe0dirtmc.apps.googleusercontent.com";
	SqlUtil database = new SqlUtil();
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//super.doPost(req, resp);
		//IdTokenVerifier s = new IdTokenVerifier();
		//s.verify(null);
		
		/*
		 * 
		 * 
		 * 
		 * returns 2 if not able to login, 1 if successful
		 * 
		 * 
		 * */
		System.out.println("received google request");
		JsonObject message = new JsonObject();
		try {
			String body = Util.getBody(req.getReader());
		message.addProperty("status", validateToken(gson.fromJson(body, JsonObject.class), req));//say that the user is logged in
		}
		catch(Exception e) {
			message.addProperty("status", 2);
		}
		finally {
		Util.writeMessage(resp, gson.toJson(message));
		System.out.println("sent" + message.toString());
		}
	}
	
	
	/*
	 * 
	 * 
	 * 
	 * 
	 * This function references code from the actions-on-google github repository.
	 * 
	 * The exact reference can be found at the link below
	 * 
	 * 
	 * https://github.com/actions-on-google/dialogflow-google-sign-in-java/blob/master/src/main/java/com/example/TokenDecoder.java
	 * 
	 * 
	 */

	
	
	int validateToken(JsonObject request, HttpServletRequest req) throws GeneralSecurityException, IOException, SQLException {
		HttpTransport http = GoogleNetHttpTransport.newTrustedTransport();
		GsonFactory j = GsonFactory.getDefaultInstance();
		GoogleIdTokenVerifier g = new GoogleIdTokenVerifier.Builder(http, j).setAudience(Collections.singletonList(clienId)).build();
		GoogleIdToken i = g.verify(request.get("id_token").getAsString());
		System.out.println(i.getPayload());
		if (i == null) return 2;
		else {//successful token
			if(!database.isExistingEmail(i.getPayload().getEmail())) {
				//create new user
				database.createNewGoogleUser(i.getPayload().getEmail(), Util.usernameFromEmail(i.getPayload().getEmail()), 50000);
			}
			database.createSession(Integer.valueOf(database.idByEmail(i.getPayload().getEmail())),req);
		}
		return 1;
	}
	
	
}
