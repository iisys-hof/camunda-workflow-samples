package de.hofuniversity.iisys.camunda.workflows.mail;

import java.util.Locale;
import java.util.ResourceBundle;

public class MailConfig
{
	private static final String PROPERTIES = "mail-delegates";
	
	private static MailConfig INSTANCE = null;
	
	private final ResourceBundle fProps;
	
	public static MailConfig getInstance()
	{
		if(INSTANCE == null)
		{
			INSTANCE = new MailConfig();
		}
		
		return INSTANCE;
	}
	
	private MailConfig()
	{
		final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        fProps = ResourceBundle.getBundle(PROPERTIES, Locale.getDefault(), loader);
	}
	
	public String getProp(String key)
	{
		return fProps.getString(key);
	}
}
