package com.telhoc.BorregoApiLibrary;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BorregoApi {

	private Socket mBorregoSocket;

	private DataInputStream dis = null;
	private DataOutputStream dos = null;
	private Object mConnectionMutex;

	private static final int BORREGO_API_VERSION = 3;
	private static final int API_TYPE_SQL = 1;
	private static final int API_TYPE_JAVA = 2;

	private static final int DATA_ACCESS_PORT = 19026;
	private static final int CMD_INSERT = 1;
	private static final int CMD_QUERY = 2;
	private static final int CMD_QUERY_DATA = 3;
	private static final int CMD_DELETE = 4;
	private static final int CMD_UPDATE = 5;
	private static final int CMD_ACK = 6;
	private static final int CMD_QUERY_ALL = 7;

	private static final int INDEX_TYPE_ROCKS = 1;
	private static final int INDEX_TYPE_S3 = 2;
	private static final int INDEX_TYPE_NDN = 3;

	public BorregoApi() {

		mConnectionMutex = new Object();
		connect();
	}

	private void connect() {

		try {
			mBorregoSocket = new Socket("127.0.0.1", DATA_ACCESS_PORT);

			try {
				dis = new DataInputStream(mBorregoSocket.getInputStream());
				dos = new DataOutputStream(mBorregoSocket.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void insertData(String table, Map<String, Object> data) {

		Map<String, Object> txData = new LinkedHashMap<String, Object>(data);
		txData.put("_table", table);
		synchronized (mConnectionMutex) {
			try {
				dos.writeInt(BORREGO_API_VERSION);
				dos.writeInt(API_TYPE_JAVA);
				dos.writeInt(CMD_INSERT);

				byte[] txBytes = null;
				try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
						ObjectOutputStream oos = new ObjectOutputStream(baos);) {
					oos.writeObject(txData);
					oos.flush();
					txBytes = baos.toByteArray();
					baos.close();
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (txBytes != null) {
					dos.writeInt(txBytes.length);
					dos.write(txBytes);
					dos.flush();
				} else {
					dos.writeInt(0);
					dos.flush();
				}
			} catch (IOException e) {
				// e.printStackTrace();
				// Connection dropped, try again
				connect();
			}
		}

	}

	public void deleteData(String table, Map<String, Object> deleteQuery) {

		Map<String, Object> txData = new LinkedHashMap<String, Object>(deleteQuery);
		txData.put("_table", table);
		synchronized (mConnectionMutex) {
			try {
				dos.writeInt(BORREGO_API_VERSION);
				dos.writeInt(API_TYPE_JAVA);
				dos.writeInt(CMD_DELETE);

				byte[] txBytes = null;
				try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
						ObjectOutputStream oos = new ObjectOutputStream(baos);) {
					oos.writeObject(txData);
					oos.flush();
					txBytes = baos.toByteArray();
					baos.close();
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (txBytes != null) {
					dos.writeInt(txBytes.length);
					dos.write(txBytes);
					dos.flush();
				} else {
					dos.writeInt(0);
					dos.flush();
				}
			} catch (IOException e) {
				// e.printStackTrace();
				connect();
			}
		}
	}

	private List<Map<String, Object>> sendQuery(String table, Map<String, Object> query) {

		List<Map<String, Object>> queryResult = null;

		try {
			dos.writeInt(BORREGO_API_VERSION);
			dos.writeInt(API_TYPE_JAVA);
			dos.writeInt(CMD_QUERY);

			byte[] txBytes = null;
			try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(baos);) {
				oos.writeObject(query);
				oos.flush();
				txBytes = baos.toByteArray();
				baos.close();
				oos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (txBytes != null) {
				dos.writeInt(txBytes.length);
				dos.write(txBytes);
				dos.flush();
			} else {
				dos.writeInt(0);
				dos.flush();
			}

			// read the results
			int apiVersion = dis.readInt();

			// Enforce correct API version
			if (apiVersion != BORREGO_API_VERSION) {
				// Incorrect API version. FATAL!
				dis.close();
				dos.close();
				mBorregoSocket.close();
				return null;
			}

			int apiType = dis.readInt();
			if (apiType == API_TYPE_JAVA) {
				int apiCmd = dis.readInt();
				if (apiCmd == CMD_QUERY) {
					int resultBytesLen = dis.readInt();
					byte[] resultBytes = new byte[resultBytesLen];
					dis.readFully(resultBytes);
					ByteArrayInputStream bis = new ByteArrayInputStream(resultBytes);
					ObjectInputStream ois = new ObjectInputStream(bis);
					Object apiObject = ois.readObject();
					return (List<Map<String, Object>>) apiObject;
				} else {
					// Syntax error ignore.
					return null;
				}
			} else {
				// Syntax error ignore.
				return null;
			}

		} catch (IOException e) {
			// e.printStackTrace();
			connect();
			if (mBorregoSocket.isConnected()) {
				sendQuery(table, query);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return queryResult;

	}

	public List<Map<String, Object>> queryData(String table, Map<String, Object> query) {

		List<Map<String, Object>> queryResult = null;

		Map<String, Object> txData = new LinkedHashMap<String, Object>(query);
		txData.put("_table", table);
		synchronized (mConnectionMutex) {
			queryResult = sendQuery(table, txData);
		}

		return queryResult;

	}

	private String sendQuerySql(String sqlQuery) {
		
		String queryResult = null;
		
		try {
			dos.writeInt(BORREGO_API_VERSION);
			dos.writeInt(API_TYPE_SQL);
			dos.writeInt(sqlQuery.getBytes().length);
			dos.write(sqlQuery.getBytes());
			dos.flush();

			// read the results
			int apiVersion = dis.readInt();

			// Enforce correct API version
			if (apiVersion != BORREGO_API_VERSION) {
				// Incorrect API version. FATAL!
				dis.close();
				dos.close();
				mBorregoSocket.close();
				return null;
			}

			int apiType = dis.readInt();
			if (apiType == API_TYPE_SQL) {
				int sqlBytesLen = dis.readInt();
				if (sqlBytesLen > 0) {
					byte[] sqlBytes = new byte[sqlBytesLen];
					dis.readFully(sqlBytes);
					queryResult = new String(sqlBytes);
					return queryResult;
				}
			} else {
				// Syntax error ignore.
				return null;
			}

		} catch (IOException e) {
			//e.printStackTrace();
			connect();
			if (mBorregoSocket.isConnected()) {
				sendQuerySql(sqlQuery);
			}
		}
		
		return queryResult;

		
	}
	
	public String querySql(String sqlQuery) {

		String queryResult = null;

		synchronized (mConnectionMutex) {
			queryResult = sendQuerySql(sqlQuery);
		}

		return queryResult;

	}

}
