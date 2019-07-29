Gradle Changelog Plugin
=====================

This plugin allows to maintain changelog for each release.

# Task Configuration

In order to update changelog file with the latest release version, you must configure the gradle task

    tasks.release.finalizedBy tasks.releaseChangelog


# Extension Provided
    changelog {
        String changelogFile = 'changelog.md'
        
        String nextReleasePlaceholder = '## [NEXT RELEASE]'
        String nextReleaseMessage = 'Add changes here...'
        Closure<String> releaseMessageFormat = { version -> "## $version ${new Date().format('yyyy-MM-dd')}" }
        
        String commitMessage = '[skip ci] update changelog'
        
        String remote = 'origin'
        boolean validateChangelog = true
    }

 
| Property                | Type              | Default                                                                   | Description                                                          |
| ----------------------- | ----------------- | ------------------------------------------------------------------------- | ---------------------------------------------------------------------|
| changelogFile           | `String`          | `changelog.md`                                                            |  name of changelog file in the project root                          |
| nextReleasePlaceholder  | `String`          | `## [NEXT RELEASE]`                                                       |  placeholder which will be replaced with the latest release version  |
| nextReleaseMessage      | `String`          | `Add changes here...`                                                     |  Dummy message to be replaced with the exact release changes         |
| commitMessage           | `String`          | `[skip ci] update changelog`                                              |  Commit message to be used for the changelog file                    |
| releaseMessageFormat    | `Closure<String>` | `{ version -> "## $version ${new Date().format('yyyy-MM-dd')}" }`         |  Closure which returns header for a current release                  |
| remote                  | `String`          | `origin`                                                                  |  Name of the remote repository                                       |
| validate                | `boolean`         | `true`                                                                    |  Validate changelog contents before release                          |

# Tasks Provided

* initChangelog - creates new empty changelog.md file in the project root
* releaseChangelog - puts information about latest released version to the changelog file, commits the changes

# Providing branch or tag name from the cmd line
By default the changes made to the changelog file will be commited to the current branch
In case when the changes must be submitted to a specific branch, set the branch property:

    ./gradlew -Pbranch=<branch name>

It's also possible to specify a tag, the branch will be taken by this tag and the changed changelog file will be commited to this branch

    ./gradlew -Ptag=<tag name>
    
Grgit library is used (https://github.com/ajoberstar/grgit), in order to authenticate git requests, please refer to this page:
https://ajoberstar.org/grgit/grgit-authentication.html

Gradle and Java Compatibility
=============================

Built with OpenJDK8
Tested with OpenJDK8

| Gradle Version | Works |
| :------------: | :---: |
| 5.4            | yes   |

LICENSE
=======

Copyright 2014-2019 https://github.com/daveight

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

<http://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.