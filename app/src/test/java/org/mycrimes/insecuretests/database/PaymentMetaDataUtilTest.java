package org.mycrimes.insecuretests.database;

import com.google.protobuf.ByteString;

import org.junit.Test;
import org.mycrimes.insecuretests.payments.proto.PaymentMetaData;
import org.mycrimes.insecuretests.util.Util;

import static org.junit.Assert.assertArrayEquals;

public final class PaymentMetaDataUtilTest {

  @Test
  public void extract_single_public_key() {
    byte[] random = Util.getSecretBytes(32);
    byte[] bytes  = PaymentMetaDataUtil.receiptPublic(PaymentMetaData.newBuilder()
                                                                     .setMobileCoinTxoIdentification(PaymentMetaData.MobileCoinTxoIdentification.newBuilder()
                                                                                                                                                .addPublicKey(ByteString.copyFrom(random))).build());
    assertArrayEquals(random, bytes);
  }
}
