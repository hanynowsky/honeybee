package org.otika.honeybee.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.otika.honeybee.view.ViewUtils;

@Named(value = "infoBean")
@RequestScoped
// TODO must be a view scoped managed bean
public class InfoBean implements Serializable {

    private static final long serialVersionUID = 564571247698526738L;
    private HttpServletRequest request;
    private String country, city;
    private String inputLine = "";
    private final static Logger ifLog = Logger.getLogger(InfoBean.class.getName());

    public InfoBean() {
    }

    /**
     * Returns Internet IP Address, otherwise local host IP Address
     *
     * @return ipAddress Remote IP Address
     */
    public String getFacesClientIpAddress() {
        request = (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest();
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

    public String getFacesClientHost() {
        request = (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest();
        return request.getRemoteHost();
    }

    public List<String> getFacesClientHeaders() {
        request = (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest();
        Enumeration<String> names = request.getHeaderNames();
        List<String> headers = new ArrayList<>();
        while (names.hasMoreElements()) {
            headers.add(names.nextElement());
        }
        return ViewUtils.asList(headers);
    }

    public String getFacesClientBrowserAgent() {
        ExternalContext externalContext = FacesContext.getCurrentInstance()
                .getExternalContext();
        String userAgent = externalContext.getRequestHeaderMap().get(
                "User-Agent");
        return userAgent;
    }

    public String getOperatingSystem() {
        String version = System.getProperty("os.version");
        String name = System.getProperty("os.name");
        return name + " " + version;
    }

    /**
     * Uses an external API to fetch client IP address, country and city
     */
    public void findIPLocation() {
        String locator = "http://api.ipinfodb.com/v3/ip-city/?key="
                + "5eab60a576fbee6d6733803b8b85c76aeda0b617128e8f232a2095b52644a8e2&ip=";
        try {
            String IP = getFacesClientIpAddress();
            URL link = new URL(locator + IP);
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    link.openStream()));

            while ((inputLine = in.readLine()) != null) {
                // System.out.println(inputLine);
                String[] d = inputLine.split("[;]");
                if (d[4].equals("-")) {
                    setCountry(null);
                    setCity(null);
                } else {
                    setCountry(d[4]);
                    setCity(d[6]);
                }
            }
            in.close();
        } catch (IOException e) {
            String info = "IO Exception: Probably No Internet Connection: "
                    + e.getMessage() + " - CAUSE: " + e.getCause();
            ifLog.severe(info);
            System.out.println("Probably No Internet Connection: "
                    + e.getMessage());
            System.out.println("Probably No Internet Connection:CAUSE: "
                    + e.getCause());
        }
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
} // END OF CLASS
