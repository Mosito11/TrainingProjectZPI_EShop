package eshop.mapping;

import org.eclipse.persistence.zpi.descriptors.ClassDescriptor;
import org.eclipse.persistence.zpi.mappings.DirectToFieldMapping;
import org.eclipse.persistence.zpi.mappings.ManyToManyMapping;
import org.eclipse.persistence.zpi.mappings.OneToManyMapping;
import org.eclipse.persistence.zpi.mappings.OneToOneMapping;
import org.eclipse.persistence.zpi.sessions.DatabaseLogin;
import org.eclipse.persistence.zpi.sessions.Project;

import eshop.bo.ciselniky.Adresa;
import eshop.bo.ciselniky.CisloDokladu;
import eshop.bo.ciselniky.Dodavatel;
import eshop.bo.ciselniky.Faktura;
import eshop.bo.ciselniky.Kontakt;
import eshop.bo.ciselniky.ObchodnyPartner;
import eshop.bo.ciselniky.Objednavka;
import eshop.bo.ciselniky.ObjednavkaPolozka;
import eshop.bo.ciselniky.Tovar;
import eshop.bo.ciselniky.TovarDruh;
import eshop.bo.ciselniky.TypObchodnehoPartnera;
import eshop.bo.ciselniky.Zakaznik;
import zelpo.eclipselink.autorizacia.Uzivatel;
import zelpo.eclipselink.autorizacia.db.DBUzivatel;
import zelpo.eclipselink.autorizacia.project.AutorizaciaProjekt;

//v mappingu vytvaram projekt, ktory extenduje zpi Project
public class EShopProjekt extends Project {

	//zadavam nazov
	public static final String APPLICATION_NAME = "skolenie";

	//konstruktor nemenim
	public EShopProjekt(DatabaseLogin login) {
		setName(APPLICATION_NAME);
		setLogin(login);
		addDescriptors();
		//setForAllDescriptors();
		new AutorizaciaProjekt(this);
	}
	
	//pridavam descriptory podla tried, s ktorymi pracujem v projekte (nie je tu seq_table ani join table medzi many_to_many ObchodnyPartner Adresa)
	public void addDescriptors() {
		this.addDescriptor(buildTovarDruhDescriptor());
		this.addDescriptor(buildTovarDescriptor());
		this.addDescriptor(buildObchodnyPartnerDescriptor());
		this.addDescriptor(buildDodavatelDescriptor());
		this.addDescriptor(buildZakaznikDescriptor());
		this.addDescriptor(buildKontaktDescriptor());
		this.addDescriptor(buildAdresaDescriptor());
		this.addDescriptor(buildObjednavkaDescriptor());
		this.addDescriptor(buildObjednavkaPolozkaDescriptor());
		this.addDescriptor(buildCisloDokladuDescriptor());
		this.addDescriptor(buildFakturaDescriptor());
	}
	
	//menim Descriptor podla toho, s cim pracujem, triedu, nazov tabulky, primary key
	public ClassDescriptor buildTovarDruhDescriptor(){
		ClassDescriptor descriptor = new ClassDescriptor();
	    descriptor.setJavaClass(TovarDruh.class);
	    descriptor.addTableName("md1_tovar_druh");
	    descriptor.addPrimaryKeyFieldName("ID");
	    
	    // ClassDescriptor properties. sekvencna tabulka properties, davam meno a id, pretoze chcem aby mi vytvarala id v tejto tabulke
	    descriptor.setSequenceNumberFieldName("ID");
	    descriptor.setSequenceNumberName("MD1_TOVAR_DRUH");
	    
	    // Mappings.
	    DirectToFieldMapping idMapping = new DirectToFieldMapping();
	    idMapping.setAttributeName("id");
	    idMapping.setFieldName("ID");
	    descriptor.addMapping(idMapping);
	    
	    DirectToFieldMapping kodMapping = new DirectToFieldMapping();
	    kodMapping.setAttributeName("kod");
	    kodMapping.setFieldName("KOD");
	    descriptor.addMapping(kodMapping);	    
	    
	    DirectToFieldMapping nazovMapping = new DirectToFieldMapping();
	    nazovMapping.setAttributeName("nazov");
	    nazovMapping.setFieldName("NAZOV");
	    descriptor.addMapping(nazovMapping);
	    
	    return descriptor;	    
	}
	
