class Config {
	private String url;
	private String username;
	private String password;
	Config(){}
	Config(String url, String username, String password){
		this.url = url;
		this.username = username;
		this.password = password;
	}
	
	public String getUrl() {
	    return url;
	}
	public void setUrl(String url) {
	    this.url = url;
	}
	public String getUsername() {
	    return username;
	}
	public void setUsername(String username) {
	    this.username = username;
	}
	public String getPassword() {
	    return password;
	}
	public void setPassword(String password) {
	    this.password = password;
	}
	private void write() {
	    
	}
	
	public static void main(String[] args) {
		Config conf = new Config("abc", "guangqi", "123");
		conf.write();
	}
	
}