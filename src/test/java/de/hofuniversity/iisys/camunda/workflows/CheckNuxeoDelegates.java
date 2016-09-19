package de.hofuniversity.iisys.camunda.workflows;

import static org.mockito.Mockito.mock;

import java.util.Properties;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.nuxeo.ecm.automation.client.model.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hofuniversity.iisys.camunda.workflows.nuxeo.AddDocumentToWorklistDelegate;
import de.hofuniversity.iisys.camunda.workflows.nuxeo.AddPermissionToDocumentDelegate;
import de.hofuniversity.iisys.camunda.workflows.nuxeo.CreateDocumentDelegate;
import de.hofuniversity.iisys.camunda.workflows.nuxeo.CreateFolderDelegate;
import de.hofuniversity.iisys.camunda.workflows.nuxeo.DeleteDocumentDelegate;
import de.hofuniversity.iisys.camunda.workflows.nuxeo.LockDocumentDelegate;
import de.hofuniversity.iisys.camunda.workflows.nuxeo.MoveDocumentDelegate;
import de.hofuniversity.iisys.camunda.workflows.nuxeo.NuxeoUtility;
import de.hofuniversity.iisys.camunda.workflows.nuxeo.PublishDocumentDelegate;
import de.hofuniversity.iisys.camunda.workflows.nuxeo.RemovePermissionFromDocumentDelegate;
import de.hofuniversity.iisys.camunda.workflows.nuxeo.RenderDocumentDelegate;
import de.hofuniversity.iisys.camunda.workflows.nuxeo.UnlockDocumentDelegate;

/**
 * @author cstrobel
 * 
 */
