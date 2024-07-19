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
        maven{setUrl("https://s01.oss.sonatype.org/content/groups/public")}
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven{ setUrl("https://maven.aliyun.com/repository/jcenter" )}
        maven { setUrl("https://jitpack.io" ) }
        maven{setUrl("https://s01.oss.sonatype.org/content/groups/public")}
    }
}

rootProject.name = "CTRMusic"
include(":app")
include(":JetpackMVVM")
include(":Center")
include(":Player")
include(":Utils")
