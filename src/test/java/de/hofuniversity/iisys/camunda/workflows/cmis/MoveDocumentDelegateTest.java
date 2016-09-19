/**
 * 
 */
package de.hofuniversity.iisys.camunda.workflows.cmis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.complete;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.processInstanceQuery;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.task;

import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.runtime.ObjectIdImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Rule;
import org.junit.Test;

import de.hofuniversity.iisys.camunda.workflows.IisysConstants;
import de.hofuniversity.iisys.camunda.workflows.TestConstants;
import de.hofuniversity.iisys.cmis.OpenCMISClient;

/**
 * @author cstrobel
 * 
 */
public class MoveDocumentDelegateTest {
    @Rule
    public ProcessEngineRule rule = new ProcessEngineRule();

    /**
     * TODO This Exception is not in our scope. Its CMIS related
     */
    @Test(expected = CmisRuntimeException.class)
    @Deployment(resources = { "cmisMoveFileToFolder.bpmn" })
    public void shouldExecuteProcess() {
        // read connection parameters
        // TODO: get "executing" user?
        Map<String, String> config = ProcessConfig.getInstance().getConfiguration();
        String nuxeoUrl = config.get(ProcessConfig.CMIS_URL);
        String user = config.get(ProcessConfig.DEBUG_USER);
        String password = config.get(ProcessConfig.DEBUG_PASSWORD);
        String bindingType = config.get(ProcessConfig.BINDING_TYPE);
        String repository = config.get(ProcessConfig.REPOSITORY);

        // get document
        OpenCMISClient util = null;
        ObjectId document = null;
        try {
            util = new OpenCMISClient(nuxeoUrl, user, password, repository, bindingType);
            // Create a Document
            Map<String, String> properties = new HashMap<String, String>();
            String titleKey = "dc:title";
            properties.put(titleKey, "testDocName");
            properties.put(PropertyIds.OBJECT_TYPE_ID, "Note");
            properties.put(PropertyIds.NAME, "someName");
            String descKey = "dc:description";
            properties.put(descKey, "some Description");
            document = util.createDocument(properties, new ObjectIdImpl(TestConstants.TEST_FOLDER_ID), null,
                    VersioningState.MINOR);

            Map<String, Object> map = new HashMap<>();
            map.put(IisysConstants.DOCUMENT_ID, document.getId());
            map.put(IisysConstants.CURRENT_FOLDER_ID, TestConstants.TEST_FOLDER_ID);
            map.put(IisysConstants.NEW_FOLDER_ID, TestConstants.TEST_FOLDER2_ID);

            // Given we create a new process instance
            // TODO This Exception is not in our scope. Its CMIS related
            ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("cmisMoveFileToFolder", map);
            // Then it should be active
            assertThat(!processInstance.isEnded());
            // And it should be the only instance
            assertThat(processInstanceQuery().count()).isEqualTo(1);
            // And there should exist just a single task within that process
            // instance
            assertThat(task(processInstance)).isNotNull();
            // When we complete that task
            complete(task(processInstance));
            // Then the process instance should be ended
            assertThat(processInstance.isEnded());
        } catch (Exception e) {
            throw e;
        } finally {
            util.delete(document);
        }
    }
}
