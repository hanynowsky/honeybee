package org.otika.honeybee.util;

import javax.annotation.PostConstruct;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timer;
import javax.inject.Inject;

/**
 * This EJB has a scheduled method that dumps the database once a day 
 * @author Hanine <hanynosky@gmail.com>
 *
 */
@Singleton
@Startup
public class DumpBean {
	
	private String s = System.getProperty("user.home");
	@Inject
	private UtilityBean utilityBean;

	/**
	 * Default constructor.
	 */
	public DumpBean() {
		System.out.println(s);
	}
	
	@PostConstruct
	public void init(){
		
	}
	//@Schedule(second = "*/10", minute = "*", hour = "*", dayOfWeek = "*", dayOfMonth = "*", month = "*", year = "*", info = "DataBaseDumpTimer")
	@Schedule(second = "1", minute = "1", hour = "12", dayOfWeek = "*", dayOfMonth = "*", month = "*", year = "*", info = "DataBaseDumpTimer")
	private void scheduledTimeout(final Timer t) {
		
		utilityBean.execBash("mkdir -v -p $HOME/app-root/data");
		//utilityBean.execBash("mysqldump -u root -pmonsql honeybee > $HOME/app-root/data/honeybeedump.sql");		    
		utilityBean.execBash("mysqldump -u adminwNJulYt -p1GK_z_NqUW7g honeybee > $HOME/app-root/data/honeybeedump.sql");
//		utilityBean.execBash("mysqldump -u adminVRxswz9 -pyBLijzxjZMYM honeybee > $HOME/app-root/data/honeybeedump.sql");		 
		System.out.println("@Schedule called at: " + new java.util.Date());
		System.out.println(t.getSchedule());		
	}
}