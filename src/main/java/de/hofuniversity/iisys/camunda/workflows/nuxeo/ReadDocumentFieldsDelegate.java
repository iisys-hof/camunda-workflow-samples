package de.hofuniversity.iisys.camunda.workflows.nuxeo;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.PropertyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hofuniversity.iisys.camunda.workflows.DelegateUtil;
import de.hofuniversity.iisys.camunda.workflows.IisysConstants;

public class ReadDocumentFieldsDelegate implements JavaDelegate
{
    private static transient Logger LOG = LoggerFactory.getLogger(ReadDocumentFieldsDelegate.class);
    
	@Override
	public void execute(DelegateExecution exec) throws Exception
	{
        // get document ID
        String docId = null;
        try
        {
            docId = exec.getVariable(IisysConstants.DOCUMENT_ID).toString();
        }
        catch (Exception e)
        {
            LOG.error("Error on reading Document", e);
        }
		
        // get nuxeo utility according to current configuration and user
        NuxeoUtility util = DelegateUtil.getNuxeoUtil();
        
        // get current values
        Document doc = util.getDocument(docId);
        PropertyMap props = doc.getProperties();
        
        // read specified fields
        String fieldString = (String) exec.getVariable("iisys_doc_fields");
        String[] fieldList = {};
        if(fieldString != null)
        {
        	fieldList = fieldString.split(",");
        }
        
        // read from document and set in context
        for(String field : fieldList)
        {
        	field = field.replaceAll(" ", "");
        	
        	exec.setVariable(IisysConstants.CONTEXT_PREFIX + field,
        			props.get(field));
        }
        
        // shut down connection
        util.shutdown();
	}

}
