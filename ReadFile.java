import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayListHandler;

public class ReadFile {
	ArrayList<String> selectAttributes = new ArrayList<>();
	int groupNum;
	ArrayList<String> groupAttributes = new ArrayList<>();
	ArrayList<String> fAttributes = new ArrayList<>();
	ArrayList<String> conditions = new ArrayList<>();
	public void readFile() throws FileNotFoundException {
		Scanner sc = new Scanner(new File("src/data2.esql"));
		ArrayList<String> attributes = new ArrayList<>();
		ArrayList<String> rs = new ArrayList<>();
		while(sc.hasNextLine()) {
			attributes.add(sc.nextLine());
		}
		for (String string : attributes) {
			System.out.println(string);
		}
		System.out.println();
		for (String attribute : attributes) {
			if (attribute.contains("ATTRIBUTE(S)")) {
				rs = (ArrayList<String>) getAttributes(attribute);
				selectAttributes.addAll(rs);
			}
			if (attribute.contains("GROUPING ATTRIBUTES(V)")) {
				rs = (ArrayList<String>) getAttributes(attribute);
				groupAttributes.addAll(rs);
//				syso(groupAttributes);
			}
			if(attribute.contains("CONDITION-VECT([σ])")) {
				conditions.addAll(getAttributes(attribute));
//				syso(conditions);
			}
			if(attribute.contains("F-VECT([F])")) {
				rs = (ArrayList<String>)getAttributes(attribute);
				fAttributes.addAll(rs);
//				syso(fAttributes);
			}
		}
		sc.close();
	}
	/*
	 * Get the attributes String
	 */
	private List<String> getAttributes(String attribute) {
		List<String> list = new ArrayList<>();
		int i = attribute.indexOf(":");
		attribute = attribute.substring(i+1);
//		System.out.println(attribute);
		String[] attributes = attribute.split(",");
		for (int j = 0; j < attributes.length; j++) {
			attributes[j] = attributes[j].trim();
			list.add(attributes[j]);
		}
		return list;
	}
	public static void main(String[] args) throws SQLException, IOException {
		createBean();
	}
	/*
	 * Create bean
	 */
	private static void createBean() throws SQLException, IOException {
		Connection conn = JdbcUtil.getConnection();
		StringBuilder sb = new StringBuilder();
		QueryRunner runner = new QueryRunner();
		String sql = "select column_name, data_type from information_schema.columns where table_name = 'sales' ";
		List<Object[]> query = runner.query(conn, sql, new ArrayListHandler());
		ArrayList<String> names = new ArrayList<>();
		ArrayList<String> types = new ArrayList<>();
		for (Object[] obj : query) {
			names.add(obj[0].toString());
			types.add(obj[1].toString());
		}
		sb.append("public class Sales {\n");
		for (int i = 0; i < names.size(); i++) {
			sb.append(addParameter(names.get(i),types.get(i)));
		}
		sb.append("}");
		File file = new File("src/Sales.java");
		file.createNewFile();
		PrintWriter p = new PrintWriter(file);
		p.write(sb.toString());
		p.flush();
		p.close();
		System.out.println(sb.toString());
	}
	private static String addParameter(String name, String type) {
		StringBuilder s = new StringBuilder();
		if(type.contains("integer")) {
			s.append("	private int " + name + ";\n");
			s.append("	public int get"+firstToUpperCase(name)+"() {\n" + 
					"		return " + name + ";\n" + 
					"	}\n");
			s.append("	public void set"+firstToUpperCase(name)+"(int "+ name +") {\n" + 
					"		this."+ name +" = "+name+";\n" + 
					"	}\n");
		}else if (type.contains("character")) {
			s.append("	private String " + name + ";\n");
			s.append("	public String get"+firstToUpperCase(name)+"() {\n" + 
					"		return " +name+ ";\n" + 
					"	}\n");
			s.append("	public void set"+firstToUpperCase(name)+"(String "+ name +") {\n" + 
					"		this." + name + " = "+name+";\n" + 
					"	}\n");
		}
		return s.toString();
	}
	private static String firstToUpperCase(String name) {
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}
}
