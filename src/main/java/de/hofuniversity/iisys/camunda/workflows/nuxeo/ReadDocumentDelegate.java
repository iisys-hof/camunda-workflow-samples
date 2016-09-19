package de.hofuniversity.iisys.camunda.workflows.nuxeo;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.PropertyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hofuniversity.iisys.camunda.workflows.DelegateUtil;
import de.hofuniversity.iisys.camunda.workflows.IisysConstants;

public class ReadDocumentDelegate implements JavaDelegate {
    private static transient Logger LOG = LoggerFactory.getLogger(ReadDocumentDelegate.class);

    @Override
    public void execute(DelegateExecution exec) throws Exception {
        // get document ID
        String docId = null;
        try {
            docId = exec.getVariable(IisysConstants.DOCUMENT_ID).toString();
        } catch (Exception e) {
            LOG.error("Error on reading Document", e);
        }

        // get nuxeo utility according to current configuration and user
        NuxeoUtility util = DelegateUtil.getNuxeoUtil();

        // get current values
        Document doc = util.getDocument(docId);
        PropertyMap props = doc.getProperties();

        String description = props.getString("dc:description");
        String source = props.getString("dc:source");

        util.shutdown();

        // set current values
        exec.setVariable("descriptionText", description);
        exec.setVariable("sourceText", source);
    }
}
