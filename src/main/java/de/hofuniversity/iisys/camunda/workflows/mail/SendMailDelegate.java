package de.hofuniversity.iisys.camunda.workflows.mail;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class SendMailDelegate implements JavaDelegate
{
	private final MailConfig fConfig;
	
	public SendMailDelegate()
	{
		fConfig = MailConfig.getInstance();
	}

	@Override
	public void execute(DelegateExecution execution) throws Exception
	{
	    String to = execution.getVariable("recipientMail").toString();

	    final String senderId = execution.getVariable("senderId").toString();
	    String from = execution.getVariable("senderMail").toString();
	    
	    String text = execution.getVariable("mailBody").toString();
	    String subject = execution.getVariable("mailSubject").toString();

	    // TODO: read from config file
	    String host = fConfig.getProp("mail.delegate.smtp.host");

	    Properties props = new Properties();
	    // connection settings
		props.put("mail.smtp.auth", "true");
//		props.put("mail.smtp.starttls.enable", "false");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", fConfig.getProp("mail.delegate.smtp.port"));
	    
	    // construct session
		Session session = Session.getInstance(props,
		    new javax.mail.Authenticator()
			{
		    	protected PasswordAuthentication getPasswordAuthentication()
		    	{
		    	    // use master account for authentication
		    		String userString = senderId + "*"
		    			+ fConfig.getProp("mail.delegate.master_user.name");
		    		String userPass = fConfig.getProp("mail.delegate.master_user.password");
		            return new PasswordAuthentication(userString, userPass);
		    	}
		    }
		);
	    
	    try
	    {
	        MimeMessage message = new MimeMessage(session);

	        // from and to
	        message.setFrom(new InternetAddress(from));
	        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));

	        // subject
	        message.setSubject(subject);

	        // body
	        message.setText(text);

	        // Send message
	        Transport.send(message);
	     }
	    catch (Exception e)
	    {
	    	e.printStackTrace();
	    }
	}
}
