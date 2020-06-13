package com.telhoc.BorregoApiLibrary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Examples {

	public static void main(String[] args) {

		BorregoApi api = new BorregoApi();
		deleteAllSql(api);
		simpleInsert(api);
		queryAllJava(api);
		queryAllSql(api);
		querySimpleJava(api);
		queryConditionJava1(api);
		queryConditionNestedJava(api);
		queryConditionOrJava(api);
		queryConditionOrSimple(api);
		//deleteAllSql(api);
	}

	public static void simpleInsert(BorregoApi api) {

		// Insert some data
		Map<String, Object> data = new LinkedHashMap<String, Object>();
		data.put("id", 1);
		data.put("name", "Alice");
		data.put("age", 26);
		api.insertData("test_table", data);

		data = new LinkedHashMap<String, Object>();
		data.put("id", 2);
		data.put("name", "Bob");
		data.put("age", 28);
		api.insertData("test_table", data);

		data = new LinkedHashMap<String, Object>();
		data.put("id", 3);
		data.put("name", "Carol");
		data.put("age", 30);
		data.put("info", "Expert on Cryptography");
		api.insertData("test_table", data);
		
		data = new LinkedHashMap<String, Object>();
		data.put("id", 4);
		data.put("name", "David");
		HashMap<String, Object> nestedData = new LinkedHashMap<String, Object>();
		nestedData.put("age", 25);
		nestedData.put("balance", 80);
		nestedData.put("credit", 20);
		data.put("info", nestedData);
		api.insertData("test_table", data);

	}

	public static void queryAllJava(BorregoApi api) {
		// Querying with an empty map means retrieve all records
		Map<String, Object> query = new LinkedHashMap<String, Object>();
		List<Map<String, Object>> resultMapQuery = api.queryData("test_table", query);
		System.out.println("Java Query All Result: " + resultMapQuery);

	}

	public static void queryAllSql(BorregoApi api) {
		String resultSqlQuery = api.querySql("SELECT * FROM test_table");
		System.out.println("SQL Query All Result: " + resultSqlQuery);
		
	}
	
	public static void deleteAllSql(BorregoApi api) {
		api.querySql("DELETE FROM test_table");
	}
	
	public static void querySimpleJava(BorregoApi api) {
		Map<String, Object> query = new LinkedHashMap<String, Object>();
		query.put("id", 2);
		System.out.println("Java Query Simple: " + query);
		List<Map<String, Object>> resultMapQuery = api.queryData("test_table", query);
		System.out.println("Java Query Simple Result: " + resultMapQuery);

	}
	
	public static void queryConditionJava1(BorregoApi api) {

		Map<String, Object> query = new LinkedHashMap<String, Object>();
		Map<String, Object> queryCondition = new LinkedHashMap<String, Object>();
		queryCondition.put("$gte", 25);
		queryCondition.put("$lt", 27);
		query.put("age", queryCondition);

		System.out.println("Java Query GTE LT: " + query);
		List<Map<String, Object>> foundData = api.queryData("test_table", query);

		System.out.println("Java Query GTE LT Result: " + foundData);
	}
	
	public static void queryConditionNestedJava(BorregoApi api) {

		Map<String, Object> query = new LinkedHashMap<String, Object>();
		query.put("info.age", 25);

		System.out.println("Java Query Nested: " + query);
		List<Map<String, Object>> foundData = api.queryData("test_table", query);

		System.out.println("Java Query Nested Result: " + foundData);
	}
	
	public static void queryConditionOrJava(BorregoApi api) {

		Map<String, Object> query = new LinkedHashMap<String, Object>();
		Map<String, Object> ageConditionInner = new LinkedHashMap<String, Object>();
		ageConditionInner.put("$gte", 25);
		ageConditionInner.put("$lt", 27);
		Map<String, Object> ageCondition = new LinkedHashMap<String, Object>();
		ageCondition.put("age", ageConditionInner);
		Map<String, Object> idCondition = new LinkedHashMap<String, Object>();
		idCondition.put("id", 2);
		
		List<Map<String, Object>> orList = new ArrayList<Map<String, Object>>();
		orList.add(idCondition);
		orList.add(ageCondition);
		query.put("$or", orList);

		System.out.println("Java Query OR: " + query);

		List<Map<String, Object>> foundData = api.queryData("test_table", query);

		System.out.println("Java Query OR Result: " + foundData);
	}
	
	public static void queryConditionOrSimple(BorregoApi api) {

		Map<String, Object> query = new LinkedHashMap<String, Object>();
		Map<String, Object> idCondition = new LinkedHashMap<String, Object>();
		idCondition.put("id", 2);
		Map<String, Object> idCondition2 = new LinkedHashMap<String, Object>();
		idCondition2.put("id", 4);		
		
		List<Map<String, Object>> orList = new ArrayList<Map<String, Object>>();
		orList.add(idCondition);
		orList.add(idCondition2);
		query.put("$or", orList);

		System.out.println("Java Query OR Simple: " + query);

		List<Map<String, Object>> foundData = api.queryData("test_table", query);

		System.out.println("Java Query OR Simple Result: " + foundData);
	}
	
}
