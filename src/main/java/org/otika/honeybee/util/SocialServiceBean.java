///**
//package org.otika.honeybee.util;
//
//import static com.google.common.collect.Lists.newArrayList;
//
//import java.io.IOException;
//import java.io.Serializable;
//import java.util.List;
//import java.util.Map;
//import java.util.logging.Logger;
//
//import javax.annotation.PostConstruct;
//import javax.enterprise.context.SessionScoped;
//import javax.enterprise.event.Observes;d
//import javax.enterprise.inject.Any;
//import javax.enterprise.inject.Produces;
//import javax.faces.context.ExternalContext;
//import javax.faces.context.FacesContext;
//import javax.inject.Inject;
//import javax.inject.Named;
//
//import org.agorava.core.api.MultiSessionManager;
//import org.agorava.core.api.UserProfile;
//import org.agorava.core.api.event.SocialEvent;
//import org.agorava.core.api.event.StatusUpdated;
//import org.agorava.core.api.oauth.OAuthService;
//import org.agorava.core.api.oauth.OAuthSession;
//import org.agorava.core.api.oauth.OAuthToken;
//import org.agorava.facebook.model.FacebookProfile;
//
//import com.google.common.base.Function;
//import com.google.common.collect.Maps;
//
//@Named
//@SessionScoped
//public class SocialServiceBean implements Serializable {
//
//    private static final long serialVersionUID = -7875755648192164905L;
//    private String Status;
//    private String selectedService;
//    private String authURL;
//    
//    private String userFullName;
//    private String userEmail;
//    private String userImageUrl;
//    private boolean userVerified;
//    private String verifier;
//    private String verifierName;
//    /*private UserProfile userProfile;
//    private FacebookProfile facebookProfile; */
//	
//    
//    // private FacebookBaseService facebookBaseService; // Add getter and setter
//
//    // Why the hell is it not eligible for injection?
//    /*@Inject
//    private MultiSessionManager manager; */
//
//    public SocialServiceBean() {
//    }
//
//    @PostConstruct
//    public void init() {
//
//    }
//
//    @Named
//    @Produces
//    public OAuthService getCurrentService() {
//        return manager.getCurrentService();
//    }
//
//    /**
//     * OAuthSession instance from MultiServiceManager
//     *
//     * @return
//     */
//    public OAuthSession getCurrentSession() {
//        return manager.getCurrentSession();
//    }
//
//    /**
//     * OAuthSession Setter
//     *
//     * @param currentSession
//     */
//    public void setCurrentSession(OAuthSession currentSession) {
//        manager.setCurrentSession(currentSession);
//    }
//
//    /**
//     * List of Active sessions
//     *
//     * @return
//     */
//    public List<OAuthSession> getSessions() {
//        return newArrayList(manager.getActiveSessions());
//    }
//
//    /**
//     * Access Token Getter from Current OAth Session
//     *
//     * @return
//     */
//    public OAuthToken getAccessToken() {
//        return getCurrentSession().getAccessToken();
//    }
//
//    /**
//     * Session Map
//     *
//     * @return
//     */
//    public Map<String, OAuthSession> getSessionsMap() {
//        return Maps.uniqueIndex(getSessions(),
//                new Function<OAuthSession, String>() {
//                    @Override
//                    public String apply(OAuthSession arg0) {
//
//                        return arg0.toString();
//                    }
//                });
//    }
//
//    /**
//     * Connect Current Social Service
//     */
//    public void connectCurrentService() {
//        Logger.getLogger(getClass().getName()).info(
//                "Connecting current social service");
//        manager.connectCurrentService();
//    }
//
//    /**
//     * Current Session Name getter
//     *
//     * @return
//     */
//    public String getCurrentSessionName() {
//        return manager.getCurrentSession() == null ? "" : manager
//                .getCurrentSession().toString();
//    }
//
//    /**
//     * Current session name setter
//     *
//     * @param cursrvHdlStr
//     */
//    public void setCurrentSessionName(String cursrvHdlStr) {
//        setCurrentSession(getSessionsMap().get(cursrvHdlStr));
//    }
//
//    /**
//     * Faces redirect to authorization URL
//     *
//     * @param url
//     * @throws IOException
//     */
//    public void redirectToAuthorizationURL(String url) throws IOException {
//
//        ExternalContext externalContext = FacesContext.getCurrentInstance()
//                .getExternalContext();
//        externalContext.redirect(url);
//    }
//
//    /**
//     * Time Line URL
//     *
//     * @return
//     */
//    public String getTimeLineUrl() {
//        if (getCurrentSession() != null && getCurrentSession().isConnected()) {
//            return "/social/"
//                    + getManager().getCurrentService().getSocialMediaName()
//                    .toLowerCase() + ".xhtml";
//        }
//        return "";
//    }
//
//    /**
//     * Initiates a new session for select social service
//     *
//     * @throws IOException
//     */
//    public void serviceInit() throws IOException {
//        Logger.getLogger(getClass().getName()).info(
//                "Initiating new session for " + selectedService.toString());
//        redirectToAuthorizationURL(manager.initNewSession(selectedService));
//
//    }
//
//    /**
//     * Observer for Status update
//     *
//     * @param statusUpdate
//     */
//    protected void statusUpdateObserver(
//            @Observes @Any StatusUpdated statusUpdate) {
//        if (statusUpdate.getStatus().equals(SocialEvent.Status.SUCCESS)) {
//            setStatus("");
//        }
//    }
//
//    /**
//     * Destroys current session to reset connection
//     */
//    public void resetConnection() {
//        Logger.getLogger(getClass().getName()).info(
//                "Reseting social connection");
//        manager.destroyCurrentSession();
//    }
//
//    /**
//     *
//     * @param url
//     * @throws IOException
//     */
//    public void redirectToAuthorizationURL() throws IOException {
//        ExternalContext externalContext = FacesContext.getCurrentInstance()
//                .getExternalContext();
//        externalContext.redirect(getAuthURL());
//    }
//
//    /**
//     * Social Media Name
//     *
//     * @return social media name as String
//     */
//    public String getSocialMediaName() {
//        return getCurrentService().getSocialMediaName().toLowerCase();
//    }
//
//    /**
//     * @return the authURL
//     */
//    public String getAuthURL() {
//        return authURL;
//    }
//
//    /**
//     * @param authURL the authURL to set
//     */
//    public void setAuthURL(String authURL) {
//        this.authURL = authURL;
//    }
//
//    /**
//     * @return the userProfile
//     */
//    public UserProfile getUserProfile() {
//        return userProfile;
//    }
//
//    /**
//     * @param userProfile the userProfile to set
//     */
//    public void setUserProfile(UserProfile userProfile) {
//        this.userProfile = userProfile;
//    }
//
//    /**
//     * @return the userFullName
//     */
//    public String getUserFullName() {
//        return userFullName;
//    }
//
//    /**
//     * @param userFullName the userFullName to set
//     */
//    public void setUserFullName(String userFullName) {
//        this.userFullName = userFullName;
//    }
//
//    /**
//     * @return the userImageUrl
//     */
//    public String getUserImageUrl() {
//        return userImageUrl;
//    }
//
//    /**
//     * @param userImageUrl the userImageUrl to set
//     */
//    public void setUserImageUrl(String userImageUrl) {
//        this.userImageUrl = userImageUrl;
//    }
//
//    /**
//     * @return the facebookProfile
//     */
//    public FacebookProfile getFacebookProfile() {
//        return facebookProfile;
//    }
//
//    /**
//     * @param facebookProfile the facebookProfile to set
//     */
//    public void setFacebookProfile(FacebookProfile facebookProfile) {
//        this.facebookProfile = facebookProfile;
//    }
//
//    /**
//     * @return the userEmail
//     */
//    public String getUserEmail() {
//        return userEmail;
//    }
//
//    /**
//     * @param userEmail the userEmail to set
//     */
//    public void setUserEmail(String userEmail) {
//        this.userEmail = userEmail;
//    }
//
//    /**
//     * @return the userVerified
//     */
//    public boolean isUserVerified() {
//        return userVerified;
//    }
//
//    /**
//     * @param userVerified the userVerified to set
//     */
//    public void setUserVerified(boolean userVerified) {
//        this.userVerified = userVerified;
//    }
//
//    /**
//     * @return the verifier
//     */
//    public String getVerifier() {
//        return verifier;
//    }
//
//    /**
//     * @param verifier the verifier to set
//     */
//    public void setVerifier(String verifier) {
//        this.verifier = verifier;
//    }
//
//    /**
//     * @return the verifierName
//     */
//    public String getVerifierName() {
//        return verifierName;
//    }
//
//    /**
//     * @param verifierName the verifierName to set
//     */
//    public void setVerifierName(String verifierName) {
//        this.verifierName = verifierName;
//    }
//
//    /**
//     * @return the status
//     */
//    public String getStatus() {
//        return Status;
//    }
//
//    /**
//     * @param status the status to set
//     */
//    public void setStatus(String status) {
//        Status = status;
//    }
//
//    /**
//     * @return the selectedService
//     */
//    public String getSelectedService() {
//        return selectedService;
//    }
//
//    /**
//     * @param selectedService the selectedService to set
//     */
//    public void setSelectedService(String selectedService) {
//        this.selectedService = selectedService;
//    }
//
//    /**
//     * @return the manager
//     */
//    public MultiSessionManager getManager() {
//        return manager;
//    }
//
//    /**
//     * @param manager the manager to set
//     */
//    public void setManager(MultiSessionManager manager) {
//        this.manager = manager;
//    }
//
//}
//**/