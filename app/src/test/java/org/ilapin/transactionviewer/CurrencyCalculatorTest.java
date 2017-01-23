package org.ilapin.transactionviewer;

import org.ilapin.transactionviewer.currency.CurrencyCalculator;
import org.ilapin.transactionviewer.currency.NoConversionException;
import org.junit.*;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

public class CurrencyCalculatorTest {

	private static final double DELTA = 0.00001;

	private final CurrencyCalculator mCurrencyCalculator = new CurrencyCalculator();

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void loadRates() {
		mCurrencyCalculator.addConversion("USD", "EUR", 0.5f);
		mCurrencyCalculator.addConversion("USD", "GBP", 0.25f);
		mCurrencyCalculator.addConversion("RUB", "USD", 0.03f);
	}

	@Test
	public void testDirectForwardConversion() throws Exception {
		assertEquals(0.9, mCurrencyCalculator.calculate("RUB", "USD", 30), DELTA);
	}

	@Test
	public void testDirectBackwardConversion() throws Exception {
		assertEquals(2, mCurrencyCalculator.calculate("EUR", "USD", 1), DELTA);
	}

	@Test
	public void testIndirectForwardConversion() throws Exception {
		assertEquals(0.225, mCurrencyCalculator.calculate("RUB", "GBP", 30), DELTA);
	}

	@Test
	public void testIndirectBackwardConversion() throws Exception {
		assertEquals(2, mCurrencyCalculator.calculate("GBP", "EUR", 1), DELTA);
	}

	@Test
	public void testUnknownCurrency() throws Exception {
		thrown.expect(NoConversionException.class);
		mCurrencyCalculator.calculate("CAD", "RUB", 100);
	}
}