	public ClassDescriptor buildTovarDescriptor(){
		ClassDescriptor descriptor = new ClassDescriptor();
	    descriptor.setJavaClass(Tovar.class);
	    descriptor.addTableName("md1_tovar");
	    descriptor.addPrimaryKeyFieldName("ID");
	    
	    // ClassDescriptor properties. sekvencna tabulka properties, davam meno a id, pretoze chcem aby mi vytvarala id v tejto tabulke
	    descriptor.setSequenceNumberFieldName("ID");
	    descriptor.setSequenceNumberName("MD1_TOVAR");
	    
	    // Mappings.
	    DirectToFieldMapping idMapping = new DirectToFieldMapping();
	    idMapping.setAttributeName("id");
	    idMapping.setFieldName("ID");
	    descriptor.addMapping(idMapping);
	    
	    DirectToFieldMapping kodMapping = new DirectToFieldMapping();
	    kodMapping.setAttributeName("kod");
	    kodMapping.setFieldName("KOD");
	    descriptor.addMapping(kodMapping);	    
	    
	    DirectToFieldMapping nazovMapping = new DirectToFieldMapping();
	    nazovMapping.setAttributeName("nazov");
	    nazovMapping.setFieldName("NAZOV");
	    descriptor.addMapping(nazovMapping);
	    
	    DirectToFieldMapping cenaMapping = new DirectToFieldMapping();
	    cenaMapping.setAttributeName("cena");
	    cenaMapping.setFieldName("CENA");
	    descriptor.addMapping(cenaMapping);
	    
	    DirectToFieldMapping velkostMapping = new DirectToFieldMapping();
	    velkostMapping.setAttributeName("velkost");
	    velkostMapping.setFieldName("VELKOST");
	    descriptor.addMapping(velkostMapping);
	    
	    DirectToFieldMapping skladovaZasobaMapping = new DirectToFieldMapping();
	    skladovaZasobaMapping.setAttributeName("skladovaZasoba");
	    skladovaZasobaMapping.setFieldName("SKL_ZASOBA");
	    descriptor.addMapping(skladovaZasobaMapping);
	   
	    //pretoze tovar ma len jedneho dodavatela
	    OneToOneMapping dodavatelMapping = new OneToOneMapping();
	    dodavatelMapping.setAttributeName("dodavatel");
	    dodavatelMapping.setReferenceClass(Dodavatel.class);
	    dodavatelMapping.useBasicIndirection();
	    dodavatelMapping.addForeignKeyFieldName("OBCH_PAR_ID", "ID");
		descriptor.addMapping(dodavatelMapping);

		//pretoze tovar ma len jeden druh
	    OneToOneMapping tovarDruhMapping = new OneToOneMapping();
	    tovarDruhMapping.setAttributeName("tovarDruh");
	    tovarDruhMapping.setReferenceClass(TovarDruh.class);
	    tovarDruhMapping.useBasicIndirection();
	    tovarDruhMapping.addForeignKeyFieldName("TOVAR_DRUH_ID", "ID");
		descriptor.addMapping(tovarDruhMapping);
	   
		return descriptor;
	}
	
