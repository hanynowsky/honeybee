package org.otika.honeybee.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.codec.digest.DigestUtils;
import org.otika.honeybee.model.Author;
import org.otika.honeybee.model.Bodypart;
import org.otika.honeybee.model.Defect;
import org.otika.honeybee.model.Honey;
import org.otika.honeybee.model.Ingredient;
import org.otika.honeybee.model.Pcategory;
import org.otika.honeybee.model.Plant;
import org.otika.honeybee.model.Prescription;
import org.otika.honeybee.model.Ptype;
import org.otika.honeybee.model.Substance;
import org.otika.honeybee.model.Virtue;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.chart.MeterGaugeChartModel;

/**
 * <p>
 * This bean must be request scoped - Otherwise you would get an EL illegal
 * state when using an El expression in a java script function and changing
 * locale
 * </p>
 *
 * @author Hanine.H <hanynowsky@gmail.com>
 *
 */
@Model
public class UtilityBean implements Serializable {

    private static final long serialVersionUID = -5489916236604357383L;
    static boolean APP_DEBUG = false;
    private boolean shown = false;
    private boolean visible = true;
    private ExternalContext externalContext;
    private FacesContext facesContext;
    private StreamedContent image;
    @EJB
    private Repository repository;
    private String viewPath;
    private Long storeId;
    private BundleBean bundle;
    static ProcessBuilder pb;
    private String unhashedPassword;

    private MeterGaugeChartModel gaugeMeterModel;

    /**
     * Constructor
     */
    public UtilityBean() {
    }

    @PostConstruct
    public void init() {
        bundle = new BundleBean();
        facesContext = FacesContext.getCurrentInstance();
        gaugeMeterModel = new MeterGaugeChartModel();
        gaugeMeterModel.addInterval(5);
        gaugeMeterModel.addInterval(16);
        gaugeMeterModel.addInterval(18.5);
        gaugeMeterModel.addInterval(20);
        gaugeMeterModel.addInterval(25);
        gaugeMeterModel.addInterval(27.5);
        gaugeMeterModel.addInterval(30);
        gaugeMeterModel.addInterval(35);
        gaugeMeterModel.addInterval(40);
        gaugeMeterModel.addInterval(50);
        gaugeMeterModel.setValue(0);
    }

    /**
     * Faces Action Method
     *
     * @param event Action event
     */
    public void dummyMethod(ActionEvent event) {
        System.out.println(event.getComponent().getFamily());
    }

    /**
     * Method to fake Command Button
     *
     * @return null
     */
    public String emptyMethod() {
        return null;
    }

