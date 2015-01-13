# stash-plugins

Repo contains some basic Atlassian Stash plug-in projects.

## branch-push-webhook

Prevents a user from pushing code to certain branches. This way you can make sure a pull request is used before code gets into
an upstream branch like master.

## merge-target-webhook

Prevents a user from merging a pull request unless it is targeting a certain branch. This way you can keep someone from 
accidentally merging a pull request into someone else's feature branch.
