package de.hofuniversity.iisys.camunda.workflows.nuxeo;

import java.util.Properties;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.nuxeo.ecm.automation.client.model.Document;

import de.hofuniversity.iisys.camunda.workflows.DelegateUtil;
import de.hofuniversity.iisys.camunda.workflows.IisysConstants;

public class CreateDocumentDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution exec) throws Exception {
        // get context variables
        String parentFolderId = exec.getVariable(IisysConstants.PARENT_FOLDER_ID).toString();

        // get document
        NuxeoUtility util = DelegateUtil.getNuxeoUtil();

        // Create a Document
        Properties properties = new Properties();
        String titleKey = "dc:title";
        properties.setProperty(titleKey, "testDocName");
        String descKey = "dc:description";
        properties.setProperty(descKey, "some Description");
        Document document = util.createDocument(util.getDocument(parentFolderId), "Note", properties);// FIXME
                                                                                                      // read
                                                                                                      // type
                                                                                                      // params

        // refresh local variables
        // PropertyMap props = document.getProperties();

        exec.setVariable(IisysConstants.DOCUMENT_ID, document.getId());
        util.shutdown();
    }
}