    /**
     * Method that redirects to a given referer view path
     *
     * @param url
     */
    public void redirectToUrl(String url) {
        try {
            ExternalContext context = FacesContext.getCurrentInstance()
                    .getExternalContext();
            if (!url.isEmpty() || url.equalsIgnoreCase("referer")) {
                context.redirect(url);
            } else {
                context.redirect(context.getRequestContextPath() + "/index");
            }
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).severe(ex.getMessage());
        }
    }

    /**
     * Method to convert an array of bytes to a file
     *
     * @param blob
     * @return DefaultStreamedContent
     */
    public StreamedContent convertBytesToFile(byte[] blob) {

        try {
            InputStream is = new ByteArrayInputStream(blob);
            StreamedContent graphic = new DefaultStreamedContent(is,
                    "image/png");
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(graphic.getContentType()));
            return graphic;
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Image retrieval problem ", null));
            Logger.getLogger(UtilityBean.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
        return null;
    } // END OF METHOD

    /**
     * Utility method to show a JSF message
     *
     * @param severity
     * <p>
     * Faces message severity (e.g. WARN, INFO, ERROR, FATAL)
     * </p>
     * @param summary
     * <p>
     * Message summary
     * </p>
     * @param detail
     * <p>
     * Message detail
     * </p>
     */
    public void showMessage(String severity, String summary, String detail) {
        FacesMessage.Severity SEV;
        FacesMessage.Severity INFO = FacesMessage.SEVERITY_INFO;
        FacesMessage.Severity WARN = FacesMessage.SEVERITY_WARN;
        FacesMessage.Severity FATAL = FacesMessage.SEVERITY_FATAL;
        FacesMessage.Severity ERROR = FacesMessage.SEVERITY_ERROR;
        if (severity.equalsIgnoreCase("info")) {
            SEV = INFO;
        } else if (severity.equalsIgnoreCase("warn")) {
            SEV = WARN;
        } else if (severity.equalsIgnoreCase("error")) {
            SEV = ERROR;
        } else if (severity.equalsIgnoreCase("fatal")) {
            SEV = FATAL;
        } else {
            SEV = INFO;
        }
        FacesMessage message = new FacesMessage(SEV, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    // GETTERS & SETTERS
    public StreamedContent getImage() {
        return image;
    }

    public void setImage(StreamedContent image) {
        this.image = image;
    }

    /**
     * @return the externalContext
     */
    public ExternalContext getExternalContext() {
        return externalContext;
    }

    /**
     * @return the facesContext
     */
    public FacesContext getFacesContext() {
        return facesContext;
    }

    public String getViewName() {
        String id = "/index";
        try {
            id = FacesContext.getCurrentInstance().getViewRoot().getViewId();
        } catch (Exception ex) {
            String error = "NULL view name: " + ex.getMessage() + " "
                    + ex.getCause().getMessage();
            Logger.getLogger(getClass().getName()).severe(error);
        }
        return id;
    }

    public String getViewPath() {
        return viewPath;
    }

    public void setViewPath(String viewPath) {
        this.viewPath = viewPath;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    /*
     * Utilities
     */
    /**
     * Method that fetches an Object from database by ID
     *
     * @param <T>
     * @param id id_as_Entity_ID
     * @param type Object_o_is_any_object_of_the_same_type_as_T
     * @return Type (Type)
     */
    public <T> T getRepoObject(Long id, Object type) {
        return repository.findById((long) id, type);
    }

    /**
     * Returns All entity items
     *
     * @param <T>
     * @param type e_Any_instance_of_T
     * @return Type (Type)
     */
    public <T> List<T> getEntityItems(Object type) {
        return repository.findAll(type);
    } // END OF METHOD

    /**
     *
     * @param input
     * @return localized_name
     */
    public String IngformName(String input) {
        String name;
        if (!input.equals("")) {
            name = bundle.i18n(input);
            return name;
        }
        return null;
    }

    /**
     * Executes a bash command - Applicable only in Linux
     *
     * @param command
     * @return Bash_STDOUT_STDERR
     */
    public String execBash(String command) {
        try {
            if (System.getProperty("os.name").contains("inux")) {
                java.util.List<String> commands = new ArrayList<>();
                commands.add("bash"); // or /bin/cat
                commands.add("-c");
                // commands.add("echo Han | grep [^*]");
                // commands.add("notify-send Bye $USER -i face-laugh -t 600 -u low -a honeybee");
                commands.add(command);
                System.out.println("Executing: " + commands);

                // Run macro on target
                pb = new ProcessBuilder(commands);
                pb.directory(new File(System.getProperty("user.home")));
                pb.redirectErrorStream(true);
                Process process = pb.start();

                // Read output
                StringBuilder sbout = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        process.getInputStream()));
                String line, previous = null;
                while ((line = br.readLine()) != null) {
                    if (!line.equals(previous)) {
                        previous = line;
                        sbout.append(line).append('\n');
                        System.out.println(line);
                    }
                }
                // Check result
                if (process.waitFor() == 0) { // Normal value is 0
                    System.out.println("Success!");
                    System.out.println("Here is the Bash-return: "
                            + sbout.toString());
                    return sbout.toString();
                } else {
                    System.err.println(commands);
                    System.err.println(sbout.toString());
                    System.exit(1);
                    return sbout.toString();
                }
                // System.exit(0);
            }
        } catch (IOException | InterruptedException ex) {
            System.out.println("Bash command exception!: " + ex);
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    } // END OF METHOD

    /**
     * Returns the object label by user language
     *
     * @param o Object_o
     * @return DB_localized label
     */
    public String switchLabel(Object o) {
        String lang = FacesContext.getCurrentInstance().getViewRoot()
                .getLocale().getLanguage();

        /* Ingredient */
        if (o != null && o instanceof Ingredient) {
            switch (lang) {
                case "ar":
                    return ((Ingredient) o).getLabelar();
                case "fr":
                    return ((Ingredient) o).getLabelfr();
                default:
                    return ((Ingredient) o).getLabel();
            }
        }

        // Plant
        if (o != null && o instanceof Plant) {
            switch (lang) {
                case "ar":
                    return ((Plant) o).getLabelar();
                case "fr":
                    return ((Plant) o).getLabelfr();
                default:
                    return ((Plant) o).getLabel();
            }
        }

        // Honey
        if (o != null && o instanceof Honey) {
            switch (lang) {
                case "ar":
                    return ((Honey) o).getLabelar();
                case "fr":
                    return ((Honey) o).getLabelfr();
                default:
                    return ((Honey) o).getLabel();
            }
        }

        // Substance
        if (o != null && o instanceof Substance) {
            switch (lang) {
                case "ar":
                    return ((Substance) o).getLabelar();
                case "fr":
                    return ((Substance) o).getLabelfr();
                default:
                    return ((Substance) o).getLabel();
            }
        }

        // Body part
        if (o != null && o instanceof Bodypart) {
            switch (lang) {
                case "ar":
                    return ((Bodypart) o).getLabelar();
                case "fr":
                    return ((Bodypart) o).getLabelfr();
                default:
                    return ((Bodypart) o).getLabel();
            }
        }

        // Virtue
        if (o != null && o instanceof Virtue) {
            switch (lang) {
                case "ar":
                    return ((Virtue) o).getLabelar();
                case "fr":
                    return ((Virtue) o).getLabelfr();
                default:
                    return ((Virtue) o).getLabel();
            }
        }

        // Defect
        if (o != null && o instanceof Defect) {
            switch (lang) {
                case "ar":
                    return ((Defect) o).getLabelar();
                case "fr":
                    return ((Defect) o).getLabelfr();
                default:
                    return ((Defect) o).getLabel();
            }
        }

        // Plant type
        if (o != null && o instanceof Ptype) {
            switch (lang) {
                case "ar":
                    return ((Ptype) o).getLabelar();
                case "fr":
                    return ((Ptype) o).getLabelfr();
                default:
                    return ((Ptype) o).getLabel();
            }
        }

        // Plant Category
        if (o != null && o instanceof Pcategory) {
            switch (lang) {
                case "ar":
                    return ((Pcategory) o).getLabelar();
                case "fr":
                    return ((Pcategory) o).getLabelfr();
                default:
                    return ((Pcategory) o).getLabel();
            }
        }

        // Prescription
        if (o != null && o instanceof Prescription) {
            switch (lang) {
                case "ar":
                    return ((Prescription) o).getTitlear();
                case "fr":
                    return ((Prescription) o).getTitlefr();
                default:
                    return ((Prescription) o).getTitle();
            }
        }

        // Author
        if (o != null && o instanceof Author) {
            switch (lang) {
                case "ar":
                    return ((Author) o).getNamear() + " "
                            + ((Author) o).getSurnamear();
                case "fr":
                    return ((Author) o).getName() + " " + ((Author) o).getSurname();
                default:
                    return ((Author) o).getName() + " " + ((Author) o).getSurname();
            }
        }

        return null;
    }

    /**
     * Faces HTML Direction
     *
     * @return html_direction
     */
    public String getHtmlDirection() {
        try {
            String language = FacesContext.getCurrentInstance().getViewRoot()
                    .getLocale().getLanguage();
            if (language.equalsIgnoreCase("ar")) {
                return "rtl";
            } else {
                return "ltr";
            }
        } catch (Exception ex) {
            System.out.println("Exception in HTML Direction " + ex);
            Logger.getLogger(getClass().getName())
                    .log(Level.SEVERE,
                            "Exception in getting HTML direction: {0}",
                            ex.getMessage());
            return "ltr";
        }
    }

    /**
     * Method that hashes a string value with SHA-256 algorithm
     *
     * @param value Hashed value
     * @return
     */
    public String hash(String value) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = messageDigest.digest(value.getBytes("UTF-8"));
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < hash.length; i++) {
                stringBuilder.append(Integer.toString((hash[i] & 0xff) + 0x100,
                        16).substring(1));
            }
            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(UtilityBean.class.getName()).severe(
                    ex.getMessage());
            return "";
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
            String msg = "SHAConverter.getAsObject: "
                    + unsupportedEncodingException.getMessage();
            Logger.getLogger(UtilityBean.class.getName()).severe(msg);
            return "";
        }
    }

    /**
     * Method to hash a string to SHA-256 Hexadecimal
     *
     * @param value String to be hashed
     * @return hashed Hashed password
     */
    public String hashHex(String value) {
        try {
            String hashed = DigestUtils.sha256Hex(value.trim());
            System.out.println("Hex hashed password : " + hashed + " for: "
                    + value.trim());
            return hashed;
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).severe(ex.getMessage());
            return null;
        }
    }

    /**
     * Method that should return honeybee------.sql
     *
     * @param somepath
     * @return file DB dump file
     */
    public File dumpFile(String somepath) {
        try {
            File file = new File(somepath);
            if (!file.exists()) {
                file.createNewFile();
            }
            zipFile(file);
            return new File(file.getPath() + ".zip");
        } catch (IOException ex) {
            System.out.println("dumpFile: Failure retrieving file");
            Logger.getLogger(getClass().getName()).severe(
                    ex.getCause().getMessage());
            return null;
        }
    }

    /**
     * Method that splits the browser referer url. Either it is a localhost url
     * or a web url
     *
     * @param string browser url string
     * @return referer Formatted Referer url as view name
     */
    public String cutRefererString(String string) {
        String cut;
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

    /**
     * Checks whether there is Internet connectivity
     *
     * @return false if no Internet
     */
    public boolean hasInternet() {
        try {
            URL url = new URL("http://www.google.com");
            InputStream is = url.openStream();
            if (is != null) {
                return true;
            }
        } catch (IOException ex) {
            String message = "No Internet Connection! " + ex.getMessage();
            org.jboss.logging.Logger.getLogger(getClass().getName()).debug(
                    message);
            return false;
        }
        return false;
    }

    /**
     * Encode mail password
     *
     * @return
     */
    public static String encodePassword() {
        String pass = "";
        // TODO use some algorithm to encode mail password
        return pass;
    }

    /**
     * Compress a file.
     * <p>
     * Used to compress an SQL file
     * </p>
     *
     * @param file
     */
    public void zipFile(File file) {
        try {
            System.out.println("Attempt to zip a file: " + file.getPath());
            FileOutputStream fos = new FileOutputStream(file.getPath() + ".zip");
            ZipOutputStream zos = new ZipOutputStream(fos);
            ZipEntry ze = new ZipEntry(file.getName());
            zos.putNextEntry(ze);
            FileInputStream in = new FileInputStream(file.getPath());
            byte[] buffer = new byte[10000024];
            int len;
            System.out.println("Writing to Zip output stream");
            while ((len = in.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }
            System.out.println("buffer written!");
            in.close();
            System.out.println("input stream closed");
            zos.closeEntry();
            System.out.println("zos close entry achieved!");
            zos.close();
            System.out.println("zos closed");
            System.out.println("Zipped! " + file.getPath());
            org.jboss.logging.Logger.getLogger(getClass().getName()).info(
                    "Zipped! " + file.getPath());

        } catch (IOException e) {
            System.err.println("Zipping file failed: " + e.getMessage());
            String msg = e.getMessage() + "" + e.getCause().getMessage();
            org.jboss.logging.Logger.getLogger(getClass().getName()).error(
                    "\n" + msg);
        }
    }

    /**
     * Capitalize a String
     *
     * @param value to capitalize
     * @return capitalized String
     */
    public String capitalize(String value) {
        try {
            String result;
            result = String.valueOf(value.substring(0, 1)).toUpperCase()
                    + value.substring(1);
            return result;
        } catch (Exception ex) {
            org.jboss.logging.Logger.getLogger(getClass().getName()).error(
                    "\n" + ex.getMessage());
        }
        return null;
    }

   /**
    * <p>
     * Trim a string and return its lowercase value</p>
    * @param value
    * @return trimmedValue
    */
    public String trimToLowerName(String value) {
        String trimmedValue;
        if (value != null) {
            trimmedValue = value.trim().replace(" ", "").toLowerCase();
            return trimmedValue;
        }
        return null;
    }

    /**
     * Checks if View name contains a string value
     *
     * @param value
     * @return
     */
    public boolean ViewNamecontains(String value) {
        return getViewName().contains(value);
    }

    /**
     * Faces redirect to Faces URL
     *
     * @param path
     */
    public void redirectToFacesURL(String path) {

        ExternalContext eContext = FacesContext.getCurrentInstance()
                .getExternalContext();
        try {
            eContext.redirect(eContext.getRequestContextPath()
                    + path);
        } catch (IOException e) {
            Logger.getLogger(getClass().getName()).log(
                    Level.SEVERE, "IO Exception while handling Faces redirection{0}", e.getMessage());
        }
    }

    /*
     * Setters & Getters
     */
    public String getUnhashedPassword() {
        return unhashedPassword;
    }

    public void setUnhashedPassword(String unhashedPassword) {
        this.unhashedPassword = unhashedPassword;
    }

    /**
     * @return the gaugeMeterModel
     */
    public MeterGaugeChartModel getGaugeMeterModel() {
        return gaugeMeterModel;
    }

    /**
     * @param gaugeMeterModel the gaugeMeterModel to set
     */
    public void setGaugeMeterModel(MeterGaugeChartModel gaugeMeterModel) {
        this.gaugeMeterModel = gaugeMeterModel;
    }

    /**
     * @return the shown
     */
    public boolean isShown() {
        return shown;
    }

    /**
     * @param shown the shown to set
     */
    public void setShown(boolean shown) {
        this.shown = shown;
    }

    /**
     * @return the visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * @param visible the visible to set
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

} // END OF CLASS
