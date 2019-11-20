public class Sales {
	private String cust;
	public String getCust() {
		return cust;
	}
	public void setCust(String cust) {
		this.cust = cust;
	}
	private String prod;
	public String getProd() {
		return prod;
	}
	public void setProd(String prod) {
		this.prod = prod;
	}
	private int day;
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	private int month;
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	private int year;
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
 
	private String state;
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	private int quant;
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