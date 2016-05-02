package com.googlecode.wicket.jquery.ui.samples;

import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.Session;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.devutils.debugbar.DebugBar;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.resource.TextTemplateResourceReference;
import org.apache.wicket.util.lang.Generics;

import com.googlecode.wicket.jquery.core.JQueryAbstractBehavior;

public abstract class TemplatePage extends WebPage
{
	private static final long serialVersionUID = 1L;

	protected static final String CSS_NONE = "";
	protected static final String CSS_HOME = "home";
	protected static final String CSS_KENDO = "kendo";
	protected static final String CSS_JQUERY = "jquery";

	private static MetaDataKey<String> template = new MetaDataKey<String>() {
		private static final long serialVersionUID = 1L;
	};

	public TemplatePage()
	{
		super();

		TemplatePage.initTemplate();

		// debug //
		this.add(new DebugBar("debug", false));

		// buttons //
		this.add(this.newKendoButton("btnKendoUI"));
		this.add(this.newJQueryButton("btnJQueryUI"));
	}

	@Override
	protected void onInitialize()
	{
		super.onInitialize();

		this.add(this.newHtmlClassBehavior());
		this.add(new GoogleAnalyticsBehavior(this));
	}

	// Methods //

	private static void initTemplate()
	{
		if (Session.get().getMetaData(template) == null)
		{
			TemplatePage.applyTemplate(CSS_HOME);
		}
	}

	protected static void resetTemplate()
	{
		TemplatePage.applyTemplate(CSS_NONE);
	}

	protected static void applyTemplate(String cssClass)
	{
		Session.get().setMetaData(template, cssClass);
	}

	// Factories //

	private Link<Void> newKendoButton(String id)
	{
		return new Link<Void>(id) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onConfigure()
			{
				super.onConfigure();

				this.setEnabled(!CSS_KENDO.equals(Session.get().getMetaData(template)));
			}

			@Override
			public void onClick()
			{
				Session.get().setMetaData(template, CSS_KENDO);
			}
		};
	}

	private Link<Void> newJQueryButton(String id)
	{
		return new Link<Void>(id) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onConfigure()
			{
				super.onConfigure();

				this.setEnabled(!CSS_JQUERY.equals(Session.get().getMetaData(template)));
			}

			@Override
			public void onClick()
			{
				Session.get().setMetaData(template, CSS_JQUERY);
			}
		};
	}

	private JQueryAbstractBehavior newHtmlClassBehavior()
	{
		return new JQueryAbstractBehavior() {

			private static final long serialVersionUID = 1L;

			@Override
			protected String $()
			{
				return String.format("$('html').addClass('%s');", Session.get().getMetaData(template));
			}
		};
	}

	// Classes //

	static class GoogleAnalyticsBehavior extends Behavior
	{
		private static final long serialVersionUID = 1L;

		private final String url;

		public GoogleAnalyticsBehavior(final WebPage page)
		{
			this.url = GoogleAnalyticsBehavior.getUrl(page);
		}

		private IModel<Map<String, Object>> newResourceModel()
		{
			Map<String, Object> map = Generics.newHashMap();
			map.put("url", this.url);

			return Model.ofMap(map);
		}

		private ResourceReference newResourceReference()
		{
			return new TextTemplateResourceReference(GoogleAnalyticsBehavior.class, "gaq.js", this.newResourceModel());
		}

		@Override
		public void renderHead(Component component, IHeaderResponse response)
		{
			super.renderHead(component, response);

			response.render(JavaScriptHeaderItem.forReference(this.newResourceReference(), "gaq"));
		}

		private static String getUrl(WebPage page)
		{
			Url pageUrl = Url.parse(page.urlFor(page.getClass(), null).toString());
			Url baseUrl = new Url(page.getRequestCycle().getUrlRenderer().getBaseUrl());

			baseUrl.resolveRelative(pageUrl);

			return String.format("%s/%s", page.getRequest().getContextPath(), baseUrl);
		}
	}
}
