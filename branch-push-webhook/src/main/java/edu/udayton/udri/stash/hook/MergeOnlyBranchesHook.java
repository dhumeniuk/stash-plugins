package edu.udayton.udri.stash.hook;

import com.atlassian.stash.hook.*;
import com.atlassian.stash.hook.repository.*;
import com.atlassian.stash.repository.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MergeOnlyBranchesHook implements PreReceiveRepositoryHook
{
    /**
     * Disables deletion of branches
     */
    @Override
    public boolean onReceive(RepositoryHookContext context, Collection<RefChange> refChanges, HookResponse hookResponse)
    {
        List<String> branchRegexes = new ArrayList<String>();
        branchRegexes.add("refs/heads/master");
        branchRegexes.add("refs/heads/development");
        branchRegexes.add("refs/heads/release/.*");
        branchRegexes.add("refs/heads/integration/.*");

        for (RefChange change : refChanges)
        {
            for (String branchRegex : branchRegexes)
            {
                if (change.getRefId().matches(branchRegex))
                {
                    // This hook is only called for pushes, not pull requests (merges), so deny this change.
                    hookResponse.err().format("%nYou may not push directly to %s (matched %s)%nPlease use a pull request.%n", change.getRefId(), branchRegex);
                    return false;
                }
            }
        }
        return true;
    }
}
