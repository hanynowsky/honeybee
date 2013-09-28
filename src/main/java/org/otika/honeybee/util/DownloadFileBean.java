package org.otika.honeybee.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.inject.Model;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

/**
 * This Class allows the user to download a file we specify
 * 
 * @version 1.0
 * @author hanine H.A.M <hanynowsky@gmail.com>
 */
@Model
public class DownloadFileBean implements Serializable {

	private static final long serialVersionUID = -5282123378112726649L;
	// Constants
	// ----------------------------------------------------------------------------------
	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
	File file = new File(getFilePath());

	// Actions
	// ------------------------------------------------------------------------------------

	/**
	 * This medthod can be used to download any type of files as long as we
	 * specify its mime type
	 */
	@Audit
	public void downloadFile(File afile) {

		// Prepare.
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		HttpServletResponse response = (HttpServletResponse) externalContext
				.getResponse();

		BufferedInputStream input = null;
		BufferedOutputStream output = null;

		try {
			// Open file.
			file = afile;
			input = new BufferedInputStream(new FileInputStream(file),
					DEFAULT_BUFFER_SIZE);

			// Init servlet response.
			response.reset();
			response.setHeader("Content-Type", "text/html");
			response.setHeader("Content-Length", String.valueOf(file.length()));
			// response.setHeader("Content-Disposition", "inline; filename=\"" +
			// getFileName() + "\"");
			String headattach = "attachment" + "; " + "filename="
					+ getFileName();
			response.setHeader("Content-Disposition", headattach);
			output = new BufferedOutputStream(response.getOutputStream(),
					DEFAULT_BUFFER_SIZE);

			// Write file contents to response.
			byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			int length;
			while ((length = input.read(buffer)) > 0) {
				output.write(buffer, 0, length);
			}

			// Finalize task.
			output.flush();
		} catch (Exception ex) {
			Logger.getLogger(DownloadFileBean.class.getName()).log(Level.ALL,
					ex.getMessage(), ex);
		} finally {
			// Gently close streams.
			close(output);
			close(input);
		}

		// Inform JSF that it doesn't need to handle response.
		// This is very important, otherwise you will get the following
		// exception in the logs:
		// java.lang.IllegalStateException: Cannot forward after response has
		// been committed.
		
		facesContext.responseComplete(); // TODO this might prevent Faces
											// Messages from showing
	}

	// Helpers (can be refactored to public utility class)
	// ----------------------------------------
	private static void close(Closeable resource) {
		if (resource != null) {
			try {
				resource.close();
			} catch (IOException ex) {
				// this will generally only be thrown when the client
				// aborted the download.
				System.out.println(ex);
				System.out.println(ex.getMessage());
				Logger.getLogger(DownloadFileBean.class.getName()).log(
						Level.ALL, ex.getMessage(), ex);
			}
		}
	}

	/**
	 * 
	 * @return name of the file
	 */
	private String getFileName() {

		return file.getName();
	}

	/**
	 * 
	 * @return the file path to the specified file Notice we used System
	 *         property method to get the user user directory in abstraction of
	 *         whether the OS used is Windows, Unix or Mac OS.
	 */
	private String getFilePath() {
		// return System.getProperty("user.dir") + "/css.css";
		return System.getProperty("user.home") + File.separator + "honeybee"
				+ File.separator + "export" + File.separator + "css.css";
	}
}
