package net.rcarz.kantime;

import java.io.IOException;
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

public class AddSubIssueToJira {

  Issue exisitingIssue = null;
  Issue ParentIssue = null;
  String qa = "";
  String team = "";

  @SuppressWarnings({ "serial", "unchecked" })
  @Test
  public void f() throws JiraException {

    BasicCredentials creds = new BasicCredentials("nick@kanrad.com", "WORF3aXXRSjXmejgZL7A9EF2");
    JiraClient jira = new JiraClient("https://kanrad.atlassian.net", creds);
    BOT autobot = new BOT();

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

      /* Create sub-task - ACCEPT FOR TESTING */
      @SuppressWarnings("rawtypes")
      Issue acceptanceSubtask = ParentIssue.createSubtask()
          .field(Field.SUMMARY, "Accept For Testing " + Key + " : " + exisitingIssue.getSummary())
          .field(Field.DESCRIPTION,
              "Check and accept for testing if it meets the story/requirements/design "
                  + "doc/KPC along with better user experience and brings value to product.")
          .field(Field.ASSIGNEE, qa).field(Field.FIX_VERSIONS, new ArrayList() {
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
              add("AcceptForTesting");
            }
          }).execute();
      System.out.println("Acceptance Subtask: " + acceptanceSubtask);

      /* Create sub-task - TEST CASE */
      @SuppressWarnings("rawtypes")
      Issue testCaseSubtask = ParentIssue.createSubtask()
          .field(Field.SUMMARY, "Test Case Writing " + Key + " : " + exisitingIssue.getSummary())
          .field(Field.DESCRIPTION, "Write test cases for the mentioned task")
          .field(Field.ASSIGNEE, "").field(Field.FIX_VERSIONS, new ArrayList() {
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
              add("Testcase");
            }
          }).execute();
      System.out.println("Test case Subtask: " + testCaseSubtask);

      /* Create sub-task - TEST CASE REVIEW */
      @SuppressWarnings("rawtypes")
      Issue testCaseReviewSubtask = ParentIssue.createSubtask()
          .field(Field.SUMMARY, "Test Case Review " + Key + " : " + exisitingIssue.getSummary())
          .field(Field.DESCRIPTION, "Review the test cases for the mentioned task")
          .field(Field.ASSIGNEE, "").field(Field.FIX_VERSIONS, new ArrayList() {
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
              add("TestCaseReview");
            }
          }).execute();
      System.out.println("Test case review Subtask: " + testCaseReviewSubtask);

      /* Create sub-task - TESTING */
      @SuppressWarnings("rawtypes")
      Issue testingSubtask = ParentIssue.createSubtask()
          .field(Field.SUMMARY, "Testing of the task " + Key + " : " + exisitingIssue.getSummary())
          .field(Field.DESCRIPTION, "Test the task in detail").field(Field.ASSIGNEE, "")
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
              add("Testing");
            }
          }).execute();
      System.out.println("Testing Subtask: " + testingSubtask);

      /* Create sub-task - DATA CORRECTION/PERMISSION VERIFICATION */
      @SuppressWarnings("rawtypes")
      Issue dcPermissionSubtask = ParentIssue.createSubtask()
          .field(Field.SUMMARY,
              "verify the data correction / permission associated to the task " + Key + " : "
                  + exisitingIssue.getSummary())
          .field(Field.DESCRIPTION,
              "Verify the data correction / permission associated to the task, if any. "
                  + "The verified data correction should be mentioned here in comments.")
          .field(Field.ASSIGNEE, "").field(Field.FIX_VERSIONS, new ArrayList() {
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
              add("dataCorrectionVerification");
            }
          }).execute();
      System.out.println("DC Subtask: " + dcPermissionSubtask);

      /* Create sub-task - TESTING STANDARD AND CLOSURE */
      @SuppressWarnings("rawtypes")
      Issue testingstandardClosureSubtask = ParentIssue.createSubtask()
          .field(Field.SUMMARY,
              "Verify the testing standard and closure details " + Key + " : "
                  + exisitingIssue.getSummary())
          .field(Field.DESCRIPTION,
              "Verify the testing standard and closure details for the task."
                  + " Reverify that the story/requirement and the KPC details "
                  + "will be satisfied with this task.")
          .field(Field.ASSIGNEE, "").field(Field.FIX_VERSIONS, new ArrayList() {
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
              add("Testing_standards");
            }
          }).execute();
      System.out.println("Closure Subtask: " + testingstandardClosureSubtask);

      String ChatRoom = "";
      switch (qa) {
        case "dwilliams":
          ChatRoom =
              "https://chat.googleapis.com/v1/spaces/-6u3zAAAAAE/messages?key=AIzaSyDdI0hCZtE6vySjMm-WEfRq3CPzqKqqsHI&token=kFtCFXHPqycU9YDd7rtqmwnN9SAa3BOvxVqbU_iYGX8%3D";
          break;
        case "alewis":
          ChatRoom =
              "https://chat.googleapis.com/v1/spaces/h-23zAAAAAE/messages?key=AIzaSyDdI0hCZtE6vySjMm-WEfRq3CPzqKqqsHI&token=7TqRRaPq_54oiOB0n1f0zStwqeoGbFcDEjpd47UGWtU%3D";
          break;
      }
      autobot.sendPost(ChatRoom, "New release tasks are added:" + acceptanceSubtask);

    } catch (JiraException ex) {
      System.err.println(ex.getMessage());

      if (ex.getCause() != null) {
        System.err.println(ex.getCause().getMessage());
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
