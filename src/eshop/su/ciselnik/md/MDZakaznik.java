package eshop.su.ciselnik.md;

import eshop.su.ciselnik.uc.UCObchodnyPartner;
import eshop.su.ciselnik.uc.UCZakaznik;
import netframework.eclipselink.EclipseLinkSession;

public class MDZakaznik extends MDObchodnyPartner {

	@Override
	protected UCObchodnyPartner create() {
		return UCZakaznik.create((EclipseLinkSession) getSessionObject());
	}

	@Override
	protected UCObchodnyPartner read(Object id) throws Exception {
		return UCZakaznik.read(id, (EclipseLinkSession) getSessionObject());
	}

	@Override
	protected String getTitleText() {
		return "Zakaznik - MDZakaznik";
	}
}
