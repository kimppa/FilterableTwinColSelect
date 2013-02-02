package org.vaadin.kim.filterabletwincolselect;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.vaadin.data.Container;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("rawtypes")
public class FilterableTwinColSelect extends CustomField<Set> implements
		Container.Viewer, Container.ItemSetChangeListener {

	private static final long serialVersionUID = 1L;

	private Container containerDataSource;

	private TextField filterUnselected;

	private TextField filterSelected;

	private ListSelect unselected;

	private ListSelect selected;

	private ClickListener addSelectedListener;

	private ClickListener addAllListener;

	private ClickListener removeSelectedListener;

	private ClickListener removeAllListener;

	private CssLayout buttonLayout;

	@Override
	protected Component initContent() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setWidth("300px");
		layout.setHeight("200px");

		Layout unselectedColumn = initializeUnselectedColumn();
		layout.addComponent(unselectedColumn);

		Layout buttonColumn = initalizeButtons();
		layout.addComponent(buttonColumn);

		Layout selectedColumn = initializeSelectedColumn();
		layout.addComponent(selectedColumn);

		layout.setExpandRatio(unselectedColumn, 1);
		layout.setExpandRatio(selectedColumn, 1);

		layout.setSpacing(true);
		layout.setComponentAlignment(buttonColumn, Alignment.MIDDLE_CENTER);

		setValue(new HashSet<Object>(), false);

		return layout;
	}

	private Layout initializeUnselectedColumn() {
		VerticalLayout unselectedColumn = new VerticalLayout();
		unselectedColumn.setSizeFull();

		filterUnselected = new TextField();
		filterUnselected.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(
					com.vaadin.data.Property.ValueChangeEvent event) {
				((Container.Filterable) unselected.getContainerDataSource())
						.removeAllContainerFilters();
				// ((Container.Filterable
				// )unselected.getContainerDataSource()).addContainerProperty(propertyId,
				// type, defaultValue)

			}
		});
		filterUnselected.setWidth("100%");
		filterUnselected.setInputPrompt("Filter...");

		unselected = new ListSelect();
		unselected.setMultiSelect(true);
		unselected.setSizeFull();

		unselectedColumn.addComponent(filterUnselected);
		unselectedColumn.addComponent(unselected);
		unselectedColumn.setExpandRatio(unselected, 1);

		return unselectedColumn;
	}

	private Layout initializeSelectedColumn() {
		VerticalLayout selectedColumn = new VerticalLayout();
		selectedColumn.setSizeFull();

		filterSelected = new TextField();
		filterSelected.setWidth("100%");
		filterSelected.setInputPrompt("Filter...");

		selected = new ListSelect();
		selected.setMultiSelect(true);
		selected.setSizeFull();

		selectedColumn.addComponent(filterSelected);
		selectedColumn.addComponent(selected);
		selectedColumn.setExpandRatio(selected, 1);

		return selectedColumn;
	}

	@SuppressWarnings("unchecked")
	private void select() {
		if (unselected.getValue() == null) {
			return;
		}

		// Add all selected items to the selected ListSelect
		for (Object itemId : (Collection) unselected.getValue()) {
			copyItem(itemId, unselected.getContainerDataSource(),
					selected.getContainerDataSource());
			getValue().add(itemId);
		}

		// Remove all selected items from the unselected ListSelect
		for (Object itemId : new HashSet<Object>(
				(Collection) unselected.getValue())) {
			unselected.removeItem(itemId);
		}
		fireValueChange(false);

	}

	@SuppressWarnings("unchecked")
	private void copyItem(Object itemId, Container fromContainer,
			Container toContainer) {
		Item item = toContainer.addItem(itemId);
		for (Object propertyId : fromContainer.getContainerPropertyIds()) {
			Property property = item.getItemProperty(propertyId);
			Object value = fromContainer.getItem(itemId)
					.getItemProperty(propertyId).getValue();
			property.setValue(value);
		}
	}

	@SuppressWarnings("unchecked")
	private void selectAll() {
		if (unselected.getValue() == null) {
			return;
		}

		// Add all selected items to the selected ListSelect
		for (Object itemId : unselected.getItemIds()) {
			copyItem(itemId, unselected.getContainerDataSource(),
					selected.getContainerDataSource());
			getValue().add(itemId);
		}

		// Remove all selected items from the unselected ListSelect
		unselected.removeAllItems();
		fireValueChange(false);
	}

	@SuppressWarnings("unchecked")
	private void deselect() {
		if (selected.getValue() == null) {
			return;
		}

		// Add all selected items to the selected ListSelect
		for (Object itemId : (Collection) selected.getValue()) {
			copyItem(itemId, selected.getContainerDataSource(),
					unselected.getContainerDataSource());
			getValue().remove(itemId);
		}

		// Remove all selected items from the unselected ListSelect
		for (Object itemId : new HashSet<Object>(
				(Collection) selected.getValue())) {
			selected.removeItem(itemId);
		}
		fireValueChange(false);
	}

	private void deselectAll() {
		if (selected.getValue() == null) {
			return;
		}

		// Add all selected items to the selected ListSelect
		for (Object itemId : selected.getItemIds()) {
			copyItem(itemId, selected.getContainerDataSource(),
					unselected.getContainerDataSource());
		}

		getValue().clear();
		// Remove all selected items from the unselected ListSelect
		selected.removeAllItems();
		fireValueChange(false);
	}

	private Layout initalizeButtons() {
		buttonLayout = new CssLayout();
		buttonLayout.setWidth("40px");

		initializeListeners();

		buttonLayout.addComponent(createButton(">", addSelectedListener));
		buttonLayout.addComponent(createButton(">>", addAllListener));
		buttonLayout.addComponent(createButton("<<", removeAllListener));
		buttonLayout.addComponent(createButton("<", removeSelectedListener));

		return buttonLayout;
	}

	private void initializeListeners() {
		addSelectedListener = new ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				select();
			}
		};

		addAllListener = new ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				selectAll();
			}
		};

		removeSelectedListener = new ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				deselect();
			}
		};

		removeAllListener = new ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				deselectAll();
			}
		};
	}

	private Button createButton(String caption, ClickListener listener) {
		Button button = new Button(caption, listener);
		button.setWidth("100%");
		return button;
	}

	@Override
	public Class<Set> getType() {
		return Set.class;
	}

	@Override
	public void setContainerDataSource(Container newDataSource) {
		IndexedContainer unselectedContainer = new IndexedContainer();
		unselected.setContainerDataSource(unselectedContainer);

		// In case new datasource is null, then clear all selections
		if (newDataSource == null) {
			selected.setContainerDataSource(new IndexedContainer());
			return;
		}

		IndexedContainer selectedContainer = new IndexedContainer();

		// Copy all properties to the new containers
		for (Object propertyId : newDataSource.getContainerPropertyIds()) {
			unselectedContainer.addContainerProperty(propertyId,
					newDataSource.getType(propertyId), null);
			selectedContainer.addContainerProperty(propertyId,
					newDataSource.getType(propertyId), null);
		}

		// Copy items to the new container
		for (Object itemId : newDataSource.getItemIds()) {
			copyItem(itemId, newDataSource, unselected);
		}

		selected.setContainerDataSource(selectedContainer);
	}

	@Override
	public Container getContainerDataSource() {
		return containerDataSource;
	}

	/**
	 * Sets the text shown above the right column.
	 * 
	 * @param caption
	 *            The text to show
	 */
	public void setRightColumnCaption(String rightColumnCaption) {
		filterSelected.setCaption(rightColumnCaption);
	}

	/**
	 * Returns the text shown above the right column.
	 * 
	 * @return The text shown or null if not set.
	 */
	public String getRightColumnCaption() {
		return filterSelected.getCaption();
	}

	/**
	 * Sets the text shown above the left column.
	 * 
	 * @param caption
	 *            The text to show
	 */
	public void setLeftColumnCaption(String leftColumnCaption) {
		filterUnselected.setCaption(leftColumnCaption);
	}

	/**
	 * Returns the text shown above the left column.
	 * 
	 * @return The text shown or null if not set.
	 */
	public String getLeftColumnCaption() {
		return filterUnselected.getCaption();
	}

	public void setItemCaptionPropertyId(Object propertyId) {
		unselected.setItemCaptionPropertyId(propertyId);
		selected.setItemCaptionPropertyId(propertyId);
	}

	public void setItemCaption(Object itemId, String caption) {
		unselected.setItemCaption(itemId, caption);
		selected.setItemCaption(itemId, caption);
	}

	public String getItemCaption(Object itemId) {
		String caption = unselected.getItemCaption(itemId);
		if (caption == null || caption.isEmpty()) {
			caption = selected.getItemCaption(itemId);
		}
		return caption;
	}

	public Object getItemCaptionPropertyId() {
		return unselected.getItemCaptionPropertyId();
	}

	public void setButtonWidth(String width) {
		buttonLayout.setWidth(width);
	}

	@Override
	public void containerItemSetChange(ItemSetChangeEvent event) {
		// TODO
	}
}
