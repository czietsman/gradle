/*
 * Copyright 2013 the original author or authors.
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

package org.gradle.nativebinaries.language.c.tasks
import org.gradle.api.Incubating
import org.gradle.api.internal.changedetection.state.Hasher
import org.gradle.cache.CacheRepository
import org.gradle.nativebinaries.internal.PlatformToolChain
import org.gradle.nativebinaries.language.c.internal.DefaultCCompileSpec
import org.gradle.nativebinaries.toolchain.internal.NativeCompileSpec

import javax.inject.Inject
/**
 * Compiles C source files into object files.
 */
@Incubating
class CCompile extends AbstractNativeCompileTask {
    @Inject
    CCompile(CacheRepository cacheRepository, Hasher hasher) {
        super(cacheRepository, hasher)
    }

    @Override
    protected NativeCompileSpec createCompileSpec() {
        new DefaultCCompileSpec()
    }

    protected org.gradle.api.internal.tasks.compile.Compiler<NativeCompileSpec> createCompiler(PlatformToolChain toolChain) {
        toolChain.createCCompiler()
    }
}
