package de.hofuniversity.iisys.camunda.workflows.cmis;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import de.hofuniversity.iisys.camunda.workflows.DelegateUtil;
import de.hofuniversity.iisys.camunda.workflows.IisysConstants;
import de.hofuniversity.iisys.cmis.OpenCMISClient;

public class MoveDocumentDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution exec) throws Exception {
        // get context variables
        String docId = exec.getVariable(IisysConstants.DOCUMENT_ID).toString();
        String currFolderId = exec.getVariable(IisysConstants.CURRENT_FOLDER_ID).toString();
        String newFolderId = exec.getVariable(IisysConstants.NEW_FOLDER_ID).toString();

        // get document
        OpenCMISClient util = DelegateUtil.getCMISClient();
        util.moveDocument(docId, currFolderId, newFolderId);
        util.shutdown();
    }
}
