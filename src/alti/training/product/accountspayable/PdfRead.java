package alti.training.product.accountspayable;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;

public class PdfRead {

	final static String userName = "bheem.karthik007@gmail.com";// change accordingly
	final static String password = "Shan@1234";// change accordingly

	public static void main(String args[]) throws IOException {
		final String fileName = downloadAttachments();
		Connection dbConnection = dbConnect("jdbc:mysql://localhost:3306/accounts_payable?serverTimezone=UTC", "root", "");
		parsePdfAndStore(dbConnection,fileName);
		
	}

	private static Connection dbConnect(String db_connect_string, String db_userid, String db_password) {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(db_connect_string, db_userid, db_password);
			// System.out.println("Connected to Database");
			return conn;
		} catch (Exception dbException) {
			System.out.println(dbException);
			System.out.println("Connection Failed");
			System.exit(0);
			return null;
		}
	}

	private static String downloadAttachments() {
		String pop3Host = "pop.gmail.com";// change accordingly
		String mailStoreType = "pop3";
		ReceiveEmailWithAttachment download = new ReceiveEmailWithAttachment();
		// call receiveEmail
		 return(download.receiveEmail(pop3Host, mailStoreType, userName, password));
	}

	private static void parsePdfAndStore(Connection dbConnection, String fileName) throws IOException {
		ParseInvoice parse = new ParseInvoice();
		try {
			parse.readPdf(dbConnection, fileName);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	 static void sendEmail() {
		SendEmailNotification sendMail = new SendEmailNotification();
		sendMail.send(userName, password, userName, "Accounts Payable", "Invoice Approved.");
		// sendMail.sed("from","password","to", "Sub", "Content")
	}
}