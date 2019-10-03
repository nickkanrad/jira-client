package net.rcarz.kantime;

import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
import net.rcarz.jiraclient.BasicCredentials;
import net.rcarz.jiraclient.CustomFieldOption;
import net.rcarz.jiraclient.Field;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.JiraException;

public class AllTeamPendingTaksToBeClosed {

  @Test
  public void f() throws JiraException {
    BasicCredentials creds = new BasicCredentials("nick@kanrad.com", "WORF3aXXRSjXmejgZL7A9EF2");
    JiraClient jira = new JiraClient("https://kanrad.atlassian.net", creds);
    BOT autobot = new BOT();

    try {
      String serverIP = "192.168.1.18";
      String databaseName = "NitishDemo_No_Drop";
      String userName = "medicaresqluser";
      String password = "kantime_123";
      String port = "1433";

      /*
       * Create a variable for the connection string using datas.
       */
      String connectionUrl = "jdbc:sqlserver://" + serverIP + ":" + port + ";databaseName="
          + databaseName + ";user=" + userName + ";password=" + password + "";

      Connection conn = DriverManager.getConnection(connectionUrl);
      Statement st = conn.createStatement();

      // delete all records from table
      st.executeUpdate("delete from JIRA");

      /* Search for issues */
      Issue.SearchResult srr = jira.searchIssues("filter=10413");
      System.out.println("Total: " + srr.total);

      for (int start = 0; start < srr.total + 100; start++) {
        Issue.SearchResult sr = jira.searchIssues("filter=10413", null, start + 100, start);
        for (Issue i : sr.issues) {

          String QA = "";
          String Team = "";
          String JIRAID = "";
          String Summary = "";
          String Dev = "";
          String QaTaskSummary = "";

          // System.out.println("issue: " + i);

          QaTaskSummary = i.getSummary();
          // System.out.println("QaTaskSummary: " + QaTaskSummary);

          // get parent
          Issue parent = jira.getIssue(i.getParent().toString());
          // System.out.println("parent: " + parent);

          if (i.getAssignee() != null) {
            QA = i.getAssignee().toString();
            // System.out.println("QA: " + QA);
          }

          JIRAID = parent.getKey();
          // System.out.println("parent JIRA:" + JIRAID);

          Summary = parent.getSummary();
          // System.out.println("Summary: " + Summary);

          if (parent.getAssignee() != null) {
            Dev = parent.getAssignee().toString();
            // System.out.println("Dev: " + Dev);
          }

          List<CustomFieldOption> cfselect = Field.getResourceArray(CustomFieldOption.class,
              parent.getField("customfield_10055"), jira.getRestClient());
          for (CustomFieldOption cfo : cfselect) {
            Team = cfo.getValue();
            // System.out.println("Team: " + Team);
          }

          // insert new records
          String staaa = "INSERT INTO JIRA VALUES ('" + QA + "', '" + Team + "', '" + JIRAID
              + "', '" + Summary + "','','', '" + Dev + "', '" + QaTaskSummary + "')";
          System.out.println(staaa);

          st.executeUpdate(staaa);
          System.out.println("***********************************");
        }
        start = start + 100;
      }
      st.executeUpdate("\n" + "delete from JIRA where id in (\n" + "SELECT a.id\n"
          + "    FROM JIRA a\n" + "        INNER JOIN\n" + "        (\n"
          + "         SELECT MAX(ID) AS ID, JIRAID \n" + "             FROM JIRA      \n"
          + "                 GROUP BY JIRAID \n" + "                    HAVING COUNT(JIRAID) > 1\n"
          + "        ) b\n" + "    ON a.ID < b.ID AND a.JIRAID=b.JIRAID)");
      conn.close();

      /*************************************************
       * GET ACCEPTANCE TASKS PENDING
       ************************************************/

      /* Search for issues */
      Issue.SearchResult AcceptancePending = jira.searchIssues("filter=10424");
      System.out.println("Total: " + AcceptancePending.total);

      for (Issue issue : AcceptancePending.issues) {

        String ChatRoom = "";
        switch (issue.getAssignee().toString()) {
          case "dwilliams":
            ChatRoom =
                "https://chat.googleapis.com/v1/spaces/-6u3zAAAAAE/messages?key=AIzaSyDdI0hCZtE6vySjMm-WEfRq3CPzqKqqsHI&token=kFtCFXHPqycU9YDd7rtqmwnN9SAa3BOvxVqbU_iYGX8%3D";
            break;
          case "alewis":
            ChatRoom =
                "https://chat.googleapis.com/v1/spaces/h-23zAAAAAE/messages?key=AIzaSyDdI0hCZtE6vySjMm-WEfRq3CPzqKqqsHI&token=7TqRRaPq_54oiOB0n1f0zStwqeoGbFcDEjpd47UGWtU%3D";
            break;
        }
        autobot.sendPost(ChatRoom,
            "Acceptance task is pending: https://kanrad.atlassian.net/browse/" + issue.getKey());
      }
    } catch (Exception e) {
      System.err.println("Got an exception! ");
      System.err.println(e.getMessage());
    }

  }
}
