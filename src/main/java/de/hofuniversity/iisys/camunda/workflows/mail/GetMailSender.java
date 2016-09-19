package de.hofuniversity.iisys.camunda.workflows.mail;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.identity.ReadOnlyIdentityProvider;

public class GetMailSender implements JavaDelegate
{

	@Override
	public void execute(DelegateExecution execution) throws Exception
	{
		// retrieve mail from user storage
		ReadOnlyIdentityProvider idProv =
	            Context.getCommandContext().getReadOnlyIdentityProvider();
        String authUserId = Context.getCommandContext().getAuthenticatedUserId();
        User authUser = null;
        String userMail = null;
        if(authUserId != null)
        {
            authUser = idProv.findUserById(authUserId);
            
            if(authUser != null)
            {
                userMail = authUser.getEmail();
            }
        }
        
        if(userMail != null)
        {
        	// set sender's ID and e-mail address
        	execution.setVariable("senderId", authUserId);
        	execution.setVariable("senderMail", userMail);
        }
	}
}
