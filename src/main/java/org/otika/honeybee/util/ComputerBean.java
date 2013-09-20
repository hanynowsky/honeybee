/*
 * License GPL V3
 * .
 */
package org.otika.honeybee.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

/**
 * CLass to compute the Body Mass Index using the international Formula
 * 
 * @author Hanine HAMZIOUI <hanynowsky@gmail.com>
 */
@Model
public class ComputerBean implements Serializable {

	private static final long serialVersionUID = 9092549948616601919L;
	private String health = "";
	// @Inject private BundleBean bundleBean;
	@Inject
	private UtilityBean utilityBean;
	@Inject
	private SomatotypeBean somatotypeBean;
	ResourceBundle bundle = java.util.ResourceBundle.getBundle("/i18n",
			FacesContext.getCurrentInstance().getViewRoot().getLocale());
	String healthy;
	String nhealthy;
	String ill;
	String fat;
	String obese;
	String skinny;
	String extreme;
	String sthinness;
	String modthinness;
	String mildthinness;
	String obese1;
	String obese2;
	String obese3;
	String preobese;
	String healthyT;
	String nhealthyT;
	String illT;
	String fatT;
	String obeseT;
	String skinnyT;
	String HealthText;
	private double result = 0;
	private double ideal = 0;
	private double iweight;
	private double EER;
	double PA;
	double BMRA;
	private double BMR;
	private double TDEE;
	double BF;
	String essFat = bundle.getString("ESSENTIAL_FAT");
	String athlete = bundle.getString("ATHLETE");
	String fitness = bundle.getString("FITNESS");
	String acceptable = bundle.getString("ACCEPTABLE");
	String ob = bundle.getString("OBESE_BODYTYPE");
	String[] BFS = { essFat, athlete, fitness, acceptable, ob, "N/A" };
	String BFString = ""; // Receives the value of BFS when BF is calculated
	String small = bundle.getString("SMALL");
	String medium = bundle.getString("MEDIUM");
	String large = bundle.getString("LARGE");
	String nomeasure_F = bundle.getString("MEASURE_F");
	String nomeasure_M = bundle.getString("MEASURE_M");
	String useElbow = bundle.getString("USE_ELBOW");
	String unknown = bundle.getString("UNKNOWN");
	String[] bodytype = { small, medium, large, useElbow, nomeasure_M,
			nomeasure_F, unknown };
	String btmsg = "";
	static String soundStatus = "normal";

	private int personAge = 30;
	private int personActivity;
	private double personHeight = 177;
	private double personWeight = 58.5;
	private int personGender;
	private String formula = "Hamwi";
	private int personMorph;
	private int personOrigin;
	private double personNeck = 38;
	private double personWaist = 87;
	private double personHip = 100;
	private double personWrist = 13;
	private double personElbow = 6;
	private boolean elbowChoice;
	private String personBodytype;
	private double personBodyfat;

	private String statusColor = "#484887";
	private boolean stomaDisabled = false;

	private Map<String, String> manMap = new HashMap<String, String>();
	private Map<String, String> womanMap = new HashMap<String, String>();
	private String ibmirange = "";
	int row;

	private float centimeter = 2.54f;
	private float inch = 1;
	private boolean cm2inch = true;

	private List<String> formulaList;

	// Constructor
	public ComputerBean() {
	}

	@PostConstruct
	public void init() {

		healthy = bundle.getString("HEALTHY");
		nhealthy = bundle.getString("NORMAL");
		ill = bundle.getString("UNDERWEIGHT");
		fat = bundle.getString("OVERWEIGHT");
		obese = bundle.getString("OBESE");
		skinny = bundle.getString("SKINNY");
		extreme = bundle.getString("EXTREME");
		sthinness = bundle.getString("SEVERE_THINNESS");
		modthinness = bundle.getString("MODERATE_THINNESS");
		mildthinness = bundle.getString("MILD_THINNESS");
		obese1 = bundle.getString("OBESE_CLASS_I");
		obese2 = bundle.getString("OBESE_CLASS_II");
		obese3 = bundle.getString("OBESE_CLASS_III");
		preobese = bundle.getString("FATNESS");
		healthyT = bundle.getString("HEALTHY_TEXT");
		nhealthyT = bundle.getString("NHEALTHY_TEXT");
		illT = bundle.getString("ILL_TEXT");
		fatT = bundle.getString("FAT_TEXT");
		obeseT = bundle.getString("OBESE_TEXT");
		skinnyT = bundle.getString("SKINNY_TEXT");
		HealthText = "";

		formulaList = new ArrayList<>();
		formulaList.add(0, "Hamwi");
		formulaList.add(1, "Robinson");
		formulaList.add(2, "Devine");
		formulaList.add(3, "Miller");
		formulaList.add(4, "Social");
	}

	/**
	 * 
	 * @param h
	 *            is Height
	 * @param w
	 *            is Weight
	 * @return the BMI
	 */
	public double computer(double h, double w) {
		Double dw = new Double(Double.valueOf(w));
		/*
		 * Height/100 to convert Cm to Meters
		 */
		Double dh = new Double(Double.valueOf(h * 0.01));
		result = dw / (dh * dh);
		return result;
	}

	/**
	 * Computer the BMI
	 * 
	 * @return
	 */
	public double computer() {
		return computer(personHeight, personWeight);
	}