	public ClassDescriptor buildObchodnyPartnerDescriptor(){
		ClassDescriptor descriptor = new ClassDescriptor();
	    descriptor.setJavaClass(ObchodnyPartner.class);
	    descriptor.addTableName("md1_obchodny_partner");
	    descriptor.addPrimaryKeyFieldName("ID");
	    
	    // ClassDescriptor properties. sekvencna tabulka properties, davam meno a id, pretoze chcem aby mi vytvarala id v tejto tabulke
	    descriptor.setSequenceNumberFieldName("ID");
	    descriptor.setSequenceNumberName("MD1_OBCHODNY_PARTNER");
	    
	    // Inheritance properties. nastavim stlpec v tabulke a typy, ktore mozu byt ako hodnoty
	 	descriptor.getInheritancePolicy().setClassIndicatorFieldName("CLASS_ID");
	 	descriptor.getInheritancePolicy().addClassIndicator(Dodavatel.class, TypObchodnehoPartnera.DODAVATEL.getKey());
	 	descriptor.getInheritancePolicy().addClassIndicator(Zakaznik.class, TypObchodnehoPartnera.ZAKAZNIK.getKey());
	    
	    // Mappings.
	    DirectToFieldMapping idMapping = new DirectToFieldMapping();
	    idMapping.setAttributeName("id");
	    idMapping.setFieldName("ID");
	    descriptor.addMapping(idMapping);
	    
	    DirectToFieldMapping kodMapping = new DirectToFieldMapping();
	    kodMapping.setAttributeName("kod");
	    kodMapping.setFieldName("KOD");
	    descriptor.addMapping(kodMapping);	    
	    
	    DirectToFieldMapping nazovMapping = new DirectToFieldMapping();
	    nazovMapping.setAttributeName("nazov");
	    nazovMapping.setFieldName("NAZOV");
	    descriptor.addMapping(nazovMapping);
	    
	    DirectToFieldMapping typMapping = new DirectToFieldMapping();
	    typMapping.setAttributeName(ObchodnyPartner.TYP.getName());
	    typMapping.setFieldName("CLASS_ID");
	    typMapping.setGetMethodName("getTypTL");
	    typMapping.setSetMethodName("setTypTL");
	    descriptor.addMapping(typMapping);
	    	    
	    OneToManyMapping kontaktyMapping = new OneToManyMapping();
	    kontaktyMapping.setAttributeName("kontakty");
	    kontaktyMapping.setReferenceClass(Kontakt.class);
	    //ak som to spravne pochopil, umoznuje zadavanie atributov cez ValueHolder
	    kontaktyMapping.useBasicIndirection();
	    kontaktyMapping.privateOwnedRelationship();
	    kontaktyMapping.addTargetForeignKeyFieldName("OBCH_PAR_ID", "ID");
		descriptor.addMapping(kontaktyMapping);
	    
		// ManyToManyMapping
	 	ManyToManyMapping adresyMapping = new ManyToManyMapping();
	 	adresyMapping.setAttributeName("adresy");
	 	adresyMapping.setReferenceClass(Adresa.class);
	 	adresyMapping.useBasicIndirection();
	 	adresyMapping.setRelationTableName("md1_obchodny_partner_adresa");
	 	adresyMapping.setSourceRelationKeyFieldName("OBCH_PAR_ID");
	 	adresyMapping.setTargetRelationKeyFieldName("ADRESA_ID");
	 	descriptor.addMapping(adresyMapping);
	    
		return descriptor;
	}
	
	public ClassDescriptor buildDodavatelDescriptor() {
		ClassDescriptor descriptor = new ClassDescriptor();
	    descriptor.setJavaClass(Dodavatel.class);

		// Inheritance properties.
		descriptor.getInheritancePolicy().setParentClass(ObchodnyPartner.class);
		
		DirectToFieldMapping icoMapping = new DirectToFieldMapping();
		icoMapping.setAttributeName("ico");
		icoMapping.setFieldName("ICO");
	    descriptor.addMapping(icoMapping);
				
	    return descriptor;
    }	
	
	public ClassDescriptor buildZakaznikDescriptor() {
		ClassDescriptor descriptor = new ClassDescriptor();
	    descriptor.setJavaClass(Zakaznik.class);

		// Inheritance properties.
		descriptor.getInheritancePolicy().setParentClass(ObchodnyPartner.class);
		
	    return descriptor;
    }	
	
	public ClassDescriptor buildKontaktDescriptor(){
		ClassDescriptor descriptor = new ClassDescriptor();
	    descriptor.setJavaClass(Kontakt.class);
	    descriptor.addTableName("md1_kontakt");
	    descriptor.addPrimaryKeyFieldName("ID");
	    
	    // ClassDescriptor properties. sekvencna tabulka properties, davam meno a id, pretoze chcem aby mi vytvarala id v tejto tabulke
	    descriptor.setSequenceNumberFieldName("ID");
	    descriptor.setSequenceNumberName("MD1_KONTAKT");
	    
	    // Mappings.
	    DirectToFieldMapping idMapping = new DirectToFieldMapping();
	    idMapping.setAttributeName("id");
	    idMapping.setFieldName("ID");
	    descriptor.addMapping(idMapping);
	    
	    DirectToFieldMapping menoMapping = new DirectToFieldMapping();
	    menoMapping.setAttributeName("meno");
	    menoMapping.setFieldName("MENO");
	    descriptor.addMapping(menoMapping);	    
	    
	    DirectToFieldMapping emailMapping = new DirectToFieldMapping();
	    emailMapping.setAttributeName("email");
	    emailMapping.setFieldName("EMAIL");
	    descriptor.addMapping(emailMapping);
	    
	    DirectToFieldMapping telefonMapping = new DirectToFieldMapping();
	    telefonMapping.setAttributeName("telefon");
	    telefonMapping.setFieldName("TELEFON");
	    descriptor.addMapping(telefonMapping);
	    
	    //one to many je to z opacnej strany
	    OneToOneMapping obchodnyPartnerMapping = new OneToOneMapping();
	    obchodnyPartnerMapping.setAttributeName("obchodnyPartner");
	    obchodnyPartnerMapping.setReferenceClass(ObchodnyPartner.class);
	    obchodnyPartnerMapping.useBasicIndirection();
	    obchodnyPartnerMapping.addForeignKeyFieldName("OBCH_PAR_ID", "ID");
		descriptor.addMapping(obchodnyPartnerMapping);
	   
		return descriptor;
	}
	
