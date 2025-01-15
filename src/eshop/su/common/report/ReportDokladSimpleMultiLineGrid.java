package eshop.su.common.report;

import java.util.ArrayList;
import java.util.List;

import netball.server.print.PRComponent;
import netball.server.print.PRDecoratePage;
import netball.server.print.PRFlowPanel;
import netball.server.print.PRLabel;
import netball.server.print.PRMultiLineLabel;
import netball.server.print.PRPage;
import netball.server.print.PRPageFormat;
import netball.server.print.PRReport;
import netball.server.print.PRSimpleMultiLineGrid;
import netframework.mediator.SessionObject;
import netframework.view.ViewCursor;
import netframework.view.ViewRow;

public abstract class ReportDokladSimpleMultiLineGrid {
	
	protected SessionObject session;
    protected Object dokladId;
    
    public PRPage[] execute(SessionObject session, Object dokladId) throws Exception {        
        this.session = session;
        this.dokladId = dokladId;        
        
        ViewRow headerRow = readHeader();
        ViewCursor polozky = readItems();        
        PageBuilder pageBuilder = new PageBuilder(headerRow);
        while (polozky.hasNext()) {
            pageBuilder.addRow(polozky.next(), !polozky.hasNext());
        }         
        pageBuilder.finish();
        return pageBuilder.getPages(); 
    }

    public PRPageFormat getPageFormat() {
    	PRPageFormat format = new PRPageFormat();
    	format.setBottomMargin(15);
    	format.setTopMargin(10);
        return format;
    }        
    
    protected abstract ViewRow readHeader() throws Exception;
    
    protected abstract ViewCursor readItems() throws Exception;

    protected abstract GridBuilder createGridBuilder();
    
    protected abstract PRReport createFirstPage(ViewRow headerRow) throws Exception;

    protected abstract PRMultiLineLabel createDolnyPopisPanel() throws Exception;  // text za poslednou polozkou

    protected abstract PRComponent createFooterPanel(ViewRow headerRow);

    protected abstract PRReport createOtherPage(ViewRow headerRow) throws Exception;

	// interface pre vykreslenie tabulky    
	protected static interface GridBuilder { 
	    public PRSimpleMultiLineGrid.Row createRow(ViewRow row, int poradoveCislo) throws Exception;
	    public PRSimpleMultiLineGrid.Row createSumarnyRiadok(ViewRow headerRow) throws Exception;
	    public PRSimpleMultiLineGrid createGrid();
	    public PRSimpleMultiLineGrid.Header getHeader();
	    public int getHeaderHeight();
	}
	
	private class PageBuilder {
	    
	    private List<PRPage> pages = new ArrayList<PRPage>(2);
	    private PRReport lastPage;
	    private GridBuilder gridBuilder = createGridBuilder();
	    private PRPageFormat pageFormat = getPageFormat();    
	    private int pc;
	    private int pouzitaHeight;     
	    private int vyuzitelnaHeight;
	    private ViewRow headerRow;
	    private PRSimpleMultiLineGrid bodyPanel;
	    private PRMultiLineLabel popis;
	    private PRComponent lastPageFooter;
	    
	    public PageBuilder(ViewRow headerRow) throws Exception {
	        this.headerRow = headerRow;
	        lastPage = createFirstPage(headerRow);
	        popis = createDolnyPopisPanel();
	        lastPageFooter = createFooterPanel(headerRow);
	        pages.add(lastPage);
	        vyuzitelnaHeight = lastPage.camputeBodyHeight(pageFormat.createPageFormat());        
	    }    
	    
	    // row ak je null vytvori sa sumarny riadok
	    public void addRow(ViewRow row, boolean isLastRow) throws Exception {
	        pc ++;
	        PRSimpleMultiLineGrid.Row gridRow = gridBuilder.createRow(row, pc);
	        int rowHeight = gridRow.getRowCount() * gridBuilder.getHeader().getRowHeight();
	        if (pages.size() == 1 && lastPage.getBody() == null) {
	           if ((rowHeight + gridBuilder.getHeaderHeight()) < vyuzitelnaHeight) { 
	              bodyPanel = gridBuilder.createGrid();
	              pouzitaHeight = gridBuilder.getHeaderHeight();
	              lastPage.setBody(bodyPanel);              
	           }else{
	              vyuzitelnaHeight = 0;
	           }
	        }
	        if (isLastRow) { // posledny riadok na novej strane       
	            vyuzitelnaHeight = vyuzitelnaHeight - popis.getSize().height - lastPageFooter.getSize().height - lastPage.getVerticalGap() - gridBuilder.getHeader().getRowHeight();
	        }
	        if (((pouzitaHeight + rowHeight) > vyuzitelnaHeight)) {
	           lastPage = createOtherPage(headerRow);
	           vyuzitelnaHeight = lastPage.camputeBodyHeight(pageFormat.createPageFormat());            
	           bodyPanel = gridBuilder.createGrid();
	           pouzitaHeight = gridBuilder.getHeaderHeight();
	           lastPage.setBody(bodyPanel);
	           pages.add(lastPage);
	        }        
	        bodyPanel.addRow(gridRow);
	        if (isLastRow) {
	           PRSimpleMultiLineGrid.Row sucet = gridBuilder.createSumarnyRiadok(headerRow);            
	           if (sucet != null)
	              bodyPanel.addRow(sucet);
	        }	
	        pouzitaHeight += rowHeight;        
	    }        
	    
	    public void finish() throws Exception {
	    	// popis
	    	if (popis != null && popis.getText() != null && popis.getText().length > 0) {
	    	   int popisHeight = popis.getSize().height; 
	  	       int totalBodyHeight = lastPage.camputeBodyHeight(pageFormat.createPageFormat());        
	    	   int bodyHeight = (lastPage.getBody() != null ? lastPage.getBody().getSize().height : 0);
	    	   if ((totalBodyHeight - bodyHeight) < popisHeight) {
	    	   	  int rowCount = (totalBodyHeight - bodyHeight) / popis.getRowHeight();
	    	   	  PRMultiLineLabel popis1 = popis;    	   	
	    	   	  if (rowCount > 0) {
		       	     PRFlowPanel panel = new PRFlowPanel(javax.swing.SwingConstants.VERTICAL);
		       	     panel.add(lastPage.getBody());    	   	  	
	    	   	     popis1 = popis.divideRow(rowCount);    	   	
	       	         panel.add(popis);
	       	         lastPage.setBody(panel);    	   	     
	       	      }          	      
	              lastPage = createOtherPage(headerRow);
	              pages.add(lastPage);
	              popis = popis1;              
	           }           
	    	   PRFlowPanel panel = new PRFlowPanel(javax.swing.SwingConstants.VERTICAL);
	       	   panel.add(lastPage.getBody());
	       	   panel.add(popis);
	       	   lastPage.setBody(panel);
	    	}    
	    	// footer
	  	    int totalBodyHeight = lastPage.camputeBodyHeight(pageFormat.createPageFormat());        
	    	int bodyHeight = (lastPage.getBody() != null ? lastPage.getBody().getSize().height : 0);
	    	if ((totalBodyHeight - bodyHeight) < lastPageFooter.getSize().height) {
	             lastPage = createOtherPage(headerRow);
	             pages.add(lastPage);    		
	        }
	    	lastPage.setFooter(lastPageFooter); 
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