	/**
	 * View BMI Computer
	 * 
	 * @return
	 */
	public String computeBMI() {
		computer(personHeight, personWeight);
		computeHealth();
		computeBMR();
		computeEER();
		computeTDEE();
		computePeopleIdeal(); // Must preceed ideal weight
		computeIdealWeight();
		computeBodyFat();
		computeBodyType();
		computeIbmiRange();
		computeSomatotype();

		String severity;
		if (result < 18 && result > 16) {
			severity = "warn";
		} else if (result < 16) {
			severity = "fatal";
		} else if (result > 18.5 && result < 25) {
			severity = "info";
		} else if (result > 25) {
			severity = "error";
		} else {
			severity = "info";
		}

		String msgBMI = bundle.getString("bmi") + " = " + formatDecimal(result);
		String msgIWEIGHT = bundle.getString("ideal_weight") + " = "
				+ formatDecimal(iweight);
		utilityBean.showMessage(severity, msgBMI, "");
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(msgIWEIGHT));

		if (result > 0 && result < 36) {
			utilityBean.getGaugeMeterModel().setValue(result);
		}

		return null;
	}

	/**
	 * Compute the Ideal weight using the specified formulas. Beware: Formula
	 * Strings are case sensitive (First Letter is Capital).
	 * 
	 * @param <em>hei Height in centimeters</em>
	 * @param formula
	 *            Ideal Weight Formula
	 * @param gender
	 *            Gender (Male or Female)
	 * @param morph
	 *            Morphology (Ectomorph | Mesomorph | Endomorph)
	 * @return Ideal Weight
	 */
	public double computeIdealWeight(double hei, String formula, int gender,
			int morph) {
		double height = hei * 0.01; // Convert centimeters to Meters
		// 1 centimeter = 0.393700787 inch
		// 1 inch = 2.54 cms
		// 1 foot = 30.48 centimeters
		double i = (hei - 152.4) * 0.393700787; //
		if (gender == 0) { // 0 for male and 1 for female

			switch (formula) {
			case "Robinson":
				iweight = 52 + (1.9 * i);
				break;
			case "Miller":
				iweight = 56.2 + (1.41 * i);
				break;
			case "Hamwi":
				if (morph == 0) { // ectomorph 0
					iweight = (48 + (2.7 * i)) - ((48 + (2.7 * i)) * 10 / 100);
				}
				if (morph == 1) { // mesomorph 1
					iweight = 48 + (2.7 * i);
				}
				if (morph == 2) { // endomorph 2
					iweight = (48 + (2.7 * i)) + ((48 + (2.7 * i)) * 10 / 100);
				}
				if (morph == -1) {
					iweight = 48 + (2.7 * i);
				}
				break;

			case "Devine":
				iweight = 50 + (2.3 * i);
				break;
			case "Social":
				iweight = getIdeal() * (Math.pow(height, 2));
				break;
			case "":
				iweight = getIdeal() * (Math.pow(height, 2));
				break;
			}

		} else if (gender == 1) {

			switch (formula) {
			case "Robinson":
				iweight = 49 + (1.7 * i);
				break;
			case "Miller":
				iweight = 53.1 + (1.36 * i);
				break;
			case "Hamwi":
				if (morph > 2 || morph == -1) {
					iweight = 45 + (2.2 * i);
				}
				if (morph == 0) { // 0 for ecto
					iweight = (45 + (2.2 * i)) - ((45 + (2.2 * i)) * 10 / 100);
				}
				if (morph == 1) { // 1 for meso
					iweight = 45 + (2.2 * i);
				}
				if (morph == 2) { // 2 for endo
					iweight = (45 + (2.2 * i)) + ((45 + (2.2 * i)) * 10 / 100);
				}
				;
				break;

			case "Devine":
				iweight = 45.5 + (2.3 * i);
				break;
			case "Social":
				iweight = getIdeal() * (Math.pow(height, 2));
				break;
			case "":
				iweight = getIdeal() * (Math.pow(height, 2));
				break;
			}
		}
		setIweight(iweight);
		return iweight;
	}

	/**
	 * Computes Ideal weight
	 * 
	 * @return
	 */
	public double computeIdealWeight() {
		return computeIdealWeight(personHeight, formula, personGender,
				personMorph);
	}

	/**
	 * 
	 * @param age
	 *            Age
	 * @param gender
	 *            Gender
	 * @param bmival
	 *            value of medical BMI
	 * @return
	 */
	public double computePeopleIdeal(int age, String gender, double bmival) {
		if (gender.equalsIgnoreCase("male")) {
			ideal = 0.5 * bmival + 11.5;
		} else {
			ideal = 0.4 * bmival + 0.03 * age + 11;
		}
		/*
		 * Men Social Ideal BMI = 0.5 * bmi + 11.5
		 * 
		 * Women Social Ideal BMI = 0.4 * bmi + 0.03*Age + 11
		 */
		return ideal;
	}

	/**
	 * Returns Social Ideal Weight
	 * <p>
	 * 0 = male
	 * </p>
	 * 
	 * @return SIW
	 */
	public double computePeopleIdeal() {
		String g = "male";
		if (personGender != 0) {
			g = "female";
		}
		return computePeopleIdeal(personAge, g, result);
	}

	/**
	 * 
	 * @return Explanation of BIM value
	 */
	public String computeHealth(int origin) {

		if (origin == 0) { // 0 for caucasian
			if (18.5 <= this.result & this.result <= 20) {
				health = healthy;
				setHealthText(healthyT);
				setSoundStatus("Healthy");
				statusColor = "green";
			} else if (25 < this.result & this.result < 27.5) {
				health = fat;
				setHealthText(fat);
				setSoundStatus("Overweight");
				setStatusColor("yellow");
			} else if (27.5 <= this.result & this.result < 30) {
				health = preobese;
				setHealthText(fat);
				setSoundStatus("Fatness");
				setStatusColor("orange");
			} else if (20 < this.result & this.result < 25) {
				health = nhealthy;
				setHealthText(nhealthyT);
				setSoundStatus("Normal");
				statusColor = "green";
			} else if (this.result < 18.5 & this.result >= 16) {
				health = ill;
				setHealthText(illT);
				setSoundStatus("Underweight");
				setStatusColor("yellow");
			} else if (this.result > 30 & this.result < 35) {
				health = obese1;
				setHealthText(obeseT);
				setSoundStatus("Obese Class I");
				setStatusColor("red");
			} else if (this.result >= 35 & this.result < 40) {
				health = obese2;
				setHealthText(obeseT);
				setSoundStatus("Obese Class II");
				setStatusColor("red");
			} else if (this.result >= 40 & this.result < 50) {
				health = obese3;
				setHealthText(obeseT);
				setSoundStatus("Obese Class III");
				setStatusColor("red");
			} else if (this.result > 5 & this.result < 16) {
				health = sthinness;
				setHealthText(skinnyT);
				setSoundStatus("Severe Thinness");
				setStatusColor("red");
			} else {
				health = extreme;
				setHealthText("Alien!");
				setSoundStatus("Extreme");
				setStatusColor("red");
			}
		} else if (origin == 1) { // 1 for asian
			if (18.5 <= this.result & this.result <= 22.9) {
				health = nhealthy;
				setHealthText(nhealthyT);
				setSoundStatus("Normal");
				setStatusColor("green");
			} else if (22.9 < this.result & this.result < 25) {
				health = fat;
				setHealthText(fatT);
				setSoundStatus("Overweight");
				setStatusColor("yellow");
			} else if (25 <= this.result & this.result < 30) {
				health = obese;
				setHealthText(obeseT);
				setSoundStatus("Obese");
				setStatusColor("orange");
			} else if (this.result < 18.5 & this.result > 16) {
				health = ill;
				setHealthText(illT);
				setSoundStatus("Underweight");
				setStatusColor("orange");
			} else if (this.result > 30 & this.result < 40) {
				health = obese;
				setHealthText(obeseT);
				setSoundStatus("Obese Class II");
				setStatusColor("red");
			} else if (this.result > 4.6 & this.result < 16) {
				health = skinny;
				setHealthText(skinnyT);
				setSoundStatus("Skinny");
				setStatusColor("red");
			} else {
				health = extreme;
				setHealthText("For Real? Check The Doctor for Aliens' Diet");
				setSoundStatus("Extreme");
				setStatusColor("red");
			}
		}

		return health;
	} // End of Method

	/**
	 * Computes Health Rank
	 * 
	 * @return
	 */
	public String computeHealth() {
		return computeHealth(personOrigin);
	}

	/**
	 * 
	 * @return a String - Detailed text corresponding to BMI details
	 */
	/**
	 ** The official formulas for the calculation of daily estimated energy
	 * requirements (EER) are provided by the Food and Nutrition Board of the
	 * Institute of Medicine of the National Academies (Trumbo et al. 2002).
	 * 
	 * EER Male = (662 - (9.53 x Age)) + (PA x ((15.91 x Weight) + (539.6 x
	 * Height))).
	 * 
	 * EER Female = (354 - (6.91 x Age)) + (PA x ((9.36 x Weight) + (726 x
	 * Height))).
	 * 
	 * PA indicates Activity Level and is equal to 1.0 for Sedentary, 1.12 for
	 * Low Active, 1.27 for Active and 1.45 for Very Active. Weight is measured
	 * in kilograms and height in meters.
	 * 
	 * @param age
	 *            Age
	 * @param activity
	 *            Activity
	 * @param heit
	 *            Height
	 * @param weit
	 *            Weight
	 * @param gender
	 *            gender
	 * @return EER
	 */
	public double computeEER(int age, int activity, double height, double weit,
			int gender) {

		if (activity == 0) { // 0 sedentary
			setPA(1.00);
		} else if (activity == 1) { // 1 low
			setPA(1.12);
		} else if (activity == 2) { // 2 active
			setPA(1.27);
		} else if (activity == 3) { // 3 very active
			setPA(1.45);
		} else if (activity == 4) { // 4 extreme
			setPA(1.66);
		} else {
			setPA(1.27);
		}

		double heit = height / 100; // Height from Centimeters to Meters
		if (gender == 0) { // 0 corresponds to male and 1 to female
			if (age >= 0 && age <= 2) {
				EER = (89 * weit - 100) + 20;
			}
			if (age >= 3 && age <= 8) {
				EER = 88.5 - (61.9 * age) + getPA()
						* (26.7 * weit + 903 * heit) + 20;
			}
			if (age >= 9 && age <= 18) {
				EER = 88.5 - (61.9 * age) + getPA()
						* (26.7 * weit + 903 * heit) + 25;
			} else {
				EER = 662 - (9.53 * age) + getPA()
						* (15.91 * weit + 539.6 * heit);
			}
		} else {
			if (age >= 0 && age <= 2) {
				EER = (89 * weit - 100) + 20;
			}
			if (age >= 3 && age <= 8) {
				EER = 135.3 - (30.8 * age) + getPA() * (10 * weit + 934 * heit)
						+ 20;
			}
			if (age >= 9 && age <= 18) {
				EER = 135.3 - (30.8 * age) + getPA() * (10 * heit + 934 * heit)
						+ 25;
			} else {
				EER = (354 - (6.91 * age))
						+ (getPA() * ((9.36 * weit) + (726 * heit)));
			}
		}
		return EER;
	}

	/**
	 * Computes EER -
	 * <p>
	 * <em>Estimated Energy Requirement</em>
	 * </p>
	 * 
	 * @return
	 */
	public double computeEER() {
		return computeEER(personAge, personActivity, personHeight,
				personWeight, personGender);
	}

	/**
	 * Compute the Basal Metabolic Rate
	 * 
	 * @param w
	 *            Weight as double
	 * @param h
	 *            Height as double
	 * @param a
	 *            Age as integer
	 * @param g
	 *            Gender as String
	 * @return BMR Basal Metabolic Rate
	 */
	public double computeBMR(double w, double h, int a, String g) {

		if (g.equalsIgnoreCase("female")) {
			/*
			 * Women BMR = 655 +(9.6*w)+(1.8*h)-(4.7*a)
			 */
			BMR = 655 + (9.6 * w) + (1.8 * h) - (4.7 * a);
		} else {
			BMR = 66 + (13.7 * w) + (5 * h) - (6.8 * a);
		}
		return BMR;
	}

	/**
	 * Computes the Basal Metabolic Rate
	 * 
	 * @return
	 */
	public double computeBMR() {
		String g = "male";
		if (personGender == 0) {
			g = "male";
		} else {
			g = "female";
		}
		return computeBMR(personWeight, personHeight, personAge, g);
	}

	/**
	 * Calculates the TDEE
	 * 
	 * @param activity
	 *            Physical Activity
	 * @return TDEE
	 */
	public double computeTDEE(int activity) {
		/**
		 * Total Daily Energy Expenditure (TDEE) = Caloric requirements to
		 * maintain weight.
		 * 
		 * TDEE = BMR * Activity Factor
		 * 
		 * Sedentary = Little or no exercise/desk job = 1.2 Lightly active = 1-3
		 * sport/week = 1.375 Moderately active = 3-5 sport week = 1.55 Very
		 * active = 6-7 sport/week = 1.725 extremely active = 2sports/day = 1.9
		 */
		double a = 0;
		if (activity == 0) { // 0 for sedentary
			a = 1.2;
		} else if (activity == 1) { // 1 low
			a = 1.375;
		} else if (activity == 2) { // 2 very active
			a = 1.725;
		} else if (activity == 3) { // 3 active
			a = 1.55;
		} else if (activity == 4) { // 4 extreme
			a = 1.9;
		}

		TDEE = BMR * a;
		return TDEE;
	}

	/**
	 * Computes TDEE <em>Total Daily Energy Expenditure</em>
	 * 
	 * @return
	 */
	public double computeTDEE() {
		return computeTDEE(personActivity);
	}

	/**
	 * US Naval Army Formula for calculating the Body Fat Percentage. The
	 * resulting Body fat is compared then to a classification table by The
	 * American Council on Exercise.
	 * 
	 * <table border="1">
	 * <thead>
	 * <tr>
	 * <th>----</th>
	 * <th>Women</th>
	 * <th>Men</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>--------</td>
	 * <td>----------</td>
	 * <td>-----------</td>
	 * </tr>
	 * <tr>
	 * <td>Essential Fat</td>
	 * <td>12-15%</td>
	 * <td>2-5%</td>
	 * </tr>
	 * <tr>
	 * <td>Athletes</td>
	 * <td>16-20%</td>
	 * <td>6-13%</td>
	 * </tr>
	 * <tr>
	 * <td>Fitness</td>
	 * <td>21-24%</td>
	 * <td>14-17%</td>
	 * </tr>
	 * <tr>
	 * <td>Acceptable</td>
	 * <td>25-31%</td>
	 * <td>18-25%</td>
	 * </tr>
	 * <tr>
	 * <td>Obese</td>
	 * <td>32%+</td>
	 * <td>25%+</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param height
	 *            Height in centimeters
	 * @param waist
	 *            Waist in centimeters
	 * @param neck
	 *            Neck in centimeters
	 * @param hip
	 *            Hip in centimeters
	 * @param gender
	 *            Gender (Male / Female)
	 * @return Body Fat as a percentage
	 */
	public double computeBodyFat(double height, double waist, double neck,
			double hip, int gender) {
		/**
		 * //man =
		 * 495/(1.0324-0.19077(LOG(waist-neck))+0.15456(LOG(height)))-450
		 * //woman =
		 * 495/(1.29579-0.35004*(LOG(waist+hip-neck))+0.22100*(LOG(height)))-450
		 */
		// 1 centimeter = 0.393700787 inches
		double h = convertCMtoINCH(height);
		double n = convertCMtoINCH(neck);
		double w = convertCMtoINCH(waist);
		double p = convertCMtoINCH(hip);

		if (gender == 0) { // 0 for male
			BF = 495 / (1.0324 - (0.19077 * (Math.log(waist - neck)) / Math
					.log(10)) + (0.15456 * (Math.log(height))) / Math.log(10)) - 450;
			setBF(BF);
			// BF =
			// 495/(1.0324-0.19077*(Math.log(w-n))+0.15456*(Math.log(h)))-450;

			if (BF >= 2 && BF <= 5) {
				setBFString(BFS[0]);
			} else if (getBF() >= 6 && getBF() <= 13) {
				setBFString(BFS[1]);
			} else if (BF >= 14 && BF <= 17) {
				setBFString(BFS[2]);
			} else if (BF >= 18 && BF <= 25) {
				setBFString(BFS[3]);
			} else if (BF >= 25 && BF <= 90) {
				setBFString(BFS[4]);
			} else {
				setBFString(BFS[5]);
			}

		} else if (gender == 1) { // 1 for female
			BF = 495 / (1.29579 - 0.35004 * (Math.log(waist + hip - neck) / Math
					.log(10)) + 0.22100 * (Math.log(height)) / Math.log(10)) - 450;
			if (BF >= 12 && BF <= 15) {
				setBFString(BFS[0]);
			} else if (BF >= 16 && BF <= 20) {
				setBFString(BFS[1]);
			} else if (BF >= 21 && BF <= 24) {
				setBFString(BFS[2]);
			} else if (BF >= 25 && BF <= 31) {
				setBFString(BFS[3]);
			} else if (BF >= 32 && BF <= 90) {
				setBFString(BFS[4]);
			} else {
				setBFString(BFS[5]);
			}
		}
		if (UtilityBean.APP_DEBUG) {
			System.out.println("Height is "
					+ new DecimalFormat("#.##").format(h) + " inches");
			System.out.println("Hip is " + p + " inches");
			System.out.println("Neck is " + n + " inches");
			System.out.println("Waist is " + w + " inches");
			System.out.println("Body Fat Percentage is "
					+ new DecimalFormat("#.##").format(BF) + " %");
			System.out.println(getBFString());
		}

		setPersonBodyfat(BF);
		return BF;

	}

	/**
	 * Computes Body fat
	 * 
	 * @return
	 */
	public double computeBodyFat() {
		return computeBodyFat(personHeight, personWaist, personNeck, personHip,
				personGender);
	}

	/**
	 * Determines the body type (Small, medium or large frame).
	 * 
	 * @param gender
	 *            Gender (Male or Female)
	 * @param height
	 *            Height length in meters
	 * @param wrist
	 *            Wrist length in centimeters
	 * @param elbow
	 *            Elbow length in centimeters
	 * @param choice
	 *            Boolean (True if Elbow measure, false if Wrist).
	 * @return Body Type (Small, Medium or Large)
	 */

	public String computeBodyType(int gender, double height, double wrist,
			double elbow, boolean choice) {
		int i = 0;
		double h = height / 100;

		if (gender == 0) { // 0 for male and 1 for female
			// male wrist

			if (!choice) {
				if (h >= 1.63) {
					if (wrist <= 16.5) {
						i = 0;
					} else if (wrist <= 19.1) {
						i = 1;
					} else {
						i = 2;
					}
				} else if (h < 1.63) {
					i = 3;
					setBtmsg("Body Type: Please use the elbow measure for male heights below 1.63m (5\'5)");
					System.err
							.println("Body Type: Please use the elbow measure for male heights below 1.63m (5'5)");
				}
			} else if (choice) {
				// male elbow breadth
				if (h <= 1.54) {
					i = 4;
					System.err
							.println("Body Type: There is no measure for males below 1.55m (5'2");
					setBtmsg("Body Type: There is no measure for males below 1.55m (5'2");
				} else if (h >= 1.55 && h < 1.58) {
					if (elbow < 6.4) {
						i = 0;
					} else if (elbow < 7.3) {
						i = 1;
					} else {
						i = 2;
					}
				} else if (h < 1.68) {
					if (elbow < 6.7) {
						i = 0;
					} else if (elbow < 7.3) {
						i = 1;
					} else {
						i = 2;
					}
				} else if (h < 1.78) {
					if (elbow < 7.0) {
						i = 0;
					} else if (elbow < 7.5) {
						i = 1;
					} else {
						i = 2;
					}
				} else if (h < 1.88) {
					if (elbow < 7.0) {
						i = 0;
					} else if (elbow < 7.9) {
						i = 1;
					} else {
						i = 2;
					}
				} else if (h < 1.98) {
					if (elbow < 7.3) {
						i = 0;
					} else if (elbow < 8.3) {
						i = 1;
					} else {
						i = 2;
					}
				} else if (h >= 1.98) {
					// very tall people are considered large frame
					i = 2; // Skeptic
				} else {
					System.err.println("Unknown Body Type");
					setBtmsg("Unknown Body Type");
					i = 6;
				}
			}

		} else {
			// female wrist
			if (!choice) {
				if (h < 1.55) {
					if (wrist < 14.0) {
						i = 0;
					} else if (wrist < 14.6) {
						i = 1;
					} else {
						i = 2;
					}
				} else if (h < 1.63) {
					if (wrist < 15.2) {
						i = 0;
					} else if (wrist < 15.9) {
						i = 1;
					} else {
						i = 2;
					}
				} else {
					if (wrist < 15.9) {
						i = 0;
					} else if (wrist < 16.5) {
						i = 1;
					} else {
						i = 2;
					}
				}
			} else if (choice) {
				// female elbow
				if (h < 1.48 && h >= 1.46) {
					if (elbow < 5.7) {
						i = 0;
					} else if (elbow < 6.4) {
						i = 1;
					} else {
						i = 2;
					}
				} else if (h < 1.58) {
					if (elbow < 5.7) {
						i = 0;
					} else if (elbow < 6.4) {
						i = 1;
					} else {
						i = 2;
					}
				} else if (h < 1.68) {
					if (elbow < 6.0) {
						i = 0;
					} else if (elbow < 6.7) {
						i = 1;
					} else {
						i = 2;
					}
				} else if (h < 1.78) {
					if (elbow < 6.0) {
						i = 0;
					} else if (elbow < 6.7) {
						i = 1;
					} else {
						i = 2;
					}
				} else if (h < 1.90) {
					if (elbow < 6.3) {
						i = 0;
					} else if (elbow < 7.0) {
						i = 1;
					} else {
						i = 2;
					}
				} else {
					i = 5;
					System.err
							.println("Body Type: There is no measure for females below 1.46m (4'10) or above 1.90m (6'4");
					FacesContext
							.getCurrentInstance()
							.addMessage(
									null,
									new FacesMessage(
											"Body Type: There is no measure for females below 1.46m (4'10) or above 1.90m (6'4"));
					setBtmsg(nomeasure_F);
				}
			}
		}
		setPersonBodytype(bodytype[i]);
		return bodytype[i];
	}

	/**
	 * Computes <b>Body Type</b>
	 * 
	 * @return
	 */
	public String computeBodyType() {
		return computeBodyType(personGender, personHeight, personWrist,
				personElbow, elbowChoice);
	}

	/* Utilities */
	/**
	 * Converts a centimeter value to feet & inches in one string.
	 * 
	 * @param height
	 *            Height in centimeters
	 * @return Measure in Feet & Inches.
	 */
	public static String convertHeight(double height) {
		double feet = height * 0.39370079 * 0.08333333;
		int f = (int) feet;
		double r = (feet - f) * 12;
		String fi = new DecimalFormat("#").format(r);
		return f + " ft " + fi + " in";
	}

	/**
	 * Converts kilograms to pounds.
	 * 
	 * @param weight
	 *            Weight in kilograms
	 * @return Formatted weight in pounds.
	 */
	public static double convertWeight(double weight) {
		double w = weight * 2.2;
		return Math.round(w);
		// return Double.parseDouble(new DecimalFormat("#.##").format(w));
	}

	/**
	 * Convert centimeters to inches.
	 * 
	 * @param cm
	 *            Value in Centimeters.
	 * @return Measure in inches
	 */
	public static double convertCMtoINCH(double cm) {
		return cm * 0.393700787;
	}

	/**
	 * Returns formatted decimal
	 * 
	 * @param value
	 * @return
	 */
	public String formatDecimal(Object value) {
		return new DecimalFormat("#.##").format(value);
	}

	/**
	 * Parses the BMI correspondence table (CSV File) and sets the BMI Range.
	 * 
	 * @param gender
	 *            Gender (Male = 0 or Female = 1)
	 * @param height
	 *            Height as double (in centimeters. ex: 175)
	 */
	public void readIBMITABLE(int gender, double heit) {
		double height = heit / 100; // Get height in meters instead of Cms
		BufferedReader bufRdr;
		try {
			File f = new File(System.getProperty("java.io.tmpdir")
					+ File.separator + "ibmitable.csv");
			if (!f.exists()) {
				InputStream inputStream = getClass().getResourceAsStream(
						"/ibmitable.csv");
				OutputStream out = new FileOutputStream(f);
				byte buf[] = new byte[1024];
				int len;
				while ((len = inputStream.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				out.close();
			}
			bufRdr = new BufferedReader(new FileReader(f)); //
			String line;

			row = 0;
			while ((line = bufRdr.readLine()) != null) {
				String[] content = line.split(";", 3);
				/*
				 * Three columns Height | Man | Woman
				 */
				manMap.put(content[0], content[1]);
				womanMap.put(content[0], content[2]);
				row++;
			}
			bufRdr.close();

			int g = gender;
			double j = 0;
			double e = 0;
			double s;
			double arg = height;
			double diff;
			double dim;
			double ah = 0;
			Iterator<String> kset = manMap.keySet().iterator();
			int k;
			for (k = 0; k < manMap.keySet().size(); k++) {
				while (kset.hasNext()) {
					s = Double.parseDouble(kset.next().toString());
					diff = Math.max(s, arg);
					dim = Math.min(s, arg);
					/*
					 * 0.013 is the maximum difference between two ordered
					 * height values System.out.println(" here is FIRST  diff: "
					 * + diff);
					 */
					double daf = diff - arg;
					if (daf <= 0.013 && (diff - arg != 0)) {
						j = diff;
					}
					if (arg - dim <= 0.013 && (dim - arg != 0)) {
						e = dim;
					}
				}
			}
			// System.err.println(j + " " + (j - arg));
			// System.err.println(e + " " + (arg - e));
			if ((arg - e) < (j - arg)) {
				ah = e;
			}
			if ((arg - e) > (j - arg)) {
				ah = j;
			}

			height = ah;
			String h = String.valueOf(height);
			if (g == 0) { /* 0 for male and 1 for female */
				ibmirange = String.valueOf(manMap.get(h));
			}
			if (g == 1) {
				ibmirange = String.valueOf(womanMap.get(h));
			}

			/* delete BMI file at the end of the operation */
			if (f.exists()) {
				f.delete();
			}
		} catch (Exception ex) {
			String msg = "iBMI Ranges CSV File is not present or corrupted."
					+ "Please delete it manually and relaunch the application.";
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, msg, ex);
			System.err.println(msg);
		}
	}

	/**
	 * Returns Medical Ideal BMI Range
	 * 
	 * @return
	 */
	public String computeIbmiRange() {
		readIBMITABLE(personGender, personHeight);
		return ibmirange;
	}

	/**
	 * Change Listener for the JSF Select One Menu or List
	 * <p>
	 * Use the Facelet Ajax component inside the list menu using the change
	 * event to render the target component
	 * </p>
	 * 
	 * @param <em>event</em> <b>Value Change Event</b>
	 */
	public void formulaChangeListener(ValueChangeEvent event) {
		if (event != null) {
			if (!String.valueOf(event).equalsIgnoreCase("hamwi")) {
				stomaDisabled = true;
				System.out.println(event.getNewValue()
						+ " :Changing stomaDisabled to: " + stomaDisabled);
			}
		}
	}

	/**
	 * <p>
	 * Function that reads csv files and retrieves somatotype value
	 * </p>
	 */
	public void computeSomatotype() {
		somatotypeBean.computeSomatotype();
	}

	/**
	 * Centimeter <-> inch converter
	 * 
	 * @param cm
	 * @param in
	 * @param cmtoin
	 * @return
	 */
	private float centimeterToInchConverter(float cm, float in, boolean cmtoin) {
		if (cmtoin) {
			in = (float) (cm * 0.393700787);
			setInch(in);
			return in;
		} else {
			cm = (float) (in / 0.393700787);
			setCentimeter(cm);
			return cm;
		}
	}

	/**
	 * Centimeter to inch converter
	 * 
	 * @return
	 */
	public float centimeterToInchConverter() {
		return centimeterToInchConverter(this.centimeter, this.inch,
				this.cm2inch);
	}

	/* Getters & Setters */

	/**
	 * @param result
	 *            the result to set
	 */
	public void setResult(double result) {
		this.result = result;
	}

	/**
	 * @return the personAge
	 */
	public int getPersonAge() {
		return personAge;
	}

	/**
	 * @param personAge
	 *            the personAge to set
	 */
	public void setPersonAge(int personAge) {
		this.personAge = personAge;
	}

	/**
	 * @return the personActivity
	 */
	public int getPersonActivity() {
		return personActivity;
	}

	/**
	 * @param personActivity
	 *            the personActivity to set
	 */
	public void setPersonActivity(int personActivity) {
		this.personActivity = personActivity;
	}

	/**
	 * @return the personHeight
	 */
	public double getPersonHeight() {
		return personHeight;
	}

	/**
	 * @param personHeight
	 *            the personHeight to set
	 */
	public void setPersonHeight(double personHeight) {
		this.personHeight = personHeight;
	}

	/**
	 * @return the personWeight
	 */
	public double getPersonWeight() {
		return personWeight;
	}

	/**
	 * @param personWeight
	 *            the personWeight to set
	 */
	public void setPersonWeight(double personWeight) {
		this.personWeight = personWeight;
	}

	/**
	 * @return the personGender
	 */
	public int getPersonGender() {
		return personGender;
	}

	/**
	 * @param personGender
	 *            the personGender to set
	 */
	public void setPersonGender(int personGender) {
		this.personGender = personGender;
	}

	/**
	 * @return the personMorph
	 */
	public int getPersonMorph() {
		return personMorph;
	}

	/**
	 * @param personMorph
	 *            the personMorph to set
	 */
	public void setPersonMorph(int personMorph) {
		this.personMorph = personMorph;
	}

	/**
	 * @return the personOrigin
	 */
	public int getPersonOrigin() {
		return personOrigin;
	}

	/**
	 * @param personOrigin
	 *            the personOrigin to set
	 */
	public void setPersonOrigin(int personOrigin) {
		this.personOrigin = personOrigin;
	}

	/**
	 * @return the personNeck
	 */
	public double getPersonNeck() {
		return personNeck;
	}

	/**
	 * @param personNeck
	 *            the personNeck to set
	 */
	public void setPersonNeck(double personNeck) {
		this.personNeck = personNeck;
	}

	/**
	 * @return the personWaist
	 */
	public double getPersonWaist() {
		return personWaist;
	}

	/**
	 * @param personWaist
	 *            the personWaist to set
	 */
	public void setPersonWaist(double personWaist) {
		this.personWaist = personWaist;
	}

	/**
	 * @return the personHip
	 */
	public double getPersonHip() {
		return personHip;
	}

	/**
	 * @param personHip
	 *            the personHip to set
	 */
	public void setPersonHip(double personHip) {
		this.personHip = personHip;
	}

	/**
	 * @return the personWrist
	 */
	public double getPersonWrist() {
		return personWrist;
	}

	/**
	 * @param personWrist
	 *            the personWrist to set
	 */
	public void setPersonWrist(double personWrist) {
		this.personWrist = personWrist;
	}

	/**
	 * @return the personElbow
	 */
	public double getPersonElbow() {
		return personElbow;
	}

	/**
	 * @param personElbow
	 *            the personElbow to set
	 */
	public void setPersonElbow(double personElbow) {
		this.personElbow = personElbow;
	}

	/**
	 * @return the elbowChoice
	 */
	public boolean isElbowChoice() {
		return elbowChoice;
	}

	/**
	 * @param elbowChoice
	 *            the elbowChoice to set
	 */
	public void setElbowChoice(boolean elbowChoice) {
		this.elbowChoice = elbowChoice;
	}

	public static String getSoundStatus() {
		return soundStatus;
	}

	public static void setSoundStatus(String soundStatus) {
		ComputerBean.soundStatus = soundStatus;
	}

	public String getHealthText() {
		// HealthText = "Health Text here";
		return HealthText;
	}

	public void setHealthText(String HealthText) {
		this.HealthText = HealthText;
	}

	public double getBF() {
		return BF;
	}

	public void setBF(double BF) {
		this.BF = BF;
	}

	public String getBFString() {
		return BFString;
	}

	public void setBFString(String BDString) {
		this.BFString = BDString;
	}

	public String getBtmsg() {
		return btmsg;
	}

	public void setBtmsg(String btmsg) {
		this.btmsg = btmsg;
	}

	public double getTDEE() {
		return TDEE;
	}

	public double getBMR() {
		return BMR;
	}

	public String[] getBodytype() {
		return bodytype;
	}

	public void setBodytype(String[] bodytype) {
		this.bodytype = bodytype;
	}

	public double getBMRA() {
		return BMRA;
	}

	public void setBMRA(double BMRA) {
		this.BMRA = BMRA;
	}

	public double getEER() {
		return EER;
	}

	public void setEER(double EER) {
		this.EER = EER;
	}

	public double getPA() {
		return PA;
	}

	public void setPA(double PA) {
		this.PA = PA;
	}

	public double getIweight() {
		return iweight;
	}

	public void setIweight(double iweight) {
		this.iweight = iweight;
	}

	public double getIdeal() {
		return ideal;
	}

	public void setIdeal(double ideal) {
		this.ideal = ideal;
	}

	public double getResult() {
		return result;
	}

	public void setResult(float result) {
		this.result = result;
	}

	/**
	 * @return the formula
	 */
	public String getFormula() {
		return formula;
	}

	/**
	 * @param formula
	 *            the formula to set
	 */
	public void setFormula(String formula) {
		this.formula = formula;
	}

	/**
	 * @return the health
	 */
	public String getHealth() {
		return health;
	}

	/**
	 * @param health
	 *            the health to set
	 */
	public void setHealth(String health) {
		this.health = health;
	}

	/**
	 * @return the personBodytype
	 */
	public String getPersonBodytype() {
		return personBodytype;
	}

	/**
	 * @param personBodytype
	 *            the personBodytype to set
	 */
	public void setPersonBodytype(String personBodytype) {
		this.personBodytype = personBodytype;
	}

	/**
	 * @return the personBodyfat
	 */
	public double getPersonBodyfat() {
		return personBodyfat;
	}

	/**
	 * @param personBodyfat
	 *            the personBodyfat to set
	 */
	public void setPersonBodyfat(double personBodyfat) {
		this.personBodyfat = personBodyfat;
	}

	/**
	 * @param bMR
	 *            the bMR to set
	 */
	public void setBMR(double bMR) {
		BMR = bMR;
	}

	/**
	 * @param tDEE
	 *            the tDEE to set
	 */
	public void setTDEE(double tDEE) {
		TDEE = tDEE;
	}

	/**
	 * @return the statusColor
	 */
	public String getStatusColor() {
		return statusColor;
	}

	/**
	 * @param statusColor
	 *            the statusColor to set
	 */
	public void setStatusColor(String statusColor) {
		this.statusColor = statusColor;
	}

	/**
	 * @return the manMap
	 */
	public Map<String, String> getManMap() {
		return manMap;
	}

	/**
	 * @param manMap
	 *            the manMap to set
	 */
	public void setManMap(Map<String, String> manMap) {
		this.manMap = manMap;
	}

	/**
	 * @return the womanMap
	 */
	public Map<String, String> getWomanMap() {
		return womanMap;
	}

	/**
	 * @param womanMap
	 *            the womanMap to set
	 */
	public void setWomanMap(Map<String, String> womanMap) {
		this.womanMap = womanMap;
	}

	/**
	 * @return the ibmirange
	 */
	public String getIbmirange() {
		return ibmirange;
	}

	/**
	 * @param ibmirange
	 *            the ibmirange to set
	 */
	public void setIbmirange(String ibmirange) {
		this.ibmirange = ibmirange;
	}

	/**
	 * @return the stomaDisabled
	 */
	public boolean isStomaDisabled() {
		return stomaDisabled;
	}

	/**
	 * @param stomaDisabled
	 *            the stomaDisabled to set
	 */
	public void setStomaDisabled(boolean stomaDisabled) {
		this.stomaDisabled = stomaDisabled;
	}

	/**
	 * @return the forumlaList
	 */
	public List<String> getForumlaList() {
		return formulaList;
	}

	/**
	 * @param forumlaList
	 *            the forumlaList to set
	 */
	public void setForumlaList(List<String> forumlaList) {
		this.formulaList = forumlaList;
	}

	/**
	 * @return the centimeter
	 */
	public float getCentimeter() {
		return centimeter;
	}

	/**
	 * @param centimeter
	 *            the centimeter to set
	 */
	public void setCentimeter(float centimeter) {
		this.centimeter = centimeter;
	}

	/**
	 * @return the inch
	 */
	public float getInch() {
		return inch;
	}

	/**
	 * @param inch
	 *            the inch to set
	 */
	public void setInch(float inch) {
		this.inch = inch;
	}

	/**
	 * @return the cm2inch
	 */
	public boolean isCm2inch() {
		return cm2inch;
	}

	/**
	 * @param cm2inch
	 *            the cm2inch to set
	 */
	public void setCm2inch(boolean cm2inch) {
		this.cm2inch = cm2inch;
	}

} // END OF CLASS

