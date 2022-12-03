package com.blogspot.jvalentino.usnrauto.main.controller

import java.util.Enumeration;
import java.awt.CardLayout;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.commons.io.FileUtils;

import com.blogspot.jvalentino.usnrauto.component.DownloadTreeNode;
import com.blogspot.jvalentino.usnrauto.component.HtmlPanel;
import com.blogspot.jvalentino.usnrauto.component.HtmlTreeNode;
import com.blogspot.jvalentino.usnrauto.component.NavTreeNode;
import com.blogspot.jvalentino.usnrauto.main.view.MainView;
import com.blogspot.jvalentino.usnrauto.util.CommandLineUtil;

class MainController extends BaseController {

	private MainView view
	
	/** This will cause a memory leak with enough screens, but right now is easier to do that save
	 *  configs and unload jpanels. We just keep all the instances after they are loaded */
	private Map<Class, JPanel> panels = new HashMap<Class, JPanel>();
	
	MainController(MainView view) {
		this.view = view
		
	}
	
	void viewConstructed() {
		MainController me = this
		
		view.getNavTree().addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				me.navTreeSelection()
			}
		});
	
	
	}
	
	private void navTreeSelection() {
		
		DefaultMutableTreeNode node = view.getNavTree().getLastSelectedPathComponent()
				
		if (node instanceof NavTreeNode) {
			
			NavTreeNode navTreeNode = (NavTreeNode) node
			this.loadView(navTreeNode.navClass, node)
						
		} else if (node instanceof HtmlTreeNode) {
		
			HtmlTreeNode htmlNode = (HtmlTreeNode) node
			this.loadView(HtmlPanel.class, node, htmlNode.url)
			
		} else if (node instanceof DownloadTreeNode) {
			
			DownloadTreeNode dNode = (DownloadTreeNode) node
 		
			this.loadView(JPanel.class, node)
			
			this.download(dNode)
			
		}
		
	}
	
	private void download(DownloadTreeNode dNode) {
		
		try {
			
			// prompt the user to download
			File output = this.browseForFileSave(dNode.toString(), "Save File")
			
			if (output != null) {
				
				InputStream is = getClass().getResourceAsStream(dNode.url);
				
				FileUtils.copyInputStreamToFile(is, output)
				
				CommandLineUtil.open(output)
				
			}		
			
		} catch (Exception e) {
			this.showExceptionInDialog(e)
		}
	}
	
	private void loadView(Class clazz, DefaultMutableTreeNode node, String url = null) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// do we already have an instance for this panel?
				JPanel panel = panels.get(clazz)
				
				// if we don't have an instance...
				if (panel == null) {
					// create the instance
					panel = clazz.newInstance()
					// store it
					panels.put(clazz, panel)
					// add it to the card layout
					view.getContentPanel().add(panel, clazz.toString())
				}
				
				// close other paths, but only for the root level nodes
								
				if (node.depth == 1) {
					Enumeration<TreeNode> e = view.getNavTree().getModel().getRoot().children()
					while (e.hasMoreElements()) {
						TreeNode treeNode = (TreeNode) e.nextElement();
						view.getNavTree().collapsePath(new TreePath(treeNode.getPath()))
					}
				}
				
				// Expand to the selection in the tree
				view.getNavTree().expandPath(new TreePath(node.getPath()))
				
				if (url != null) {
					HtmlPanel html = (HtmlPanel) panel
					html.updateUrl(url)
				}
								
				// display the panel
				CardLayout cardLayout = (CardLayout) view.getContentPanel().getLayout();
				cardLayout.show(view.getContentPanel(), clazz.toString());
			
				view.getContentPanel().revalidate()
			}
		});
	}
	
	
}
