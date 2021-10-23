package zabdulre_CSCI201L_Assignment4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class Util {
 public static String getBody(BufferedReader read) throws IOException{
		String bod = new String();
		while(read.ready()) {
			bod += read.readLine();
		}
		return bod;
	}
	
 public static String usernameFromEmail(String email) {
	 return email.split("@")[0];
 }
 
 public static void compareHash(String password, String existingHash) throws Exception{//come back to add salt
	 try {
	 password = Util.hash(password);
	 }
	 catch(NoSuchAlgorithmException e){
		 System.out.println("change the hash algorithm please");
	 }
	if(!password.equals(existingHash)) throw new Exception();//come back to add security, right now its kinda pathetic
 }
 
 public static String hash(String password) throws NoSuchAlgorithmException{
	 MessageDigest s = MessageDigest.getInstance("SHA-256");
	 byte[] hash = s.digest(password.getBytes(StandardCharsets.UTF_8));//better not put an emoji in there
	 return new String(hash);
 }
 
 public static void writeMessage(HttpServletResponse resp, String message) throws IOException{
	 PrintWriter print = resp.getWriter();
	 print.write(message);
	 print.flush();//might have to close here
 }
}
