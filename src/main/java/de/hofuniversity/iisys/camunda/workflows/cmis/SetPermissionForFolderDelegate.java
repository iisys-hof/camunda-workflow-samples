package de.hofuniversity.iisys.camunda.workflows.cmis;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import de.hofuniversity.iisys.camunda.workflows.DelegateUtil;
import de.hofuniversity.iisys.camunda.workflows.IisysConstants;
import de.hofuniversity.iisys.cmis.OpenCMISClient;

public class SetPermissionForFolderDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution exec) throws Exception {
        // get context variables
        String docId = exec.getVariable(IisysConstants.FOLDER_ID).toString();
        // TODO get the permission to set...

        // get client
        OpenCMISClient util = DelegateUtil.getCMISClient();

        CmisObject doc = util.getObject(docId);
        util.delete(doc);
        util.shutdown();
    }
}
