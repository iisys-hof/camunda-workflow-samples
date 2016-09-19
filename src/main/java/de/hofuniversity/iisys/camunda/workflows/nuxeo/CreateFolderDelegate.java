package de.hofuniversity.iisys.camunda.workflows.nuxeo;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.nuxeo.ecm.automation.client.model.Document;

import de.hofuniversity.iisys.camunda.workflows.DelegateUtil;
import de.hofuniversity.iisys.camunda.workflows.IisysConstants;

public class CreateFolderDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution exec) throws Exception {

        String parentFolderId = exec.getVariable(IisysConstants.PARENT_FOLDER_ID).toString();
        String folderName = exec.getVariable(IisysConstants.NEW_FOLDER_NAME).toString();

        // get document
        NuxeoUtility util = DelegateUtil.getNuxeoUtil();
        Document document = util.createFolder(folderName, parentFolderId);

        // refresh local variables
        // PropertyMap props = document.getProperties();

        exec.setVariable(IisysConstants.NEW_FOLDER_ID, document.getId());
        util.shutdown();
    }
}
