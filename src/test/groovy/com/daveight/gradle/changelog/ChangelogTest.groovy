package com.daveight.gradle.changelog

import org.gradle.api.GradleException
import spock.lang.Specification

class ChangelogTest extends Specification {

    File file
    File projectDir = new File("build")

    def setup() {
        projectDir.mkdirs()
        file = new File(projectDir, 'changelog.md')
    }

    def cleanup() {
        projectDir.delete()
    }

    def writeText(text) {
        file.newWriter().withWriter { w -> w << text }
    }

    def "test creation of changelog.md file"() {
        given:
        def ext = new ChangelogPluginExtension()
        def changelog = new Changelog(projectDir, ext)
        when:
        changelog.create()
        then:
        file.text == '## [NEXT RELEASE]\nAdd changes here...'
    }

    def "test modify version, validate header"() {
        given:
        def ext = new ChangelogPluginExtension()
        def changelog = new Changelog(projectDir, ext)
        writeText('## 1.0.0 - Release')
        when:
        changelog.modifyVersion('1.0.0')
        then:
        GradleException ex = thrown()
        ex.message == 'Changelog must start from mandatory header: ## [NEXT RELEASE]'
    }

    def "test modify version, validate first message"() {
        given:
        def ext = new ChangelogPluginExtension()
        def changelog = new Changelog(projectDir, ext)
        writeText('## [NEXT RELEASE]\nAdd changes here...')
        when:
        changelog.modifyVersion('1.0.0')
        then:
        GradleException ex = thrown()
        ex.message == "Changelog cannot be released with dummy message"
    }

    def "test modify version, validation disabled"() {
        given:
        def ext = new ChangelogPluginExtension(validateChangelog: false)
        def changelog = new Changelog(projectDir, ext)
        writeText('## 1.0.0 - Release')
        when:
        changelog.modifyVersion('1.0.0')
        then:
        file.text == '## [NEXT RELEASE]\nAdd changes here...\n\n## 1.0.0 - Release'
    }

    def "test modify version, validation enabled"() {
        given:
        def ext = new ChangelogPluginExtension(validateChangelog: false)
        def changelog = new Changelog(projectDir, ext)
        writeText('## [NEXT RELEASE]\nChange 1\nChange 2\n')
        when:
        changelog.modifyVersion('1.0.0')
        then:
        def lines = file.text.readLines()
        lines.size() == 6
        lines[0] == '## [NEXT RELEASE]'
        lines[1] == 'Add changes here...'
        lines[3].startsWith('## 1.0.0')
        lines[4].startsWith('Change 1')
        lines[5].startsWith('Change 2')
    }
}
