package model;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "config")
public class Config implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5379587259720579002L;
	
	@DatabaseField(columnName="id", generatedId=true)
	private int id;

	@DatabaseField(columnName = "lpfrUrl", canBeNull = true)
	private String lpfrUrl;

	@DatabaseField(columnName = "pin", canBeNull = true)
	private String pin;

	@DatabaseField(columnName = "printReceipt", canBeNull = true)
	private int printReceipt;

	@DatabaseField(columnName = "printerName", canBeNull = true)
	private String printerName;

	@DatabaseField(columnName = "paperWidth", canBeNull = true)
	private int paperWidth;

	@DatabaseField(columnName = "numOfPrintCopies", canBeNull = true)
	private int numOfPrintCopies;

	public String getLpfrUrl() {
		return lpfrUrl;
	}

	public void setLpfrUrl(String lpfrUrl) {
		this.lpfrUrl = lpfrUrl;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public int getPrintReceipt() {
		return printReceipt;
	}

	public void setPrintReceipt(int printReceipt) {
		this.printReceipt = printReceipt;
	}

	public String getPrinterName() {
		return printerName;
	}

	public void setPrinterName(String printerName) {
		this.printerName = printerName;
	}

	public int getPaperWidth() {
		return paperWidth;
	}

	public void setPaperWidth(int paperWidth) {
		this.paperWidth = paperWidth;
	}

	public int getNumOfPrintCopies() {
		return numOfPrintCopies;
	}

	public void setNumOfPrintCopies(int numOfPrintCopies) {
		this.numOfPrintCopies = numOfPrintCopies;
	}

	public Config() {
		// TODO Auto-generated constructor stub
	}

}
