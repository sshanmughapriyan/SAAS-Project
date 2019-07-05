package alti.training.product.accountspayable;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import com.mysql.cj.protocol.Resultset;

public class ParsePdf extends PdfRead {
	protected void readPdf(Connection dbConnection, String fileName) throws IOException, SQLException {
		PDDocument document = null;
		Scanner sc = new Scanner(System.in);
		try {
			document = PDDocument.load(new File("D:/" + fileName));
			PDFTextStripperByArea stripper = new PDFTextStripperByArea();
			stripper.setSortByPosition(true);

			Rectangle orderNumber = new Rectangle(0, 150, 140, 10);
			Rectangle invoiceNumber = new Rectangle(0, 130, 140, 10);
			Rectangle customerPo = new Rectangle(250, 150, 180, 10);
			Rectangle invoiceDate = new Rectangle(100, 130, 140, 10);
			Rectangle address = new Rectangle(0, 170, 200, 50);
			Rectangle totalInvoice = new Rectangle(550, 400, 200, 50);
			
			stripper.addRegion("Order Number", orderNumber);
			stripper.addRegion("Invoice Number", invoiceNumber);
			stripper.addRegion("Customer PO", customerPo);
			stripper.addRegion("Invoice Date", invoiceDate);
			stripper.addRegion("Sold To", address);
			stripper.addRegion("Total Invoice", totalInvoice);
			
			PDPage firstPage = document.getPage(11);
			stripper.extractRegions(firstPage);
			
			System.out.println("Order Number " + stripper.getTextForRegion("Order Number"));
			System.out.println("Invoice number " + stripper.getTextForRegion("Invoice Number"));
			System.out.println("Customer PO " + stripper.getTextForRegion("Customer PO"));
			System.out.println("Invoice date " + stripper.getTextForRegion("Invoice Date"));
			System.out.println("Address " + stripper.getTextForRegion("Sold To"));
			System.out.println("Total Invoice " + stripper.getTextForRegion("Total Invoice"));
             
            
			ArrayList<String> list = new ArrayList<String>();
			list.add(stripper.getTextForRegion("Order Number"));
			list.add(stripper.getTextForRegion("Invoice Number"));
			list.add(stripper.getTextForRegion("Customer PO"));
			list.add(stripper.getTextForRegion("Invoice Date"));
			list.add(stripper.getTextForRegion("Sold To"));
			list.add(stripper.getTextForRegion("Total Invoice"));
			
			String invoiceNo = list.get(1);
			
			String query = " insert into invoice_details (order_number, invoice_number,"
					+ " customer_po, invoice_date, sold_to, total_invoice, status)" + " values (?, ?, ?, ?, ?, ?, ?)";

			PreparedStatement preparedStmt = dbConnection.prepareStatement(query);
			preparedStmt.setObject(1, list.get(0));
			preparedStmt.setObject(2, list.get(1));
			preparedStmt.setObject(3, list.get(2));
			preparedStmt.setObject(4, list.get(3));
			preparedStmt.setObject(5, list.get(4));
			preparedStmt.setObject(6, list.get(5));
			preparedStmt.setString(7, "Unapproved");
			preparedStmt.execute();

			int choice =0;
			while(choice<4) {
				System.out.println("1. Approve 2.Display 3.exit");
				
				choice = sc.nextInt();
			switch (choice) {
			case 1:
				updateStatus(dbConnection, invoiceNo);
				
				break;
			case 2:
				display(dbConnection,invoiceNo);
					
				break;
			case 3:
				System.out.println("Exit");
				System.exit(0);
				break;
			default:
				System.out.println("Unapproved");
				System.exit(0);
			}
			}
		} catch (Exception pdfException) {
			System.out.println("Error Occured");
			System.out.println(pdfException);
			System.exit(0);

		} finally {
			if (document != null) {
				document.close();
			}
			sc.close();
			dbConnection.close();
		}
	}

	private static void updateStatus(Connection dbConnection, String invoiceNumber) throws SQLException {
		try {
			String query1 = "Select status from invoice_details where invoice_number = ?";
			PreparedStatement preparedSt = dbConnection.prepareStatement(query1);
			preparedSt.setString(1, invoiceNumber);
		   ResultSet rs = preparedSt.executeQuery();
		   rs.next();
		 
		  
		   if(rs.getString("status").equals("Approved")) {
			   System.out.println(" This Invoice is already approved");
		   }
		   else {
			
			String query = "update invoice_details set status = ? where invoice_number = ?";
			PreparedStatement preparedStmt = dbConnection.prepareStatement(query);
			preparedStmt.setString(1, "Approved");
			preparedStmt.setString(2, invoiceNumber);
			preparedStmt.executeUpdate();
			sendEmail();
	   }
		} catch (Exception e) {
			System.out.println("Error in approving invoice");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	private static void display(Connection dbConnection, String invoiceNumber) throws SQLException {
		try {
			
		String query = "Select * from invoice_details";
		PreparedStatement preparedStmt = dbConnection.prepareStatement(query);
		ResultSet resultSet = preparedStmt.executeQuery();
		
		 while (resultSet.next()) {
		 System.out.println("OrderNumber:"+resultSet.getString("order_number").trim());
		 System.out.println("----------------------------------");
		 System.out.println();
		 System.out.println("InvoiceNumber:"+resultSet.getString("invoice_number").trim());
		 System.out.println("----------------------------------");
		 System.out.println();
		 System.out.println("InvoiceDate:"+resultSet.getString("invoice_date").trim());
		 System.out.println("------------------------------------");
		 System.out.println();
		 System.out.println("Address:"+resultSet.getString("sold_to").trim());
		 System.out.println("------------------------------------");
		 System.out.println();
		 }
		}
		catch (Exception e) {
			System.out.println("Error in displaying");
			e.printStackTrace();
			System.exit(0);
		}
		
	}
}
