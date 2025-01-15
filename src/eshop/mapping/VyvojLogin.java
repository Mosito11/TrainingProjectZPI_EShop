package eshop.mapping;

import netframework.eclipselink.EclipseLinkLogin;

import org.eclipse.persistence.zpi.platform.database.InformixPlatform;
import org.eclipse.persistence.zpi.sequencing.TableSequence;
import org.eclipse.persistence.zpi.sessions.DatabaseLogin;

public class VyvojLogin implements EclipseLinkLogin {
    
	@Override
	public DatabaseLogin getLogin(String sourceName) {
	    DatabaseLogin login = new DatabaseLogin();
	    login.usePlatform(new InformixPlatform());
	    login.setDriverClass(com.informix.jdbc.IfxDriver.class);
	    
	    //menim iba user a password
	    login.setConnectionString("jdbc:informix-sqli://vyvoj:1525/db_hracia:INFORMIXSERVER=ds0_vyvoj");
	    login.setUserName("Dvorsky");
	    login.setPassword("michal274");        
	   
	  /*  
	    login.setConnectionString("jdbc:informix-sqli://192.168.4.45:1525/dbzvyr:INFORMIXSERVER=ds0_vyvoj");
	    login.setUserName("vyrtst");
	    login.setPassword("xinu");        
	   */ 
        login.setProperty("DBDATE","DMY4.");
        login.setProperty("DBMONEY",".");
        login.setProperty("DB_LOCALE","sk_sk.912");
        login.setProperty("CLIENT_LOCALE","sk_sk.1250");
    	
        //vytvaram a nastavujem sekvencnu tabulku
        TableSequence sequence = new TableSequence();
        sequence.setTableName("MD1_SEQ_TABLE");
        sequence.setNameFieldName("MENO");
        sequence.setCounterFieldName("POCET");
        sequence.setPreallocationSize(10);
        login.setDefaultSequence(sequence);
        
        //zatial nemenim
	    login.setShouldBindAllParameters(false);
	    login.setShouldCacheAllStatements(false);
	    login.setUsesByteArrayBinding(true);
	    login.setUsesStringBinding(false);
	    if (login.shouldUseByteArrayBinding()) { // Can only be used with binding.
		    login.setUsesStreamsForBinding(false);
	    }
	    login.setShouldForceFieldNamesToUpperCase(false);
	    login.setShouldOptimizeDataConversion(true);
	    login.setShouldTrimStrings(true);
	    login.setUsesBatchWriting(false);
	    if (login.shouldUseBatchWriting()) { // Can only be used with batch writing.
		    login.setUsesJDBCBatchWriting(true);
	    }
	    login.setUsesExternalConnectionPooling(false);
	    login.setUsesExternalTransactionController(false);
	    return login;
	}
}    
