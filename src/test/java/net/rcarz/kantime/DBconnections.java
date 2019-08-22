package net.rcarz.kantime;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class DBconnections {

  /**
   * read data from database using query.
   * 
   * @param query query to execute
   * @return object
   * @throws IOException  IO Exception
   * @throws SQLException SQL Exception
   */
  public Object[][] readDataFromDatabaseUsingQuery(String Casenumber)
      throws IOException, SQLException {

    int totalRows = 0;
    int totalColumns = 0;
    int ci = 0;
    int cj = 0;

    String serverIP = "192.168.1.23";
    String databaseName = "Production_KPC";
    String userName = "medicaresqluser";
    String password = "kantime_123";
    String port = "1433";

    /*
     * Create a variable for the connection string using datas.
     */
    String connectionUrl = "jdbc:sqlserver://" + serverIP + ":" + port + ";databaseName="
        + databaseName + ";user=" + userName + ";password=" + password + "";

    Connection con = DriverManager.getConnection(connectionUrl);
    Statement stmt = con.createStatement();
    String sql = "select isClosed, isReadyToClose from CaseMaster where CaseNo = '" + Casenumber
        + "' and (isnull(isClosed,0)=1 or isnull(isReadyToClose,0)=1)";

    /*
     * total column
     */
    ResultSet rs = stmt.executeQuery(sql);
    ResultSetMetaData rsmd = rs.getMetaData();

    // totalColumns = rsmd.getColumnCount();
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
}
