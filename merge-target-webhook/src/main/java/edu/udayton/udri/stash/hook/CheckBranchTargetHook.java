package edu.udayton.udri.stash.hook;

import com.atlassian.stash.hook.repository.*;
import com.atlassian.stash.pull.PullRequestParticipant;
import com.atlassian.stash.pull.PullRequestRef;
import com.atlassian.stash.repository.Repository;
import com.atlassian.stash.setting.*;

import java.util.Set;
import java.util.HashSet;

public class CheckBranchTargetHook implements RepositoryMergeRequestCheck, RepositorySettingsValidator
{
    private final Set<String> validTargetPatterns = new HashSet<>();

    public CheckBranchTargetHook()
    {
        validTargetPatterns.add("refs/heads/development");
        validTargetPatterns.add("refs/heads/integration/.*");
        validTargetPatterns.add("refs/heads/release/.*");
    }

    /**
     * Vetos a pull-request if there aren't enough reviewers.
     */
    @Override
    public void check(RepositoryMergeRequestCheckContext context)
    {
        PullRequestRef toRef = context.getMergeRequest().getPullRequest().getToRef();

        for (String validTargetPattern : validTargetPatterns)
        {
            if (toRef.getId().matches(validTargetPattern))
            {
                // target is valid, we are done
                return;
            }
        }

        context.getMergeRequest().veto("Not a valid merge target", 
            String.format("'%s' is not a valid merge target. Must match patterns: %s", toRef.getDisplayId(), validTargetPatterns));
    }

    @Override
    public void validate(Settings settings, SettingsValidationErrors errors, Repository repository)
    {
        /*String numReviewersString = settings.getString("reviewers", "0").trim();
        if (Integer.parseInt(numReviewersString) <= 0)
        {
            errors.addFieldError("reviewers", "Number of reviewers must be greater than zero");
        }*/
    }
}
