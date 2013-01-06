/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.api.publish.maven

import org.gradle.integtests.fixtures.CrossVersionIntegrationSpec
import org.gradle.integtests.fixtures.TargetVersions
import org.gradle.test.fixtures.maven.MavenFileRepository
import org.gradle.test.fixtures.server.http.HttpServer
import org.junit.Rule

@TargetVersions('1.0+')
class MavenPublishCrossVersionIntegrationTest extends CrossVersionIntegrationSpec {

    @Rule public final HttpServer server = new HttpServer()
    final MavenFileRepository repo = new MavenFileRepository(file("maven-repo"))

    def "uses cached artifacts from previous Gradle version when no sha1 header"() {
        given:
        projectPublishedWithCurrentVersionUsingMavenPublishPlugin()

        expect:
        canRetrievePublicationWithPreviousVersion()
    }

    def projectPublishedWithCurrentVersionUsingMavenPublishPlugin() {
        settingsFile.text = "rootProject.name = 'published'"

        buildFile.text = """
apply plugin: 'java'
apply plugin: 'maven-publish'

group = 'org.gradle.crossversion'
version = '1.9'

repositories {
    mavenCentral()
}
dependencies {
    compile "commons-collections:commons-collections:3.0"
}
publishing {
    repositories {
        maven { url "${repo.uri}" }
    }
}
"""

        version current withTasks 'publish' run()
    }

    def canRetrievePublicationWithPreviousVersion() {
        settingsFile.text = "rootProject.name = 'consumer'"

        buildFile.text = """
configurations {
    lib
}
repositories {
    mavenCentral()
    maven { url "${repo.uri}" }
}
dependencies {
    lib 'org.gradle.crossversion:published:1.9'
}
task retrieve(type: Sync) {
    into 'build'
    from configurations.lib
}
"""

        version previous withTasks 'retrieve' run()

        file('build').assertHasDescendants('published-1.9.jar', 'commons-collections-3.0.jar')
    }
}