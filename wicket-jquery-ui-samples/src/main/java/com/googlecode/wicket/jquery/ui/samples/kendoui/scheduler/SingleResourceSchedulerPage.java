package com.googlecode.wicket.jquery.ui.samples.kendoui.scheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.core.template.IJQueryTemplate;
import com.googlecode.wicket.jquery.core.template.JQueryTemplate;
import com.googlecode.wicket.jquery.ui.samples.data.dao.scheduler.ResourceEventsDAO;
import com.googlecode.wicket.kendo.ui.form.button.AjaxButton;
import com.googlecode.wicket.kendo.ui.form.multiselect.MultiSelect;
import com.googlecode.wicket.kendo.ui.panel.KendoFeedbackPanel;
import com.googlecode.wicket.kendo.ui.scheduler.ISchedulerVisitor;
import com.googlecode.wicket.kendo.ui.scheduler.Scheduler;
import com.googlecode.wicket.kendo.ui.scheduler.SchedulerEvent;
import com.googlecode.wicket.kendo.ui.scheduler.SchedulerModel;
import com.googlecode.wicket.kendo.ui.scheduler.resource.Resource;
import com.googlecode.wicket.kendo.ui.scheduler.resource.ResourceList;

public class SingleResourceSchedulerPage extends AbstractSchedulerPage
{
	private static final long serialVersionUID = 1L;
	private static final String AGENDA_1 = "Sebastien";
	private static final String AGENDA_2 = "Samantha";

	private List<String> agendas;
	private ResourceList resources;

	public SingleResourceSchedulerPage()
	{
		// Form //
		final Form<Date> form = new Form<Date>("form");
		this.add(form);

		// FeedbackPanel //
		final KendoFeedbackPanel feedback = new KendoFeedbackPanel("feedback");
		form.add(feedback.setOutputMarkupId(true));

		// MultiSelect //
		this.agendas = new ArrayList<String>();
		this.agendas.add(AGENDA_1);

		final MultiSelect<String> multiselect = new MultiSelect<String>("select", Model.ofList(this.agendas), Arrays.asList(AGENDA_1, AGENDA_2));
		form.add(multiselect);

		// Scheduler //
		this.resources = newResourceList();

		Options options = new Options();
		options.set("date", "Date.now()");
		options.set("editable", true); // default
		options.set("views", "[ { type: 'day', showWorkHours: true }, { type: 'week', showWorkHours: true }, { type: 'month', selected: true }, { type: 'timeline' } ]");
		options.set("workDayStart", "new Date('2014/1/1 08:00 AM')");
		options.set("workDayEnd", "new Date('2014/1/1 6:00 PM')");

		final Scheduler scheduler = new Scheduler("scheduler", newSchedulerModel(), this.resources, options) {

			private static final long serialVersionUID = 1L;

			@Override
			public void onCreate(AjaxRequestTarget target, SchedulerEvent event)
			{
				ResourceEventsDAO.get().create(event);

				this.info("Created: " + event);
				target.add(feedback);
			}

			@Override
			public void onUpdate(AjaxRequestTarget target, SchedulerEvent event)
			{
				ResourceEventsDAO.get().update(event);

				this.info("Updated: " + event);
				target.add(feedback);
			}

			@Override
			public void onDelete(AjaxRequestTarget target, SchedulerEvent event)
			{
				ResourceEventsDAO.get().delete(event);

				this.info("Deleted: " + event);
				target.add(feedback);
			}

			@Override
			protected IJQueryTemplate newEventTemplate()
			{
				return new JQueryTemplate() {

					private static final long serialVersionUID = 1L;

					@Override
					public String getText()
					{
						return "# if (!isAllDay) { #" // lf
								+ "#= kendo.toString(start, 'hh:mm') # - " // lf
								+ "# } #" // lf
								+ "#= title #";
					}
				};
			}
		};

		form.add(scheduler);

		// Buttons //

		form.add(new AjaxButton("refresh") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form)
			{
				scheduler.refresh(target);
			}
		});
	}

	// Properties //

	protected boolean isAgendaSelected(Integer agendaId)
	{
		String agendaName = "";

		for (Resource resource : this.resources)
		{
			if (agendaId.equals(resource.getId()))
			{
				agendaName = resource.getText();
				break;
			}
		}

		return this.agendas.contains(agendaName);
	}

	// Factories //

	private SchedulerModel newSchedulerModel()
	{
		return new MySchedulerModel() {

			private static final long serialVersionUID = 1L;

			@Override
			public void visit(SchedulerEvent event)
			{
				Integer agendaId = event.getValue(ResourceEventsDAO.AGENDA_ID, Integer.class);

				if (agendaId != null)
				{
				    event.setVisible(isAgendaSelected(agendaId));
				}
			}
		};
	}

	static ResourceList newResourceList()
	{
		ResourceList list = new ResourceList("Agenda", ResourceEventsDAO.AGENDA_ID);
		list.add(new Resource(1, AGENDA_1, "#6699cc"));
		list.add(new Resource(2, AGENDA_2, "#cc6699"));

		return list;
	}

	// Classes //

	abstract static class MySchedulerModel extends SchedulerModel implements ISchedulerVisitor
	{
		private static final long serialVersionUID = 1L;

		@Override
		protected List<SchedulerEvent> load()
		{
			return ResourceEventsDAO.get().getEvents(this.getStart(), this.getEnd());
		}
	}
}
