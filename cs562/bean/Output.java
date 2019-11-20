package cs562.bean;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
public class Output {
	static HashMap<String,ArrayList<Sale>> map = new HashMap<String, ArrayList<Sale>>();
	static String user = "postgres";
	static String password = "12345";
	static String url = "jdbc:postgresql://localhost:5432/Guangqi";
	static Connection conn;
	static List<Sale> list;
	static QueryRunner runner = null;
	static HashMap<String,ArrayList<Sale>>[] mapSet = null;
	static {
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(url,user,password);
			list = getSales();
			System.out.println("Database connect success");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) throws SQLException, FileNotFoundException, NoSuchMethodException, SecurityException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Project p = new Project();
		p.readFile();
		setParameter(p.selectAttributes, p.groupNum, p.groupAttributes, p.fAttributes, p.conditions);
		
	}
	private static void setParameter(ArrayList<String> selectAttributes,int groupNum,ArrayList<String> groupAttributes, ArrayList<String> fAttributes, ArrayList<String> conditions) throws SQLException, NoSuchMethodException, SecurityException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
//		0.group n for n times.{
//		1.handle group by attributes (use groupAttributes)
//		Map("key",Arraylist);
//		2.handle aggregation function(use selectAttributes to find)
		List<Object[]> key = groupBy(groupAttributes);
		setCondition(groupAttributes,key,conditions);
		handleAggregation(fAttributes,selectAttributes);
	}
	private static void handleAggregation(ArrayList<String> fAttributes,ArrayList<String> selectAttributes) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		for (int i = 0; i < fAttributes.size(); i++) {
//			System.out.println(fAttributes.get(i));
			int index = fAttributes.get(i).indexOf("_");
			String agString = fAttributes.get(i).substring(0, index - 1);
			String property = fAttributes.get(i).substring(fAttributes.get(i).indexOf("_") + 1);
			property = property.substring(0, 1).toUpperCase() + property.substring(1);
//			for (String string : selectAttributes) {
//				if(!string.contains("sale")) {
//					System.out.println(string);
//				}
//			}
//			System.out.println(property);
//			System.out.println(agString);
			switch (agString) {
			case "sum":
				System.out.println("sum");
				HashMap<String,Integer> sumSet = getSum(mapSet[i],property);
				for (String string : sumSet.keySet()) {
					System.out.println(string + "	"+sumSet.get(string) );
				}
				System.out.println("--------");
				break;
			case "count":
				System.out.println("count");
				HashMap<String, Integer> countSet = getCount(mapSet[i],property);
				for (String string : countSet.keySet()) {
					System.out.println(string + "	" + countSet.get(string));
				}
				System.out.println("--------");
			case "avg":
				System.out.println("avg");
				HashMap<String, Integer> avgSet = getAvg(mapSet[i],property);
				for (String string : avgSet.keySet()) {
					System.out.println(string + "	" + avgSet.get(string));
				}
				System.out.println("--------");
				break;
			case "min":
				System.out.println("min");
				HashMap<String, Integer> minSet = getMin(mapSet[i],property);
				for (String string : minSet.keySet()) {
					System.out.println(string + "	" + minSet.get(string).toString());
				}
				System.out.println("--------");
				break;
			case "max":
				System.out.println("max");
				HashMap<String, Integer> maxSet = getMax(mapSet[i],property);
				for (String string : maxSet.keySet()) {
						System.out.println(string + "	" + maxSet.get(string));
				}
				System.out.println("--------");
				break;
			}
		}
	}
	private static void setCondition(ArrayList<String> groupAttributes,List<Object[]> key, ArrayList<String> conditions) throws SQLException, NoSuchMethodException, SecurityException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		int n = key.get(0).length;
		Method[] methods = new Method[n];
		for (int i = 0; i < methods.length; i++) {
			String s = "get" + groupAttributes.get(i).substring(0, 1).toUpperCase() + groupAttributes.get(i).substring(1);
			methods[i] = Sale.class.getMethod(s);
		}
		Method[] conditionMethods = new Method[conditions.size()];
		String[] conditionResult = new String[conditions.size()];
		for (int i = 0; i < conditionMethods.length; i++) {
//			System.out.println(conditions.get(i));
			int begin = conditions.get(i).indexOf('.') + 1;
			int end = conditions.get(i).indexOf('=');
			String s = conditions.get(i).substring(begin, end);
			s = "get" + s.substring(0,1).toUpperCase() + s.substring(1);
//			System.out.println(s);
			conditionMethods[i] = Sale.class.getMethod(s);
			conditionResult[i] = conditions.get(i).substring(conditions.get(i).indexOf('\'') + 1, conditions.get(i).lastIndexOf('\''));
//			System.out.println(conditionResult[i]);
		}
		mapSet = new HashMap[conditions.size()];
		for (int i = 0; i < mapSet.length; i++) {
			mapSet[i] = new HashMap<String,ArrayList<Sale>>();
			for (String s : map.keySet()) {
				mapSet[i].put(s, new ArrayList<Sale>());
			}
		}
//		System.out.println(mapSet[0] == mapSet[1]);
		for (int mapCount = 0; mapCount < mapSet.length; mapCount++) {
			for (Sale sale : list) {
				String k = "";
				for (int i = 0; i < n; i++) {
					k = k + methods[i].invoke(sale) + " ";
				}
//				System.out.println(k);
				if(conditionResult[mapCount].equals(" ")) {
					mapSet[mapCount].get(k).add(sale);
					continue;
				}
				if(!(conditionMethods[mapCount].invoke(sale).toString().equals(conditionResult[mapCount]))) {
//					System.out.println(conditionResult[mapCount]);
					continue;
				}
//					System.out.println(k + "  key");
				if(mapSet[mapCount].get(k)!=null) {
//					System.out.println(sale);
					mapSet[mapCount].get(k).add(sale);
				}
			}
		}
