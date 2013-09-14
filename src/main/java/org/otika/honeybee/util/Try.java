package org.otika.honeybee.util;

import java.text.DecimalFormat;

public class Try {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Try().halfRoundedSoma(0.1);
	}

	/**
	 * Value rounded to the nearest half-unit,
	 * 
	 * @param value
	 * @return half rounded value
	 */
	private double halfRoundedSoma(double value) {
		double returnedValue = value;
		double formatedValue = Double.parseDouble(new DecimalFormat("#.#")
				.format(value));
		int lowerLimit = (int) value;
		double middleLimit = lowerLimit + 0.5;
		int upperLimit = (int) (value + 1);
		if (lowerLimit > 0) {
			if (middleLimit - value > 0
					&& (middleLimit - value <= value - lowerLimit)) {
				System.out.println(" ML");
				returnedValue = middleLimit;
			}
			if (middleLimit - value > 0
					&& (middleLimit - value >= value - lowerLimit)) {
				System.out.println(" MU");
				returnedValue = lowerLimit;
			}
			if (upperLimit - value > 0 && value - middleLimit > 0
					&& (value - middleLimit >= upperLimit - value)) {
				System.out.println(" HU");
				returnedValue = upperLimit;
			}
			if (upperLimit - value > 0 && value - middleLimit > 0
					&& (value - middleLimit <= upperLimit - value)) {
				System.out.println(" HM");
				returnedValue = middleLimit;
			}

			if (value == middleLimit) {
				System.out.println(" EQ");
				returnedValue = middleLimit;
			}
		}
		if (returnedValue == 0) {
			System.out.println(" 0");
			returnedValue = 0.1;
		}

		System.out.println("Value : " + value);
		System.out.println("formatedValue : " + formatedValue);
		System.out.println("lowerLimit : " + lowerLimit);
		System.out.println("middleLimit : " + middleLimit);
		System.out.println("upperLimit : " + upperLimit);
		System.out.println("returnedValue : " + returnedValue);
		return returnedValue;
	}

}
