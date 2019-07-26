package com.lm.plugin.artifact

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.api.artifacts.Dependency

class UploadPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.plugins.apply('maven')
        // read properties
        Properties properties = new Properties()
        properties.load(new FileInputStream(new File(project.getProjectDir(), 'artifact.properties')))

        project.tasks.create(name: 'androidJavadocs', type: Javadoc, {
            exclude '**/*.kt'
            source = project.android.sourceSets.main.java.srcDirs
            classpath += project.files(project.android.getBootClasspath().join(File.pathSeparator))

            //包含module路徑
            project.android.libraryVariants.all { variant ->
                classpath += variant.javaCompileProvider.get().classpath
            }
        })
        project.tasks.create(name: 'androidJavadocsJar', type: Jar, dependsOn: project.tasks['androidJavadocs'], {
            classifier = 'javadoc'
            from project.tasks['androidJavadocs'].destinationDir
        })
        project.tasks.create(name: 'androidSourcesJar', type: Jar, {
            classifier = 'sources'
            from project.android.sourceSets.main.java.sourceFiles
        })

        project.artifacts.add(Dependency.ARCHIVES_CONFIGURATION, project.tasks['androidSourcesJar'])
        project.artifacts.add(Dependency.ARCHIVES_CONFIGURATION, project.tasks['androidJavadocsJar'])

        project.afterEvaluate {
            project.uploadArchives {
                repositories {
                    mavenDeployer {
                        pom.groupId = getGroup(properties)
                        pom.artifactId = getArtifactId(properties)
                        pom.version = getVersionName(properties)
                        repository(url: getRepositoryUrl(properties)) {
                            authentication(userName: getRepositoryUserName(properties), password: getRepositoryUserPsw(properties))
                        }
                    }
                }
            }
        }
    }

    static def isReleaseBuild(Properties properties) {
        return !properties.getProperty('VERSION_NAME').toUpperCase().contains("SNAPSHOT")
    }

    static def getRepositoryUserName(Properties properties) {
        return properties.getProperty('USERNAME')
    }

    static def getRepositoryUserPsw(Properties properties) {
        return properties.getProperty('PASSWORD')
    }

    static def getRepositoryUrl(Properties properties) {
        return isReleaseBuild(properties) ? properties.getProperty('RELEASE_REPOSITORY_RUL') : properties.getProperty('SNAPSHOT_REPOSITORY_URL')
    }

    static def getGroup(Properties properties) {
        return properties.getProperty('GROUP')
    }

    static def getArtifactId(Properties properties) {
        return properties.getProperty('POM_ARTIFACT_ID')
    }

    static def getVersionName(Properties properties) {
        properties.getProperty('VERSION_NAME')
    }

}

