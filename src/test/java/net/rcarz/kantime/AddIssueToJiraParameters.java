package net.rcarz.kantime;

import java.util.ArrayList;
import java.util.List;

import net.rcarz.jiraclient.BasicCredentials;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.JiraException;
import net.rcarz.jiraclient.agile.AgileClient;
import net.rcarz.jiraclient.agile.Board;

import org.testng.annotations.Test;
import net.rcarz.jiraclient.Field;

public class AddIssueToJiraParameters {

  @SuppressWarnings({ "serial", "unchecked" })
  @Test()
  public void f() throws JiraException {

    BasicCredentials creds = new BasicCredentials("nick@kanrad.com", "WORF3aXXRSjXmejgZL7A9EF2");
    JiraClient jira = new JiraClient("https://kanrad.atlassian.net", creds);
    AgileClient ag = new AgileClient(jira);

    String caseNumber = System.getProperty("CaseNumber");
    String Assignee = System.getProperty("Developer");
    String qa = System.getProperty("QA");
    String JiraEpic = System.getProperty("JiraEpic");
    String HHTeam = JiraEpic.split(":")[1];
    String fixVersion = System.getProperty("Version");

    JiraEpic = JiraEpic.split(":")[0];

    try {
      /* Create a new issue. */
      @SuppressWarnings({ "rawtypes" })
      Issue newIssue =
          jira.createIssue("HH", "Task").field(Field.SUMMARY, "Live Case - " + caseNumber)
              .field(Field.DESCRIPTION, "Live Case - " + caseNumber).field(Field.ASSIGNEE, Assignee)
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
              }).field(Field.TICKET, caseNumber).field(Field.EPICLINK, JiraEpic).execute();

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
        if (ss.getName().contains(HHTeam.split(" ")[0])) {
          ss.moveIssueToSprint(newIssue.toString(), String.valueOf(ss.getId()));
          System.out.println("moved issue " + newIssue + "to sprint " + ss.getName());
        }
      }

      /* Create sub-task */
      @SuppressWarnings("rawtypes")
      Issue subtask =
          newIssue.createSubtask().field(Field.SUMMARY, "QA Live Case Task - " + caseNumber)
              .field(Field.DESCRIPTION, "QA Live Case Task - " + caseNumber)
              .field(Field.ASSIGNEE, qa).field(Field.FIX_VERSIONS, new ArrayList() {
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
              }).field(Field.TICKET, caseNumber).execute();
      System.out.println("subtask:" + subtask);

    } catch (JiraException ex) {
      System.err.println(ex.getMessage());

      if (ex.getCause() != null)
        System.err.println(ex.getCause().getMessage());
    }
  }
}
