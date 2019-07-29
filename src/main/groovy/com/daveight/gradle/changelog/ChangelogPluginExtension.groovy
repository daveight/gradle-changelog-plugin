package com.daveight.gradle.changelog

class ChangelogPluginExtension {

    // changelog file name
    String changelogFile = 'changelog.md'

    // placeholder which will be replaced by the version of new release
    String nextReleasePlaceholder = '## [NEXT RELEASE]'

    // dummy text message, which will be inserted after the headline after new release
    String nextReleaseMessage = 'Add changes here...'

    // updated changelog will be commited with this message
    String commitMessage = '[skip ci] update changelog'

    // closure which allows to modify format of release headers in the changelog file
    Closure<String> releaseMessageFormat = { version -> "## $version ${new Date().format('yyyy-MM-dd')}" }

    // git remote
    String remote = 'origin'

    // if changelog content must be validated before release
    boolean validateChangelog = true
}
