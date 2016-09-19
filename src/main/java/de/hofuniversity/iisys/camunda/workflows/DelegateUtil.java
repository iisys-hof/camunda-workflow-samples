package de.hofuniversity.iisys.camunda.workflows;

import java.util.Map;

import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.impl.identity.Authentication;

import de.hofuniversity.iisys.camunda.workflows.nuxeo.NuxeoUtility;
import de.hofuniversity.iisys.camunda.workflows.nuxeo.ProcessConfig;
import de.hofuniversity.iisys.cmis.OpenCMISClient;

public class DelegateUtil {
	
	public static NuxeoUtility getNuxeoUtil()
	{
		// get either default or specialized authentication
        Map<String, String> config = ProcessConfig.getInstance().getConfiguration();
        String user = config.get(ProcessConfig.DEBUG_USER);
        String password = config.get(ProcessConfig.DEBUG_PASSWORD);
        
        // get password for currently authenticated user if possible
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		IdentityService identityService = processEngine.getIdentityService();
		Authentication auth = identityService.getCurrentAuthentication();
		if(auth != null) {
			if(auth.getUserId() != null
				&& !auth.getUserId().isEmpty())
			{
				user = auth.getUserId();
				password = config.get("nuxeo.users.passwords." + user);
			}
		}

        // read connection parameters
        String nuxeoUrl = config.get(ProcessConfig.NUXEO_URL);

        return new NuxeoUtility(nuxeoUrl, user, password);
	}
	
	public static OpenCMISClient getCMISClient()
	{
		Map<String, String> config = ProcessConfig.getInstance().getConfiguration();
        String nuxeoUrl = config.get(de.hofuniversity.iisys.camunda.workflows.cmis.ProcessConfig.CMIS_URL);
        String user = config.get(de.hofuniversity.iisys.camunda.workflows.cmis.ProcessConfig.DEBUG_USER);
        String password = config.get(de.hofuniversity.iisys.camunda.workflows.cmis.ProcessConfig.DEBUG_PASSWORD);
        String bindingType = config.get(de.hofuniversity.iisys.camunda.workflows.cmis.ProcessConfig.BINDING_TYPE);
        String repository = config.get(de.hofuniversity.iisys.camunda.workflows.cmis.ProcessConfig.REPOSITORY);
        
        // get password for currently authenticated user if possible
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		IdentityService identityService = processEngine.getIdentityService();
		Authentication auth = identityService.getCurrentAuthentication();
		if(auth != null) {
			if(auth.getUserId() != null
				&& !auth.getUserId().isEmpty())
			{
				user = auth.getUserId();
				password = config.get("nuxeo.users.passwords." + user);
			}
		}
		
		return new OpenCMISClient(nuxeoUrl, user, password, repository, bindingType);
	}
}
