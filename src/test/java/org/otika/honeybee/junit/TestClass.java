package org.otika.honeybee.junit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.otika.honeybee.model.Store;
import org.otika.honeybee.util.Repository;
import org.otika.honeybee.util.RequestBean;
import org.otika.honeybee.util.UtilityBean;

public class TestClass {

	private UtilityBean utilityBean = new UtilityBean();
	private EJBContainer ec;
	private Context ctx;

	@Before
	public void initContainer() throws Exception {
		ec = EJBContainer.createEJBContainer();
		ctx = ec.getContext();
	}

	@After
	public void closeContainer() throws Exception {
		if (ec != null)
			ec.close();
	}

	@Test
	public void test() {
		try {
			Repository repository = (Repository) ctx
					.lookup("java:global/classes/Repository");
			RequestBean statelessBean = (RequestBean) ctx
					.lookup("java:global/classes/StatelessBean");
			statelessBean.showHello();
			// assertNotNull(repository.findAllStoreItems());
			for (Store store : repository.findAllStoreItems()) {
				System.out.println(store.getLabel());
			}
		} catch (NamingException e) {
			Logger.getLogger(getClass().getName()).severe(e.getMessage());
		}
	}

	void method() {
		String username = "root";
		String pass = "monsql";
		utilityBean.execBash("mysqldump -u " + username + " -p" + pass
				+ " honeybee > $HOME/app-root/data/honeybeedump.sql");
	}

	void showDate() {
		Date date = new Date();
		DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		DateFormat monthFormat = new SimpleDateFormat("MM");
		DateFormat yearFormat = new SimpleDateFormat("yyyy");
		DateFormat dayFormat = new SimpleDateFormat("dd");

		System.out.println(yearFormat.format(date) + monthFormat.format(date)
				+ dayFormat.format(date) + "-" + timeFormat.format(date));
	}

	void writeFile() {
		try {
			String p = System.getProperty("user.home");
			File presFile = new File(p + File.separator + "honeybee"
					+ File.separator + "export" + File.separator
					+ "presFile.html");
			new File(p + File.separator + "honeybee" + File.separator
					+ "export").mkdirs();
			presFile.createNewFile();
			FileWriter fstream = new FileWriter(presFile, false);

			BufferedWriter out = new BufferedWriter(fstream);
			String tag = "<html>";
			out.write(tag);
			out.newLine();
			out.write("<head>");
			out.append("</head>");
			out.append("<body>");
			out.newLine();
			out.append("-----");
			out.write("<br />");
			out.write("<h1>");
			out.write("Ingredients");
			out.write("</h1>");
			out.write("</body>");
			out.write("</html>");
			System.out.println("- presFile.html File Created in "
					+ presFile.getCanonicalPath());
			out.close();

		} catch (Exception ex) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE,
					ex.getMessage(), ex);
		}
	} // END OF METHOD

	void bash() {
		utilityBean.execBash("aptitude search mysql | grep workbench");
	}

	void fileToString() {
		InputStream is = getClass().getResourceAsStream("/css.css");
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		try {
			while ((br.read()) != -1) {
				sb.append(br.readLine());
				sb.append("\n");
			}
		} catch (IOException e) {
			System.out.println("System OUT:  " + e.getMessage() + " "
					+ e.getCause());
		}
		System.out.println(sb.subSequence(0, sb.length()));
	}

	String hashValue(String value) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			byte[] hash = messageDigest.digest(value.getBytes("UTF-8"));
			StringBuilder stringBuilder = new StringBuilder();
			for (int i = 0; i < hash.length; i++) {
				stringBuilder.append(Integer.toString((hash[i] & 0xff) + 0x100,
						16).substring(1));
			}
			System.out.println(stringBuilder.toString());
			return stringBuilder.toString();
		} catch (NoSuchAlgorithmException ex) {
			Logger.getLogger(TestClass.class.getName()).severe(ex.getMessage());
			return "";
		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			String msg = "SHAConverter.getAsObject: "
					+ unsupportedEncodingException.getMessage();
			Logger.getLogger(TestClass.class.getName()).severe(msg);
			return "";
		}
	}

	File someFile() {
		File file = new File(System.getenv("HOME") + File.separator
				+ "Desktop/toto.txt");
		utilityBean.execBash("gedit " + file.getPath());
		return file;
	}

	String cutString(String string) {

		String cut = "";
		if (string.contains(".com")) {
			cut = string.split(".com")[1];
		} else if (string.contains(".fr")) {
			cut = string.split(".fr")[1];
		} else if (string.contains(".ma")) {
			cut = string.split(".ma")[1];
		} else {
			cut = string.split(":[0-9][0-9][0-9][0-9]")[1].replace("/honeybee",
					"");
		}

		return cut;
	}

	File zipFile(File file) {
		File zippedFile = file;
		// TODO process file here
		try {
			
			FileOutputStream fos = new FileOutputStream(file.getPath()+".zip");
			ZipOutputStream zos = new ZipOutputStream(fos);
			FileInputStream fis = new FileInputStream(file);
			int i;
			byte[] bytes = new byte[10024];
			while ((i = fis.read(bytes)) != -1){
			fos.write(bytes, 0, i);
			//zos.write(bytes, 0, i);
			}			
			fis.close();			
			zos.setLevel(9);
			ZipEntry ze = new ZipEntry(file.getName());
			zos.putNextEntry(ze);
			zos.closeEntry();
			fos.flush();
			zos.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			String msg = e.getMessage();
			org.jboss.logging.Logger.getLogger(getClass().getName()).error(msg);
			return null;
		}
		return zippedFile;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new TestClass().zipFile(new File("/home/hanine/Desktop/headers.txt"));
		String s = System.getProperty("user.dir");
		String h = System.getProperty("user.home");
		System.out.println(s);
		System.out.println(h);
		System.out.println(System.getenv("XDG_CURRENT_DESKTOP"));
		new TestClass().hashValue("honeybee");
		new TestClass().hashValue("honeybee");
		System.out.println(DigestUtils.sha256Hex("honeybee"));
		System.out.println(DigestUtils.sha256Hex(""));
		System.out.println(String.valueOf(UUID.randomUUID().toString())
				.substring(0, 5)
				+ "_"
				+ String.valueOf(UUID.randomUUID().toString()).substring(0, 6)
						.toUpperCase());
		System.out
				.println(new TestClass()
						.cutString("http://localhost:8080/honeybee/defect/search.xhtml"));
		System.out
				.println(new TestClass()
						.cutString("http://honeybee-otika.rhcloud.com/defect/search.xhtml"));
	}
}
