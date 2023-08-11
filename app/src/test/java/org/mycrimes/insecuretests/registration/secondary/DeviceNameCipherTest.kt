package org.mycrimes.insecuretests.registration.secondary

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Test
import org.mycrimes.insecuretests.crypto.IdentityKeyUtil
import org.mycrimes.insecuretests.database.loaders.DeviceListLoader
import org.mycrimes.insecuretests.devicelist.protos.DeviceName
import java.nio.charset.Charset

class DeviceNameCipherTest {

  @Test
  fun encryptDeviceName() {
    val deviceName = "xXxCoolDeviceNamexXx"
    val identityKeyPair = IdentityKeyUtil.generateIdentityKeyPair()

    val encryptedDeviceName = DeviceNameCipher.encryptDeviceName(deviceName.toByteArray(Charset.forName("UTF-8")), identityKeyPair)

    val plaintext = DeviceListLoader.decryptName(DeviceName.ADAPTER.decode(encryptedDeviceName), identityKeyPair)

    assertThat(String(plaintext, Charset.forName("UTF-8")), `is`(deviceName))
  }
}
