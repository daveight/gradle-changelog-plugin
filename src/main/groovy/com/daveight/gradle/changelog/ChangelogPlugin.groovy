package com.daveight.gradle.changelog

import org.gradle.api.Plugin
import org.gradle.api.Project

class ChangelogPlugin implements Plugin<Project> {

    private static final String INIT_CHANGELOG_TASK_NAME = 'initChangelog'
    private static final String RELEASE_CHANGELOG_TASK_NAME = 'releaseChangelog'

    @Override
    void apply(Project project) {
        def extension = project.extensions.create('changelog', ChangelogPluginExtension)

        def git = new Git(project, extension)
        def changelog = new Changelog(project.projectDir, extension)

        project.tasks.create(RELEASE_CHANGELOG_TASK_NAME) {
            description = 'Updates changelog with the current releases version'
            doLast {
                changelog.modifyVersion(project.version.toString())
                git.commitChanges()
            }
        }

        project.tasks.create(INIT_CHANGELOG_TASK_NAME) {
            description = 'Create fresh copy of changelog file'
            doLast {
                changelog.create()
            }
        }
    }
}
