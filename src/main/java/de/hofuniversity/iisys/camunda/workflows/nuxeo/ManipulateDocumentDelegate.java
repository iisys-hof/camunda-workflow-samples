package de.hofuniversity.iisys.camunda.workflows.nuxeo;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.PropertyMap;

import de.hofuniversity.iisys.camunda.workflows.DelegateUtil;
import de.hofuniversity.iisys.camunda.workflows.IisysConstants;

public class ManipulateDocumentDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution exec) throws Exception {
        // get context variables
        String docId = exec.getVariable(IisysConstants.DOCUMENT_ID).toString();
        String description = exec.getVariable("descriptionText").toString();
        String source = exec.getVariable("sourceText").toString();

        // get document
        NuxeoUtility util = DelegateUtil.getNuxeoUtil();

        Document doc = util.getDocument(docId);
        PropertyMap props = doc.getProperties();

        // update values
        doc.set("dc:description", description);
        doc.set("dc:source", source);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("properties", props);

        util.updateDocument(doc, params);

        // refresh local variables
        doc = util.getDocument(docId);
        props = doc.getProperties();

        description = props.getString("dc:description");
        source = props.getString("dc:source");

        exec.setVariable("descriptionText", description);
        exec.setVariable("sourceText", source);
    }
}
