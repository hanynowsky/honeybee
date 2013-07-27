package org.otika.honeybee.util;

import java.io.IOException;
import java.util.Enumeration;

import javax.inject.Inject;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.Logger;

/**
 * Servlet Filter implementation : class UriFilter
 */
@WebFilter(dispatcherTypes = { DispatcherType.REQUEST, DispatcherType.FORWARD,
		DispatcherType.INCLUDE, DispatcherType.ERROR }, servletNames = { "Faces Servlet" })
public class UriFilter implements Filter {

	@Inject
	private UtilityBean utilityBean;
	@Inject
	private SessionBean sessionBean;

	/**
	 * Default constructor.
	 */
	public UriFilter() {
		// TODO
	}

	/**
	 * @see Filter#destroy()
	 */
	@Override
	public void destroy() {
		// TODO
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		boolean signin = request.getRequestURI().contains("signin");
		Enumeration<String> headers = request.getHeaderNames();

		try {
			if (!signin) {
				while (headers.hasMoreElements()) {
					String header = (String) headers.nextElement();
					String headerValue = request.getHeader(header);
					if (header.equalsIgnoreCase("referer")
							&& request.getRemoteUser() == null) {
						if (!headerValue.contains("javax.faces.resource")
								&& !headerValue.equals("")) {
							/*
							 * TODO the operation is executed many times: figure
							 * out how to make it execute only once
							 * System.out.println
							 * (utilityBean.cutRefererString(headerValue));
							 */
							sessionBean.setOriginalViewName(utilityBean
									.cutRefererString(headerValue));
							break;
						}
						// TODO break here maybe :( ?
					}
				}

			} else {
				// TODO set original view name to some proper value
				response.getCharacterEncoding();
			}
		} catch (Exception ex) {
			Logger.getLogger(getClass().getName())
					.error("\n" + ex.getMessage());
		}
		chain.doFilter(req, res);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	@Override
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO
	}
}
