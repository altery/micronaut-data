/*
 * Copyright 2017-2019 original authors
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
package io.micronaut.data.processor.visitors

import io.micronaut.data.annotation.Query
import io.micronaut.data.intercept.annotation.PredatorMethod
import io.micronaut.data.model.Pageable
import io.micronaut.data.model.entities.Person
import io.micronaut.inject.BeanDefinition
import io.micronaut.inject.ExecutableMethod

class DtoSpec extends AbstractPredatorSpec {

    void "test build repository with DTO projection - invalid types"() {
        when:
        buildRepository('test.MyInterface' , """

import io.micronaut.data.model.entities.Person;
import io.micronaut.core.annotation.Introspected;

@Repository
interface MyInterface extends GenericRepository<Person, Long> {

    List<PersonDto> list(String name);
}

@Introspected
class PersonDto {
    private int name;
    
    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }
    
}
""")
        then:
        def e = thrown(RuntimeException)
        e.message.contains('Property [name] of type [int] is not compatible with equivalent property declared in entity: io.micronaut.data.model.entities.Person')
    }


    void "test build repository with DTO projection"() {
        when:
        def repository = buildRepository('test.MyInterface', """

import io.micronaut.data.model.entities.Person;
import io.micronaut.core.annotation.Introspected;

@Repository
interface MyInterface extends GenericRepository<Person, Long> {

    List<PersonDto> list(String name);
    
    PersonDto find(String name);
    
    Page<PersonDto> searchByNameLike(String title, Pageable pageable);
    
    java.util.stream.Stream<PersonDto> queryByNameLike(String title, Pageable pageable);
}

@Introspected
class PersonDto {
    private String name;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}
""")
        then:
        repository != null
        def method = repository.getRequiredMethod("list", String)
        def ann = method.synthesize(PredatorMethod)
        ann.resultType().name.contains("PersonDto")
        ann.rootEntity() == Person
        method.synthesize(Query).value() == "SELECT person.name AS name FROM $Person.name AS person WHERE (person.name = :p1)"
        method.isTrue(PredatorMethod, PredatorMethod.META_MEMBER_DTO)

        and:
        def findMethod = repository.getRequiredMethod("find", String)
        findMethod.synthesize(PredatorMethod).resultType().simpleName == "PersonDto"

        and:
        def pageMethod = repository.getRequiredMethod("searchByNameLike", String, Pageable)
        pageMethod.synthesize(PredatorMethod).resultType().simpleName == "PersonDto"

        and:
        def streamMethod = repository.getRequiredMethod("queryByNameLike", String, Pageable)
        streamMethod.synthesize(PredatorMethod).resultType().simpleName == "PersonDto"
    }
}