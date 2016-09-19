package de.hofuniversity.iisys.camunda.workflows.cmis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.complete;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.processInstanceQuery;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.task;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Rule;
import org.junit.Test;

import de.hofuniversity.iisys.camunda.workflows.IisysConstants;
import de.hofuniversity.iisys.camunda.workflows.TestConstants;

/**
 * 
 */

/**
 * @author cstrobel
 * 
 */
public class CreateDeleteDocumentTest {
    @Rule
    public ProcessEngineRule rule = new ProcessEngineRule();

    @Test
    @Deployment(resources = { "cmisCreateDeleteDocument_Workflow.bpmn" })
    public void shouldExecuteProcess() {

        Map<String, Object> map = new HashMap<>();
        map.put(IisysConstants.PARENT_FOLDER_ID, TestConstants.TEST_FOLDER_ID);
        // Given we create a new process instance
        ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("cmisCreateDeleteDocument", map);
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
    }
}
