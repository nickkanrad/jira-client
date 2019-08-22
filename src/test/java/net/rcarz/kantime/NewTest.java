package net.rcarz.kantime;

import org.testng.annotations.Test;
import java.util.List;
import net.rcarz.jiraclient.BasicCredentials;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.JiraException;
import net.rcarz.jiraclient.RestClient;
import net.rcarz.jiraclient.agile.AgileClient;
import net.rcarz.jiraclient.agile.AgileResource;
import net.rcarz.jiraclient.agile.Board;
import net.rcarz.jiraclient.greenhopper.Epic;
import net.rcarz.jiraclient.greenhopper.GreenHopperClient;
import net.rcarz.jiraclient.greenhopper.Marker;
import net.rcarz.jiraclient.greenhopper.RapidView;
import net.rcarz.jiraclient.greenhopper.Sprint;
import net.rcarz.jiraclient.greenhopper.SprintIssue;
import net.rcarz.jiraclient.greenhopper.SprintReport;
import net.sf.json.JSONObject;

public class NewTest {

  @Test
  public void f() throws JiraException {
    BasicCredentials creds = new BasicCredentials("nick@kanrad.com", "WORF3aXXRSjXmejgZL7A9EF2");
    JiraClient jira = new JiraClient("https://kanrad.atlassian.net", creds);
    AgileClient ag = new AgileClient(jira);

    String team = "Nithya";
    Board boards = null;

    switch (team) {
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

    List<net.rcarz.jiraclient.agile.Sprint> xx = boards.getActiveSprints();
    for (net.rcarz.jiraclient.agile.Sprint ss : xx) {
      if (ss.getName().contains(team)) {
        System.out.println(ss.getId());
        System.out.println(ss.getName());
        ss.moveIssueToSprint("HH-18963", String.valueOf(ss.getId()));
      }
    }

  }

}