	public ClassDescriptor buildAdresaDescriptor(){
		ClassDescriptor descriptor = new ClassDescriptor();
	    descriptor.setJavaClass(Adresa.class);
	    descriptor.addTableName("md1_adresa");
	    descriptor.addPrimaryKeyFieldName("ID");
	    
	    // ClassDescriptor properties. sekvencna tabulka properties, davam meno a id, pretoze chcem aby mi vytvarala id v tejto tabulke
	    descriptor.setSequenceNumberFieldName("ID");
	    descriptor.setSequenceNumberName("MD1_ADRESA");
	    
	    // Mappings.
	    DirectToFieldMapping idMapping = new DirectToFieldMapping();
	    idMapping.setAttributeName("id");
	    idMapping.setFieldName("ID");
	    descriptor.addMapping(idMapping);
	    
	    DirectToFieldMapping ulicaMapping = new DirectToFieldMapping();
	    ulicaMapping.setAttributeName("ulica");
	    ulicaMapping.setFieldName("ULICA");
	    descriptor.addMapping(ulicaMapping);	    
	    
	    DirectToFieldMapping cisloMapping = new DirectToFieldMapping();
	    cisloMapping.setAttributeName("cislo");
	    cisloMapping.setFieldName("CISLO");
	    descriptor.addMapping(cisloMapping);
	    
	    DirectToFieldMapping mestoMapping = new DirectToFieldMapping();
	    mestoMapping.setAttributeName("mesto");
	    mestoMapping.setFieldName("MESTO");
	    descriptor.addMapping(mestoMapping);
	    
	    DirectToFieldMapping pscMapping = new DirectToFieldMapping();
	    pscMapping.setAttributeName("psc");
	    pscMapping .setFieldName("PSC");
	    descriptor.addMapping(pscMapping );
	   
		return descriptor;
	}
	
	public ClassDescriptor buildObjednavkaDescriptor(){
		ClassDescriptor descriptor = new ClassDescriptor();
	    descriptor.setJavaClass(Objednavka.class);
	    descriptor.addTableName("md1_objednavka");
	    descriptor.addPrimaryKeyFieldName("ID");
	    
	    // ClassDescriptor properties. sekvencna tabulka properties, davam meno a id, pretoze chcem aby mi vytvarala id v tejto tabulke
	    descriptor.setSequenceNumberFieldName("ID");
	    descriptor.setSequenceNumberName("MD1_OBJEDNAVKA");
	    
	    // Mappings.
	    DirectToFieldMapping idMapping = new DirectToFieldMapping();
	    idMapping.setAttributeName("id");
	    idMapping.setFieldName("ID");
	    descriptor.addMapping(idMapping);
	    
	    DirectToFieldMapping cisloObjednavkyMapping = new DirectToFieldMapping();
	    cisloObjednavkyMapping.setAttributeName("cisloObjednavky");
	    cisloObjednavkyMapping.setFieldName("CISLO_OBJ");
	    descriptor.addMapping(cisloObjednavkyMapping);	    
	    
	    DirectToFieldMapping datumMapping = new DirectToFieldMapping();
	    datumMapping.setAttributeName("datum");
	    datumMapping.setFieldName("DATUM");
	    descriptor.addMapping(datumMapping);
	    
	    DirectToFieldMapping sumaMapping = new DirectToFieldMapping();
	    sumaMapping.setAttributeName("suma");
	    sumaMapping.setFieldName("SUMA");
	    descriptor.addMapping(sumaMapping);
	    
	    DirectToFieldMapping stavObjednavkyMapping = new DirectToFieldMapping();
	    stavObjednavkyMapping.setAttributeName("stavObjednavky");
	    stavObjednavkyMapping.setFieldName("STAV");
	    stavObjednavkyMapping.setGetMethodName("getStavObjednavkyTL");
	    stavObjednavkyMapping.setSetMethodName("setStavObjednavkyTL");
	    descriptor.addMapping(stavObjednavkyMapping);
	    
	    OneToOneMapping zakaznikMapping = new OneToOneMapping();
	    zakaznikMapping.setAttributeName("zakaznik");
	    zakaznikMapping.setReferenceClass(Zakaznik.class);
	    zakaznikMapping.useBasicIndirection();
	    zakaznikMapping.addForeignKeyFieldName("OBCH_PAR_ID", "ID");
		descriptor.addMapping(zakaznikMapping);
		
		//OneToManyMapping musim vzdy podla analyzy a viditelnosti, v kode aj Objednavka potrebuje vidiet Polozky aj vice versa
		OneToManyMapping polozkyMapping = new OneToManyMapping();
		polozkyMapping.setAttributeName("polozky");
		polozkyMapping.setReferenceClass(ObjednavkaPolozka.class);
		polozkyMapping.useBasicIndirection();
		polozkyMapping.privateOwnedRelationship();
		polozkyMapping.addTargetForeignKeyFieldName("OBJEDNAVKA_ID", "ID");
		descriptor.addMapping(polozkyMapping);
		
		//OneToOneMapping je dobre?
		OneToOneMapping povodnaObjednavkaMapping = new OneToOneMapping();
		povodnaObjednavkaMapping.setAttributeName("povodnaObjednavka");
		povodnaObjednavkaMapping.setReferenceClass(Objednavka.class);
		povodnaObjednavkaMapping.useBasicIndirection();
		povodnaObjednavkaMapping.addForeignKeyFieldName("POVODNA_OBJ_ID", "ID");
		descriptor.addMapping(povodnaObjednavkaMapping);
		
		//OneToOneMapping by mal byt podobne ako povodna objednavka
		OneToOneMapping vystavilMapping = new OneToOneMapping();
		vystavilMapping.setAttributeName("vystavil");
		vystavilMapping.setReferenceClass(Uzivatel.class);
		vystavilMapping.useBasicIndirection();
		//foreign key takto?
		vystavilMapping.addForeignKeyFieldName("VYSTAVIL", DBUzivatel.ID.getFieldName());
		descriptor.addMapping(vystavilMapping);
		      
	    return descriptor;
	}
	
