package org.otika.honeybee.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.primefaces.event.ItemSelectEvent;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;

/**
 * This class computes the somatotype.
 * 
 * @author Hanine.H Al Madani <hanynowsky@gmail.com>
 */
@Model
public class SomatotypeBean implements Serializable {

	/**
	 * @since 1.0.1 - Generated Serial
	 */
	private static final long serialVersionUID = 5165441473749452832L;
	/* Measurments */
	private double subcapularSkinFold = 8.6; // mm
	private double suprailiacSkinFold = 8.3; // mm
	private double calfSkinFold = 1.8; // mm
	private double tricepsSkinFold = 6.3; // mm
	private double height = 177; // Cm
	private double weight = 58.1; // Kg
	private double humerusBicondyle = 5.65; // cm
	private double femurBicondyle = 10.95; // cm
	private double upperArmCirumference = 24.3; // cm
	private double calfCirumference = 32.2; // cm

	/* ------------ */
	private int mesoDeviation;

	/* Morphology Components */
	private boolean equationMethod = true;
	private double ectoComponent;
	private double mesoComponent;
	private double endoComponent;
	private int somatotype;
	private String somatotypeDesc;
	private String somatotypeGlobalDesc;
	private String roundedSomatotype;
	private String panel;

	/* Somatotype Coordinates */
	private double somatotypeX;
	private double somatotypeY;

	/* SVG Chart X & Y */
	private double svgX = 364;
	private double svgY = 347;

	int row;
	int mesoComponentRow;

	/* Chart */
	private CartesianChartModel somatotypeLineModel;
	private List<SomatoChartEntity> ectoLineList;
	private List<SomatoChartEntity> mesoLineList;
	private List<SomatoChartEntity> endoLineList;
	private List<SomatoChartEntity> girthList;
	private List<SomatoChartEntity> baseList;

	@Inject
	private ComputerBean computerBean;
	@Inject
	private SessionBean sessionBean;

	/**
	 * Constructor
	 */
	public SomatotypeBean() {
	}

	/**
	 * Post Bean construction method
	 */
	@PostConstruct
	public void init() {
		somatotypeLineModel = new CartesianChartModel();
		endoLineList = new ArrayList<SomatoChartEntity>();
		mesoLineList = new ArrayList<SomatoChartEntity>();
		ectoLineList = new ArrayList<SomatoChartEntity>();
		girthList = new ArrayList<SomatoChartEntity>();
		baseList = new ArrayList<SomatoChartEntity>();

		// Populate Meso List
		mesoLineList.add(new SomatoChartEntity(1, 7, 1));
		mesoLineList.add(new SomatoChartEntity(2, 6, 2));
		mesoLineList.add(new SomatoChartEntity(2, 5, 2));
		mesoLineList.add(new SomatoChartEntity(3, 5, 3));
		mesoLineList.add(new SomatoChartEntity(3, 4, 3));
		mesoLineList.add(new SomatoChartEntity(4, 4, 4));
		mesoLineList.add(new SomatoChartEntity(4, 3, 4));
		mesoLineList.add(new SomatoChartEntity(4, 2, 4));
		mesoLineList.add(new SomatoChartEntity(5, 1, 5));

		// Populate Endo List with common somatotypes
		endoLineList.add(new SomatoChartEntity(7, 1, 1));
		endoLineList.add(new SomatoChartEntity(7, 2, 2));
		endoLineList.add(new SomatoChartEntity(6, 2, 2));
		endoLineList.add(new SomatoChartEntity(5, 2, 2));
		endoLineList.add(new SomatoChartEntity(5, 3, 3));
		endoLineList.add(new SomatoChartEntity(4, 3, 3));
		endoLineList.add(new SomatoChartEntity(4, 4, 4));
		endoLineList.add(new SomatoChartEntity(3, 4, 4));
		endoLineList.add(new SomatoChartEntity(2, 4, 4));
		endoLineList.add(new SomatoChartEntity(1, 4, 4));
		endoLineList.add(new SomatoChartEntity(1, 5, 5));

		// Populate Ecto list
		ectoLineList.add(new SomatoChartEntity(1, 1, 7));
		ectoLineList.add(new SomatoChartEntity(2, 2, 7));
		ectoLineList.add(new SomatoChartEntity(2, 2, 6));
		ectoLineList.add(new SomatoChartEntity(2, 2, 5));
		ectoLineList.add(new SomatoChartEntity(3, 3, 5));
		ectoLineList.add(new SomatoChartEntity(3, 3, 4));
		ectoLineList.add(new SomatoChartEntity(4, 4, 4));
		ectoLineList.add(new SomatoChartEntity(4, 4, 3));
		ectoLineList.add(new SomatoChartEntity(4, 4, 2));
		ectoLineList.add(new SomatoChartEntity(5, 5, 1));

		// Populate Girth Line
		girthList.add(new SomatoChartEntity(7, 1, 1));
		// girthList.add(new SomatoChartEntity(7, 2, 1));
		// girthList.add(new SomatoChartEntity(7, 3, 1));
		girthList.add(new SomatoChartEntity(5, 5, 1));
		girthList.add(new SomatoChartEntity(3, 7, 1));
		girthList.add(new SomatoChartEntity(2, 7, 1));
		girthList.add(new SomatoChartEntity(1, 7, 1));
		girthList.add(new SomatoChartEntity(1, 7, 2));
		girthList.add(new SomatoChartEntity(1, 7, 3));
		girthList.add(new SomatoChartEntity(1, 5, 5));
		girthList.add(new SomatoChartEntity(1, 3, 7));
		girthList.add(new SomatoChartEntity(1, 2, 7));
		girthList.add(new SomatoChartEntity(1, 1, 7));

		// Populate base line
		baseList.add(new SomatoChartEntity(7, 1, 1));
		baseList.add(new SomatoChartEntity(7, 1, 2));
		baseList.add(new SomatoChartEntity(7, 1, 3));
		baseList.add(new SomatoChartEntity(5, 1, 5));
		baseList.add(new SomatoChartEntity(3, 1, 7));
		baseList.add(new SomatoChartEntity(2, 1, 7));
		baseList.add(new SomatoChartEntity(1, 1, 7));
	}

