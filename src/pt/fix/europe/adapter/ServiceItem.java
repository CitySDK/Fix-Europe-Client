package pt.fix.europe.adapter;

public class ServiceItem {
	public static final int ITEM = 0;
	public static final int SECTION = 1;

	public final int type;
	public final String text;
	public final String title;
	public final String serviceCode;
	
	public ServiceItem(int type, String text, String title, String serviceCode) {
		this.type = type;
		this.text = text;
		this.title = title;
		this.serviceCode = serviceCode;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (other == this)
			return true;
		if (!(other instanceof String))
			return false;
		
		return this.title.equals(other.toString());
	}

	public boolean isHeader() {
		return type == SECTION;
	}
}