	public ClassDescriptor buildObjednavkaPolozkaDescriptor(){
		ClassDescriptor descriptor = new ClassDescriptor();
	    descriptor.setJavaClass(ObjednavkaPolozka.class);
	    descriptor.addTableName("md1_objednavka_polozka");
	    descriptor.addPrimaryKeyFieldName("ID");
	    
	    // ClassDescriptor properties. sekvencna tabulka properties, davam meno a id, pretoze chcem aby mi vytvarala id v tejto tabulke
	    descriptor.setSequenceNumberFieldName("ID");
	    descriptor.setSequenceNumberName("MD1_OBJEDNAVKA_POLOZ");
	    
	    // Mappings.
	    DirectToFieldMapping idMapping = new DirectToFieldMapping();
	    idMapping.setAttributeName("id");
	    idMapping.setFieldName("ID");
	    descriptor.addMapping(idMapping);
	    
	    DirectToFieldMapping jednotkovaCenaMapping = new DirectToFieldMapping();
	    jednotkovaCenaMapping.setAttributeName("jednotkovaCena");
	    jednotkovaCenaMapping.setFieldName("JEDN_CENA");
	    descriptor.addMapping(jednotkovaCenaMapping);	    
	    
	    DirectToFieldMapping mnozstvoMapping = new DirectToFieldMapping();
	    mnozstvoMapping.setAttributeName("mnozstvo");
	    mnozstvoMapping.setFieldName("MNOZSTVO");
	    descriptor.addMapping(mnozstvoMapping);
	    
	    DirectToFieldMapping sumaMapping = new DirectToFieldMapping();
	    sumaMapping.setAttributeName("suma");
	    sumaMapping.setFieldName("SUMA");
	    descriptor.addMapping(sumaMapping);
	    
	    OneToOneMapping tovarMapping = new OneToOneMapping();
	    tovarMapping.setAttributeName("tovar");
	    tovarMapping.setReferenceClass(Tovar.class);
	    tovarMapping.useBasicIndirection();
	    tovarMapping.addForeignKeyFieldName("TOVAR_ID", "ID");
		descriptor.addMapping(tovarMapping);
	    
		//OneToOneMapping musim vzdy podla analyzy a viditelnosti, v kode aj Polozka potrebuje vidiet Objednavku aj vice versa
	    OneToOneMapping objednavkaMapping = new OneToOneMapping();
	    objednavkaMapping.setAttributeName("objednavka");
	    objednavkaMapping.setReferenceClass(Objednavka.class);
	    objednavkaMapping.useBasicIndirection();
	    objednavkaMapping.addForeignKeyFieldName("OBJEDNAVKA_ID", "ID");
		descriptor.addMapping(objednavkaMapping);
	    	        
	    return descriptor;
	}
	
