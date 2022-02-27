package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import model.Config;

import org.json.JSONObject;

import util.DbUtil;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;



@Path("/status")
public class Status {
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStatus(){
		
		URL url;
		try {
            
            ConnectionSource dbConnection = DbUtil.getConnSource();
            Dao<Config, String> configDao = DaoManager.createDao(dbConnection, Config.class);
            
            Config conf = configDao.queryForFirst();
            
            String[] lpfrUrlArray = conf.getLpfrUrl().split(";");
            String[] lpfrPins = conf.getPin().split(";");
            String lpfrActiveUrl = "";
            String lpfrActivePin = "";
            
            dbConnection.close();
            
            HttpURLConnection lpfrCheck = null;
            
            
            
            for (int i = 0; i < lpfrUrlArray.length; i++) {
				URL urlCheck = new URL(lpfrUrlArray[i] + "pin");
				lpfrCheck = (HttpURLConnection) urlCheck.openConnection();
				lpfrCheck.setRequestMethod("POST");
				lpfrCheck.setRequestProperty("Accept", "application/json");
				lpfrCheck.setRequestProperty("Content-Type",
						"application/json;charset=utf-8");
				lpfrCheck.setDoOutput(true);
				OutputStream os = lpfrCheck.getOutputStream();
				os.write(lpfrPins[i].getBytes("utf-8"));
				os.flush();
				os.close();
				BufferedReader br = new BufferedReader(new InputStreamReader(
						lpfrCheck.getInputStream()));

				String responseMessage = "";

				String strCurrentLine;
				while ((strCurrentLine = br.readLine()) != null) {
					responseMessage += strCurrentLine;
				}
				lpfrCheck.disconnect();

				if (responseMessage.equalsIgnoreCase("\"0100\"")) {
					lpfrActiveUrl = lpfrUrlArray[i];
					lpfrActivePin = lpfrPins[i];
					break;
				}
			}          
            
            String urlS = lpfrActiveUrl +"pin";
			url = new URL(urlS);
			
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
						
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
			conn.setDoOutput(true);	
			
			OutputStream os = conn.getOutputStream();
			os.write(lpfrActivePin.getBytes());
			os.flush();
			os.close();
			
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				JSONObject jsonResponse = new JSONObject();
				jsonResponse.put("message", "LPFR is not ready");	
				conn.disconnect();
				return Response.status(404).entity(jsonResponse.toString()).build();
			}
			
			JSONObject jsonResponse = new JSONObject();
			
			
			BufferedReader   br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			
			
			String responseMessage = "";
			
			String strCurrentLine;
	        while ((strCurrentLine = br.readLine()) != null) {
	        	responseMessage += strCurrentLine;
	        }
	        conn.disconnect();
	        
			if(responseMessage.equalsIgnoreCase("\"0100\"")){
				jsonResponse.put("message", "OK");
				return Response.status(conn.getResponseCode()).entity(jsonResponse.toString()).build();
			} else{
				jsonResponse.put("message", "PIN Error");
				return Response.status(400).entity(jsonResponse.toString()).build();
			}
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONObject jsonResponse = new JSONObject();
		jsonResponse.put("message", "Something went wrong");	
		return Response.status(500).entity(jsonResponse.toString()).build();
	}
	
	
	@GET
	@Path("/{pin}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response verifyPin(@PathParam("pin") String pin){
		 
		try {
			ConnectionSource dbConnection = DbUtil.getConnSource();
	        Dao<Config, String> configDao = DaoManager.createDao(dbConnection, Config.class);
	        Config conf = configDao.queryForFirst(); 
	        String[] lpfrUrlArray = conf.getLpfrUrl().split(";");
	        
	        dbConnection.close();
            
            HttpURLConnection lpfrCheck = null;
            
            for (int i = 0; i < lpfrUrlArray.length; i++) {
				URL urlCheck = new URL(lpfrUrlArray[i] + "pin");
				lpfrCheck = (HttpURLConnection) urlCheck.openConnection();
				lpfrCheck.setRequestMethod("POST");
				lpfrCheck.setRequestProperty("Accept", "application/json");
				lpfrCheck.setRequestProperty("Content-Type",
						"application/json;charset=utf-8");
				lpfrCheck.setDoOutput(true);
				OutputStream os = lpfrCheck.getOutputStream();
				os.write(pin.getBytes("utf-8"));
				os.flush();
				os.close();
				BufferedReader br = new BufferedReader(new InputStreamReader(
						lpfrCheck.getInputStream()));

				String responseMessage = "";

				String strCurrentLine;
				while ((strCurrentLine = br.readLine()) != null) {
					responseMessage += strCurrentLine;
				}
				lpfrCheck.disconnect();

				if (responseMessage.equalsIgnoreCase("\"0100\"")) {
					return Response.status(200).build();
				}
			}  
            
            return Response.status(400).entity("PIN incorect").build();
	        
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return Response.status(404).build();
         
         
		
		
	}

	public Status() {
		// TODO Auto-generated constructor stub
	}

}
