package com.googlecode.wicket.jquery.ui.samples.kendoui.datetimepicker;

import java.util.Date;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;

import com.googlecode.wicket.kendo.ui.form.button.AjaxButton;
import com.googlecode.wicket.kendo.ui.form.button.Button;
import com.googlecode.wicket.kendo.ui.form.datetime.AjaxTimePicker;
import com.googlecode.wicket.kendo.ui.form.datetime.TimePicker;
import com.googlecode.wicket.kendo.ui.panel.KendoFeedbackPanel;

public class AjaxTimePickerPage extends AbstractTimePickerPage
{
	private static final long serialVersionUID = 1L;

	public AjaxTimePickerPage()
	{
		Form<?> form = new Form<Void>("form");
		this.add(form);

		// FeedbackPanel //
		final KendoFeedbackPanel feedback = new KendoFeedbackPanel("feedback");
		form.add(feedback);

		// TimePicker //
		final TimePicker timepicker = new AjaxTimePicker("timepicker", Model.of(new Date())) {

			private static final long serialVersionUID = 1L;

			@Override
			public void onValueChanged(IPartialPageRequestHandler handler)
			{
				this.info("Value Changed: " + this.getModelObject());

				handler.add(feedback);
			}
		};

		form.add(timepicker);

		// Buttons //
		form.add(new Button("submit") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit()
			{
				this.info("Time: " + timepicker.getModelObject()); // warning, model object can be null
			}
		});

		form.add(new AjaxButton("button") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form)
			{
				this.info("Time: " + timepicker.getModelObject()); // warning, model object can be null
				target.add(feedback);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form)
			{
				target.add(feedback);
			}
		});
	}
}
