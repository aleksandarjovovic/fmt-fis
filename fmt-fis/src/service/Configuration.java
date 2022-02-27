package service;

import java.sql.SQLException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import model.Config;

import org.json.JSONArray;
import org.json.JSONObject;

import util.DbUtil;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.mysql.cj.xdevapi.JsonArray;


@Path("/config")
public class Configuration {
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getConfig(String jsonParams, @Context HttpHeaders headers){
		
		
		try {
			ConnectionSource dbConnection = DbUtil.getConnSource();
			Dao<Config, String> configDao = DaoManager.createDao(dbConnection, Config.class);
			
			Config conf = configDao.queryForFirst();
			
			String[] lpfrs = conf.getLpfrUrl().split(";");
			String[] pins = conf.getPin().split(";");
			int printReceipt = conf.getPrintReceipt();
			String printerName = conf.getPrinterName();
			int paperWidth = conf.getPaperWidth();
			int numOfPrintCopies = conf.getNumOfPrintCopies();
			
			JSONObject configuration = new JSONObject();
			
			JSONArray lpfrArray = new JSONArray();
			JSONArray pinArray = new JSONArray();
			
			for(int i=0;i<lpfrs.length;i++){
				lpfrArray.put(i, lpfrs[i]);
				pinArray.put(i, pins[i]);				
			}
			
			configuration.put("lpfrUrl", lpfrArray);
			configuration.put("pin", pinArray);
			configuration.put("printReceipt", printReceipt);
			configuration.put("printerName", printerName);
			configuration.put("paperWidth", paperWidth);
			configuration.put("numOfPrintCopies", numOfPrintCopies);
			
			return Response.status(200).entity(configuration.toString()).build();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return Response.status(404).build();
		
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response setConfig(String jsonParams, @Context HttpHeaders headers){
		
		
		try {
			ConnectionSource dbConnection = DbUtil.getConnSource();
			Dao<Config, String> configDao = DaoManager.createDao(dbConnection, Config.class);
			
			Config conf = configDao.queryForFirst();
			
			
			
			JSONObject params = new JSONObject(jsonParams);
			
			JSONArray lpfrUrlArray = params.getJSONArray("lpfrUrl");
			System.out.println(lpfrUrlArray);
			String s="";			
			for(int i=0;i<lpfrUrlArray.length();i++){
				s += lpfrUrlArray.get(i) + ";";
			}
			conf.setLpfrUrl(s);
			
			JSONArray pinArray = params.getJSONArray("pin");
			
			System.out.println(pinArray);
			String p="";
			for(int i=0;i<pinArray.length();i++){
				p += pinArray.get(i) + ";";
			}
			conf.setPin(p);
			
			conf.setPrinterName(params.getString("printerName"));
			conf.setPaperWidth(params.getInt("paperWidth"));
			conf.setNumOfPrintCopies(params.getInt("numOfPrintCopies"));
			conf.setPrintReceipt(params.getInt("printReceipt"));
			
			
			configDao.update(conf);
			
			return Response.status(200).build();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return Response.status(404).build();
		
	}

	public Configuration() {
		// TODO Auto-generated constructor stub
	}

}
