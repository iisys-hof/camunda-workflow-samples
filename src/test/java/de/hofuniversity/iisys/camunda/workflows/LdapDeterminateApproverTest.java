/**
 * 
 */
package de.hofuniversity.iisys.camunda.workflows;

import static org.assertj.core.api.Assertions.assertThat;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.processInstanceQuery;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.task;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hofuniversity.iisys.camunda.workflows.ldap.LdapDetermineApprover;

/**
 * @author cstrobel
 * 
 */
public class LdapDeterminateApproverTest {
    private static final transient Logger LOG = LoggerFactory.getLogger(LdapDeterminateApproverTest.class);
    @Rule
    public ProcessEngineRule rule = new ProcessEngineRule();

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * There should be a log entry like: I can`t get the starting UserId!...
     * 
     * @throws Exception
     */
    @Test
    public void test() throws Exception {

        try {
            // preconditions
            DelegateExecution execution = mock(DelegateExecution.class);
            Mockito.when(execution.getVariable(IisysConstants.LDAP_SEARCHQUERY)).thenReturn("objectClass=*");
            Mockito.when(execution.getVariable(IisysConstants.LDAP_SCOPE)).thenReturn("SUB");

            // execute unit under test
            JavaDelegate myDelegate = new LdapDetermineApprover();
            myDelegate.execute(execution);
        } catch (Exception e) {
            throw e;
        } finally {

        }
    }

    @Test
    @Deployment(resources = { "LdapProcessTest.bpmn" })
    public void shouldExecuteProcess() {
        try {
            Map<String, Object> map = new HashMap<>();
            // map.put("initiator", "gabi");

            // Given we create a new process instance
            // TODO This Exception is not in our scope. Its CMIS related
            ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("LdapProcessTest", map);
            // Then it should be active
            assertThat(!processInstance.isEnded());
            // And it should be the only instance
            assertThat(processInstanceQuery().count()).isEqualTo(0);
            // And there should exist just a single task within that process instance
            assertThat(task(processInstance)).isNull();
            // Then the process instance should be ended
            assertThat(processInstance.isEnded());
        } catch (Exception e) {
            throw e;
        } finally {
        }
    }

}
