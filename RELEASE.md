Releases
========

The release process is as follows:

1. Ensure all required changes have been merged

1. Check out the master branch and ensure there are no local changes

1. Update CHANGELOG.md and version in README.md, committing the changes

1. Initiate the release by creating a remote tag:

    ```git push origin HEAD:refs/tags/release```

The Maven release plugin will run in CI and will create and push two commits in git,
one with the release version and one with the following snapshot.
It will also create and push a tag that actually performs the release.
