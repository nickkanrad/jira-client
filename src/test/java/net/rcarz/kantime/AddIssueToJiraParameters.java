package net.rcarz.kantime;

import java.io.IOException;
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

    BOT autobot = new BOT();
    BasicCredentials creds = new BasicCredentials("nick@kanrad.com", "WORF3aXXRSjXmejgZL7A9EF2");
    JiraClient jira = new JiraClient("https://kanrad.atlassian.net", creds);
    AgileClient ag = new AgileClient(jira);

    String caseNumber = System.getProperty("CaseNumber");
    String Assignee = System.getProperty("Developer");
    String qa = System.getProperty("QA");
    String JiraEpic = System.getProperty("JiraEpic");
    String HHTeam = JiraEpic.split(":")[1];
    String fixVersion = System.getProperty("Version");
    String ChatRoom = "";
    JiraEpic = JiraEpic.split(":")[0];

    String[] Case = caseNumber.split("\\s*,\\s*");

    for (String caseno : Case) {

      try {
        /* Create a new issue. */
        @SuppressWarnings({ "rawtypes" })
        Issue newIssue =
            jira.createIssue("HH", "Task").field(Field.SUMMARY, "Live Case - " + caseno)
                .field(Field.DESCRIPTION, "Live Case - " + caseno).field(Field.ASSIGNEE, Assignee)
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
                }).field(Field.TICKET, caseno).field(Field.EPICLINK, JiraEpic).execute();

        System.out.println("Parent task:" + newIssue);

        Board boards = null;

        switch (HHTeam) {
          case "Nithya":
            boards = ag.getBoard(28);
            ChatRoom =
                "https://chat.googleapis.com/v1/spaces/AAAA5STe_Ys/messages?key=AIzaSyDdI0hCZtE6vySjMm-WEfRq3CPzqKqqsHI&token=sQ6gB8rZK8y2ew2HCi2YAV3byEEC_rO6TxznYb_54xk%3D";
            break;
          case "Praful":
            boards = ag.getBoard(30);
            break;
          case "Syed":
            boards = ag.getBoard(8);
            ChatRoom =
                "https://chat.googleapis.com/v1/spaces/AAAAoIik-8s/messages?key=AIzaSyDdI0hCZtE6vySjMm-WEfRq3CPzqKqqsHI&token=1bjRE_UBy0LKi412h-UwCsTA5klFpbyjVTZXGMfDC54%3D";
            break;
          case "Shruthi (SSRS)":
            boards = ag.getBoard(34);
            ChatRoom =
                "https://chat.googleapis.com/v1/spaces/AAAAULS9ugA/messages?key=AIzaSyDdI0hCZtE6vySjMm-WEfRq3CPzqKqqsHI&token=8ST6dOW-UdhFRKjRgRSORlwCwCG_xy8xgJzpA7_2hcY%3D";
            break;
          case "Shaji":
            boards = ag.getBoard(33);
            ChatRoom =
                "https://chat.googleapis.com/v1/spaces/AAAATuJwdBE/messages?key=AIzaSyDdI0hCZtE6vySjMm-WEfRq3CPzqKqqsHI&token=yjBuKcfxbg-jKhS4yozpay5pO-sizhwRPflI7Gq1z04%3D";
            break;
          case "Rasheed":
            boards = ag.getBoard(32);
            ChatRoom =
                "https://chat.googleapis.com/v1/spaces/AAAA6ZwFqhc/messages?key=AIzaSyDdI0hCZtE6vySjMm-WEfRq3CPzqKqqsHI&token=G5MGfp71kopRNkZtoWwHst-gyrGNjvccAum8Mwv6s4A%3D";
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
            newIssue.createSubtask().field(Field.SUMMARY, "QA Live Case Task - " + caseno)
                .field(Field.DESCRIPTION, "QA Live Case Task - " + caseno).field(Field.ASSIGNEE, qa)
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
                }).field(Field.TICKET, caseno).execute();
        System.out.println("subtask:" + subtask);

        try {
          autobot.sendPost(ChatRoom, "Case#: " + caseno
              + " added to JIRA. Key: https://kanrad.atlassian.net/browse/" + newIssue);
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      } catch (JiraException ex) {
        System.err.println(ex.getMessage());

        if (ex.getCause() != null)
          System.err.println(ex.getCause().getMessage());
      }
    }
  }
}
