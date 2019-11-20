package cs562.bean;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.dbutils.QueryRunner;

public class Project {
	ArrayList<String> selectAttributes = new ArrayList<>();
	int groupNum;
	ArrayList<String> groupAttributes = new ArrayList<>();
	ArrayList<String> fAttributes = new ArrayList<>();
	ArrayList<String> conditions = new ArrayList<>();
	public void readFile() throws FileNotFoundException {
		Scanner sc = new Scanner(new File("ta2.esql"));
		ArrayList<String> attributes = new ArrayList<>();
		ArrayList rs = new ArrayList<>();
		while(sc.hasNextLine()) {
			attributes.add(sc.nextLine());
		}
		for (String string : attributes) {
			System.out.println(string);
		}
		System.out.println();
		for (String attribute : attributes) {
			if (attribute.contains("ATTRIBUTE(S)")) {
				rs = (ArrayList) getAttributes(attribute);
				selectAttributes.addAll(rs);
			}
			if (attribute.contains("GROUPING ATTRIBUTES(V)")) {
				rs = (ArrayList) getAttributes(attribute);
				groupAttributes.addAll(rs);
//				syso(groupAttributes);
			}
			if(attribute.contains("CONDITION-VECT([σ])")) {
				conditions.addAll(getAttributes(attribute));
//				syso(conditions);
			}
			if(attribute.contains("F-VECT([F])")) {
				rs = (ArrayList)getAttributes(attribute);
				fAttributes.addAll(rs);
//				syso(fAttributes);
			}
		}
	}
	/*
	 * Get the attributes String
	 */
	private List getAttributes(String attribute) {
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
}
