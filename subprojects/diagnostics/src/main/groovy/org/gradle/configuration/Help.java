/*
 * Copyright 2010 the original author or authors.
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
package org.gradle.configuration;

import org.gradle.api.DefaultTask;
import org.gradle.api.internal.tasks.CommandLineOption;
import org.gradle.api.tasks.TaskAction;
import org.gradle.execution.TaskSelector;
import org.gradle.initialization.BuildClientMetaData;
import org.gradle.logging.StyledTextOutput;
import org.gradle.logging.StyledTextOutputFactory;
import org.gradle.util.GradleVersion;

import static org.gradle.logging.StyledTextOutput.Style.UserInput;

public class Help extends DefaultTask {
    private String taskPath;

    @TaskAction
    void displayHelp() {
        StyledTextOutput output = getServices().get(StyledTextOutputFactory.class).create(Help.class);
        BuildClientMetaData metaData = getServices().get(BuildClientMetaData.class);
        if (taskPath != null) {
            printTaskHelp(output);
        } else {
            printDefaultHelp(output, metaData);
        }
    }

    private void printTaskHelp(StyledTextOutput output) {
        TaskSelector selector = getServices().get(TaskSelector.class);
        final TaskSelector.TaskSelection selection = selector.getSelection(taskPath);
        TaskDetailPrinter taskDetailPrinter = new TaskDetailPrinter(taskPath, selection);
        taskDetailPrinter.print(output);
    }

    private void printDefaultHelp(StyledTextOutput output, BuildClientMetaData metaData) {
        output.println();
        output.formatln("Welcome to Gradle %s.", GradleVersion.current().getVersion());
        output.println();
        output.text("To run a build, run ");
        metaData.describeCommand(output.withStyle(UserInput), "<task> ...");
        output.println();
        output.println();
        output.text("To see a list of available tasks, run ");
        metaData.describeCommand(output.withStyle(UserInput), "tasks");
        output.println();
        output.println();
        output.text("To see a list of command-line options, run ");
        metaData.describeCommand(output.withStyle(UserInput), "--help");
        output.println();
    }

    @CommandLineOption(options = "task", description = "The task, detailed help is requested for.")
    public void setTaskPath(String taskPath) {
        this.taskPath = taskPath;
    }
}
