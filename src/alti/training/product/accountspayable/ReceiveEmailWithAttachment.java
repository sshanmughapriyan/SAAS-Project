package alti.training.product.accountspayable;

import java.io.File;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.SearchTerm;

//import org.apache.commons.io.IOUtils;

/**
 * This class is used to receive email with attachment.
 * 
 * @author codesjava
 */
public class ReceiveEmailWithAttachment extends PdfRead {
	
	private Address[] toAddresses;

	protected String receiveEmail(String pop3Host, String mailStoreType, String userName, String password) {
		// Set properties
		int count=0;
		int temp = 0;
		String fileName = null;
		Properties props = new Properties();
		props.put("mail.store.protocol", "pop3");
		props.put("mail.pop3.host", pop3Host);
		props.put("mail.pop3.port", "995");
		props.put("mail.pop3.starttls.enable", "true");

		// Get the Session object.
		Session session = Session.getInstance(props);

		try {
			// Create the POP3 store object and connect to the pop store.
			Store store = session.getStore("pop3s");
			store.connect(pop3Host, userName, password);

			// Create the folder object and open it in your mailbox.
			Folder emailFolder = store.getFolder("INBOX");
			emailFolder.open(Folder.READ_ONLY);
        
			SearchTerm condition = new SearchTerm() {
				

				@Override
				public boolean match(Message message) {
					try {
						if(message.getSubject().contains("Invoice")) {
							return true;
						} 
						
					}catch (Exception e) {
						e.printStackTrace();
						// TODO: handle exception
					}
					// TODO Auto-generated method stub
					return false;
				}
			};
			// Retrieve the messages from the folder object.
			Message[] messages = emailFolder.search(condition );
			System.out.println("Total Message" + messages.length);

			// Iterate the messages
			for (int i = 0; i < messages.length; i++) {
				Message message = messages[i];
				toAddresses = message.getRecipients(Message.RecipientType.TO);
				System.out.println("---------------------------------");
				System.out.println("Details of Email Message " + (i + 1) + " :");
				System.out.println("Subject: " + message.getSubject());
				System.out.println("From: " + message.getFrom()[0]);
				String from =  message.getFrom()[0].toString();
				// Iterate recipients
//				System.out.println("To: ");
//				for (int j = 0; j < toAddress.length; j++) {
//					System.out.println(toAddress[j].toString());
//				}

				if(from.contains("bheem.karthik007@gmail.com")) {
					String attach = "";
				
				// Iterate multiparts
				Multipart multipart = (Multipart) message.getContent();
				for (int k = 0; k < multipart.getCount(); k++) {
					BodyPart bodyPart = multipart.getBodyPart(k);
					if (bodyPart.getDisposition() != null
							&& bodyPart.getDisposition().equalsIgnoreCase(Part.ATTACHMENT)) {
						System.out.println("file name " + bodyPart.getFileName());
						System.out.println("size " + bodyPart.getSize());
						System.out.println("content type " + bodyPart.getContentType());
						InputStream stream = (InputStream) bodyPart.getInputStream();
						File targetFile = new File("D:\\" + bodyPart.getFileName());
						fileName = bodyPart.getFileName();
						attach = attach+fileName;
						java.nio.file.Files.copy(stream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
						// IOUtils.closeQuietly(stream);
					}
				}
				count++;
				temp = temp+attach.length();
			}

			// close the folder and store objects
			}
			emailFolder.close(false);
			store.close();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
			System.exit(0);
		} catch (MessagingException e) {
			e.printStackTrace();
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		finally {
			
			if(count>0) {
				if(temp==0) {
					System.out.println("Attachments not found");
					System.exit(0);
				}
				
			}
			else {
				System.out.println("Mail not found");
				System.exit(0);
			}
		}
		return fileName;
	}
}