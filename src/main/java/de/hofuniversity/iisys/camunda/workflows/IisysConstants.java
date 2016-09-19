package de.hofuniversity.iisys.camunda.workflows;

/**
 * This class contains the variable names which are used by the Delegates.
 * 
 * @author cstrobel
 * 
 */
public class IisysConstants {
	public static final String CONTEXT_PREFIX = "iisys_";
	public static final String WORKFLOW_INITIATOR = "iisys_workflow_initiator";
	
    public static final String PARENT_FOLDER_ID = "iisys_parentFolderId";
    public static final String DOCUMENT_ID = "iisys_documentId";
    public static final String DOCUMENT2_ID = "iisys_document2Id";
    public static final String CURRENT_FOLDER_ID = "iisys_currentFolderId";
    public static final String FOLDER_ID = "iisys_folderId";
    public static final String NEW_FOLDER_ID = "iisys_newFolderId";
    public static final String SECTION_ID = "iisys_sectionId";

    // Rendering Nuxeo Client
    /**
     * @see http://explorer.nuxeo.com/nuxeo/site/distribution/current/viewOperation /Render.Document
     */
    public static final String RENDER_TEMPLATE = "iisys_renderTemplate";
    public static final String RENDER_FILENAME = "iisys_renderFilename";
    public static final String RENDER_MIME_TYPE = "iisys_renderMimetype";
    public static final String RENDER_TYPE = "iisys_renderType";

    // Names for Creation of new elements
    public static final String DOCUMENT_NAME = "iisys_documentName";
    public static final String NEW_FOLDER_NAME = "iisys_folderName";

    // Permissions Nuxeo Client
    public static final String PERM_USER = "iisys_permUser";
    public static final String PERM_ACL = "iisys_permAcl";
    public static final String PERM_PERMISSION = "iisys_permPermission";
    public static final String PERM_BLOCKINHERITANCE = "iisys_permInheritance"; // Boolean

    public static final String OVERRIDE = "iisys_override";

    // Labels or tags separated by comma: "Tag1, Tag2, Tag3"
    public static final String TAG_NAME = "iisys_tag";

    // LDAP Paramters
    public static final String LDAP_SEARCHQUERY = "iisys_ldapSearch";
    /**
     * 1 for manager; 2 for manager of the manager
     */
    public static final String LDAP_SEARCHDEPTH = "iisys_ldapSearchDeapth";

    /**
     * ONE, SUB, BASE or SUBORDINATE_SUBTREE
     */
    public static final String LDAP_SCOPE = "iisys_ldapScope";

    /**
     * if more than one approver is present. You will get a List of approver like: iisys:approver, iisys:approver1,
     * iisys:approver2
     */
    public static final String Approver1 = "iisys_approver1";
    public static final String Approver1DispName = "iisys_approver1name";
    public static final String Approver2 = "iisys_approver2";
    public static final String Approver2DispName = "iisys_approver2name";

    /**
     * a private constructor...
     */
    private IisysConstants() {

    }

}