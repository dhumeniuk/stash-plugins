package edu.udayton.udri.stash.hook;

import com.atlassian.stash.commit.CommitService;
import com.atlassian.stash.content.Changeset;
import com.atlassian.stash.content.ChangesetsBetweenRequest;
import com.atlassian.stash.hook.*;
import com.atlassian.stash.hook.repository.*;
import com.atlassian.stash.repository.*;
import com.atlassian.stash.util.*;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MergeOnlyBranchesHook implements PreReceiveRepositoryHook
{
    private static final PageRequestImpl PAGE_REQUEST = new PageRequestImpl(0, 100);
    
    private final CommitService commitService;

    private final Map<String, Set<Rule>> branchRules= new HashMap<String, Set<Rule>>();
    
    public MergeOnlyBranchesHook(CommitService commitService) 
    {
        this.commitService = commitService;
        
        // disabled for now, need a rule so that only fast forward merges from dev can happen, as it is now, rule doesn't work
        //branchRules.put("refs/heads/master", Sets.newHashSet(Rule.PULL_REQUEST, Rule.NO_CONFLICT_MERGE));
        
        branchRules.put("refs/heads/development", Sets.newHashSet(Rule.PULL_REQUEST));
        branchRules.put("refs/heads/release/.*", Sets.newHashSet(Rule.PULL_REQUEST));
        branchRules.put("refs/heads/integration/.*", Sets.newHashSet(Rule.PULL_REQUEST, Rule.NO_CONFLICT_MERGE));
    }
    
    /**
     * Disables deletion of branches
     */
    @Override
    public boolean onReceive(RepositoryHookContext context, Collection<RefChange> refChanges, HookResponse hookResponse)
    {
        Repository repository = context.getRepository();

        for (RefChange change : refChanges)
        {
            for (String branchRegex : branchRules.keySet())
            {
                if (change.getRefId().matches(branchRegex))
                {
                	// found rules for the branch being updated, see which applies
                	if (branchRules.get(branchRegex).contains(Rule.NO_CONFLICT_MERGE))
                	{
                	    for (Changeset changeSet : getChangesetsBetween(repository, change))
	                    {
                	    	if (changeSet.getMessage().startsWith("Merge branch"))
                    		{
	                        	// found a change set with a merge, make sure there are no conflicts
	                        	if (changeSet.getMessage().contains("Conflicts:"))
	                        	{
	                        		hookResponse.err().format(
	                        				"%nYou may not push merges with conflicts to %s (matched %s)%nPlease use a pull request.%n%n", 
	                        				change.getRefId(), branchRegex);
	                                return false;
	                        	}
                    		}
	                    }
                	}
                	else
                	{
	                    // found a commit push were only pull requests are allowed
	                    hookResponse.err().format("%nYou may not push (even merge commits) directly to %s (matched %s)%n"
	                    							+ "Please use a pull request.%n%n", change.getRefId(), branchRegex);
	                    return false;
                	}
                }
            }
        }
        return true;
    }

    private Iterable<Changeset> getChangesetsBetween(final Repository repository, final RefChange refChange) {
        return new PagedIterable<Changeset>(new PageProvider<Changeset>() {
            @Override
            public Page<Changeset> get(PageRequest pageRequest) {
                return commitService.getChangesetsBetween(new ChangesetsBetweenRequest.Builder(repository)
                        .exclude(refChange.getFromHash())
                        .include(refChange.getToHash())
                        .build(), pageRequest);
            }
        }, PAGE_REQUEST);
    }
}
