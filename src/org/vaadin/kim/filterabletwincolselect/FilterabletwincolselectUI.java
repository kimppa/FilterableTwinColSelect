package org.vaadin.kim.filterabletwincolselect;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * Main UI class
 */
@SuppressWarnings("serial")
public class FilterabletwincolselectUI extends UI {

	private FilterableTwinColSelect select;
	private IndexedContainer container;

	@Override
	protected void init(VaadinRequest request) {
		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		setContent(layout);

		Button button = new Button("Change item caption");
		button.addClickListener(new Button.ClickListener() {
			private int i = 0;

			public void buttonClick(ClickEvent event) {
				select.setItemCaption(2, "Sweden " + i++);
			}
		});
		layout.addComponent(button);
		
		Button button2 = new Button("Get item caption");
		button2.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				Notification.show(select.getItemCaption(2));
			}
		});
		layout.addComponent(button2);
		Button button3 = new Button("Add item");
		button3.addClickListener(new Button.ClickListener() {
			@SuppressWarnings("unchecked")
			public void buttonClick(ClickEvent event) {
				Object itemId = container.addItem();
				Item item = container.getItem(itemId);
				item.getItemProperty("name").setValue("Iceland");
			}
		});
		layout.addComponent(button3);

		container = new IndexedContainer();
		container.addContainerProperty("name", String.class, null);
		addCountry(container, "Finland");
		addCountry(container, "Sweden");
		addCountry(container, "Norway");
		addCountry(container, "Denmark");

		select = new FilterableTwinColSelect();
		layout.addComponent(select);
		select.setContainerDataSource(container);
		select.setItemCaptionPropertyId("name");

		Label label = new Label();
		label.setConverter(new CollectionToStringConverter());
		label.setPropertyDataSource(select);

		layout.addComponent(label);

	}

	private void addCountry(IndexedContainer container, String name) {
		Object itemId = container.addItem();
		Item item = container.getItem(itemId);
		item.getItemProperty("name").setValue(name);
	}

}