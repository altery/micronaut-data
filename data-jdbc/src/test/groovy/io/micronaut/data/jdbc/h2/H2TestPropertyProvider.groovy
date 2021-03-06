/*
 * Copyright 2017-2020 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.data.jdbc.h2

import io.micronaut.test.support.TestPropertyProvider

trait H2TestPropertyProvider implements TestPropertyProvider {
    @Override
    Map<String, String> getProperties() {
        [
                "datasources.default.name": "mydb",
                "datasources.default.schema-generate": "CREATE_DROP",
                "datasources.default.dialect": "H2",
        ] as Map<String, String>
    }

}