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
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import model.Config;

import org.json.JSONArray;
import org.json.JSONObject;

import util.DbUtil;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;

@Path("/taxRates")
public class TaxRates {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTaxRates() {

		URL url;
		try {

			ConnectionSource dbConnection = DbUtil.getConnSource();
			Dao<Config, String> configDao = DaoManager.createDao(dbConnection,
					Config.class);

			Config conf = configDao.queryForFirst();
			
			String[] lpfrUrlArray = conf.getLpfrUrl().split(";");
			String[] lpfrPins = conf.getPin().split(";");
			String lpfrActiveUrl = "";
			
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
					break;
				}
			}

			if (lpfrActiveUrl.equalsIgnoreCase("")) {
				return Response.status(400).entity("Bed request").build();
			}

			String urlS = lpfrActiveUrl + "status";
			url = new URL(urlS);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setRequestMethod("GET");
			conn.setDoOutput(true);

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				conn.disconnect();
				return Response.status(400).entity(conn.getContent()).build();
				
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			StringBuffer response = new StringBuffer();
			String responseLine = null;
			while ((responseLine = br.readLine()) != null) {
				response.append(responseLine.trim());
			}

			JSONObject jsonResponse = new JSONObject(response.toString());

			JSONObject myResponse = new JSONObject();

			JSONArray allTaxRatesArray = new JSONArray(
					jsonResponse.getJSONArray("allTaxRates"));
			myResponse.put("allTaxRates", allTaxRatesArray);

			myResponse.put("currentTaxRates",
					jsonResponse.getJSONObject("currentTaxRates"));

			return Response.status(200).entity(myResponse.toString()).build();

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

		return Response.status(404).build();
	}

	public TaxRates() {
		// TODO Auto-generated constructor stub
	}

}
