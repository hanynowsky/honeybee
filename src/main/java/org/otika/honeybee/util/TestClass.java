package org.otika.honeybee.util;

public class TestClass {

	UtilityBean utilityBean = new UtilityBean();

	private void method() {		
		String username = "root";
		String pass = "monsql";	
		utilityBean.execBash("mysqldump -u " + username + " -p" + pass
				+ " honeybee > $HOME/app-root/data/honeybeedump.sql");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String s = System.getProperty("user.home");
		System.out.println(s);
		new TestClass().method();

	}

}
