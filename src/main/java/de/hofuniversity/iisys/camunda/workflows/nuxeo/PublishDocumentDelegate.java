package de.hofuniversity.iisys.camunda.workflows.nuxeo;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hofuniversity.iisys.camunda.workflows.DelegateUtil;
import de.hofuniversity.iisys.camunda.workflows.IisysConstants;

public class PublishDocumentDelegate implements JavaDelegate {
    private static final transient Logger LOG = LoggerFactory.getLogger(PublishDocumentDelegate.class);

    @Override
    public void execute(DelegateExecution exec) throws Exception {
        // get context variables
        String docId = exec.getVariable(IisysConstants.DOCUMENT_ID).toString();
        String targetSection = exec.getVariable(IisysConstants.SECTION_ID).toString();
        boolean override = Boolean.parseBoolean(exec.getVariable(IisysConstants.OVERRIDE).toString());

        NuxeoUtility util = DelegateUtil.getNuxeoUtil();
        LOG.debug("docId=" + docId + " published to Section:" + targetSection);
        util.publishDocumentToSection(util.getDocument(docId), util.getDocument(targetSection), override);
        util.shutdown();
    }
}
