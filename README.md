# camunda-workflow-samples
Camunda workflow and delegate samples (Nuxeo, CMIS, LDAP, ...)

# How to use
Installation:

1. Do `mvn install` on the camunda-nuxeo-connector (https://github.com/iisys-hof/camunda-nuxeo-connector). You need to have the Project camunda-nuxeo-connector locally installed in your maven repository because this project depends on it. To do this You need to run mvn install on camunda-nuxeo-connector.
2. Import Project into Camunda Eclipse Editor
3. To build this project use something like: `mvn clean install jar:jar`. The jar:jar part is important! You will get two jars. One has the Code of the Project and the other one will contain all necessary dependencies except for camunda-nuxeo-connector. Place these two files in the `Camunda7.4/server/apache-tomcat-8.0.24/lib` folder and deploy a Process via the Command:

`curl -X POST --form "fileupload=@testProcess.bpmn" --form deployment-name=demoProcess http://localhost:8080/engine-rest/engine/default/deployment/create`

The constant variable names used for the delegates are defined here: /src/main/java/de/hofuniversity/iisys/camunda/workflows/IisysConstants.java

# How to use alternate/old way
Logs in using a supplied user (see configuration)

Can be used in conjuction with https://github.com/iisys-hof/camunda-nuxeo-connector

But after deployment, the war file and context may have to be removed again. The process itself will remain deployed in Camunda's database.

Installation:

1. Do `mvn install` on the camunda-nuxeo-connector. You need this as a Dependency in this project
2. Import Project into Camunda Eclipse Editor
3. Run Maven build, with target package
4. Deploy generated war file on Camunda Tomcat server

## Important Information!
This Project is not deployable because it currently contains more than one Workflow. This Project is only used for the Delegates for the Service Tasks and the testing of the Delegates.

CMIS/Nuxeo configuration: /src/main/resources/nuxeo-process-test.properties

LDAP configuration: /src/main/resources/ldap-config.properties

Mail configuration: /src/main/resources/mail-delegates.properties

Caution: To delete this deployment, besides undeploying it, you will need to delete it via the Camunda REST API


Setting a category can be acheived by editing /src/main/resources/nuxeo_workflow.bpmn
The parameter to set to a category name is "targetNamespace" in line 2.


# Create a Workflow
If You want to use the Delegates or other classes from this project You need to follow the instructions of camunda to make a new workflow and add this Project as a dependency.

OR
You can model a process in a new Project and set the delegate names via copy and paste. The Delegates should be in the lib-Folder of the Tomcat on which Camunda runs



# Delegate Core Concepts

## How the Delegates get their connection parameters
In either package `de.hofuniversity.iisys.camunda.workflows.cmis` or `de.hofuniversity.iisys.camunda.workflows.nuxeo` is a `ProcessConfig` Class which handles the obtaining of the connection parameters from a properties file located in `src/main/resources` the default name is `nuxeo-process-test.properties`.

## How a Delegates get/set variables to/from the `ProcessInstance`

### Encapsulated Variable Strings 
The `de.hofuniversity.iisys.camunda.workflows.IisysConstants.java` Class contains the variable Keys used by the delegates.

The Delegates reads and/or writes variables to the `ProcessInstance` of Camunda. The Delegates get the Paramters from The `DelegateExecution` provided as a Parameter. `delegateExecution.getVariable(IisysConstants.DOCUMENT_ID)`

To set a value simply use `delegateExecution.setVariable(key, value);`

## CMIS Delegates
Properties File (nuxeo-process-test.ptoperties) Connection Parameters for CMIS

debug.user=name

debug.password=password

cmis.url=http://127.0.0.1:8080/nuxeo/atom/cmis

cmis.bindingtype=atompub

cmis.repository=default


|  Class | Input Parameter  | Output Parameter |
| ------------- |:-------------:| -----:|
| CreateDocumentDelegate      | X | DOCUMENT_ID |
| CreateFolderDelegate      | NEW_FOLDER_NAME, PARENT_FOLDER_ID|   NEW_FOLDER_ID |
|DeleteDocumentDelegate| DOCUMENT_ID      |    X |
|MoveDocumentDelegate | DOCUMENT_ID, CURRENT_FOLDER_ID, NEW_FOLDER_ID      |   X |
(DOCUMENT_ID = IisysConstants.DOCUMENT_ID)
## Nuxeo Delegates
Properties File (nuxeo-process-test.ptoperties) Connection Parameters for Nuxeo

nuxeo.url=http://127.0.0.1:8080/nuxeo/

debug.user=name

debug.password=password



|  Class | Input Parameter  | Output Parameter |
| ------------- |:-------------:| -----:|
| AddDocumentToWorklistDelegate |DOCUMENT_ID | X|
| AddPermissionToDocumentDelegate | DOCUMENT_ID, PERM_USER, PERM_ACL, PERM_PERMISSION| X|
| CreateDocumentDelegate      | X | DOCUMENT_ID |
| CreateFolderDelegate      | NEW_FOLDER_NAME, PARENT_FOLDER_ID|   NEW_FOLDER_ID |
| DeleteDocumentDelegate| DOCUMENT_ID      |    X |
| LockDocumentDelegate| DOCUMENT_ID | X |
| DeleteDocumentDelegate| DOCUMENT_ID      |    X |
| MoveDocumentDelegate | DOCUMENT_ID, NEW_FOLDER_ID      |   X |
| PublishDocumentDelegate| DOCUMENT_ID, SECTION_ID, OVERRIDE| X |
| RemovePermissionFromDocumentDelegate | DOCUMENT_ID, PERM_USER, PERM_ACL| X |
| RenderDocumentDelegate | DOCUMENT_ID, RENDER_FILENAME, RENDER_MIME_TYPE, RENDER_TEMPLATE, RENDER_TYPE| X |
| UnlockDocumentDelegate | DOCUMENT_ID | X |
| AprroveAndCreateVersionOfDocumentDelegate | DOCUMENT_ID | X |
| AddTagToDocumentDelegate | DOCUMENT_ID, TAG_NAME | X |