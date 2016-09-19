package de.hofuniversity.iisys.camunda.workflows.mail;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.identity.ReadOnlyIdentityProvider;

import de.hofuniversity.iisys.camunda.workflows.IisysConstants;

public class GetStarterMailSender extends GetMailSender
{

	@Override
	public void execute(DelegateExecution exec) throws Exception
	{
		// get user who started the workflow from variable
		String user = (String) exec.getVariable(IisysConstants.WORKFLOW_INITIATOR);
		String mail = null;
		
		if(user != null)
		{
			// retrieve mail from user storage
			ReadOnlyIdentityProvider idProv =
		            Context.getCommandContext().getReadOnlyIdentityProvider();
			
			User authUser = idProv.findUserById(user);
            
            if(authUser != null)
            {
                mail = authUser.getEmail();
            }
            
            // set variables if available
            if(user != null && mail != null)
    		{
    			super.execute(exec);
    		}
		}
		
		
		// fall back to authenticated user if variables are not set
		if(user == null || mail == null)
		{
			super.execute(exec);
		}
	}

}
