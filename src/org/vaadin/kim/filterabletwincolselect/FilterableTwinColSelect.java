package org.vaadin.kim.filterabletwincolselect;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.vaadin.data.Container;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
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

	private Object itemCaptionPropertyId;

	private IndexedContainer selectedContainer = new IndexedContainer();

	private IndexedContainer unselectedContainer = new IndexedContainer();

	// Property <-> ItemId map
	private Map<Property.ValueChangeNotifier, Object> properties = new HashMap<Property.ValueChangeNotifier, Object>();

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
		filterUnselected.addTextChangeListener(new TextChangeListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void textChange(TextChangeEvent event) {
				unselectedContainer.removeAllContainerFilters();
				unselectedContainer.addContainerFilter("caption",
						event.getText(), true, false);
			}
		});
		filterUnselected.setWidth("100%");
		filterUnselected.setInputPrompt("Filter...");

		unselected = new ListSelect(null, unselectedContainer);
		unselected.setMultiSelect(true);
		unselected.setSizeFull();
		unselectedContainer.addContainerProperty("caption", Object.class, null);
		unselected.setItemCaptionPropertyId("caption");
		unselectedContainer.sort(new String[] { "caption" },
				new boolean[] { true });

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
		filterSelected.addTextChangeListener(new TextChangeListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void textChange(TextChangeEvent event) {
				selectedContainer.removeAllContainerFilters();
				selectedContainer.addContainerFilter("caption",
						event.getText(), true, false);
			}
		});

		selected = new ListSelect(null, selectedContainer);
		selected.setMultiSelect(true);
		selected.setSizeFull();
		selectedContainer.addContainerProperty("caption", Object.class, null);
		selected.setItemCaptionPropertyId("caption");
		selectedContainer.sort(new String[] { "caption" },
				new boolean[] { true });

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
		if(item == null) {
			return;
		}
		Object caption;
		if (fromContainer == selectedContainer
				|| fromContainer == unselectedContainer) {
			caption = fromContainer.getContainerProperty(itemId, "caption")
					.getValue();
		} else {
			caption = itemCaptionPropertyId == null ? itemId : fromContainer
					.getContainerProperty(itemId, itemCaptionPropertyId)
					.getValue();
		}

		item.getItemProperty("caption").setValue(caption);
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
		// Don't use removeAllItems(), since there might be filters applied
		for (Object itemId : new HashSet<Object>(unselected.getItemIds())) {
			unselected.removeItem(itemId);
		}

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

		// Remove all selected items from the unselected ListSelect
		// Don't use removeAllItems(), since there might be filters applied
		for (Object itemId : new HashSet<Object>(selected.getItemIds())) {
			selected.removeItem(itemId);
			getValue().remove(itemId);
		}
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
		// If the same container is already set, then ignore this method call
		if (newDataSource == containerDataSource) {
			return;
		}

		// Check if the currently assigned itemCaptionPropertyId exists in the
		// new container
		if (newDataSource != null
				&& !newDataSource.getContainerPropertyIds().contains(
						itemCaptionPropertyId)) {
			itemCaptionPropertyId = null;
		}

		if (containerDataSource != null
				&& containerDataSource instanceof Container.ItemSetChangeNotifier) {
			((Container.ItemSetChangeNotifier) containerDataSource)
					.removeItemSetChangeListener(this);
		}
		containerDataSource = newDataSource;
		if (containerDataSource instanceof Container.ItemSetChangeNotifier) {
			((Container.ItemSetChangeNotifier) containerDataSource)
					.addItemSetChangeListener(this);
		}
		updateContainers(newDataSource, false);
	}

	private void updateContainers(Container newDataSource,
			boolean preserveSelections) {
		containerDataSource = newDataSource;
		unselectedContainer.removeAllItems();

		// In case new datasource is null, then clear all selections
		if (newDataSource == null) {
			selectedContainer.removeAllItems();
			clearAllValueChangeListeners();
			getValue().clear();
			fireValueChange(true);
			return;
		}

		// Copy items to the new container
		for (Object itemId : newDataSource.getItemIds()) {
			copyItem(itemId, newDataSource, unselected);
		}

		if (preserveSelections) {
			// Add all selected items to the selected ListSelect
			for (Object itemId : getValue()) {
				copyItem(itemId, unselected.getContainerDataSource(),
						selected.getContainerDataSource());
			}

			// Remove all selected items from the unselected ListSelect
			boolean fireEvent = false;
			for (Object itemId : getValue()) {
				if (unselected.getItem(itemId) == null) {
					getValue().remove(itemId);
					fireEvent = true;
				} else {
					unselected.removeItem(itemId);
				}
			}
			if (fireEvent) {
				fireValueChange(true);
			}
		} else {
			getValue().clear();
			fireValueChange(true);
		}
		clearAllValueChangeListeners();
		registerPropertyListeners();
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
		itemCaptionPropertyId = propertyId;
		updateItemCaptions();
	}

	@SuppressWarnings("unchecked")
	public void setItemCaption(Object itemId, String caption) {
		Item item = unselectedContainer.getItem(itemId);
		if (item != null) {
			item.getItemProperty("caption").setValue(caption);
		}
		item = selectedContainer.getItem(itemId);
		if (item != null) {
			item.getItemProperty("caption").setValue(caption);
		}
	}

	public String getItemCaption(Object itemId) {
		Object caption = unselectedContainer.getContainerProperty(itemId,
				"caption").getValue();
		if (caption == null) {
			caption = selected.getContainerProperty(itemId, "caption")
					.getValue();
		}

		return caption == null ? null : caption.toString();
	}

	public Object getItemCaptionPropertyId() {
		return itemCaptionPropertyId;
	}

	public void setButtonWidth(String width) {
		buttonLayout.setWidth(width);
	}

	@Override
	public void containerItemSetChange(ItemSetChangeEvent event) {
		updateContainers(containerDataSource, true);
	}

	@SuppressWarnings("unchecked")
	private void updateItemCaptions() {
		for (Object itemId : selectedContainer.getItemIds()) {
			Object caption = containerDataSource.getContainerProperty(itemId,
					itemCaptionPropertyId).getValue();
			selectedContainer.getContainerProperty(itemId, "caption").setValue(
					caption);
		}
		for (Object itemId : unselectedContainer.getItemIds()) {
			Object caption = containerDataSource.getContainerProperty(itemId,
					itemCaptionPropertyId).getValue();
			unselectedContainer.getContainerProperty(itemId, "caption")
					.setValue(caption);
		}
	}

	private void clearAllValueChangeListeners() {
		for (Property.ValueChangeNotifier property : properties.keySet()) {
			property.removeValueChangeListener(this);
		}
		properties.clear();
	}

	private void registerPropertyListeners() {
		if (itemCaptionPropertyId != null) {
			for (Object itemId : containerDataSource.getItemIds()) {
				Property property = containerDataSource.getContainerProperty(
						itemId, itemCaptionPropertyId);
				if (property instanceof Property.ValueChangeNotifier) {
					((Property.ValueChangeNotifier) property)
							.addValueChangeListener(this);
					properties.put((Property.ValueChangeNotifier) property,
							itemId);
				}

			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
		Object itemId = properties.get(event.getProperty());
		if (itemId != null) {
			Item item = selectedContainer.getItem(itemId);
			if (item != null) {
				item.getItemProperty("caption").setValue(
						event.getProperty().getValue());
			}
			item = unselectedContainer.getItem(itemId);
			if (item != null) {
				item.getItemProperty("caption").setValue(
						event.getProperty().getValue());
			}
		}
	}
}
