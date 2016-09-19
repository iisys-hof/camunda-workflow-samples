package de.hofuniversity.iisys.camunda.workflows.cmis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.client.runtime.ObjectIdImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import de.hofuniversity.iisys.camunda.workflows.DelegateUtil;
import de.hofuniversity.iisys.camunda.workflows.IisysConstants;
import de.hofuniversity.iisys.cmis.OpenCMISClient;

public class CreateDocumentDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution exec) throws Exception {
        // get context variables
        // String docId = exec.getVariable("documentId").toString();
        // String description = exec.getVariable("descriptionText").toString();
        String folder = exec.getVariable(IisysConstants.PARENT_FOLDER_ID).toString();

        // get document
        OpenCMISClient util = DelegateUtil.getCMISClient();

        // Create a Document
        Map<String, String> properties = new HashMap<String, String>();
        String titleKey = "dc:title";
        properties.put(titleKey, "testDocName");
        properties.put(PropertyIds.OBJECT_TYPE_ID, "Note");
        properties.put(PropertyIds.NAME, "someName");
        String descKey = "dc:description";
        properties.put(descKey, "some Description");
        ObjectId document = util.createDocument(properties, new ObjectIdImpl(folder), null, VersioningState.MINOR);

        // refresh local variables
        List<Property<?>> props = util.getObject(document).getProperties();
        String description = null;
        String title = null;
        for (Property<?> prop : props) {
            if (prop.getDisplayName().equals(descKey)) {
                description = prop.getValue();
            } else if (prop.getDisplayName().equals(titleKey)) {
                title = prop.getValue();
            }
        }
        exec.setVariable(IisysConstants.DOCUMENT_ID, document.getId());

        util.shutdown();
    }
}
