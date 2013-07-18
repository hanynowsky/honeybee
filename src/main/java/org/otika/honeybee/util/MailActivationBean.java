package org.otika.honeybee.util;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Pattern;

import org.otika.honeybee.model.Enduser;
import org.otika.honeybee.view.EnduserBean;

/**
 * Activates a member using the UUID key sent by email to him/her.
 *
 * @author Hanine HAMZIOUI <hanine.hamzioui@gmail.com>
 * @since 1.0
 *
 */
@ManagedBean(name = "mailActivationBean")
@javax.faces.bean.RequestScoped
@Stateless
public class MailActivationBean {

    private static final String PASSWORD_PATTERN =
            "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";
    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    private EntityManager entityManager;
    @EJB
    private Repository repository;
    @Pattern(regexp = PASSWORD_PATTERN, message = "Invalid email / Email non valide")
    private String emailAddress = ""; // Value is retrieved from input Text
    @Inject
    private MailBean mailBean;
    @EJB
    private EnduserBean enduserBean;
    @EJB
    private AuthenticationBean authenticationBean;
    @Inject
    private UtilityBean utilityBean;
    @Inject
    private BundleBean bundleBean;
    @ManagedProperty(value = "#{param.key}")
    private String key;
    private boolean valid;
    private String manualKey;
    FacesContext context;
    HttpServletRequest request;
    HttpServletResponse response;

    public MailActivationBean() {
    }

    @PostConstruct
    public void init() {
        valid = checkKey(key);
        context = FacesContext.getCurrentInstance();
        request = (HttpServletRequest) context.getExternalContext()
                .getRequest();
        response = (HttpServletResponse) context.getExternalContext()
                .getResponse();
    }

    /**
     * Checks the existence of a user in database using its key
     *
     * @param key2 user key
     * @return false if user is found on database
     */
    private boolean checkKey(String key2) {
        // TODO This method should be moved to repository
        Query q = entityManager.createNamedQuery("Enduser.findByUserkey");
        q.setParameter("userkey", key2);
        List<?> list = q.getResultList();
        if (list.size() > 0) {
            enableUser(key2);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Manually activate a member
     *
     * @param key4 User key
     */
    public void manuallyEnableUser(String key4) {
        if (key4.equalsIgnoreCase("k")) {
            key4 = manualKey;
        }
        enableUser(key4);
    }

    /**
     * Sets the member property 'isenabled' to true
     *
     * @param key3 - user UUID key in member table
     */
    private String enableUser(String key3) {

        Enduser m = repository.findByUserkey(key3);
        if (m != null) {
            System.out.println("Trying to activate Enduser: " + m.getEmail()
                    + " whose state is: " + m.getIsactive());
            boolean enabled = m.getIsactive();
            if (!enabled) {
                m.setIsactive(true);
                System.out.println("Activation - set to true");
                try {
                    enduserBean.updateEnduser(m);
                    System.out
                            .println("Activated end user has been merged. New "
                            + "state is: " + m.getIsactive());
                    utilityBean
                            .showMessage("info", "Account has been activated.",
                            " Account activated");
                    return connectMember(m);
                } catch (Exception e) {
                    FacesContext.getCurrentInstance().addMessage(
                            null,
                            new FacesMessage("Activation Failure!!!",
                            " Failure"));
                    System.out.println("ENTITY MANAGER MERGING FAILURE");
                    System.err.println(e.getMessage() + " - cause : "
                            + e.getCause());
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage("Account Already Activated.",
                        " Already active"));
            }

        } else {
            System.out.println("No End user with " + key3 + " found!");
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                    "No End user with key: " + key3 + " found!",
                    " No user with key " + key3));
        }
        return null;
    }

    /**
     * Sends an email with the UUID key to the member who did not succeed in
     * activating his/her account.
     */
    public void activateMember() {
        String address = getEmailAddress();

        Query q = entityManager.createNamedQuery("Enduser.findByEmail",
                Enduser.class);
        q.setParameter("email", address);
        List<?> mlist = q.getResultList();

        if (mlist.size() < 1) {
            String msg = address + " : " + bundleBean.i18n("email_notfound");
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
        } else {
            Enduser m = repository.findByEmail(address);
            if (!m.getIsactive()) {
                // TODO send a new password
                newPassword(m, address, false);
                String messg = "We have sent a confirmation email to the address: "
                        + address + " Please check your email inbox.";
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(messg, null));
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Account Already Activated!"));
            }
        }
    }

    /**
     * Password Recovery. Sends the user an email with a new password
     */
    public void passwordRecovery() {
        String address = getEmailAddress();
        Query q = entityManager.createNamedQuery("Enduser.findByEmail",
                Enduser.class);
        q.setParameter("email", address);
        List<?> mlist = q.getResultList();

        if (mlist.size() < 1) {
            String msg = address + " : "
                    + bundleBean.i18n("email_notfound");
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
        } else {
            Enduser m = repository.findByEmail(address);
            if (m != null) {
                newPassword(m, address, true);
            } else {
                FacesContext
                        .getCurrentInstance()
                        .addMessage(
                        null,
                        new FacesMessage(
                        "Problem sending password recovery mail! Contact us."));
            }
        }
    }

    /**
     * utility method that sets a new password for a given user
     *
     * @param m End user
     * @param email Email address
     * @param recovery Whether to recover password or activate the user
     */
    private void newPassword(Enduser m, String email, boolean recovery) {
        String newPassword = String.valueOf(
                UUID.randomUUID().toString()).substring(0, 5)
                + "_"
                + String.valueOf(UUID.randomUUID().toString())
                .substring(0, 6).toUpperCase();
        m.setPassword(utilityBean.hashHex(newPassword));
        m.setPassconf(utilityBean.hashHex(newPassword));
        enduserBean.updateEnduser(m);
        if (recovery) {
            mailBean.simpleMail(email, newPassword);
            utilityBean.showMessage("info",
                    "Recovery Email sent to " + m.getEmail(), "");
        } else {
            mailBean.deliverEmail(email, m.getName(), m.getEmail(),
                    m.getPassword(), false, m.getUserkey());
        }
    }

    /**
     * Method used to authenticate the newly activated member
     *
     * @param m End user
     */
    private String connectMember(Enduser m) {
        try {
            if (m != null) {
                System.out.println("Trying to connect end user with: "
                        + m.getEmail());
                authenticationBean.login(m.getEmail(), m.getPassword(), false);
                String path = "/misc/activation.xhtml?key="+m.getUserkey();
                FacesContext
                        .getCurrentInstance()
                        .getExternalContext().redirect(FacesContext
                        .getCurrentInstance()
                        .getExternalContext().getRequestContextPath()
                        + path);
                return "/misc/activation?faces-redirect=false";
            }
        } catch (Exception ex) {
            System.out
                    .println("Failure authenticating the newly activated user");
            String msg = ex.getMessage() + " | " + ex.getCause().getMessage();
            Logger.getLogger(getClass().getName()).severe(msg);
            return null;
        }
        return null;
    }

    /**
     * @return the valid
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * @param valid the valid to set
     */
    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getManualKey() {
        return manualKey;
    }

    public void setManualKey(String manualKey) {
        this.manualKey = manualKey;
    }
}
