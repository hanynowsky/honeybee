/**
 * 
 */
package org.otika.honeybee.util;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

/**
 * Interceptor For live testing purposes 
 * @author Hanine <hanynowsky@gmail.com>
 * 
 */
@Interceptor
@Audit
public class AuditInterceptor {

	public AuditInterceptor() {
		// TODO
	}

	/**
	 * @param context
	 * @return context_process_object
	 * @throws Exception
	 */
	@AroundInvoke
	public Object aroundInvoke(InvocationContext context) throws Exception {
		System.out.println("BEFORE: " + context.getMethod());
		Object result = context.proceed();
		System.out.println("AFTER: " + context.getMethod());
		return result;
	}

} // END OF CLASS
