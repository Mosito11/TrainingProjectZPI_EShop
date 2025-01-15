package eshop.su.common.report;

import netball.server.component.tree.TreeContainer;
import netball.server.component.tree.TreeNode;
import netframework.mediator.MDReportTree;

public class ReportTree extends MDReportTree {

	@Override
	protected TreeContainer createTreeContainer() {
		TreeNode root = createNode("tlacoveZostavy", "Tlacove Zostavy", null);
		root.add(createObjednavkaNode());
		return new TreeContainer(root);
	}
	
	protected TreeNode createObjednavkaNode() {
    	TreeNode root = createNode("objednavky", "Objednavky", null);
    	root.add(createLeaf(new ReportObjednavkaPolozka()));
        root.add(createLeaf(new ReportObjednavka()));
        root.add(createLeaf(new ReportObjednavkaMySource()));
        root.add(createLeaf(new ReportObjednavkaMySumator()));
        root.add(createLeaf(new ReportObjednavkaMyCursor()));
        root.add(createLeaf(new ReportObjednavkaTovarMySumator()));
        return root;        
    }


}
