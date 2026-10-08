package model.inventory;

import java.io.Serial;
import java.io.Serializable;

public class Supplier implements Serializable {
	@Serial
	private static final long serialVersionUID = 1;

	private String name;
	private String contactInfo;

	public Supplier(String name, String contactInfo) {
		this.name = name;
		this.contactInfo = contactInfo;
	}

	public String getName() {
		return name;
	}

	public String getContactInfo() {
		return contactInfo;
	}

	public static Supplier getDefaultSupplier(Sector s) {
		switch (s) {
		case SMARTPHONES:
			return new Supplier("MobileTech Distributors", "mobile@mobiletech.it");
		case IT:
			return new Supplier("IT Solutions LTD", "it@itsolutions.ltd.uk");
		case GAMING:
			return new Supplier("GameWORLD", "gaming@gameworld.uk");
		case ACCESSORIES:
			return new Supplier("Accessory Hub", "acc@accessoryhub.ch");
		default:
			return new Supplier("Generic Supplier", "N/A");

		}
	}
}
