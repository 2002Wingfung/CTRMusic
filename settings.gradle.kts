pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven{ setUrl("https://maven.aliyun.com/repository/jcenter" )}
        maven { setUrl("https://jitpack.io" ) }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven{ setUrl("https://maven.aliyun.com/repository/jcenter" )}
        maven { setUrl("https://jitpack.io" ) }
    }
}

rootProject.name = "CTRMusic"
include(":app")
include(":JetpackMVVM")
include(":Center")