	public ClassDescriptor buildCisloDokladuDescriptor(){
		ClassDescriptor descriptor = new ClassDescriptor();
		descriptor.setJavaClass(CisloDokladu.class);
		descriptor.addTableName("md1_cislo_dokladu");
		descriptor.addPrimaryKeyFieldName("ID");
		    
		// ClassDescriptor properties. sekvencna tabulka properties, davam meno a id, pretoze chcem aby mi vytvarala id v tejto tabulke
		descriptor.setSequenceNumberFieldName("ID");
		descriptor.setSequenceNumberName("MD1_CISLO_DOKLADU");
		    
		// Mappings.
		DirectToFieldMapping idMapping = new DirectToFieldMapping();
		idMapping.setAttributeName("id");
		idMapping.setFieldName("ID");
		descriptor.addMapping(idMapping);
		    
		DirectToFieldMapping rokMapping = new DirectToFieldMapping();
		rokMapping.setAttributeName("rok");
		rokMapping.setFieldName("ROK");
		descriptor.addMapping(rokMapping);	    
		    
		DirectToFieldMapping typMapping = new DirectToFieldMapping();
		typMapping.setAttributeName("typ");
		typMapping.setFieldName("TYP");
		descriptor.addMapping(typMapping);
		    
		DirectToFieldMapping poradoveCisloMapping = new DirectToFieldMapping();
		poradoveCisloMapping.setAttributeName("poradoveCislo");
		poradoveCisloMapping.setFieldName("PORADOVE_CISLO");
		descriptor.addMapping(poradoveCisloMapping);
		    
		return descriptor;
	}
		
		public ClassDescriptor buildFakturaDescriptor(){
			ClassDescriptor descriptor = new ClassDescriptor();
		    descriptor.setJavaClass(Faktura.class);
		    descriptor.addTableName("md1_faktura");
		    descriptor.addPrimaryKeyFieldName("ID");
		    
		    // ClassDescriptor properties. sekvencna tabulka properties, davam meno a id, pretoze chcem aby mi vytvarala id v tejto tabulke
		    descriptor.setSequenceNumberFieldName("ID");
		    descriptor.setSequenceNumberName("MD1_FAKTURA");
		    
		    // Mappings.
		    DirectToFieldMapping idMapping = new DirectToFieldMapping();
		    idMapping.setAttributeName("id");
		    idMapping.setFieldName("ID");
		    descriptor.addMapping(idMapping);
		    
		    DirectToFieldMapping cisloFakturyMapping = new DirectToFieldMapping();
		    cisloFakturyMapping.setAttributeName("cisloFaktury");
		    cisloFakturyMapping.setFieldName("CISLO_FAK");
		    descriptor.addMapping(cisloFakturyMapping);	    
		    
		    DirectToFieldMapping datumVystaveniaMapping = new DirectToFieldMapping();
		    datumVystaveniaMapping.setAttributeName("datumVystavenia");
		    datumVystaveniaMapping.setFieldName("DATUM_VYST");
		    descriptor.addMapping(datumVystaveniaMapping);
		    
		    DirectToFieldMapping datumDodaniaMapping = new DirectToFieldMapping();
		    datumDodaniaMapping.setAttributeName("datumDodania");
		    datumDodaniaMapping.setFieldName("DATUM_DOD");
		    descriptor.addMapping(datumDodaniaMapping);
		    
		    DirectToFieldMapping datumSplatnostiMapping = new DirectToFieldMapping();
		    datumSplatnostiMapping.setAttributeName("datumSplatnosti");
		    datumSplatnostiMapping.setFieldName("DATUM_SPLAT");
		    descriptor.addMapping(datumSplatnostiMapping);
		    
		    OneToOneMapping objednavkaMapping = new OneToOneMapping();
		    objednavkaMapping.setAttributeName("objednavka");
		    objednavkaMapping.setReferenceClass(Objednavka.class);
		    objednavkaMapping.useBasicIndirection();
		    objednavkaMapping.addForeignKeyFieldName("OBJ_ID", "ID");
			descriptor.addMapping(objednavkaMapping);
		    	        
		    return descriptor;
		}
}
