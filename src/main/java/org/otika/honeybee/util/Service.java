package org.otika.honeybee.util;

import javax.ejb.Remote;

/**
 * Interface that serves as a service structure
 * Qualifying this interface as @Remote so that it can be accessed by Java SE
 * @author Hanine H.A.M <kysohuu.madani@gmail.com>
 *
 */
@Remote
public interface Service {
	
	String message(String msg);
	void log(String info);
}
