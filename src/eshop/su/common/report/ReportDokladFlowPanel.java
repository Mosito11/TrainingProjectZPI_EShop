package eshop.su.common.report;

import java.util.ArrayList;
import java.util.List;

import netball.server.print.PRComponent;
import netball.server.print.PRDecoratePage;
import netball.server.print.PRFlowPanel;
import netball.server.print.PRLabel;
import netball.server.print.PRPage;
import netball.server.print.PRPageFormat;
import netball.server.print.PRReport;
import netframework.mediator.SessionObject;
import netframework.view.ViewCursor;
import netframework.view.ViewRow;

public abstract class ReportDokladFlowPanel {
	
	protected SessionObject session;
    protected Object dokladId;    
    
    public PRPage[] execute(SessionObject session, Object dokladId) throws Exception {        
    	this.session = session;
    	this.dokladId = dokladId;
        return execute();
    }
    
    protected PRPage[] execute() throws Exception {
        ViewRow headerRow = readHeader();
        ViewCursor polozky = readItems();        
        PageBuilder pageBuilder = new PageBuilder(headerRow);
        while (polozky.hasNext()) {
            pageBuilder.addRow(polozky.next(), headerRow, !polozky.hasNext());
        } 
        return pageBuilder.getPages(); 
    } 
    
    public PRPageFormat getPageFormat() {
        return new PRPageFormat();
    }        

    protected abstract ViewRow readHeader() throws Exception;
    
    protected abstract ViewCursor readItems() throws Exception;

    protected abstract ItemBuilder createItemBuilder();

    protected abstract PRReport createPage(ViewRow headerRow, int pageIndex) throws Exception;

    protected interface ItemBuilder {	
		public abstract int getHeaderHeight();	
		public abstract PRComponent getHeader();	
	    public abstract PRComponent createRow(ViewRow row, ViewRow headerRow, boolean isLastRow, int pc) throws Exception;
	}
	
	private class PageBuilder {
	    
	    private List<PRPage> pages = new ArrayList<PRPage>(2);
	    private PRReport lastPage;
	    private ItemBuilder itemBuilder = createItemBuilder();
	    private PRPageFormat pageFormat = getPageFormat();    
	    private int pc;
	    private int pouzitaHeight;     
	    private int vyuzitelnaHeight;
	    private PRFlowPanel bodyPanel;
	    
	    public PageBuilder(ViewRow headerRow) throws Exception {
	        lastPage = createPage(headerRow, 0);
	        pages.add(lastPage);
	        vyuzitelnaHeight = lastPage.camputeBodyHeight(pageFormat.createPageFormat());        
	    }
	    
	    public void addRow(ViewRow row, ViewRow headerRow, boolean isLastRow) throws Exception {
	        pc ++;
	        PRComponent gridRow = itemBuilder.createRow(row, headerRow, isLastRow, pc);
	        int rowHeight = gridRow.getSize().height;
	        if (pages.size() == 1 && lastPage.getBody() == null) {
	           if ((rowHeight + itemBuilder.getHeaderHeight()) < vyuzitelnaHeight) { 
	              bodyPanel = new PRFlowPanel(javax.swing.SwingConstants.VERTICAL);
	              bodyPanel.add(itemBuilder.getHeader());
	              pouzitaHeight = itemBuilder.getHeaderHeight();
	              lastPage.setBody(bodyPanel);              
	           }else{
	              vyuzitelnaHeight = 0;
	           }
	        }
	        if ((pouzitaHeight + rowHeight) > vyuzitelnaHeight) {
	           lastPage = createPage(headerRow, 1);
	           if (pages.size() == 1)
	              vyuzitelnaHeight = lastPage.camputeBodyHeight(pageFormat.createPageFormat());            
	           bodyPanel = new PRFlowPanel(javax.swing.SwingConstants.VERTICAL);
	           bodyPanel.add(itemBuilder.getHeader());
	           pouzitaHeight = itemBuilder.getHeaderHeight();
	           lastPage.setBody(bodyPanel);
	           pages.add(lastPage);
	        }
	        bodyPanel.add(gridRow);
	        pouzitaHeight += rowHeight;        
	    }
	    
	    public PRPage[] getPages() {     
	        PRDecoratePage reports[] = new PRDecoratePage[pages.size()];
	        for (int i = 0; i < pages.size(); i++) {
	           reports[i] = new PRDecoratePage((PRPage) pages.get(i));
	           reports[i].setRightTopCorner(new PRLabel(session.translateText("Strana ") + (i + 1) + " / " + pages.size() + "  "));
	        }       
	        return reports;        
	     }    
	}
}
