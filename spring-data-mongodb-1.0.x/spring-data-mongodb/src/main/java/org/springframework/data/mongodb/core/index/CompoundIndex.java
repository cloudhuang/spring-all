/*
 * Copyright 2011-2012 the original author or authors.
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
package org.springframework.data.mongodb.core.index;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a class to use compound indexes.
 * 
 * @author Jon Brisbin
 * @author Oliver Gierke
 */
@Target({ ElementType.TYPE })
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface CompoundIndex {

	/**
	 * The actual index definition in JSON format. The keys of the JSON document are the fields to be indexed, the values
	 * define the index direction (1 for ascending, -1 for descending).
	 * 
	 * @return
	 */
	String def();

	/**
	 * It does not actually make sense to use that attribute as the direction has to be defined in the {@link #def()}
	 * attribute actually.
	 * 
	 * @return
	 */
	@Deprecated
	IndexDirection direction() default IndexDirection.ASCENDING;

	boolean unique() default false;

	boolean sparse() default false;

	boolean dropDups() default false;

	/**
	 * The name of the index to be created.
	 * 
	 * @return
	 */
	String name() default "";

	/**
	 * The collection the index will be created in. Will default to the collection the annotated domain class will be
	 * stored in.
	 * 
	 * @return
	 */
	String collection() default "";
}
