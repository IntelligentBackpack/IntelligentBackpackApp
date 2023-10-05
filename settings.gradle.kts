pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("com.gradle.enterprise") version "3.15.1"
    id("org.danilopianini.gradle-pre-commit-git-hooks") version "1.1.10"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
    }
}

gitHooks {
    preCommit {
        tasks("ktlintCheck")
    }
    commitMsg { conventionalCommits() }
    createHooks(true)
}

rootProject.name = "IntelligentBackpackApp"
include(":app")
include(":accessDomain")
include(":accessData")
include(":desktopDomain")
include(":desktopData")
include(":schoolDomain")
include(":schoolData")
include(":reminderDomain")
include(":reminderData")
include(":networkUtility")
