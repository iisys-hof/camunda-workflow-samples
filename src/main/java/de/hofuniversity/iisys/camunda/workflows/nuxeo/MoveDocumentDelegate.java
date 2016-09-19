package de.hofuniversity.iisys.camunda.workflows.nuxeo;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.nuxeo.ecm.automation.client.model.Document;

import de.hofuniversity.iisys.camunda.workflows.DelegateUtil;
import de.hofuniversity.iisys.camunda.workflows.IisysConstants;

public class MoveDocumentDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution exec) throws Exception {
        // get context variables
        String docId = exec.getVariable(IisysConstants.DOCUMENT_ID).toString();
        String newFolderId = exec.getVariable(IisysConstants.NEW_FOLDER_ID).toString();

        NuxeoUtility util = DelegateUtil.getNuxeoUtil();

        Document document = util.getDocument(docId);
        Document targetFolder = util.getDocument(newFolderId);
        util.moveDocument(document, targetFolder);
        util.shutdown();
    }
}