public class CheckNuxeoDelegates {
    private static final String URL = "http://127.0.0.1:8080/nuxeo/";
    private static final transient Logger LOG = LoggerFactory.getLogger(CheckNuxeoDelegates.class);
    private static NuxeoUtility nuxeo;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        nuxeo = new NuxeoUtility(URL, "demo", "secret");
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        nuxeo.shutdown();
    }

    @Rule
    public ProcessEngineRule rule = new ProcessEngineRule();

    @Test
    // @Deployment(resources = { "cmisCreateFolder.bpmn",
    // "cmisDeleteDocumentOrFolder.bpmn" })
    public void testCreateDocumentDelegate() throws Exception {
        String docId = null;
        try {
            // preconditions
            DelegateExecution execution = mock(DelegateExecution.class);
            Mockito.when(execution.getVariable(IisysConstants.PARENT_FOLDER_ID)).thenReturn(
                    TestConstants.TEST_FOLDER_ID);

            // execute unit under test
            JavaDelegate myDelegate = new CreateDocumentDelegate();
            myDelegate.execute(execution);

            // postconditions
            ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

            // Verification
            Mockito.verify(execution).setVariable(Mockito.eq(IisysConstants.DOCUMENT_ID), argumentCaptor.capture());

            // get Value
            docId = argumentCaptor.getValue();

        } catch (Exception e) {
            throw e;
        } finally {
            LOG.debug(nuxeo.toString());
            nuxeo.deleteDocument(nuxeo.getDocument(docId));
        }
    }

    @Test
    public void testDeleteDocumentDelegate() throws Exception {
        Document doc = createTestNoteDoc();

        try {
            // preconditions
            DelegateExecution execution = mock(DelegateExecution.class);
            Mockito.when(execution.getVariable(IisysConstants.DOCUMENT_ID)).thenReturn(doc.getId());

            // execute unit under test
            JavaDelegate myDelegate = new DeleteDocumentDelegate();
            myDelegate.execute(execution);
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                nuxeo.deleteDocument(doc);
            } catch (Exception e) {
                // then its already deleted...
            }
        }
    }

    @Test
    public void testMoveDocumentDelegate() throws Exception {
        // create a test doc
        Document doc = createTestNoteDoc();

        try {
            // preconditions
            DelegateExecution execution = mock(DelegateExecution.class);
            Mockito.when(execution.getVariable(IisysConstants.DOCUMENT_ID)).thenReturn(doc.getId());
            Mockito.when(execution.getVariable(IisysConstants.CURRENT_FOLDER_ID)).thenReturn(
                    TestConstants.TEST_FOLDER_ID);
            Mockito.when(execution.getVariable(IisysConstants.NEW_FOLDER_ID)).thenReturn(TestConstants.TEST_FOLDER2_ID);

            // execute unit under test
            JavaDelegate myDelegate = new MoveDocumentDelegate();
            myDelegate.execute(execution);
        } catch (Exception e) {
            throw e;
        } finally {
            nuxeo.deleteDocument(doc);
        }
    }

    @Test
    public void testCreateFolderDelegate() throws Exception {
        String docId = null;
        try {
            // preconditions
            DelegateExecution execution = mock(DelegateExecution.class);
            Mockito.when(execution.getVariable(IisysConstants.PARENT_FOLDER_ID)).thenReturn(
                    TestConstants.TEST_FOLDER_ID);
            Mockito.when(execution.getVariable(IisysConstants.NEW_FOLDER_NAME)).thenReturn("CamundaTestFolder");

            // execute unit under test
            JavaDelegate myDelegate = new CreateFolderDelegate();
            myDelegate.execute(execution);
            // postconditions
            ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

            // Verification
            Mockito.verify(execution).setVariable(Mockito.eq(IisysConstants.NEW_FOLDER_ID), argumentCaptor.capture());

            // get Value
            docId = argumentCaptor.getValue();
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                nuxeo.deleteDocument(nuxeo.getDocument(docId));
            } catch (Exception e) {
                // then its already deleted...
            }
        }
    }

    @Test
    public void testAddDocumentToWorklistDelegate() throws Exception {
        Document doc = createTestNoteDoc();

        try {
            // preconditions
            DelegateExecution execution = mock(DelegateExecution.class);
            Mockito.when(execution.getVariable(IisysConstants.DOCUMENT_ID)).thenReturn(doc.getId());

            // execute unit under test
            JavaDelegate myDelegate = new AddDocumentToWorklistDelegate();
            myDelegate.execute(execution);
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                nuxeo.deleteDocument(doc);
            } catch (Exception e) {
                // then its already deleted...
            }
        }
    }

    @Test
    public void testLockDocumentDelegate() throws Exception {
        Document doc = createTestNoteDoc();

        try {
            // preconditions
            DelegateExecution execution = mock(DelegateExecution.class);
            Mockito.when(execution.getVariable(IisysConstants.DOCUMENT_ID)).thenReturn(doc.getId());

            // execute unit under test
            JavaDelegate myDelegate = new LockDocumentDelegate();
            myDelegate.execute(execution);
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                nuxeo.deleteDocument(doc);
            } catch (Exception e) {
                // then its already deleted...
            }
        }
    }

    @Test
    public void testUnlockDocumentDelegate() throws Exception {
        Document doc = createTestNoteDoc();

        try {
            // preconditions
            DelegateExecution execution = mock(DelegateExecution.class);
            Mockito.when(execution.getVariable(IisysConstants.DOCUMENT_ID)).thenReturn(doc.getId());

            // execute unit under test
            JavaDelegate myDelegate = new UnlockDocumentDelegate();
            myDelegate.execute(execution);
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                nuxeo.deleteDocument(doc);
            } catch (Exception e) {
                // then its already deleted...
            }
        }
    }

    @Test
    public void testPublishDocumentDelegate() throws Exception {
        Document doc = createTestNoteDoc();

        try {
            // preconditions
            DelegateExecution execution = mock(DelegateExecution.class);
            Mockito.when(execution.getVariable(IisysConstants.DOCUMENT_ID)).thenReturn(doc.getId());
            Mockito.when(execution.getVariable(IisysConstants.SECTION_ID)).thenReturn(TestConstants.TEST_SECTION_ID);
            Mockito.when(execution.getVariable(IisysConstants.OVERRIDE)).thenReturn(true);

            // execute unit under test
            JavaDelegate myDelegate = new PublishDocumentDelegate();
            myDelegate.execute(execution);
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                nuxeo.deleteDocument(doc);
            } catch (Exception e) {
                // then its already deleted...
            }
        }
    }

    @Test
    public void testRenderDocumentDelegate() throws Exception {
        Document doc = createTestNoteDoc();
        try {
            // preconditions
            DelegateExecution execution = mock(DelegateExecution.class);
            Mockito.when(execution.getVariable(IisysConstants.DOCUMENT_ID)).thenReturn(doc.getId());
            Mockito.when(execution.getVariable(IisysConstants.RENDER_FILENAME)).thenReturn("testOutput.ftl");
            Mockito.when(execution.getVariable(IisysConstants.RENDER_MIME_TYPE)).thenReturn("text/xml");
            Mockito.when(execution.getVariable(IisysConstants.RENDER_TEMPLATE)).thenReturn(
                    "templates:Customer Reference using ODT Template");
            Mockito.when(execution.getVariable(IisysConstants.RENDER_TYPE)).thenReturn("ftl, mvel");

            // execute unit under test
            JavaDelegate myDelegate = new RenderDocumentDelegate();
            myDelegate.execute(execution);
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                nuxeo.deleteDocument(doc);
            } catch (Exception e) {
                // then its already deleted...
            }
        }
    }

    @Test
    public void testAddPermissionToDocumentDelegate() throws Exception {
        Document doc = createTestNoteDoc();
        try {
            // preconditions
            DelegateExecution execution = mock(DelegateExecution.class);
            Mockito.when(execution.getVariable(IisysConstants.DOCUMENT_ID)).thenReturn(doc.getId());
            Mockito.when(execution.getVariable(IisysConstants.PERM_ACL)).thenReturn(null);
            Mockito.when(execution.getVariable(IisysConstants.PERM_BLOCKINHERITANCE)).thenReturn("false");
            Mockito.when(execution.getVariable(IisysConstants.PERM_PERMISSION)).thenReturn("READ");
            Mockito.when(execution.getVariable(IisysConstants.PERM_USER)).thenReturn("anton");

            // execute unit under test
            JavaDelegate myDelegate = new AddPermissionToDocumentDelegate();
            myDelegate.execute(execution);
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                nuxeo.deleteDocument(doc);
            } catch (Exception e) {
                // then its already deleted...
            }
        }
    }

    @Test
    public void testRemovePermissionFromDocumentDelegate() throws Exception {
        Document doc = createTestNoteDoc();
        try {
            // preconditions
            DelegateExecution execution = mock(DelegateExecution.class);
            Mockito.when(execution.getVariable(IisysConstants.DOCUMENT_ID)).thenReturn(doc.getId());
            Mockito.when(execution.getVariable(IisysConstants.PERM_ACL)).thenReturn(null);
            Mockito.when(execution.getVariable(IisysConstants.PERM_USER)).thenReturn("anton");

            // execute unit under test
            JavaDelegate myDelegate = new RemovePermissionFromDocumentDelegate();
            myDelegate.execute(execution);
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                nuxeo.deleteDocument(doc);
            } catch (Exception e) {
                // then its already deleted...
            }
        }
    }

    private Document createTestNoteDoc() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("dc:title", "testDocName");
        properties.setProperty("dc:description", "some Description");
        Document doc = nuxeo.createDocument(nuxeo.getDocument(TestConstants.TEST_FOLDER_ID), "Note", properties);
        return doc;
    }
}
