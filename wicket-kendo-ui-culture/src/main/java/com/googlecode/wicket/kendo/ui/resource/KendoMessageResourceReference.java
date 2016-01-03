/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.wicket.kendo.ui.resource;

import java.util.List;

import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.resource.JQueryPluginResourceReference;
import org.apache.wicket.util.lang.Generics;

import com.googlecode.wicket.kendo.ui.KendoMessage;
import com.googlecode.wicket.kendo.ui.settings.KendoUILibrarySettings;

/**
 * The resource reference for the Kendo Message javascript library.
 *
 * @author Sebastien Briquet - sebfz1
 *
 */
public class KendoMessageResourceReference extends JQueryPluginResourceReference
{
	private static final long serialVersionUID = 1L;

	public static final String FILENAME_PATTERN = "messages/kendo.messages.%s.js";

	/**
	 * Constructor
	 *
	 * @param message the {@link KendoMessage}
	 */
	public KendoMessageResourceReference(KendoMessage message)
	{
		this(message.toString());
	}

	/**
	 * Constructor
	 *
	 * @param language the language, ie: 'fr-FR'
	 */
	public KendoMessageResourceReference(String language)
	{
		super(KendoMessageResourceReference.class, String.format(FILENAME_PATTERN, language));
	}

	public Iterable<? extends HeaderItem> getDependencies()
	{
		List<HeaderItem> dependencies = Generics.newArrayList();

		for (HeaderItem item : super.getDependencies())
		{
			dependencies.add(item);
		}

		dependencies.add(JavaScriptHeaderItem.forReference(KendoUILibrarySettings.get().getJavaScriptReference()));

		return dependencies;
	}
}
