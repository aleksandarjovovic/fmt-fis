package service;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

@Path("/printers")
public class Printers {
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getConfig(String jsonParams, @Context HttpHeaders headers){
		
		
		JSONObject printersObject = new JSONObject();
		JSONArray printersArray = new JSONArray();
		
		PrintService printService[] = PrintServiceLookup
				.lookupPrintServices(null, null);

		for (int i = 0; i < printService.length; i++) {
			printersArray.put(i, printService[i].getName());				
		}
		
		printersObject.put("printers", printersArray);
		return Response.status(200).entity(printersObject.toString()).build();
	}

	public Printers() {
		// TODO Auto-generated constructor stub
	}

}
