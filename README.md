# gradle-publish-plugin
Gradle plugin for publishing artifact to nexus or maven.

# Usage

## Step 1
Add repository in build.gradle of project:

```
maven {url 'http://nenux.228318my.cn/repository/maven-releases/'}
```

## Step 2
Add classpath dependency in build.gradle of project:

```
classpath 'com.lm.plugin:publish-artifact:1.0.0'
```

## Step 3
Apply plugin in build.gradle of module:

```
apply plugin: 'com.lm.plugin.artifact'
```

## Step 4
Create artifact.properties file in module dir:

```
GROUP=
VERSION_NAME=
POM_ARTIFACT_ID=

SNAPSHOT_REPOSITORY_URL=
RELEASE_REPOSITORY_RUL=

USERNAME=
PASSWORD=
```

## Step 5
Run uploadArchives task.