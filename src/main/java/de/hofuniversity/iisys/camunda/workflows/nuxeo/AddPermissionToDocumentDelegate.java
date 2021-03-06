package de.hofuniversity.iisys.camunda.workflows.nuxeo;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.nuxeo.ecm.automation.client.model.Document;

import de.hofuniversity.iisys.camunda.workflows.DelegateUtil;
import de.hofuniversity.iisys.camunda.workflows.IisysConstants;

public class AddPermissionToDocumentDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution exec) throws Exception {
        // get context variables
        String docId = exec.getVariable(IisysConstants.DOCUMENT_ID).toString();
        String subject = exec.getVariable(IisysConstants.PERM_USER).toString();
        String acl = exec.getVariable(IisysConstants.PERM_ACL) != null ? exec.getVariable(IisysConstants.PERM_ACL)
                .toString() : null;
        String permission = exec.getVariable(IisysConstants.PERM_PERMISSION).toString();
        boolean blockInteritence = Boolean.parseBoolean(exec.getVariable(IisysConstants.PERM_BLOCKINHERITANCE)
                .toString());

        // get document
        NuxeoUtility util = DelegateUtil.getNuxeoUtil();
        Document doc = util.getDocument(docId);
        util.addPermissionToDocument(doc, permission, subject, acl, blockInteritence);
        util.shutdown();
    }
}
