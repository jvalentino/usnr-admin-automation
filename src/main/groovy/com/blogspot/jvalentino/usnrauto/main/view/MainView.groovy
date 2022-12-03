package com.blogspot.jvalentino.usnrauto.main.view

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import com.blogspot.jvalentino.usnrauto.cacutil.view.CacUtilView;
import com.blogspot.jvalentino.usnrauto.component.DownloadTreeNode;
import com.blogspot.jvalentino.usnrauto.component.HtmlTreeNode;
import com.blogspot.jvalentino.usnrauto.component.IconTreeRenderer;
import com.blogspot.jvalentino.usnrauto.component.NavTreeNode;
import com.blogspot.jvalentino.usnrauto.main.AppState;
import com.blogspot.jvalentino.usnrauto.main.controller.MainController;
import com.blogspot.jvalentino.usnrauto.reportmerger.view.ReportMergerView;
import com.blogspot.jvalentino.usnrauto.sharepointdown.view.SharePointDownView;
import com.blogspot.jvalentino.usnrauto.sms.view.SMSView;

class MainView extends JFrame {

	private MainController controller
	
	JSplitPane splitPane
	JTree navTree
	JPanel contentPanel
	DefaultMutableTreeNode top = new DefaultMutableTreeNode("Root")
	
	MainView() {
		controller = new MainController(this)
		
		JSplitPane panel = this.constructView()
		this.getContentPane().add(panel)
		
		AppState.getInstance().frame = this
		
		controller.viewConstructed()
	}
	
	private JSplitPane constructView() {
		
		
		contentPanel = new JPanel(new CardLayout())
		JScrollPane pane = new JScrollPane(contentPanel)
		navTree = new JTree(top)
		createTree()
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
			navTree, pane);
		splitPane.setOneTouchExpandable(true)
		splitPane.setDividerLocation(320)
		
		Dimension minimumSize = new Dimension(320, 50)
		navTree.setMinimumSize(minimumSize)		
		
		return splitPane
	}
	
	
	private void createTree() {
		
		top.add(this.createCacUtil())
		
		top.add(this.createSharePointDownloader())
		
		DefaultMutableTreeNode merger = this.createReportMerger()		
		top.add(merger);
		
		DefaultMutableTreeNode sms = this.createSMSTool()
		top.add(sms)
		
		DefaultMutableTreeNode about = this.createAbout()
		top.add(about);
		
		top.add(new HtmlTreeNode("Navy Websites", "/links/index.html")	)
		
		top.add(new DownloadTreeNode(
			"Instruction Manual.docx",
			"/USNR-Automation-Software-Instructions.docx"))
				
		
		navTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION)
		navTree.expandRow(0)
		navTree.setRootVisible(false)
		
		navTree.setCellRenderer(new IconTreeRenderer())
		navTree.setRowHeight(25);
		
		
	}
	
	private DefaultMutableTreeNode createCacUtil() {
		NavTreeNode root = new NavTreeNode("CAC Utility", CacUtilView.class)
		
		root.add(new DownloadTreeNode(
			"CACKey_0.7.0p1_Sltomav.pkg.tar.gz",
			"/cacutil/CACKey_0.7.0p1_Sltomav.pkg.tar.gz"))
		
		return root
	}
	
	private DefaultMutableTreeNode createSharePointDownloader() {
		NavTreeNode root = new NavTreeNode("SharePoint Downloader", SharePointDownView.class)
		
		return root
	}
	
	private DefaultMutableTreeNode createReportMerger() {
		NavTreeNode root = new NavTreeNode("Report Merger", ReportMergerView.class)
		
		HtmlTreeNode what = new HtmlTreeNode("What is this?", "/reportmerger/instructions.html")
		root.add(what)
		
		root.add(new DownloadTreeNode(
			"Example ElearningStatus.xlsx", 
			"/reportmerger/Example-ElearningStatus.xlsx"))
		
		root.add(new DownloadTreeNode(
			"Example GMTCourseCompletionStatus.xlsx",
			"/reportmerger/Example-GMTCourseCompletionStatus.xlsx"))
		
		root.add(new DownloadTreeNode(
			"Example IndivAugTrngStat.xlsx",
			"/reportmerger/Example-IndivAugTrngStat.xlsx"))
		
		root.add(new DownloadTreeNode(
			"Example Manual Unit Inputs.xlsx",
			"/reportmerger/Example-Manual-Unit-Inputs.xlsx"))
		
		root.add(new DownloadTreeNode(
			"Example Individual Medical Readiness.csv",
			"/reportmerger/Example-Nrrm-System-Report-Individual-Medical-Readiness.csv"))
		
		root.add(new DownloadTreeNode(
			"Example SmartRUAD_RUIC.xlsx",
			"/reportmerger/Example-SmartRUAD_RUIC.xlsx"))
		
		root.add(new DownloadTreeNode(
			"Example Individual Member Action Plan.pdf",
			"/reportmerger/Generated-Individual-Member-Action-Plan.pdf"))
		
		root.add(new DownloadTreeNode(
			"Example NROWS Report.txt",
			"/reportmerger/NROWS-MiscSelresReport.txt"))
		
		root.add(new DownloadTreeNode(
			"Example Generated Unit Tracker.xls",
			"/reportmerger/Generated-Unit-Tracker.xls"))
				
		return root
	}
	
	private DefaultMutableTreeNode createSMSTool() {
		NavTreeNode root = new NavTreeNode("Mass Message Sender", SMSView.class)
		
		root.add(new HtmlTreeNode("What is this?", "/sms/index.html"))
		
		return root
	}
	
	private DefaultMutableTreeNode createAbout() {
		HtmlTreeNode root = new HtmlTreeNode("About this Tool", "/about/index.html")	
		
		
		return root
	}
	
}
