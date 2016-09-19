/**
 * 
 */
package de.hofuniversity.iisys.camunda.workflows.nuxeo;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.nuxeo.ecm.automation.client.model.Document;

import de.hofuniversity.iisys.camunda.workflows.DelegateUtil;
import de.hofuniversity.iisys.camunda.workflows.IisysConstants;

/**
 * @author cstrobel
 * 
 */
public class AddTagToDocumentDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution exec) throws Exception {
        // get context variables
        String docId = exec.getVariable(IisysConstants.DOCUMENT_ID).toString();

        // Labels or tags separated by comma: "Tag1, Tag2, Tag3"
        String tag = exec.getVariable(IisysConstants.TAG_NAME).toString();

        // get document
        NuxeoUtility util = DelegateUtil.getNuxeoUtil();
        Document doc = util.getDocument(docId);
        util.tagDocument(doc, tag);
        util.shutdown();
    }
}