	/**
	 * <b>Computing Somatotype.</b> <h2>Endomorphic component</h2>
	 * <p>
	 * Add up the subtriceps, subscapular and suprailiac skinfolds and subtract
	 * calf skinfolds (this is commonly 0.0) and project the result on the
	 * endomorphic chart table (The value must correpond to a range : upper
	 * limit '2d column' and a lower limit '1st column'). This range corresponds
	 * to a component value on the third column.
	 * </p>
	 * <h2>Mesomorphic Component</h2>
	 * <p>
	 * </p>
	 * <h2>Ectomorphic Component</h2>
	 * <p>
	 * Height / cube root of weight;
	 * </p>
	 * <h2>Final somatotype value</h2>
	 * <p>
	 * A somatotype value is composed of 3 digits. (e.g. 553); where the first
	 * digit is the endomorphic component, the second is the mesomorphic
	 * component and the third digit is the ectomorphic component. Values of the
	 * three components are rounded up. (e.g. 4.5 becomes 5)
	 * </p>
	 * <ol>
	 * <li>Endomorphic component</li>
	 * <li>Mesomorphic component</li>
	 * <li>Ectomorphic component</li>
	 * </ol>
	 */
	public void computeSomatotype() {

		height = computerBean.getPersonHeight();
		weight = computerBean.getPersonWeight();

		// TODO make sure measures do not exceed their lowest and highest values
		// in the board by setting view validators
		try {
			String filePath = System.getProperty("java.io.tmpdir")
					+ File.separator + "honeybee" + File.separator
					+ Math.random() + File.separator;
			File folder = new File(filePath);
			if (!folder.exists()) {
				folder.mkdirs();
			}

			File endoFile = new File(filePath + "endoFile.csv");
			File mesoFile = new File(filePath + "mesoFile.csv");
			File ectoFile = new File(filePath + "ectoFile.csv");
			List<File> fileList = new ArrayList<>();
			fileList.add(endoFile);
			fileList.add(mesoFile);
			fileList.add(ectoFile);

			/* Create files */
			InputStream inputStream;
			OutputStream out;
			for (File f : fileList) {
				if (new File(filePath).isDirectory()) {
					inputStream = getClass().getResourceAsStream(
							File.separator + f.getName());
					out = new FileOutputStream(f);
					byte buf[] = new byte[10024];
					int len;
					while ((len = inputStream.read(buf)) > 0) {
						out.write(buf, 0, len);
					}
					out.close();
				}
			}
			BufferedReader bufferedReader;
			/* Browse Endomorphic component file */
			bufferedReader = new BufferedReader(new FileReader(endoFile)); //
			double endoEvaluation = 0;
			String line;
			row = 0;
			while ((line = bufferedReader.readLine()) != null) {
				/* 3 columns : Lower Limit | Upper Limit | Component */
				String[] content = line.split(";", 3);
				endoEvaluation = (subcapularSkinFold + tricepsSkinFold + suprailiacSkinFold)
						* (170.18 / height);
				if (endoEvaluation >= Double.parseDouble(content[0])
						&& endoEvaluation <= Double.parseDouble(content[1])) {
					setEndoComponent(Double.parseDouble(content[2]));
				}
				row++;
			}
			bufferedReader.close();

			/* Browse Mesomorphic File */
			bufferedReader = new BufferedReader(new FileReader(mesoFile)); //
			String mesoLine;
			int heightIndex = 0;
			int humerusIndex = 0;
			int femurIndex = 0;
			int armMeasureIndex = 0;
			int calfMeasureIndex = 0;
			double armMeasure = upperArmCirumference - (tricepsSkinFold / 10);
			double calfMeasure = calfCirumference - (calfSkinFold / 10);
			double mesoEvaluation;
			row = 0;
			while ((mesoLine = bufferedReader.readLine()) != null) {
				/*
				 * 12 columns : Height L&U | Humerus Bicondyle L&U | Femur
				 * Bicondyle L&U | Upper arm circumference – Triceps skinfold
				 * L&U | Calf circumference – calf skinfold L&U | Component L&U
				 */
				String[] content = mesoLine.split(";", 12);
				if (Double.parseDouble(content[0]) <= height
						&& height <= Double.parseDouble(content[1])) {
					heightIndex = row;
				}
				if (Double.parseDouble(content[2]) <= humerusBicondyle
						&& humerusBicondyle <= Double.parseDouble(content[3])) {
					humerusIndex = row;
				}
				if (Double.parseDouble(content[4]) <= femurBicondyle
						&& femurBicondyle <= Double.parseDouble(content[5])) {
					femurIndex = row;
				}
				if (Double.parseDouble(content[6]) <= armMeasure
						&& armMeasure <= Double.parseDouble(content[7])) {
					armMeasureIndex = row;
				}
				if (Double.parseDouble(content[8]) <= calfMeasure
						&& calfMeasure <= Double.parseDouble(content[9])) {
					calfMeasureIndex = row;
				}

				/*
				 * TODO Doubt: 4.0 is always the starting point for mesomorphic
				 * component if ratings are ambiguous or extreme
				 */
				// mesoEvaluation = ((humerusIndex - heightIndex)
				// + (femurIndex - heightIndex)
				// + (armMeasureIndex - heightIndex) + (calfMeasureIndex -
				// heightIndex)) / 4;
				// if (Double.parseDouble(content[10]) <= 4
				// && 4 <= Double.parseDouble(content[11])) {
				// mesoComponentRow = row + mesoEvaluation;
				// }
				row++;
			}
			bufferedReader.close();

			/**
			 * Mesomorphy pre-equation Sum of deviations divided by 8 and then
			 * added to 4.0.
			 * 
			 * */
			int D = ((humerusIndex - heightIndex) + (femurIndex - heightIndex)
					+ (armMeasureIndex - heightIndex) + (calfMeasureIndex - heightIndex));
			mesoEvaluation = (D / 8) + 4;
			double mesoValue = halfRoundedSoma(mesoEvaluation);
			setMesoComponent(mesoValue);

			/**
			 * Browse Mesomorphic file again and assign a value to component
			 * <p>
			 * e.g. If the meso evaluation is +1, we go one step forward from
			 * 4.0. If the meso evaluation is -2, we go 2 steps backward from
			 * 4.0.
			 * </p>
			 */
			/*
			 * bufferedReader = new BufferedReader(new FileReader(mesoFile));
			 * String mLine; int i = 0; while ((mLine =
			 * bufferedReader.readLine()) != null) { String[] contentLine =
			 * mLine.split(";", 12); if (i == mesoComponentRow) {
			 * setMesoComponent(Double.parseDouble(contentLine[11])); } i++; }
			 * 
			 * bufferedReader.close();
			 */

			/* Browse Ectomorphic File */
			bufferedReader = new BufferedReader(new FileReader(ectoFile));
			double ectoEvaluation;
			String ectoLine;
			row = 0;
			while ((ectoLine = bufferedReader.readLine()) != null) {
				/* 3 columns : Lower Limit | Upper Limit | Component */
				String[] content = ectoLine.split(";", 3);
				ectoEvaluation = height / Math.cbrt(weight);
				if (ectoEvaluation >= Double.parseDouble(content[0])
						&& ectoEvaluation <= Double.parseDouble(content[1])) {
					setEctoComponent(Double.parseDouble(content[2]));
				}
				row++;
			}
			bufferedReader.close();

			/* If equation method is used */
			if (equationMethod) {
				/**
				 * <h1>Endomorphic evaluation</h1>
				 * <p>
				 * X = (sum of triceps, subscapular and supraspinale skinfolds)
				 * multiplied by (170.18/height in cm). This is called
				 * height-corrected endomorphy and is the preferred method for
				 * calculating endomorphy.
				 * </p>
				 * <p>
				 * Endomorphy = -0.7182+0.1451(X) - 0.00068(X²) + 0.0000014(X3)
				 * </p>
				 */
				double correctedSkinfoldSum = (subcapularSkinFold
						+ tricepsSkinFold + suprailiacSkinFold)
						* (170.18 / height);
				endoEvaluation = -0.7182 + (0.1451 * correctedSkinfoldSum)
						- 0.00068 * (Math.pow(correctedSkinfoldSum, 2))
						+ 0.0000014 * (Math.pow(correctedSkinfoldSum, 3));
				setEndoComponent(Double.parseDouble(new DecimalFormat("#.#")
						.format(endoEvaluation)));
				if (endoEvaluation <= 0) {
					setEndoComponent(0.1);
				}

				/**
				 * <h1>Mesomorphic evaluation</h1>
				 * <p>
				 * mesomorphy = 0.858 x humerus breadth + 0.601 x femur breadth
				 * + 0.188 x corrected arm girth + 0.161 x corrected calf girth
				 * – height 0.131 + 4.5.
				 * </p>
				 * */

				double mesoEval = ((0.858 * humerusBicondyle)
						+ (0.601 * femurBicondyle) + (0.188 * armMeasure) + (0.161 * calfMeasure))
						- (height * 0.131) + 4.5;
				setMesoComponent(Double.parseDouble(new DecimalFormat("#.#")
						.format(mesoEval)));
				if (mesoEval <= 0) {
					setMesoComponent(0.1);
				}

				/**
				 * <h1>Ectomorphic Equation</h1>
				 * <p>
				 * Three different equations are used to calculate ectomorphy
				 * according to the height-weight ratio: If HWR is greater than
				 * or equal to 40.75 then ectomorphy = 0.732 HWR - 28.58 If HWR
				 * is less than 40.75 but greater than 38.25 then ectomorphy =
				 * 0.463 HWR - 17.63 If HWR is equal to or less than 38.25 then
				 * ectomorphy = 0.1
				 * </p>
				 * */
				double HWR = height / Math.cbrt(weight);
				if (HWR >= 40.75) {
					ectoEvaluation = (0.732 * HWR) - 28.58;
				} else if (HWR < 40.75 && HWR > 38.25) {
					ectoEvaluation = (0.463 * HWR) - 17.63;
				} else if (HWR <= 38.25) {
					ectoEvaluation = 0.1;
				} else {
					ectoEvaluation = 0.1;
				}
				setEctoComponent(Double.parseDouble(new DecimalFormat("#.#")
						.format(ectoEvaluation)));
			}

			/* Calculate Stomatotype */
			/**
			 * <p>
			 * The somatotype is always quoted in the order endomorphy,
			 * mesomorphy, ectomorphy, as a 3 digit number. Any component value
			 * should be rounded up (e.g. 4.5 becomes 5 and 5.5 becomes 6).
			 * </p>
			 */

			String rsoma = halfRoundedSoma(endoComponent) + " - "
					+ halfRoundedSoma(mesoComponent) + " - "
					+ halfRoundedSoma(ectoComponent);
			setRoundedSomatotype(rsoma);

			String somaCharSeq = Math.round(endoComponent) + ""
					+ Math.round(mesoComponent) + ""
					+ Math.round(ectoComponent);
			setSomatotype(Integer.parseInt(somaCharSeq));

			/* Define Somatotype category */
			double endo_meso = endoComponent - mesoComponent;
			double endo_ecto = endoComponent - ectoComponent;
			double meso_endo = mesoComponent - endoComponent;
			double meso_ecto = mesoComponent - ectoComponent;
			double ecto_meso = ectoComponent - mesoComponent;
			double ecto_endo = ectoComponent - endoComponent;
			if ((endo_meso >= -1 && endo_meso <= 1)
					&& (ecto_meso >= -1 && ecto_meso <= 1)
					&& (endo_ecto >= -1 && endo_ecto <= 1)) {
				/**
				 * no component differs by more than one unit from the other
				 * two.
				 */
				setSomatotypeDesc("Central");
			} else if (endo_meso > 0 && endo_ecto > 0
					&& (ecto_meso <= 0.5 && ecto_meso >= -0.5)) {
				/**
				 * endomorphy is dominant and mesomorphy and ectomorphy are
				 * equal (or do not differ by more than one-half unit).
				 */
				setSomatotypeDesc("Balanced endomorph");
			}

			else if (endo_meso > 0 && endo_ecto > 0 && meso_ecto > 0) {
				/**
				 * endomorphy is dominant and mesomorphy is greater than
				 * ectomorphy.
				 */
				setSomatotypeDesc("Mesomorphic endomorph");
			} else if (endo_meso >= -0.5 && endo_meso <= 0.5
					&& (meso_ecto > 0 && endo_ecto > 0)) {
				/**
				 * endomorphy and mesomorphy are equal (or do not differ by more
				 * than onehalf unit), and ectomorphy is smaller.
				 */
				setSomatotypeDesc("Mesomorph-endomorph");
			} else if ((meso_ecto > 0 && meso_endo > 0) && endo_ecto > 0) {
				/**
				 * mesomorphy is dominant and endomorphy is greater than
				 * ectomorphy.
				 */
				setSomatotypeDesc("Endomorphic mesomorph");
			} else if ((meso_ecto > 0 && meso_endo > 0)
					&& (endo_ecto <= 0.5 && endo_ecto >= -0.5)) {
				/**
				 * mesomorphy is dominant and endomorphy and ectomorphy are
				 * equal (or do not differ by more than one-half unit).
				 */
				setSomatotypeDesc("Balanced mesomorph");
			} else if ((meso_ecto > 0 && meso_endo > 0) && (ecto_endo > 0)) {
				/**
				 * mesomorphy is dominant and ectomorphy is greater than
				 * endomorphy.
				 */
				setSomatotypeDesc("Ectomorphic mesomorph");
			}

			else if ((meso_ecto >= -0.5 && meso_ecto <= 0.5)
					&& (ecto_endo > 0 && meso_endo > 0)) {
				/**
				 * mesomorphy and ectomorphy are equal (or do not differ by more
				 * than onehalf unit), and endomorphy is smaller.
				 */
				setSomatotypeDesc("Mesomorph-ectomorph");
			} else if (ecto_endo > 0 && ecto_meso > 0 && meso_endo > 0) {
				/**
				 * ectomorphy is dominant and mesomorphy is greater than
				 * endomorphy.
				 */
				setSomatotypeDesc("Mesomorphic ectomorph");
			} else if ((ecto_meso > 0 && ecto_endo > 0)
					&& (endo_meso >= -0.5 && endo_meso <= 0.5)) {
				/**
				 * ectomorphy is dominant and endomorphy and mesomorphy are
				 * equal (or do not differ by more than one-half unit).
				 */
				setSomatotypeDesc("Balanced ectomorph");
			} else if ((ecto_meso > 0 && ecto_endo > 0) && endo_meso > 0) {
				/**
				 * ectomorphy is dominant and endomorphy is greater than
				 * mesomorphy.
				 */
				setSomatotypeDesc("Endomorphic ectomorph");
			} else if ((endo_ecto >= -0.5 && endo_ecto <= 0.5)
					&& (endo_meso > 0 && ecto_meso > 0)) {
				/**
				 * endomorphy and ectomorphy are equal (or do not differ by more
				 * than onehalf unit), and mesomorphy is lower.
				 */
				setSomatotypeDesc("Endomorph-ectomorph");
			} else if ((endo_meso > 0 && endo_ecto > 0) && (ecto_meso > 0)) {
				/**
				 * endomorphy is dominant and ectomorphy is greater than
				 * mesomorphy.
				 */
				setSomatotypeDesc("Ectomorphic endomorph");
			} else {
				setSomatotypeDesc("@See Chart!!");
			}

			/* Set somatotype global description (category) */
			/**
			 * <p>
			 * Central: no component differs by more than one unit from the
			 * other two.
			 * </p>
			 * <p>
			 * Endomorph: endomorphy is dominant, mesomorphy and ectomorphy are
			 * more than one-half unit lower.
			 * </p>
			 * <p>
			 * Mesomorph: mesomorphy is dominant, endomorphy and ectomorphy are
			 * more than one-half unit lower.
			 * </p>
			 * <p>
			 * Ectomorph: ectomorphy is dominant, endomorphy and mesomorphy are
			 * more than one-half unit lower.
			 * </p>
			 */

			if ((meso_endo <= 1 && meso_endo >= -1)
					&& (meso_ecto <= 1 && meso_ecto >= -1)) {
				/**
				 * no component differs by more than one unit from the other
				 * two.
				 */
				setSomatotypeGlobalDesc("Central");
			} else if ((endo_meso > 0 && endo_ecto > 0) && (endo_meso > 0.5)
					&& (endo_ecto > 0.5)) {
				/**
				 * endomorphy is dominant, mesomorphy and ectomorphy are more
				 * than one-half unit lower.
				 */
				setSomatotypeGlobalDesc("Endomorph");
			} else if ((meso_endo > 0 && meso_ecto > 0) && (meso_ecto > 0.5)
					&& (meso_endo > 0.5)) {
				/**
				 * mesomorphy is dominant, endomorphy and ectomorphy are more
				 * than one-half unit lower.
				 */
				setSomatotypeGlobalDesc("Mesomorph");
			} else if ((ecto_endo > 0 && ecto_meso > 0) && (ecto_meso > 0.5)
					&& (ecto_endo > 0.5)) {
				/**
				 * ectomorphy is dominant, endomorphy and mesomorphy are more
				 * than one-half unit lower.
				 */
				setSomatotypeGlobalDesc("Ectomorph");
			} else {
				setSomatotypeGlobalDesc("@See chart!");
			}

			/* Set Sport Panel (Category) */
			// TODO set a panel depending on categgory
			setPanel("N/A");

			/* Set somatotype X & Y coordinates */
			double ectoComponentRounded = Math.round(ectoComponent);
			double mesoComponentRounded = Math.round(mesoComponent);
			double endoComponentRounded = Math.round(endoComponent);

			if (equationMethod) {
				ectoComponentRounded = halfRoundedSoma(ectoComponent);
				mesoComponentRounded = halfRoundedSoma(mesoComponent);
				endoComponentRounded = halfRoundedSoma(endoComponent);
			}

			somatotypeX = ectoComponentRounded - endoComponentRounded;
			somatotypeY = (2 * mesoComponentRounded)
					- (endoComponentRounded + ectoComponentRounded);

			/* Create Plot chart */
			ChartSeries endoPath = new ChartSeries("Endomorphy");
			ChartSeries mesoPath = new ChartSeries("Mesomorphy");
			ChartSeries ectoPath = new ChartSeries("Ectomorphy");
			ChartSeries chartGirth = new ChartSeries("");
			ChartSeries basePath = new ChartSeries("");
			ChartSeries personSomato = new ChartSeries("Somatotype");

			for (SomatoChartEntity e : mesoLineList) {
				mesoPath.set((e.getEctoElement() - e.getEndoElement()), (2 * e
						.getMesoElement() - (e.getEndoElement() + e
						.getEctoElement())));
			}

			for (SomatoChartEntity e : endoLineList) {
				endoPath.set((e.getEctoElement() - e.getEndoElement()), (2 * e
						.getMesoElement() - (e.getEndoElement() + e
						.getEctoElement())));
			}

			for (SomatoChartEntity e : ectoLineList) {
				ectoPath.set((e.getEctoElement() - e.getEndoElement()), (2 * e
						.getMesoElement() - (e.getEndoElement() + e
						.getEctoElement())));
			}

			chartGirth.set(-6, -6);
			for (SomatoChartEntity e : girthList) {
				chartGirth.set((e.getEctoElement() - e.getEndoElement()),
						(2 * e.getMesoElement() - (e.getEndoElement() + e
								.getEctoElement())));
			}

			for (SomatoChartEntity e : baseList) {
				basePath.set((e.getEctoElement() - e.getEndoElement()), (2 * e
						.getMesoElement() - (e.getEndoElement() + e
						.getEctoElement())));
			}

			personSomato.set(somatotypeX, somatotypeY); // Customized plot point

			somatotypeLineModel.addSeries(mesoPath);
			somatotypeLineModel.addSeries(endoPath);
			somatotypeLineModel.addSeries(ectoPath);
			somatotypeLineModel.addSeries(personSomato);
			somatotypeLineModel.addSeries(chartGirth);
			somatotypeLineModel.addSeries(basePath);

			/* Translate SVG Coordinates */
			translateXY();

			/* Finally Delete csv files */
			for (File f : fileList) {
				if (f.exists()) {
					f.delete();
				}
			}

		} catch (IOException | NumberFormatException ex) {
			System.err.println("Exception in fetching Morph Files");
			System.err.println(ex.getClass());
			Logger.getLogger(getClass().getName()).severe(ex.getMessage());
		}
	}

