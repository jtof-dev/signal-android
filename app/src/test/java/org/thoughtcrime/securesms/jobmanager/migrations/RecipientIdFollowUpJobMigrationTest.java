package org.mycrimes.insecuretests.jobmanager.migrations;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mycrimes.insecuretests.jobmanager.JsonJobData;
import org.mycrimes.insecuretests.jobmanager.Job;
import org.mycrimes.insecuretests.jobmanager.JobMigration.JobData;
import org.mycrimes.insecuretests.jobs.FailingJob;
import org.mycrimes.insecuretests.jobs.SendDeliveryReceiptJob;
import org.mycrimes.insecuretests.recipients.Recipient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

public class RecipientIdFollowUpJobMigrationTest {

  @Rule
  public MockitoRule rule = MockitoJUnit.rule();

  @Mock
  private MockedStatic<Recipient> recipientMockedStatic;

  @Mock
  private MockedStatic<Job.Parameters> jobParametersMockedStatic;

  @Test
  public void migrate_sendDeliveryReceiptJob_good() throws Exception {
    JobData testData = new JobData("SendDeliveryReceiptJob", null, new JsonJobData.Builder().putString("recipient", "1")
                                                                                            .putLong("message_id", 1)
                                                                                            .putLong("timestamp", 2)
                                                                                            .serialize());
    RecipientIdFollowUpJobMigration subject   = new RecipientIdFollowUpJobMigration();
    JobData                         converted = subject.migrate(testData);

    assertEquals("SendDeliveryReceiptJob", converted.getFactoryKey());
    assertNull(converted.getQueueKey());

    JsonJobData data = JsonJobData.deserialize(converted.getData());
    assertEquals("1", data.getString("recipient"));
    assertEquals(1, data.getLong("message_id"));
    assertEquals(2, data.getLong("timestamp"));

    new SendDeliveryReceiptJob.Factory().create(mock(Job.Parameters.class), converted.getData());
  }

  @Test
  public void migrate_sendDeliveryReceiptJob_bad() throws Exception {
    JobData testData = new JobData("SendDeliveryReceiptJob", null, new JsonJobData.Builder().putString("recipient", "1")
                                                                                            .serialize());
    RecipientIdFollowUpJobMigration subject   = new RecipientIdFollowUpJobMigration();
    JobData                         converted = subject.migrate(testData);

    assertEquals("FailingJob", converted.getFactoryKey());
    assertNull(converted.getQueueKey());

    new FailingJob.Factory().create(mock(Job.Parameters.class), converted.getData());
  }
}
