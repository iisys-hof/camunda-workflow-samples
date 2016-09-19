package de.hofuniversity.iisys.camunda.workflows.cmis;

import java.util.Map;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import de.hofuniversity.iisys.camunda.workflows.DelegateUtil;
import de.hofuniversity.iisys.camunda.workflows.IisysConstants;
import de.hofuniversity.iisys.cmis.OpenCMISClient;

public class DeleteDocumentDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution exec) throws Exception {
        // get context variables
        String docId = exec.getVariable(IisysConstants.DOCUMENT_ID).toString();

        // get document
        OpenCMISClient util = DelegateUtil.getCMISClient();

        CmisObject doc = util.getObject(docId);
        util.delete(doc);
        util.shutdown();
    }
}
