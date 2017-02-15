JavaSDK Releases
================

The release process for JavaSDK is as follows:

1. Ensure all required changes have been merged

1. Check out the master branch and ensure there are no local changes

1. Change version of JavaSDK, *{major}.{minor}.{patch}*, e.g. 1.0.0:  
`mvn versions:set -DgenerateBackupPoms=false -DartifactId=* -DgroupId=com.opengamma.sdk`

1. Create Git commit, with message such as "Release v1.0.0", and push

1. Add Git tag (beginning with `v`):  
`git tag -a v1.0.0 -m "Release v1.0.0"`

1. Push the tag:  
`git push --follow-tags`

1. Travis will automatically detect the new tag and perform a build.
Being a release tag, beginning with `v`, additional operations are triggered during the build which 
will perform the deployment to Bintray.
Note that there will be a concurrent build for the earlier push to master which will behave normally.

1. At Bintray, publish the files.
Edit the description of the version, adding the release date, description and VCS tag.
Ensure the readme and release notes tags are correctly setup.

1. Publish the version to Maven Central from Bintray using the appropriate password.

1. Bump version of JavaSDK to SNAPSHOT, e.g. 1.1.0-SNAPSHOT:  
`mvn versions:set -DgenerateBackupPoms=false -DartifactId=* -DgroupId=com.opengamma.sdk`

1. Create Git commit, with message such as "Bump version", and push
