/**
 * 
 */
package de.hofuniversity.iisys.camunda.workflows.ldap;

import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.impl.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;

import de.hofuniversity.iisys.camunda.workflows.IisysConstants;

/**
 * This class is only usable on a Camunda Engine because the Context-Command is only executable there.
 * 
 * @author cstrobel
 * 
 */
public class LdapDetermineApprover implements JavaDelegate {
    private static transient Logger LOG = LoggerFactory.getLogger(LdapDetermineApprover.class);

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        // read connection parameters
        Map<String, String> config = LdapConfig.getInstance().getConfiguration();
        String ldapHost = config.get(LdapConfig.LDAP_HOST);
        int ldapPort = Integer.parseInt(config.get(LdapConfig.LDAP_PORT));
        String user = config.get(LdapConfig.LDAP_USER);
        String password = config.get(LdapConfig.LDAP_PASSWORD);

        String baseDn = config.get(LdapConfig.LDAP_BASEDN);

        // get context variables
        // String searchQuery = execution.getVariable(IisysConstants.LDAP_SEARCHQUERY).toString();
        // String searchScopeString = execution.getVariable(IisysConstants.LDAP_SCOPE).toString();

        String searchScopeString = "SUB";
        SearchScope searchScope = parseScope(searchScopeString);
        // LOG.debug("Params \n" + ldapHost + ":" + ldapPort + "\n" + user + ":"
        // + password + "\n" + baseDn + "\n"
        // + searchQuery + "\n" + searchScopeString);

        // get User who started the Workflow
        String requestingUser = null;

        try {
            requestingUser = Context.getCommandContext().getAuthenticatedUserId();
        } catch (Exception e) {
            LOG.error("", e);
            if (requestingUser == null) {
                LOG.error("I can`t get the starting UserId! "
                        + "You need to execute this on a Camunda Engine. If it is on a Camunda Engine. Something else is wrong");
                return;
            }
        }

        LOG.info("Looking for Managers of User " + requestingUser);
        // #1 get Manager of the Requesting Person
        String searchQuery = "(uid=" + requestingUser + " )";
        List<SearchResultEntry> resultSetOne = queryLdap(ldapHost, ldapPort, user, password, baseDn, searchQuery,
                searchScope);
        if (resultSetOne.size() == 0) {
            LOG.warn("No LDPA Entry for User:" + requestingUser + " found");
            return;
        }

        String approver1 = getManagerDN(resultSetOne);
        if (approver1 == null) {
            LOG.warn("No Manager found for User:" + requestingUser);
            return;
        }
        approver1 = getUidValue(approver1);
        LOG.debug(approver1);
        
        
        // #2 get Managers of the second degree
        searchQuery = "(uid=" + approver1 + " )";
        List<SearchResultEntry> resultSetTwo = queryLdap(ldapHost, ldapPort, user, password, baseDn, searchQuery,
                searchScope);

        String approver1name = null;
        String approver2 = null;
        if (resultSetTwo.size() > 0) {
            // retrieve display name for first approver in the process
        	approver1name = getDisplayName(resultSetTwo);

            approver2 = getManagerDN(resultSetTwo);
            if (approver2 == null) {
                LOG.warn("No Manager found for User:" + approver1);

            } else {
                approver2 = getUidValue(approver2);
                LOG.debug(approver2);
            }
        }
        
        
        // get display name for the second approver
        String approver2name = null;
        if(approver2 != null)
        {
            searchQuery = "(uid=" + approver2 + " )";
            List<SearchResultEntry> resultSetThree = queryLdap(ldapHost, ldapPort, user, password, baseDn, searchQuery,
                    searchScope);
            
            if(resultSetThree.size() > 0)
            {
            	approver2name = getDisplayName(resultSetThree);
            }
        }