	/**
	 * Chart Item select listener
	 * 
	 * @param event
	 *            <b>PrimeFaces Item Select Event</b>
	 */
	public void chartItemSelect(ItemSelectEvent event) {
		System.out.println("Triggering PrimeFaces Chart Item Select Event");
		if (event != null) {
			String msg = "Chart Event: " + event.getItemIndex() + " | "
					+ event.getSeriesIndex();
			System.out.println(msg);
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(msg));
		}
	}

	/**
	 * Value rounded to the nearest half-unit,
	 * 
	 * @param value
	 * @return half rounded value
	 */
	public double halfRoundedSoma(double value) {
		double returnedValue = value;
		int lowerLimit = (int) value;
		double middleLimit = lowerLimit + 0.5;
		int upperLimit = (int) (value + 1);
		if (lowerLimit > 0) {
			if (middleLimit - value > 0
					&& (middleLimit - value <= value - lowerLimit)) {
				returnedValue = middleLimit;
			}
			if (middleLimit - value > 0
					&& (middleLimit - value >= value - lowerLimit)) {
				returnedValue = lowerLimit;
			}
			if (upperLimit - value > 0 && value - middleLimit > 0
					&& (value - middleLimit >= upperLimit - value)) {
				returnedValue = upperLimit;
			}
			if (upperLimit - value > 0 && value - middleLimit > 0
					&& (value - middleLimit <= upperLimit - value)) {
				returnedValue = middleLimit;
			}
			if (value == middleLimit) {
				returnedValue = middleLimit;
			}
		}
		if (returnedValue == 0) {
			System.out.println(" 0");
			returnedValue = 0.1;
		}
		return returnedValue;
	}

	/**
	 * Translate Chart X & Y to HTML SVG cX & cY
	 * 
	 */
	public void translateXY() {
		svgX += somatotypeX * 39;
		svgY = (svgY) - (somatotypeY * (45 / 2));
		sessionBean.setSvgCX(svgX);
		sessionBean.setSvgCY(svgY);
		// System.out.println("svgX = " + svgX + " svgY = " + svgY);
	}

	/**
	 * @return the subcapularSkinFold
	 */
	public double getSubcapularSkinFold() {
		return subcapularSkinFold;
	}

	/**
	 * @param subcapularSkinFold
	 *            the subcapularSkinFold to set
	 */
	public void setSubcapularSkinFold(double subcapularSkinFold) {
		this.subcapularSkinFold = subcapularSkinFold;
	}

	/**
	 * @return the suprailiacSkinFold
	 */
	public double getSuprailiacSkinFold() {
		return suprailiacSkinFold;
	}

	/**
	 * @param suprailiacSkinFold
	 *            the suprailiacSkinFold to set
	 */
	public void setSuprailiacSkinFold(double suprailiacSkinFold) {
		this.suprailiacSkinFold = suprailiacSkinFold;
	}

	/**
	 * @return the calfSkinFold
	 */
	public double getCalfSkinFold() {
		return calfSkinFold;
	}

	/**
	 * @param calfSkinFold
	 *            the calfSkinFold to set
	 */
	public void setCalfSkinFold(double calfSkinFold) {
		this.calfSkinFold = calfSkinFold;
	}

	/**
	 * @return the tricepsSkinFold
	 */
	public double getTricepsSkinFold() {
		return tricepsSkinFold;
	}

	/**
	 * @param tricepsSkinFold
	 *            the tricepsSkinFold to set
	 */
	public void setTricepsSkinFold(double tricepsSkinFold) {
		this.tricepsSkinFold = tricepsSkinFold;
	}

	/**
	 * @return the height
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * @param height
	 *            the height to set
	 */
	public void setHeight(double height) {
		this.height = height;
	}

	/**
	 * @return the weight
	 */
	public double getWeight() {
		return weight;
	}

	/**
	 * @param weight
	 *            the weight to set
	 */
	public void setWeight(double weight) {
		this.weight = weight;
	}

	/**
	 * @return the humerusBicondyle
	 */
	public double getHumerusBicondyle() {
		return humerusBicondyle;
	}

	/**
	 * @param humerusBicondyle
	 *            the humerusBicondyle to set
	 */
	public void setHumerusBicondyle(double humerusBicondyle) {
		this.humerusBicondyle = humerusBicondyle;
	}

	/**
	 * @return the femurBicondyle
	 */
	public double getFemurBicondyle() {
		return femurBicondyle;
	}

	/**
	 * @param femurBicondyle
	 *            the femurBicondyle to set
	 */
	public void setFemurBicondyle(double femurBicondyle) {
		this.femurBicondyle = femurBicondyle;
	}

	/**
	 * @return the upperArmCirumference
	 */
	public double getUpperArmCirumference() {
		return upperArmCirumference;
	}

	/**
	 * @param upperArmCirumference
	 *            the upperArmCirumference to set
	 */
	public void setUpperArmCirumference(double upperArmCirumference) {
		this.upperArmCirumference = upperArmCirumference;
	}

	/**
	 * @return the mesoDeviation
	 */
	public int getMesoDeviation() {
		return mesoDeviation;
	}

	/**
	 * @param mesoDeviation
	 *            the mesoDeviation to set
	 */
	public void setMesoDeviation(int mesoDeviation) {
		this.mesoDeviation = mesoDeviation;
	}

	/**
	 * @return the ectoComponent
	 */
	public double getEctoComponent() {
		return ectoComponent;
	}

	/**
	 * @param ectoComponent
	 *            the ectoComponent to set
	 */
	public void setEctoComponent(double ectoComponent) {
		this.ectoComponent = ectoComponent;
	}

	/**
	 * @return the mesoComponent
	 */
	public double getMesoComponent() {
		return mesoComponent;
	}

	/**
	 * @param mesoComponent
	 *            the mesoComponent to set
	 */
	public void setMesoComponent(double mesoComponent) {
		this.mesoComponent = mesoComponent;
	}

	/**
	 * @return the endoComponent
	 */
	public double getEndoComponent() {
		return endoComponent;
	}

	/**
	 * @param endoComponent
	 *            the endoComponent to set
	 */
	public void setEndoComponent(double endoComponent) {
		this.endoComponent = endoComponent;
	}

	/**
	 * @return the somatotype
	 */
	public int getSomatotype() {
		return somatotype;
	}

	/**
	 * @param somatotype
	 *            the somatotype to set
	 */
	public void setSomatotype(int somatotype) {
		this.somatotype = somatotype;
	}

	/**
	 * @return the calfCirumference
	 */
	public double getCalfCirumference() {
		return calfCirumference;
	}

	/**
	 * @param calfCirumference
	 *            the calfCirumference to set
	 */
	public void setCalfCirumference(double calfCirumference) {
		this.calfCirumference = calfCirumference;
	}

	/**
	 * @return the mesoComponentRow
	 */
	public int getMesoComponentRow() {
		return mesoComponentRow;
	}

	/**
	 * @param mesoComponentRow
	 *            the mesoComponentRow to set
	 */
	public void setMesoComponentRow(int mesoComponentRow) {
		this.mesoComponentRow = mesoComponentRow;
	}

	public String getSomatotypeDesc() {
		return somatotypeDesc;
	}

	public void setSomatotypeDesc(String somatotypeDesc) {
		this.somatotypeDesc = somatotypeDesc;
	}

	/**
	 * @return the somatotypeX
	 */
	public double getSomatotypeX() {
		return somatotypeX;
	}

	/**
	 * @param somatotypeX
	 *            the somatotypeX to set
	 */
	public void setSomatotypeX(double somatotypeX) {
		this.somatotypeX = somatotypeX;
	}

	/**
	 * @return the somatotypeY
	 */
	public double getSomatotypeY() {
		return somatotypeY;
	}

	/**
	 * @param somatotypeY
	 *            the somatotypeY to set
	 */
	public void setSomatotypeY(double somatotypeY) {
		this.somatotypeY = somatotypeY;
	}

	/**
	 * @return the somatotypeGlobalDesc
	 */
	public String getSomatotypeGlobalDesc() {
		return somatotypeGlobalDesc;
	}

	/**
	 * @param somatotypeGlobalDesc
	 *            the somatotypeGlobalDesc to set
	 */
	public void setSomatotypeGlobalDesc(String somatotypeGlobalDesc) {
		this.somatotypeGlobalDesc = somatotypeGlobalDesc;
	}

	/**
	 * @return the somatotypeLineModel
	 */
	public CartesianChartModel getSomatotypeLineModel() {
		return somatotypeLineModel;
	}

	/**
	 * @param somatotypeLineModel
	 *            the somatotypeLineModel to set
	 */
	public void setSomatotypeLineModel(CartesianChartModel somatotypeLineModel) {
		this.somatotypeLineModel = somatotypeLineModel;
	}

	/**
	 * @return the ectoLineList
	 */
	public List<SomatoChartEntity> getEctoLineList() {
		return ectoLineList;
	}

	/**
	 * @param ectoLineList
	 *            the ectoLineList to set
	 */
	public void setEctoLineList(List<SomatoChartEntity> ectoLineList) {
		this.ectoLineList = ectoLineList;
	}

	/**
	 * @return the mesoLineList
	 */
	public List<SomatoChartEntity> getMesoLineList() {
		return mesoLineList;
	}

	/**
	 * @param mesoLineList
	 *            the mesoLineList to set
	 */
	public void setMesoLineList(List<SomatoChartEntity> mesoLineList) {
		this.mesoLineList = mesoLineList;
	}

	/**
	 * @return the endoLineList
	 */
	public List<SomatoChartEntity> getEndoLineList() {
		return endoLineList;
	}

	/**
	 * @param endoLineList
	 *            the endoLineList to set
	 */
	public void setEndoLineList(List<SomatoChartEntity> endoLineList) {
		this.endoLineList = endoLineList;
	}

	/**
	 * @return the girthList
	 */
	public List<SomatoChartEntity> getGirthList() {
		return girthList;
	}

	/**
	 * @param girthList
	 *            the girthList to set
	 */
	public void setGirthList(List<SomatoChartEntity> girthList) {
		this.girthList = girthList;
	}

	/**
	 * @return the baseList
	 */
	public List<SomatoChartEntity> getBaseList() {
		return baseList;
	}

	/**
	 * @param baseList
	 *            the baseList to set
	 */
	public void setBaseList(List<SomatoChartEntity> baseList) {
		this.baseList = baseList;
	}

	/**
	 * @return the equationMethod
	 */
	public boolean isEquationMethod() {
		return equationMethod;
	}

	/**
	 * @param equationMethod
	 *            the equationMethod to set
	 */
	public void setEquationMethod(boolean equationMethod) {
		this.equationMethod = equationMethod;
	}

	/**
	 * @return the roundedSomatotype
	 */
	public String getRoundedSomatotype() {
		return roundedSomatotype;
	}

	/**
	 * @param roundedSomatotype
	 *            the roundedSomatotype to set
	 */
	public void setRoundedSomatotype(String roundedSomatotype) {
		this.roundedSomatotype = roundedSomatotype;
	}

	/**
	 * @return the panel
	 */
	public String getPanel() {
		return panel;
	}

	/**
	 * @param panel
	 *            the panel to set
	 */
	public void setPanel(String panel) {
		this.panel = panel;
	}

	/**
	 * @return the svgX
	 */
	public double getSvgX() {
		return svgX;
	}

	/**
	 * @param svgX
	 *            the svgX to set
	 */
	public void setSvgX(double svgX) {
		this.svgX = svgX;
	}

	/**
	 * @return the svgY
	 */
	public double getSvgY() {
		return svgY;
	}

	/**
	 * @param svgY
	 *            the svgY to set
	 */
	public void setSvgY(double svgY) {
		this.svgY = svgY;
	}

}
