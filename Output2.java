import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;


public class Output2 {
	static List<Sales> list;
	static Connection conn;
	static HashMap<String,ArrayList<Sales>>[] resultMap;
	public static void main(String[] args) throws SQLException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		conn = JdbcUtil.getConnection();
		list = getSales();
		ReadFile p = new ReadFile();
		p.readFile();
		setParameter(p.selectAttributes, p.groupNum, p.groupAttributes, p.fAttributes, p.conditions);
	}
	
	private static List<Sales> getSales() throws SQLException {
		QueryRunner runner = new QueryRunner();
		String sql = " select * from sales ";
		return runner.query(conn, sql, new BeanListHandler<>(Sales.class));
	}
	private static void setParameter(ArrayList<String> selectAttributes, int groupNum,
			ArrayList<String> groupAttributes, ArrayList<String> fAttributes, ArrayList<String> conditions) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		groupBy(groupAttributes,conditions);
		handleAggregation(fAttributes,selectAttributes);
	}
	private static void handleAggregation(ArrayList<String> fAttributes, ArrayList<String> selectAttributes) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		for (int i = 0; i < fAttributes.size(); i++) {
			int index = fAttributes.get(i).indexOf("_");
			String agString = fAttributes.get(i).substring(0, index - 1);
			String property = fAttributes.get(i).substring(fAttributes.get(i).indexOf("_") + 1);
			property = property.substring(0, 1).toUpperCase() + property.substring(1);
			
			switch (agString) {
			case "sum":
				System.out.println("sum");
				HashMap<String,Integer> sumSet = getSum(resultMap[i],property);
				for (String string : sumSet.keySet()) {
					System.out.println( string+ "	"+sumSet.get(string) );
				}
				System.out.println("--------");
				break;
			case "count":
				System.out.println("count");
				HashMap<String, Integer> countSet = getCount(resultMap[i],property);
				for (String string : countSet.keySet()) {
					System.out.println(string + "	" + countSet.get(string));
				}
				System.out.println("--------");
			case "avg":
				System.out.println("avg");
				HashMap<String, Integer> avgSet = getAvg(resultMap[i],property);
				for (String string : avgSet.keySet()) {
//					System.out.println(string + "	" + avgSet.get(string) + "      " +resultMap[i].get(string).toString());
					System.out.println(string + "	" + avgSet.get(string).toString());
				}
				System.out.println("--------");
				break;
			case "min":
				System.out.println("min");
				HashMap<String, Integer> minSet = getMin(resultMap[i],property);
				for (String string : minSet.keySet()) {
					System.out.println(string + "	" + minSet.get(string).toString());
				}
				System.out.println("--------");
				break;
			case "max":
				System.out.println("max");
				HashMap<String, Integer> maxSet = getMax(resultMap[i],property);
				for (String string : maxSet.keySet()) {
						System.out.println(string + "	" + maxSet.get(string).toString());
				}
				System.out.println("--------");
				break;
			}
		}
	}
	private static void groupBy(ArrayList<String> groupAttributes,ArrayList<String> conditions) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		resultMap = new HashMap[conditions.size()];
		Method[] conditionMethods = new Method[conditions.size()];
		String[] conditionResult = new String[conditions.size()];
		for (int i = 0; i < resultMap.length; i++) {
			int begin = conditions.get(i).indexOf('.') + 1;
			int end = conditions.get(i).indexOf('=');
			String s = conditions.get(i).substring(begin, end);
			conditionMethods[i] = addGet(s);
			conditionResult[i] = conditions.get(i).substring(conditions.get(i).indexOf('\'') + 1, conditions.get(i).lastIndexOf('\''));
			HashMap<String, ArrayList<Sales>> map = new HashMap<String,ArrayList<Sales>>();
			for (Sales Sales : list) {
				String key = "";
				if(conditionResult[i].equals(" ")) {
					for (int j = 0; j < groupAttributes.size(); j++) {
						Method method = addGet(groupAttributes.get(j));
						key = key + method.invoke(Sales) + " ";
					}
					if(map.get(key) == null) {
						ArrayList<Sales> list = new ArrayList<Sales>();
						list.add(Sales);
						map.put(key, list);
					}else {
						map.get(key).add(Sales);
					}
					continue;
				}else if(conditionResult[i].equals(conditionMethods[i].invoke(Sales).toString())){
					for (int j = 0; j < groupAttributes.size(); j++) {
						Method method = addGet(groupAttributes.get(j));
						key = key + method.invoke(Sales) + " ";
					}
					if(map.get(key) == null) {
						ArrayList<Sales> list = new ArrayList<Sales>();
						list.add(Sales);
						map.put(key, list);
					}else {
						map.get(key).add(Sales);
					}
				}
			}
			resultMap[i] = map;
		}
	}
	private static HashMap<String, Integer> getSum(HashMap<String, ArrayList<Sales>> map, String property) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		HashMap<String,Integer> resultMap = new HashMap<>();
		Method m = Sales.class.getMethod("get" + property);
		for (String s : map.keySet()) {
			int sum = 0;
			for (Sales sales : map.get(s)) {
				if(m.invoke(sales) == null)
					continue;
				sum += (int) m.invoke(sales);
			}
			if(sum>0)
				resultMap.put(s, sum);
		}
		return resultMap;
	}
	private static HashMap<String, Integer> getCount(HashMap<String, ArrayList<Sales>> map, String property) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		HashMap<String,Integer> resultMap = new HashMap<>();
		Method m = Sales.class.getMethod("get" + property);
		for (String s : map.keySet()) {
			int count = 0;
			for (Sales sales : map.get(s)) {
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
	private static HashMap<String,Integer> getMax(HashMap<String, ArrayList<Sales>> map, String property) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		HashMap<String,Integer> resultMap = new HashMap<>();
		Method m = Sales.class.getMethod("get" + property);
		for (String s : map.keySet()) {
			int max = Integer.MIN_VALUE;
			for (Sales sales : map.get(s)) {
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
	private static HashMap<String, Integer> getMin(HashMap<String, ArrayList<Sales>> map, String property) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		HashMap<String,Integer> resultMap = new HashMap<>();
		Method m = Sales.class.getMethod("get" + property);
		for (String s : map.keySet()) {
			int min = Integer.MAX_VALUE;
			for (Sales sales : map.get(s)) {
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
	private static HashMap<String, Integer> getAvg(HashMap<String, ArrayList<Sales>> map, String property) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		HashMap<String,Integer> resultMap = new HashMap<>();
		Method m = Sales.class.getMethod("get" + property);
		for (String s : map.keySet()) {
			int num = 0;
			int sum = 0;
			int avg = 0;
			for (Sales sales : map.get(s)) {
				if(m.invoke(sales) == null)
					continue;
				else {
					sum = sum + (int) m.invoke(sales);
					num++;
				}
			}
			if(num != 0) {
				avg = sum / num;
				resultMap.put(s,avg);
			}
		}
		return resultMap;
	}
	private static Method addGet(String string) throws NoSuchMethodException, SecurityException {
		String s = "get" + string.substring(0, 1).toUpperCase()+string.substring(1);
		return Sales.class.getMethod(s);
	}
}
