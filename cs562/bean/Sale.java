package cs562.bean;

public class Sale {
	private String cust;
	private String prod;
	private int day;
	private int month;
	private int year;
	private String state;
	private int quant;
	public String getCust() {
		return cust;
	}
	public void setCust(String cust) {
		this.cust = cust;
	}
	public String getProd() {
		return prod;
	}
	public void setProd(String prod) {
		this.prod = prod;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public int getQuant() {
		return quant;
	}
	public void setQuant(int quant) {
		this.quant = quant;
	}
	@Override
	public String toString() {
		return "Sales [cust=" + cust + ", prod=" + prod + ", day=" + day + ", month=" + month + ", year=" + year
				+ ", state=" + state + ", quant=" + quant + "]";
	}
}