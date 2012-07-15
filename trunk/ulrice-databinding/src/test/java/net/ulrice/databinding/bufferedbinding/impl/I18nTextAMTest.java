package net.ulrice.databinding.bufferedbinding.impl;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.bufferedbinding.IFAttributeInfo;
import net.ulrice.databinding.modelaccess.impl.ReflectionMVA;
import net.ulrice.databinding.validation.IFValidator;
import net.ulrice.databinding.validation.ValidationError;
import net.ulrice.databinding.validation.ValidationResult;
import net.ulrice.databinding.validation.impl.RegExValidator;
import net.ulrice.ui.components.LocaleSelectorItem;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the list attribute model.
 * 
 * @author christof
 */
public class I18nTextAMTest {

	private Map<Locale, String> map1;
	private Map<Locale, String> map2;

	private I18nTextAM textAM1;
	private I18nTextAM textAM2;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		IFAttributeInfo attributeInfo = new IFAttributeInfo() {
		};

		map1 = new HashMap<Locale, String>();
		map1.put(Locale.GERMAN, "Deutsch");
		map1.put(Locale.US, "American English");
		map1.put(Locale.UK, "British English");

		map2 = new HashMap<Locale, String>();
		map2.put(Locale.GERMAN, "Deutsch");
		map2.put(Locale.US, "American English");
		map2.put(Locale.UK, "British English");

		textAM1 = new I18nTextAM(new ReflectionMVA(this, "map1"), attributeInfo);
		textAM1.setAvailableLocales(new LocaleSelectorItem(Locale.GERMAN), new LocaleSelectorItem(Locale.US), new LocaleSelectorItem(Locale.UK));

		textAM2 = new I18nTextAM(new ReflectionMVA(this, "map2"), attributeInfo);
		textAM2.setAvailableLocales(new LocaleSelectorItem(Locale.GERMAN), new LocaleSelectorItem(Locale.US), new LocaleSelectorItem(Locale.UK));
		textAM2.addValidator(new IFValidator<Map<Locale, String>>() {

			private ValidationResult lastValidationResult;

			@Override
			public ValidationResult isValid(IFBinding bindingId, Map<Locale, String> attribute, Object displayedValue) {
				if (attribute.get(Locale.GERMAN) != "Deutsch") {
					lastValidationResult = new ValidationResult(new ValidationError(bindingId, "Error", null));
					return lastValidationResult;
				}
				return null;
			}

			@Override
			public ValidationResult getLastValidationErrors() {
				return lastValidationResult;
			}

			@Override
			public void clearValidationErrors() {
				lastValidationResult = null;
			}
		});

	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		map1.clear();
	}

	/**
	 * Tests, if the data is read into the attribute model by calling read.
	 */
	@Test
	public void read() {
		textAM1.read();
		assertEquals(3, textAM1.getCurrentValue().size());
		assertEquals("Deutsch", textAM1.getCurrentValue().get(Locale.GERMAN));
		assertEquals("American English", textAM1.getCurrentValue().get(Locale.US));
		assertEquals("British English", textAM1.getCurrentValue().get(Locale.UK));
	}

	/**
	 * Tests, if the data is read into the attribute model by calling read.
	 */
	@Test
	public void readWithNoAvailableLanguages() {
		textAM1.setAvailableLocales();
		textAM1.read();
		assertEquals(0, textAM1.getCurrentValue().size());
	}

	/**
	 * Tests, if the state of the datagroup is set correctly.
	 */
	@Test
	public void states() {

		assertEquals(false, textAM1.isInitialized());
		assertEquals(false, textAM1.isDirty());
		assertEquals(true, textAM1.isValid());

		textAM1.read();
		assertEquals(true, textAM1.isInitialized());
		assertEquals(false, textAM1.isDirty());
		assertEquals(true, textAM1.isValid());

		textAM1.getCurrentValue().put(Locale.GERMAN, "Changed");		
		assertEquals(true, textAM1.isInitialized());
		assertEquals(true, textAM1.isDirty());
		assertEquals(true, textAM1.isValid());

		textAM1.getCurrentValue().put(Locale.GERMAN, "Deutsch");
		assertEquals(true, textAM1.isInitialized());
		assertEquals(false, textAM1.isDirty());
		assertEquals(true, textAM1.isValid());

		textAM2.read();
		assertEquals(true, textAM2.isInitialized());
		assertEquals(false, textAM2.isDirty());
		assertEquals(true, textAM2.isValid());

		textAM2.getCurrentValue().put(Locale.GERMAN, "Changed");
		assertEquals(true, textAM2.isInitialized());
		assertEquals(true, textAM2.isDirty());
		assertEquals(false, textAM2.isValid());

		textAM2.getCurrentValue().put(Locale.GERMAN, "Deutsch");
		assertEquals(true, textAM2.isInitialized());
		assertEquals(false, textAM2.isDirty());
		assertEquals(true, textAM2.isValid());

	}
	
	 @Test
	 public void externalErrorMessage() {
		 textAM1.read();
		 assertEquals(true, textAM1.isInitialized());
		 assertEquals(false, textAM1.isDirty());
		 assertEquals(true, textAM1.isValid());
		
		 textAM1.addExternalValidationError("Test");
		 assertEquals(false, textAM1.isDirty());
		 assertEquals(false, textAM1.isValid());

		 textAM1.getCurrentValue().put(Locale.GERMAN, "Changed");
		 textAM1.clearExternalValidationErrors();
		 assertEquals(true, textAM1.isDirty());
		 assertEquals(true, textAM1.isValid());
	 }
	 
	 @Test
	 public void removeEmptyLocales() {
		 textAM1.read();
		 Map<Locale, String> map = textAM1.getCurrentValue();
		 map.put(Locale.GERMAN, "");
		 textAM1.setCurrentValue(map);
		 textAM1.write();
		 
		 assertEquals(2, map1.size());
	 }
}
