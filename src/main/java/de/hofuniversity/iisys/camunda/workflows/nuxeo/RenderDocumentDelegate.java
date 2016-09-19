package de.hofuniversity.iisys.camunda.workflows.nuxeo;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import de.hofuniversity.iisys.camunda.workflows.DelegateUtil;
import de.hofuniversity.iisys.camunda.workflows.IisysConstants;

public class RenderDocumentDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution exec) throws Exception {
        // get context variables
        String docId = exec.getVariable(IisysConstants.DOCUMENT_ID).toString();
        String filename = exec.getVariable(IisysConstants.RENDER_FILENAME).toString();
        String mimeType = exec.getVariable(IisysConstants.RENDER_MIME_TYPE).toString();
        String template = exec.getVariable(IisysConstants.RENDER_TEMPLATE).toString();
        String type = exec.getVariable(IisysConstants.RENDER_TYPE).toString();

        // get document
        NuxeoUtility util = DelegateUtil.getNuxeoUtil();
        util.renderDocument(util.getDocument(docId), template, filename, mimeType, type);
        util.shutdown();
    }
}
