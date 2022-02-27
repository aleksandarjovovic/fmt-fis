package util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.Size2DSyntax;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaSize;

public class Printer implements Printable{
	
	private String printerName;

	public Printer() {
		// TODO Auto-generated constructor stub
	}

	public Printer(String printerName) {
		this.printerName = printerName;
	}

	public List<String> getPrinters() {

		DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
		PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();

		PrintService printServices[] = PrintServiceLookup.lookupPrintServices(
				flavor, pras);

		List<String> printerList = new ArrayList<String>();
		for (PrintService printerService : printServices) {
			printerList.add(printerService.getName());
		}

		return printerList;
	}

	public PrintService getDefaultPrintService() {
		return PrintServiceLookup.lookupDefaultPrintService();
	}

	@Override
	public int print(Graphics g, PageFormat pf, int page)
			throws PrinterException {
		if (page > 0) { /* We have only one page, and 'page' is zero-based */
			return NO_SUCH_PAGE;
		}

		/*
		 * User (0,0) is typically outside the imageable area, so we must
		 * translate by the X and Y values in the PageFormat to avoid clipping
		 */
		Graphics2D g2d = (Graphics2D) g;
		g2d.translate(pf.getImageableX(), pf.getImageableY());
		/* Now we perform our rendering */

		g.setFont(new Font(Font.SERIF, Font.BOLD, 50));
		g.setColor(Color.black);
		g.drawString("Hello world !", 100, 100);

		return PAGE_EXISTS;
	}
	
	
	public PrintService getPrinter(){
		
		PrintService printService[] = PrintServiceLookup
				.lookupPrintServices(null, null);

		for (int i = 0; i < printService.length; i++) {
			if (printService[i].getName().contains(printerName)) {
				return printService[i];
			}
		}
		return getDefaultPrintService();
	}
	
	
	public PrintRequestAttributeSet getAset(){

		try {

			Properties prop = new Properties();
			String path = System.getProperty("user.home")
					+ "\\fmt-fis\\config.properties";
			InputStream inputStream = new FileInputStream(path);
			prop.load(inputStream);			
			int width = Integer.parseInt(prop.getProperty("paperWidth", "57"));
			PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
			aset.add(MediaSize.findMedia(width, width, Size2DSyntax.MM));
			aset.add(new Copies(1));
			return aset;

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void printString(String text) {

		DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
		PrintRequestAttributeSet aset = getAset();
		PrintService service = getPrinter();
		DocPrintJob job = service.createPrintJob();
		try {

			byte[] bytes;
			bytes = text.getBytes();
			Doc doc = new SimpleDoc(bytes, flavor, null);
			job.print(doc, aset);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void printInputStream(InputStream is) {

		DocFlavor flavor = DocFlavor.INPUT_STREAM.GIF;
		PrintRequestAttributeSet aset = getAset();
		PrintService service = getPrinter();
		DocPrintJob job = service.createPrintJob();
		Doc doc = new SimpleDoc(is, flavor, null);

		try {
			job.print(doc, aset);
		} catch (PrintException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void printBytes(byte[] bytes) {

		DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
		PrintRequestAttributeSet aset =getAset();
		PrintService service = getPrinter();
		DocPrintJob job = service.createPrintJob();
		try {
			Doc doc = new SimpleDoc(bytes, flavor, null);
			job.print(doc, aset);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
