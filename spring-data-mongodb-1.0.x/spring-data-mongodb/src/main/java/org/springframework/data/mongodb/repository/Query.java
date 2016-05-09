/*
 * Copyright 2011 the original author or authors.
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
package org.springframework.data.mongodb.repository;

import java.lang.annotation.*;

/**
 * Annotation to declare finder queries directly on repository methods. Both attributes allow using a placeholder
 * notation of {@code ?0}, {@code ?1} and so on.
 * 
 * @author Oliver Gierke
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Query {

	/**
	 * Takes a MongoDB JSON string to define the actual query to be executed. This one will take precendece over the
	 * method name then.
	 * 
	 * @return
	 */
	String value() default "";

	/**
	 * Defines the fields that should be returned for the given query. Note that only these fields will make it into the
	 * domain object returned.
	 * 
	 * @return
	 */
	String fields() default "";
}