        // finally set results
        setResultsToExecution(execution, approver1, approver1name, approver2, approver2name);
    }

    /**
     * Extracts the uid value of a given DN
     * 
     * @param distName
     *            given DN
     * @return the value after "uid=" and the first comma.
     */
    private String getUidValue(String distName) {
        String preString = "uid=";
        return distName.substring(distName.indexOf(preString) + preString.length(), distName.indexOf(","));
    }

    /**
     * Reads the Manager field if its present
     * 
     * @param resultSet
     *            which will be looked at for managers
     * @return the MangerDN
     */
    private String getManagerDN(List<SearchResultEntry> resultSet) {
        String mangerDn = null;
        if (resultSet.size() > 1) {
            LOG.warn("More than one Manager found. Choosing arbitrarily...");
        }
        for (SearchResultEntry result : resultSet) {
            // LOG.debug(result.getDN());
            if (result.hasAttribute("manager")) {
                // Parse DN, set Var
                mangerDn = result.getAttribute("manager").getValue();
            }
        }
        return mangerDn;
    }
    
    private String getDisplayName(List<SearchResultEntry> resultSet)
    {
    	String name = null;
    	
    	for (SearchResultEntry result : resultSet) {
            if (result.hasAttribute("displayName")) {
                name = result.getAttribute("displayName").getValue();
            }
        }
    	
    	return name;
    }

    /**
     * This persists the results to the execution variables
     * 
     * @param execution
     * @param approver1
     * @param approver2
     */
    private void setResultsToExecution(DelegateExecution execution, String approver1,
    		String approver1name, String approver2, String approver2name) {
        String logString = "Setting Process Var= ";
        // set variable 1
        if (approver1 != null) {
            LOG.debug(logString + IisysConstants.Approver1 + ":" + approver1);
            execution.setVariable(IisysConstants.Approver1, approver1);
        }
        
        if(approver1name != null)
        {
            execution.setVariable(IisysConstants.Approver1DispName, approver1name);
        }
        
        // set variable 2
        if (approver2 != null) {
            LOG.debug(logString + IisysConstants.Approver2 + ":" + approver2);
            execution.setVariable(IisysConstants.Approver2, approver2);
        }
        
        if(approver1name != null)
        {
            execution.setVariable(IisysConstants.Approver2DispName, approver2name);
        }
    }

    /**
     * Queries the LDAP-Server with the given arguments
     * 
     * @param ldapHost
     *            Host
     * @param ldapPort
     *            Port
     * @param user
     *            Username
     * @param password
     *            Password
     * @param baseDn
     *            BaseDN of the directory
     * @param searchQuery
     *            the SearchTerm
     * @param searchScope
     *            SearchScope
     * @return the Rusults of the Query
     */
    private List<SearchResultEntry> queryLdap(String ldapHost, int ldapPort, String user, String password,
            String baseDn, String searchQuery, SearchScope searchScope) {
        // return
        List<SearchResultEntry> searchEntries = null;

        // Connection
        LDAPConnection connection = null;
        try {
            // Create a LDAP connection
            connection = new LDAPConnection(ldapHost, ldapPort);
            // BindResult bindResult =
            connection.bind(user, password);

            // Do the actual search
            SearchResult searchResult = connection.search(baseDn, searchScope, searchQuery);
            searchEntries = searchResult.getSearchEntries();

            // Evaluate Results
            LOG.debug("Found " + searchResult.getSearchEntries().size() + " Results for the given Query");

        } catch (Exception e) {
            LOG.error("", e);
            e.printStackTrace();
        } finally {
            // close connection
            if (connection != null) {
                connection.close();
            }
        }

        return searchEntries;
    }

    /**
     * Maps a given String to a SearchScope. The SearchScope.BASE is the default
     * 
     * @param searchScopeString
     *            which will be parsed
     * @return the corresponding SearchScope
     */
    private SearchScope parseScope(String searchScopeString) {
        // Map SearchScope
        SearchScope searchScope = null;
        switch (searchScopeString) {
        case "SUB":
            searchScope = SearchScope.SUB;
            break;
        case "SUBORDINATE_SUBTREE":
            searchScope = SearchScope.SUBORDINATE_SUBTREE;
            break;
        case "ONE":
            searchScope = SearchScope.ONE;
            break;
        default:
        case "BASE":
            searchScope = SearchScope.BASE;
            break;
        }
        return searchScope;
    }
}
