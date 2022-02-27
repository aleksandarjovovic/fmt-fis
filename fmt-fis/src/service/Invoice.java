package service;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;

import model.Config;
import model.Req;

import org.json.JSONObject;

import util.DbUtil;
import util.Printer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;



@Path("/invoice")
public class Invoice {
	
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createInvoice(String jsonParams, @Context HttpHeaders headers){
		
		URL url;
				
		try {	

            ConnectionSource dbConnection = DbUtil.getConnSource();
            Dao<Config, String> configDao = DaoManager.createDao(dbConnection, Config.class);
            
            Config conf = configDao.queryForFirst();
            
            int numOfPrintedCopies = conf.getNumOfPrintCopies();
            
            String[] lpfrUrlArray = conf.getLpfrUrl().split(";");
            String[] lpfrPins = conf.getPin().split(";");
            String printerName = conf.getPrinterName();
            String lpfrActiveUrl = "";
            
            dbConnection.close();
      
            HttpURLConnection lpfrCheck = null;
            
            for(int i=0;i<lpfrUrlArray.length;i++){
            	URL urlCheck = new URL(lpfrUrlArray[i] + "pin");
            	lpfrCheck = (HttpURLConnection) urlCheck.openConnection();
            	lpfrCheck.setRequestMethod("POST");
            	lpfrCheck.setRequestProperty("Accept", "application/json");
            	lpfrCheck.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            	lpfrCheck.setDoOutput(true);
            	OutputStream os = lpfrCheck.getOutputStream();
				os.write(lpfrPins[i].getBytes("utf-8"));
				os.flush();
				os.close();
				BufferedReader   br = new BufferedReader(new InputStreamReader(lpfrCheck.getInputStream()));
				
				String responseMessage = "";
				
				String strCurrentLine;
		        while ((strCurrentLine = br.readLine()) != null) {
		        	responseMessage += strCurrentLine;
		        }
		        lpfrCheck.disconnect();
		        
				if(responseMessage.equalsIgnoreCase("\"0100\"")){
					lpfrActiveUrl = lpfrUrlArray[i];
					break;
				}            	
            }
            
            if(lpfrActiveUrl.equalsIgnoreCase("")){
            	return Response.status(400).entity("Bed request").build();
            }
            
            String urlS = lpfrActiveUrl +"invoices";
			url = new URL(urlS);
			
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
			conn.setRequestProperty("RequestId", headers.getRequestHeader("RequestId").get(0));
			conn.setRequestProperty("Accept-Language", headers.getRequestHeader("Accept-Language").get(0));
			conn.setDoOutput(true);		
			
			//validate request
			
			ObjectMapper objectMapper = new ObjectMapper();
			
			JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V201909);			
			InputStream schemaStream = getClass()
					.getClassLoader().getResourceAsStream("schemaInvoice.json");		
			JsonNode json = objectMapper.readTree(jsonParams);			
			JsonSchema schema = schemaFactory.getSchema(schemaStream);			
			Set<ValidationMessage> validationResult = schema.validate(json);
			
			if (validationResult.isEmpty()) {
				
				
				Dao<Req, String> reqDao = DaoManager.createDao(dbConnection, Req.class);
											
				Req req = new Req();
				req.setRequestId(headers.getRequestHeader("RequestId").get(0));
				req.setRequest(jsonParams);
							
				OutputStream os = conn.getOutputStream();
				os.write(jsonParams.getBytes("utf-8"));
				os.flush();
				os.close();
				
				if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
					
					return Response.status(conn.getResponseCode()).entity(conn.getResponseMessage()).build();
				}
				

				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream())); 
				StringBuffer response = new StringBuffer();
				String responseLine = null;
				
				while ((responseLine = br.readLine()) != null) {
					response.append(responseLine.trim());
				}
				conn.disconnect();
				
				
				req.setResponse(response.toString());
				reqDao.create(req);
				dbConnection.close();
				
				JSONObject jsonResponse = new JSONObject(response.toString());
								
				byte[] imageByte = DatatypeConverter.parseBase64Binary(jsonResponse
						.get("verificationQRCode").toString());
				
				if(conf.getPrintReceipt() == 1){
					
					int third = nthLastIndexOf(2, "\r\n", jsonResponse.getString("journal"));
					
/*					for (int i = 0; i < numOfPrintedCopies; i++) {
						Printer printer = new Printer(printerName);
						printer.printBytes(jsonResponse.get("journal").toString().substring(0, third).getBytes("windows-1251"));
						
//						printer.printBytes((jsonResponse.getString("journal")
//								.getBytes("windows-1251")));
						InputStream qrCodeInputStream = new ByteArrayInputStream(
								imageByte);
						printer.printInputStream(qrCodeInputStream);
						
						printer.printBytes(jsonResponse.get("journal").toString().substring(third+1).getBytes("windows-1251"));
						
						printer.printString("\n\n\n\n\n\n\n\n");
						byte[] cutP = new byte[] { 0x1d, 'V', 1 };
						printer.printBytes(cutP);
					}
*/				}				
	
				return Response.status(200).entity(jsonResponse.toString()).build();
							
			}else{
				return Response.status(400).entity("Bed request").build();
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
				
		return Response.status(404).build();
	}


	@GET
	@Path("/{RequestId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getInvoice(@PathParam("RequestId") String requestId){
		
		
		try {
			ConnectionSource dbConnection = DbUtil.getConnSource();		
			Dao<Req, String> reqDao = DaoManager.createDao(dbConnection, Req.class);
			
			QueryBuilder<Req, String> qb = reqDao.queryBuilder();
			
			CloseableIterator<Req> iterator = qb.where().eq("requestId", requestId).iterator();
			
			Req temp = null;
						
			if (iterator.hasNext()) {
				temp = iterator.next();
				return Response.status(200).entity(temp.getResponse()).build();
			} else{
				return Response.status(400).entity("No such record").build();
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return Response.status(404).build();
		
	}
	
	public int nthLastIndexOf(int nth, String ch, String string) {
	    if (nth <= 0) return string.length();
	    return nthLastIndexOf(--nth, ch, string.substring(0, string.lastIndexOf(ch)));
	}
	
	public Invoice() {
		// TODO Auto-generated constructor stub
	}

}
