package model;

import java.io.Serializable;
import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="request")
public class Req implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1234196933882768405L;

	@DatabaseField(generatedId=true)
	private long id;
	
	@DatabaseField(columnName="requestId", canBeNull = true)
	private String requestId;
	
	@DatabaseField(columnName="request", dataType=DataType.LONG_STRING, canBeNull = true)
	private String request;
	
	@DatabaseField(columnName="response", dataType=DataType.LONG_STRING, canBeNull = true)
	private String response;
	
	@DatabaseField(columnName="requestDateAndTime", canBeNull = true)
	private Date requestDateAndTime;

	public Req() {
		// TODO Auto-generated constructor stub
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}
	
	public Date getRequestDateAndTime() {
		return requestDateAndTime;
	}

	public void setRequestDateAndTime(Date timestamp) {
		this.requestDateAndTime = timestamp;
	}

}
