package org.otika.honeybee.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Model;
import javax.inject.Inject;

/**
 * This class computes the somatotype
 * 
 * @author Hanine.H Al Madani <hanynowsky@gmail.com>
 * 
 */
@Model
public class SomatotypeBean implements Serializable {

	private static final long serialVersionUID = 5165441473749452832L;
	/* Measurments */
	private double subtricepsSkinFold = 15.6;
	private double subcapularSkinFold = 14.6;
	private double suprailiacSkinFold = 19.5;
	private double calfSkinFold = 0.1;
	private double tricepsSkinFold = 12.3;
	private double height = 170.00; // Cm
	private double weight = 61.2; // Kg
	private double humerusBicondyle = 5.15;
	private double femurBicondyle = 8.20;
	private double upperArmCirumference = 39.3;
	private double calfCirumference = 38;
	/* ------------ */
	private int mesoDeviation;

	/* Morphology Components */
	private double ectoComponent;
	private double mesoComponent;
	private double endoComponent;
	private int somatotype;
        private String somatotypeDesc;

	int row;
	int mesoComponentRow;
	
	@Inject
	private ComputerBean computerBean;

	public SomatotypeBean() {
		// Constructor
	}

	@PostConstruct
	public void init() {

	}

	/**
	 * <h1>Computing Somatotype.</h1> <h2>Endomorphic component</h2>
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
			String line;
			row = 0;
			while ((line = bufferedReader.readLine()) != null) {
				/* 3 columns : Lower Limit | Upper Limit | Component */
				String[] content = line.split(";", 3);
				double endoEvaluation = subcapularSkinFold + subtricepsSkinFold
						+ suprailiacSkinFold - calfSkinFold;
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
			double armMeasure = upperArmCirumference - tricepsSkinFold;
			double calfMeasure = calfCirumference - calfSkinFold;
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

				/* 4.0 is always the starting point for mesomorphic component */
				int mesoEvaluation = ((humerusIndex - heightIndex)
						+ (femurIndex - heightIndex)
						+ (armMeasureIndex - heightIndex) + (calfMeasureIndex - heightIndex)) / 4;
				if (Double.parseDouble(content[10]) <= 4
						&& 4 <= Double.parseDouble(content[11])) {
					mesoComponentRow = row + mesoEvaluation;
				}
				row++;
			}
			bufferedReader.close();
			/**
			 * Browse Mesomorphic file again and assign a value to component
			 * <p>
			 * e.g. If the meso evaluation is +1, we go one step forward from
			 * 4.0. If the meso evaluation is -2, we go 2 steps backward from
			 * 4.0.
			 * </p>
			 */
			bufferedReader = new BufferedReader(new FileReader(mesoFile));
			String mLine;
			int i = 0;
			while ((mLine = bufferedReader.readLine()) != null) {
				String[] contentLine = mLine.split(";", 12);
				if (i == mesoComponentRow) {
					setMesoComponent(Double.parseDouble(contentLine[11]));
				}
				i++;
			}

			bufferedReader.close();

			/* Browse Ectomorphic File */
			bufferedReader = new BufferedReader(new FileReader(ectoFile)); //
			String ectoLine;
			row = 0;
			while ((ectoLine = bufferedReader.readLine()) != null) {
				/* 3 columns : Lower Limit | Upper Limit | Component */
				String[] content = ectoLine.split(";", 3);
				double ectoEvaluation = height / Math.cbrt(weight);
				if (ectoEvaluation >= Double.parseDouble(content[0])
						&& ectoEvaluation <= Double.parseDouble(content[1])) {
					setEctoComponent(Double.parseDouble(content[2]));
				}
				row++;
			}
			bufferedReader.close();
			/* Calculate Stomatotype */
			/**
			 * <p>
			 * The somatotype is always quoted in the order endomorphy,
			 * mesomorphy, ectomorphy, as a 3 digit number. Any component value
			 * should be rounded up (e.g. 4.5 becomes 5 and 5.5 becomes 6).
			 * </p>
			 */
			String somaCharSeq = Math.round(endoComponent) + ""
					+ Math.round(mesoComponent) + ""
					+ Math.round(ectoComponent);
			setSomatotype(Integer.parseInt(somaCharSeq));			

			/*
			 * TODO use the global somatotype chart to project the somatotype
			 * value againt the chart value translation
			 */
                        /* Browse Somatotype Chart file */
                        setSomatotypeDesc("NOT-YET-IMP!");

		} catch (IOException | NumberFormatException ex) {
			System.err.println("Exception in fetching Morph Files");
			System.err.println(ex.getClass());
			Logger.getLogger(getClass().getName()).severe(ex.getMessage());
		}
	}

	/**
	 * @return the subtricepsSkinFold
	 */
	public double getSubtricepsSkinFold() {
		return subtricepsSkinFold;
	}

	/**
	 * @param subtricepsSkinFold
	 *            the subtricepsSkinFold to set
	 */
	public void setSubtricepsSkinFold(double subtricepsSkinFold) {
		this.subtricepsSkinFold = subtricepsSkinFold;
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

}
