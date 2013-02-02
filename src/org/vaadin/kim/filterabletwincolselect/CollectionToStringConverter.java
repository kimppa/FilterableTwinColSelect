package org.vaadin.kim.filterabletwincolselect;

import java.util.Locale;
import java.util.Set;

import com.vaadin.data.util.converter.Converter;

@SuppressWarnings("rawtypes")
public class CollectionToStringConverter implements Converter<String, Set> {

	private static final long serialVersionUID = 1L;

	@Override
	public Set convertToModel(String value, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		// IGNORE: Not needed in our use case
		return null;
	}

	@Override
	public String convertToPresentation(Set value, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		String presentation = "";
		if (value != null) {
			for (Object o : value) {
				if (presentation.length() > 0) {
					presentation += ", ";
				}

				presentation += o.toString();
			}
		}
		return presentation;
	}

	@Override
	public Class<Set> getModelType() {
		return Set.class;
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}

}
