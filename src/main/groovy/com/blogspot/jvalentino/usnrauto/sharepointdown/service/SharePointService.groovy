package com.blogspot.jvalentino.usnrauto.sharepointdown.service

import java.util.List;

import javax.net.ssl.SSLContext;

import org.apache.commons.io.IOUtils;

import com.blogspot.jvalentino.cac.CacUtil;
import com.blogspot.jvalentino.cac.security.CacIdentity;
import com.blogspot.jvalentino.cac.security.CacIdentityType;
import com.blogspot.jvalentino.cac.security.SSLContextHttpWebConnection;
import com.blogspot.jvalentino.usnrauto.component.JProgressDialog;
import com.blogspot.jvalentino.usnrauto.component.progress.ProgressAreaDialog;
import com.blogspot.jvalentino.usnrauto.sharepointdown.data.Download;
import com.gargoylesoftware.htmlunit.BinaryPage;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlImageInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

class SharePointService {

	private ProgressAreaDialog dialog
	
	String isLocationValid(String url, File directory) {
		if (!url.startsWith("https://private.navyreserve.navy.mil")) {
			return "The URL must start with https://private.navyreserve.navy.mil"
		}
		
		if (url.equals("https://private.navyreserve.navy.mil/...")) {
			return "You must enter a valid SharePoint location, try the URL to documents on your unit SharePoint"
		}
		
		if (url.equals("https://private.navyreserve.navy.mil") ||
			url.equals("https://private.navyreserve.navy.mil/")) {
			return "You can't enter the root of the Navy SharePoint, try the URL to documents on your unit SharePoint"
		}
			
		return null
		
	}
	
	void download(List<CacIdentity> identities, List<Download> downloads, ProgressAreaDialog dialog) throws Exception {
		this.dialog = dialog
		
		dialog.appendLine("Obtaining email identity from CAC")
		CacIdentity identity = CacUtil.selectFirstIdentity(CacIdentityType.EMAIL, identities)
		
		if (identity == null) {
			throw new Exception("An email certificate could not be found on the CAC")
		}
		
		dialog.appendLine("Establishing an SSL context to communicate over HTTPS using the certificate")
		SSLContext context = CacUtil.createSSLContext(identity)
		
		for (Download item : downloads) {
			try {
				this.download(item, context)
			} catch (Exception e) {
				e.printStackTrace()
				dialog.appendLine("# ERROR - Unable to download " + download.url + " - " + e.getMessage())
			}
		}
		
	}
	
	protected void download(Download download, SSLContext context) {
		
		dialog.appendLine("Creating an HTML Unit Web Client emulating Firefox 3.6 for " + download.url)
		WebClient webClient = new WebClient(BrowserVersion.FIREFOX_3_6)
		SSLContextHttpWebConnection conn = new SSLContextHttpWebConnection(webClient, context)
		webClient.setWebConnection(conn)
		
		try {
			dialog.appendLine("Moving through the SharePoint Accept/Decline Page")
			HtmlPage page = this.navigateToIntendedPage(webClient, download.url)			
			this.downloadSharePointFiles(page, download.directory)
		
		} catch (Exception e) {
			e.printStackTrace()
			dialog.appendLine(e.getMessage())
		} finally {
			webClient.closeAllWindows();
		}
		
	}
	
	protected Page navigateToIntendedPage(WebClient webClient, String url) {
		// You get redirected to the "Accept" or "Decline" page
		HtmlPage page = webClient.getPage(url)
		
		//Get the form
		HtmlForm form = page.getFormByName("ctl00")
		
		// Find the Accept button
		HtmlImageInput button = form.getInputByName("btnAccept")
		
		// click it
		HtmlPage page2 = button.click()
		
		return page2
		
	}
	
	protected List<HtmlAnchor> getFoldersAndLinks(HtmlPage page) {
		// the table with the summary attribute is the content
		String xpath = "//table[@summary]"
		// the column is the td with the class of ms-vb-title
		xpath += "/descendant::td[contains(@class, 'ms-vb-title')]"
		// the link in the column must not contain an href of javascript
		// those javascript links are for the popup menus
		xpath += "/descendant::a[not(contains(@href, 'javascript'))]"
		
		List<HtmlAnchor> divs = page.getByXPath(xpath)
		
		return divs
	}
	
	protected boolean isSharePointFolder(HtmlAnchor thing) {
		return thing.getAttribute("onclick").contains("HandleFolder")
	}
	
	protected HtmlPage downloadFile(HtmlPage page, HtmlAnchor thing, String path) {
		
		File dir = new File(path)
		dir.mkdirs()
		
		String href = thing.getHrefAttribute()
		String fileName = new File(href).getName()
		
		File file = new File(path + "/" + fileName)
				
		dialog.appendLine(href)
				
		Page downoad = thing.click()
		BinaryPage bp = new BinaryPage(downoad.getWebResponse(),downoad.getEnclosingWindow());
		InputStream input = bp.getInputStream();
		
		OutputStream out = new FileOutputStream(file);
		IOUtils.copy(input,out);
		input.close();
		out.close();
		
		return page.refresh()
	}
	
	protected void downloadSharePointFiles(HtmlPage page, String path) {
		dialog.appendLine("Iterating over all files and folders for " + path)
		// Get all the folders and files
		List<HtmlAnchor> divs = this.getFoldersAndLinks(page)
		
		// for each file...
		for (HtmlAnchor thing : divs) {
			
			if (!this.isSharePointFolder(thing)) {
				try {
					page = this.downloadFile(page, thing, path)
				} catch (Exception e) {
					e.printStackTrace()
					dialog.appendLine("# ERROR - Unable to download file " + thing.getHrefAttribute() + 
						" - " + e.getMessage())
				} 
				// TODO: If auth exception, page = re-=authenticate, start downloadSharePointFiles at path
				// all over again
				
			}
			
		}
		
		// for each folder...
		for (HtmlAnchor thing : divs) {
			
			if (this.isSharePointFolder(thing)) {
				String newPath = path + "/" + thing.getTextContent()
				
				try {
					HtmlPage newPage = thing.click()
					this.downloadSharePointFiles(newPage, newPath)
				} catch (Exception e) {
					e.printStackTrace()
					dialog.appendLine("# ERROR - Unable to go to page " + newPath + 
						" - " + e.getMessage())
				} 
			}
			
		}
	}
}
