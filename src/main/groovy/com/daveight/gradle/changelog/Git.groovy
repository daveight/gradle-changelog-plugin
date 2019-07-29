package com.daveight.gradle.changelog

import org.ajoberstar.grgit.Branch
import org.ajoberstar.grgit.Grgit
import org.ajoberstar.grgit.operation.BranchListOp
import org.eclipse.jgit.errors.RepositoryNotFoundException
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging

import java.util.regex.Pattern

final class Git {

    static Logger logger = Logging.getLogger(Git)

    private Grgit git
    private String changelogFileName
    private String commitMessage
    private String remote
    private String branch
    private String tag

    Git(Project project, ChangelogPluginExtension extension) {
        def gitRoot = project.hasProperty('git.root') ? project.property('git.root') : project.rootProject.projectDir
        try {
            git = Grgit.open(dir: gitRoot)
        } catch(RepositoryNotFoundException e) {
            logger.warn("Git repository not found at $gitRoot. Use the git.root Gradle property to specify a different directory.", e)
            return
        }
        changelogFileName = extension.changelogFile
        commitMessage = extension.commitMessage
        remote = extension.remote
        branch = project.hasProperty('branch') ? project.property('branch') : null
        tag = project.hasProperty('tag') ? project.property('tag') : null
    }

    void commitChanges() {
        if (branch) {
            commitToBranch(branch)
        } else if (tag) {
            commitToTaggedBranch(tag)
        } else {
            commitToCurrentBranch()
        }
    }

    private void commitToBranch(String branch) {
        checkoutBranch(branch)
        commitToCurrentBranch()
    }

    private void commitToTaggedBranch(String tag) {
        Branch branch = git.branch.list(mode: BranchListOp.Mode.REMOTE, contains: tag).find { it.name != 'HEAD' }
        if (!branch) {
            throw new GradleException("Cannot find branch for the tag: $tag")
        }
        commitToBranch(branch.name.replaceFirst(Pattern.quote("$remote/"), ''))
    }

    private void checkoutBranch(String branch) {
        git.checkout(branch: branch, startPoint: "$remote/$branch", createBranch: true)
    }

    private void commitToCurrentBranch() {
        git.add(patterns: [changelogFileName])
        git.commit(message: [commitMessage])
        git.push()
    }
}
