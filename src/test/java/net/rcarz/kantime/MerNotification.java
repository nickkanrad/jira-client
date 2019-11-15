package net.rcarz.kantime;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import org.testng.annotations.Test;

@Test()
public class MerNotification {
	BOT autobot = new BOT();
	LocalDate earlier = LocalDate.now().minusMonths(1);
	int month = earlier.getMonthValue();
	int year = earlier.getYear();
	String agenciesList = "";

	/**
	 * read data from database using query.
	 * 
	 * @param query
	 *            query to execute
	 * @return object
	 * @throws IOException
	 *             IO Exception
	 * @throws SQLException
	 *             SQL Exception
	 */
	private Object[][] readDataFromDatabaseUsingQuery() throws IOException, SQLException {

		int totalRows = 0;
		int totalColumns = 0;
		int ci = 0;
		int cj = 0;

		String serverIP = "192.168.1.23";
		String databaseName = "Production_ZephyrMaster";
		String userName = "medicaresqluser";
		String password = "kantime_123";
		String port = "1433";

		/*
		 * Create a variable for the connection string using datas.
		 */
		String connectionUrl = "jdbc:sqlserver://" + serverIP + ":" + port + ";databaseName=" + databaseName + ";user="
				+ userName + ";password=" + password + "";

		Connection con = DriverManager.getConnection(connectionUrl);
		Statement stmt = con.createStatement();
		String sql = "exec _Utils_GetMonthEndClosePendingAgencies " + month + "," + year;

		/*
		 * total column
		 */
		ResultSet rs = stmt.executeQuery(sql);
		ResultSetMetaData rsmd = rs.getMetaData();

		totalColumns = rsmd.getColumnCount();
		// System.out.println("Total column : " + totalColumns);

		/*
		 * get the row count
		 */
		ResultSet rs1 = stmt.executeQuery(sql);
		while (rs1.next()) {
			totalRows = totalRows + 1;
		}

		/*
		 * create array
		 */
		Object[][] tabArray = null;
		tabArray = new String[totalRows][totalColumns];

		/*
		 * get column & row data
		 */
		stmt.execute(sql);
		ResultSet rs2 = stmt.getResultSet();
		while (rs2.next()) {
			cj = 0;
			for (int j = 1; j <= totalColumns; j++, cj++) {
				tabArray[ci][cj] = rs2.getString(rsmd.getColumnName(j));
			}
			ci = ci + 1;
		}

		/*
		 * close connections
		 */
		rs2.close();
		rs1.close();
		rs.close();
		stmt.close();

		/*
		 * return object
		 */
		return tabArray;
	}

	/**
	 * get mer pending agencies list
	 * 
	 * @throws SQLException
	 * @throws IOException
	 */
	public void getMerPendingAgencies() throws IOException, SQLException {
		try {
			String[][] pendingAgencies = (String[][]) readDataFromDatabaseUsingQuery();

			for (String[] agencies : pendingAgencies) {
				agenciesList = agenciesList + agencies[0].toString() + " \n";

			}
			System.out.println(agenciesList);

			if (!agenciesList.equals("")) {
				try {
					autobot.sendPost(
							"https://chat.googleapis.com/v1/spaces/AAAA6iDuIgI/messages?key=AIzaSyDdI0hCZtE6vySjMm-WEfRq3CPzqKqqsHI&token=Y1fHIVoW1tMmnXU05d95c7ARfBT6M7_TO7z4gFhzkHA%3D",
							"```Month end report locking failing for: \n" + agenciesList + "```");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			try {
				autobot.sendPost(
						"https://chat.googleapis.com/v1/spaces/AAAA6iDuIgI/messages?key=AIzaSyDdI0hCZtE6vySjMm-WEfRq3CPzqKqqsHI&token=Y1fHIVoW1tMmnXU05d95c7ARfBT6M7_TO7z4gFhzkHA%3D",
						"Could not check month end report lock as DB restoration in progress. It will be rechecked later after an hour");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
