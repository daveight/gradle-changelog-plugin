package com.daveight.gradle.changelog

import org.gradle.api.GradleException
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging

import java.util.regex.Pattern

class Changelog {

    static Logger logger = Logging.getLogger(Changelog)

    private File changelogFile
    private Closure<String> releaseMessageFormat
    private String nextReleasePlaceholder
    private String nextReleaseMessage
    private boolean mustValidate

    Changelog(File projectDir, ChangelogPluginExtension extension) {
        changelogFile = new File(projectDir, extension.changelogFile)
        releaseMessageFormat = extension.releaseMessageFormat
        nextReleaseMessage = extension.nextReleaseMessage
        nextReleasePlaceholder = extension.nextReleasePlaceholder
        mustValidate = extension.validateChangelog
    }

    void create() {
        logger.info "creating new changelog file: ${changelogFile.name} in the project root"
        String text = """|$nextReleasePlaceholder
            |$nextReleaseMessage""".stripMargin()
        overwriteFile(text)
    }

    void modifyVersion(String version) {
        logger.info "modify version of ${changelogFile.name}"
        if (mustValidate) {
            validate()
        }
        String releaseMessage = releaseMessageFormat(version)
        String text = changelogFile.text
        text = text.replaceFirst('^' + Pattern.quote(nextReleasePlaceholder), releaseMessage)
        text = """|$nextReleasePlaceholder
           |$nextReleaseMessage
           |
           |$text""".stripMargin()
        overwriteFile(text)
    }

    private void overwriteFile(String text) {
        changelogFile.newWriter().withWriter { w -> w << text }
    }

    private void validate() {
        def text = changelogFile.text
        if (!text.contains(nextReleasePlaceholder)) {
            throw new GradleException("Changelog must start from mandatory header: ${nextReleasePlaceholder}")
        }
        if (text.readLines().take(2).contains(nextReleaseMessage)) {
            throw new GradleException("Changelog cannot be released with dummy message")
        }
    }
}
