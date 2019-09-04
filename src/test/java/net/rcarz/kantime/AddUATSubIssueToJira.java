package net.rcarz.kantime;

import java.util.ArrayList;
import java.util.List;

import net.rcarz.jiraclient.BasicCredentials;
import net.rcarz.jiraclient.CustomFieldOption;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.JiraException;
import net.rcarz.jiraclient.Version;

import org.testng.annotations.Test;
import net.rcarz.jiraclient.Field;

public class AddUATSubIssueToJira {

  Issue exisitingIssue = null;
  Issue ParentIssue = null;
  String qa = "";
  String team = "";

  @SuppressWarnings({ "serial", "unchecked" })
  @Test
  public void f() throws JiraException {

    BasicCredentials creds = new BasicCredentials("nick@kanrad.com", "WORF3aXXRSjXmejgZL7A9EF2");
    JiraClient jira = new JiraClient("https://kanrad.atlassian.net", creds);

    String Key = System.getProperty("TaskKey");

    try {
      exisitingIssue = jira.getIssue(Key);
      System.out.println("Given task: " + exisitingIssue);

      try {
        ParentIssue = jira.getIssue(exisitingIssue.getParent().toString());
      } catch (Exception e) {
        ParentIssue = exisitingIssue;
      }

      System.out.println("Parent task: " + ParentIssue);
      List<CustomFieldOption> cfselect = Field.getResourceArray(CustomFieldOption.class,
          ParentIssue.getField("customfield_10055"), jira.getRestClient());
      for (CustomFieldOption cfo : cfselect) {
        team = cfo.getValue();
      }

      switch (team) {
        case "Devender":
          qa = "dwilliams";
          break;
        case "Nithya":
          qa = "dwilliams";
          break;
        case "Praful":
          qa = "gbrown";
          break;
        case "Rasheed":
          qa = "alewis";
          break;
        case "Shaji":
          qa = "dwilliams";
          break;
        case "Shruthi (SSRS)":
          qa = "alewis";
          break;
        case "Syed":
          qa = "alewis";
          break;
        case "Web":
          qa = "alewis";
          break;
        case "Onboarding":
          qa = "dwilliams";
          break;
        default:
          qa = "";
          break;
      }

      /* Create sub-task - TEST CASE REVIEW */
      @SuppressWarnings("rawtypes")
      Issue testCaseReviewSubtask = ParentIssue.createSubtask()
          .field(Field.SUMMARY, "UAT " + Key + " : " + ParentIssue.getSummary())
          .field(Field.DESCRIPTION, "UAT").field(Field.ASSIGNEE, "")
          .field(Field.FIX_VERSIONS, new ArrayList() {
            {
              List<Version> cfselec = ParentIssue.getFixVersions();
              for (Version cfo : cfselec) {
                add(cfo.getName());
              }
            }
          }).field(Field.CUSTOMFIELD_HHTEAM, new ArrayList() {
            {
              for (CustomFieldOption cfo : cfselect) {
                add(cfo.getValue());
              }
            }
          }).field(Field.LABELS, new ArrayList() {
            {
              addAll(exisitingIssue.getLabels());
              add("QA_Task");
              add("UAT");
            }
          }).execute();
      System.out.println("UAT Subtask: " + testCaseReviewSubtask);

    } catch (JiraException ex) {
      System.err.println(ex.getMessage());

      if (ex.getCause() != null) {
        System.err.println(ex.getCause().getMessage());
      }
    }
  }
}
