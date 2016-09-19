package de.hofuniversity.iisys.camunda.workflows.cmis;

import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.runtime.ObjectIdImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hofuniversity.iisys.camunda.workflows.IisysConstants;
import de.hofuniversity.iisys.camunda.workflows.TestConstants;
import de.hofuniversity.iisys.cmis.OpenCMISClient;
import de.hofuniversity.iisys.cmis.OpenCMISClient.CMISDocumentTypes;

/**
 * @author cstrobel
 * 
 */
public class CheckCMISDelegates {
    private static final String URL = "http://127.0.0.1:8080/nuxeo/atom/cmis";
    private static final transient Logger LOG = LoggerFactory.getLogger(CheckCMISDelegates.class);

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        client = new OpenCMISClient(URL, "demo", "secret", "default", BindingType.ATOMPUB);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        client.shutdown();
    }

    @Rule
    public ProcessEngineRule rule = new ProcessEngineRule();
    private static OpenCMISClient client;

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
            client.delete(new ObjectIdImpl(docId));
        }
    }

    @Test
    public void testDeleteDocumentDelegate() throws Exception {
        ObjectId doc = createTestNoteDoc();

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
                client.delete(doc);
            } catch (Exception e) {
                // then its already deleted...
            }
        }
    }

    /**
     * TODO CMIS ERROR! Not our Scope
     * 
     * @throws Exception
     */
    @Test(expected = CmisRuntimeException.class)
    public void testMoveDocumentDelegate() throws Exception {
        // create a test doc
        ObjectId doc = createTestNoteDoc();

        try {
            // preconditions
            DelegateExecution execution = mock(DelegateExecution.class);
            Mockito.when(execution.getVariable(IisysConstants.DOCUMENT_ID)).thenReturn(doc.getId());
            Mockito.when(execution.getVariable(IisysConstants.CURRENT_FOLDER_ID)).thenReturn(
                    TestConstants.TEST_FOLDER_ID);
            Mockito.when(execution.getVariable(IisysConstants.NEW_FOLDER_ID)).thenReturn(TestConstants.TEST_FOLDER2_ID);

            // execute unit under test
            JavaDelegate myDelegate = new MoveDocumentDelegate();
            myDelegate.execute(execution);// TODO CMIS Error
        } catch (Exception e) {
            throw e;
        } finally {
            client.delete(doc);
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
                client.delete(new ObjectIdImpl(docId));
            } catch (Exception e) {
                // then its already deleted...
            }
        }
    }

    private ObjectId createTestNoteDoc() throws Exception {
        ContentStream contentStream = client.getContentStreamForSource("text/plain; charset=UTF-8", "xyzTest",
                "CMIS-TestFile");
        Map<String, String> map = new HashMap<String, String>();
        map.put(PropertyIds.OBJECT_TYPE_ID, CMISDocumentTypes.DOC.getIdentifier());
        map.put(PropertyIds.NAME, "CamundaTestDoc");

        ObjectId doc = client.createDocument(map, OpenCMISClient.getObjectId(TestConstants.TEST_FOLDER_ID),
                contentStream, VersioningState.MAJOR);
        return doc;
    }
}
