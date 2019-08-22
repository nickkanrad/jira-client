package net.rcarz.kantime;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.DataProvider;
import net.rcarz.jiraclient.BasicCredentials;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.JiraException;
import net.rcarz.jiraclient.agile.AgileClient;
import net.rcarz.jiraclient.agile.Board;

import org.testng.annotations.Test;
import net.rcarz.jiraclient.Field;

public class AddIssueToJira {

  @DataProvider(name = "JIRAdp")
  public static Object[][] credentials() throws Exception {
    ReadExcel excels = new ReadExcel();
    Object[][] returnObject = excels.getEntireExcelSheetData("JIRA.xlsx", "Sheet1");
    return returnObject;
  }

  @SuppressWarnings({ "serial", "unchecked" })
  @Test(dataProvider = "JIRAdp")
  public void f(String CaseNumber, String Assignee, final String HHTeam, String JiraEpic,
      final String fixVersion, String QA, String jiraQaName) throws JiraException {

    BasicCredentials creds = new BasicCredentials("nick@kanrad.com", "WORF3aXXRSjXmejgZL7A9EF2");
    JiraClient jira = new JiraClient("https://kanrad.atlassian.net", creds);

    AgileClient ag = new AgileClient(jira);

    try {
      /* Create a new issue. */
      @SuppressWarnings({ "rawtypes" })
      Issue newIssue =
          jira.createIssue("HH", "Task").field(Field.SUMMARY, "Live Case - " + CaseNumber)
              .field(Field.DESCRIPTION, "Live Case - " + CaseNumber).field(Field.ASSIGNEE, Assignee)
              .field(Field.FIX_VERSIONS, new ArrayList() {
                {
                  add(fixVersion);
                }
              }).field(Field.CUSTOMFIELD_HHTEAM, new ArrayList() {
                {
                  add(HHTeam);
                }
              }).field(Field.LABELS, new ArrayList() {
                {
                  add("LiveCase");
                  add("ScopeCreep");
                }
              }).field(Field.TICKET, CaseNumber).field(Field.EPICLINK, JiraEpic).execute();

      System.out.println("Parent task:" + newIssue);

      Board boards = null;

      switch (HHTeam) {
        case "Nithya":
          boards = ag.getBoard(28);
          break;
        case "Praful":
          boards = ag.getBoard(30);
          break;
        case "Syed":
          boards = ag.getBoard(8);
          break;
        case "Shruthi (SSRS)":
          boards = ag.getBoard(34);
          break;
        case "Shaji":
          boards = ag.getBoard(33);
          break;
        case "Rasheed":
          boards = ag.getBoard(32);
          break;
        case "Devender":
          boards = ag.getBoard(25);
          break;
      }

      /*
       * move the issue to active sprint
       */
      List<net.rcarz.jiraclient.agile.Sprint> xx = boards.getActiveSprints();
      for (net.rcarz.jiraclient.agile.Sprint ss : xx) {
        if (ss.getName().contains(HHTeam)) {
          ss.moveIssueToSprint(newIssue.toString(), String.valueOf(ss.getId()));
          System.out.println("moved issue " + newIssue + "to sprint " + ss.getName());
        }
      }

      /* Create sub-task */
      @SuppressWarnings("rawtypes")
      Issue subtask =
          newIssue.createSubtask().field(Field.SUMMARY, "QA Live Case Task - " + CaseNumber)
              .field(Field.DESCRIPTION, "QA Live Case Task - " + CaseNumber)
              .field(Field.ASSIGNEE, jiraQaName).field(Field.FIX_VERSIONS, new ArrayList() {
                {
                  add(fixVersion);
                }
              }).field(Field.CUSTOMFIELD_HHTEAM, new ArrayList() {
                {
                  add(HHTeam);
                }
              }).field(Field.LABELS, new ArrayList() {
                {
                  add("LiveCase");
                  add("ScopeCreep");
                }
              }).field(Field.TICKET, CaseNumber).execute();
      System.out.println("subtask:" + subtask);

    } catch (JiraException ex) {
      System.err.println(ex.getMessage());

      if (ex.getCause() != null)
        System.err.println(ex.getCause().getMessage());
    }
  }
}