//		for (String string : mapSet[0].keySet()) {
//			ArrayList<Sales> arrayList = mapSet[0].get(string);
//			for (Sales sales : arrayList) {
//				System.out.println(sales);
//			}
//		}
	}
//	private static void getMethods(String string) throws SQLException, NoSuchMethodException, SecurityException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
//		System.out.println(string);
//		String sql = "select data_type from INFORMATION_SCHEMA.COLUMNS where table_name = 'sales' and column_name = '" + string + "'";
//		System.out.println(sql);
//		String s = runner.query(conn, sql, new ScalarHandler<String>());
//		System.out.println(s);
//		if(s.equals("integer")) {
//			Method method = Sales.class.getMethod("getYear");
//			Object invoke = method.invoke(list.get(0));
//			System.out.println(invoke);
//		}else if (s.contains("character")) {
//		}
//	}
	/*
	 * handle the group by attributes build a map
	 */
	private static List<Object[]> groupBy(ArrayList<String> groupAttributes) throws SQLException {
		if(groupAttributes == null) {
			System.out.println("no groupAttributes");
			return null;
		}
		QueryRunner runner = new QueryRunner();
		String attributes = "";
		for (int i = 0; i < groupAttributes.size(); i++) {
			attributes = attributes + groupAttributes.get(i);
			if(i != groupAttributes.size()-1)
				attributes +=  ",";
		}
//		System.out.println(attributes);
		String sql = "select "+ attributes +" from sales group by "  + attributes ;
		List<Object[]> query = runner.query(conn, sql, new ArrayListHandler());
		for (Object[] object : query) {
//			System.out.println(Arrays.toString(object));
			String key = getKey(object);
//			System.out.println(key);
			map.put(key, new ArrayList<Sale>());
		}
		
		return query;
	}
	
	/*
	 * Select the data more convinent
	 */
	private static List<Sale> getSales() throws SQLException {
		runner = new QueryRunner();
		String sql = "select * from sales";
		List<Sale> list = runner.query(conn, sql, new BeanListHandler<>(Sale.class));
//		System.out.println("-------");
//		for (Sales sales : list) {
//			System.out.println(sales.toString());
//		}
//		System.out.println("-------");
//		conn.close();
		return list;
	}

	private static HashMap<String, Integer> getSum(HashMap<String, ArrayList<Sale>> map, String property) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		HashMap<String,Integer> resultMap = new HashMap<>();
		Method m = Sale.class.getMethod("get" + property);
		for (String s : map.keySet()) {
			int sum = 0;
			for (Sale sales : map.get(s)) {
				if(m.invoke(sales) == null)
					continue;
				sum += (int) m.invoke(sales);
			}
			if(sum>0)
				resultMap.put(s, sum);
		}
		return resultMap;
	}
	private static HashMap<String, Integer> getCount(HashMap<String, ArrayList<Sale>> map, String property) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		HashMap<String,Integer> resultMap = new HashMap<>();
		Method m = Sale.class.getMethod("get" + property);
		for (String s : map.keySet()) {
			int count = 0;
			for (Sale sales : map.get(s)) {
				if(m.invoke(sales) == null)
					continue;
				else {
					count++;
				}
			}
			if(count > 0)
			resultMap.put(s, count);
		}
		return resultMap;
	}
	private static HashMap<String,Integer> getMax(HashMap<String, ArrayList<Sale>> map, String property) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		HashMap<String,Integer> resultMap = new HashMap<>();
		Method m = Sale.class.getMethod("get" + property);
		for (String s : map.keySet()) {
			int max = Integer.MIN_VALUE;
			for (Sale sales : map.get(s)) {
				if(m.invoke(sales) == null)
					continue;
				else {
					if((int)m.invoke(sales) > max) {
						max = (int) m.invoke(sales);
					}
				}
			}
			if (max != Integer.MIN_VALUE) {
				resultMap.put(s, max);
			}
		}
		return resultMap;
	}
	private static HashMap<String, Integer> getMin(HashMap<String, ArrayList<Sale>> map, String property) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		HashMap<String,Integer> resultMap = new HashMap<>();
		Method m = Sale.class.getMethod("get" + property);
		for (String s : map.keySet()) {
			int min = Integer.MAX_VALUE;
			for (Sale sales : map.get(s)) {
				if(m.invoke(sales) == null)
					continue;
				else {
					if((int)m.invoke(sales) < min) {
						min = (int) m.invoke(sales);
					}
				}
			}
			if(min != Integer.MAX_VALUE)
				resultMap.put(s, min);
		}
		return resultMap;
	}
	private static HashMap<String, Integer> getAvg(HashMap<String, ArrayList<Sale>> map, String property) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		HashMap<String,Integer> resultMap = new HashMap<>();
		Method m = Sale.class.getMethod("get" + property);
		for (String s : map.keySet()) {
			int num = 0;
			int sum = 0;
			int avg = 0;
			for (Sale sales : map.get(s)) {
				if(m.invoke(sales) == null)
					continue;
				else {
					sum = sum + (int) m.invoke(sales);
					num++;
//					System.out.println(sum);
//					System.out.println(sales + "Sales");
				}
			}
			if(num != 0) {
				avg = sum / num;
				resultMap.put(s,avg);
			}
		}
		return resultMap;
	}
	public static String getKey(Object[]object) {
		String key = "";
		for (Object string : object) {
			key = key + string.toString() + " " ;
		}
		return key;
	}

}

