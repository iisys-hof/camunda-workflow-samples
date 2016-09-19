package de.hofuniversity.iisys.camunda.workflows.cmis;

import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import de.hofuniversity.iisys.camunda.workflows.DelegateUtil;
import de.hofuniversity.iisys.camunda.workflows.IisysConstants;
import de.hofuniversity.iisys.cmis.OpenCMISClient;

public class CreateFolderDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution exec) throws Exception {
        // get context variables
        String folderName = exec.getVariable(IisysConstants.NEW_FOLDER_NAME).toString();
        String parentFolderId = exec.getVariable(IisysConstants.PARENT_FOLDER_ID).toString();

        // get Client
        OpenCMISClient cmisClient = DelegateUtil.getCMISClient();

        // get target Folder
        Folder parentFolder = (Folder) cmisClient.getObject(parentFolderId);

        // create new Folder
        Map<String, Object> properties = new HashMap<>();
        properties.put(PropertyIds.NAME, folderName);
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");

        Folder folder = parentFolder.createFolder(properties);

        // refresh local variables
        exec.setVariable(IisysConstants.NEW_FOLDER_ID, folder.getId());

        cmisClient.shutdown();
    }
}
