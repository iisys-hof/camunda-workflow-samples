package de.hofuniversity.iisys.camunda.workflows.nuxeo;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import de.hofuniversity.iisys.camunda.workflows.DelegateUtil;
import de.hofuniversity.iisys.camunda.workflows.IisysConstants;

public class AddDocumentToWorklistDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution exec) throws Exception {
        // get context variables
        String docId = exec.getVariable(IisysConstants.DOCUMENT_ID).toString();

        NuxeoUtility util = DelegateUtil.getNuxeoUtil();
        // FIXME make this more dynamic
        util.addCurrentDocumentToWorklist(util.getDocument(docId));
        util.shutdown();
    }
}
