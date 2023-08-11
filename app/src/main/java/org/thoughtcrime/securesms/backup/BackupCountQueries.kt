package org.mycrimes.insecuretests.backup

import org.mycrimes.insecuretests.database.AttachmentTable
import org.mycrimes.insecuretests.database.GroupReceiptTable
import org.mycrimes.insecuretests.database.MessageTable

/**
 * Queries used by backup exporter to estimate total counts for various complicated tables.
 */
object BackupCountQueries {

  const val mmsCount: String = "SELECT COUNT(*) FROM ${MessageTable.TABLE_NAME} WHERE ${MessageTable.EXPIRES_IN} <= 0 AND ${MessageTable.VIEW_ONCE} <= 0"

  @get:JvmStatic
  val groupReceiptCount: String = """
      SELECT COUNT(*) FROM ${GroupReceiptTable.TABLE_NAME} 
      INNER JOIN ${MessageTable.TABLE_NAME} ON ${GroupReceiptTable.TABLE_NAME}.${GroupReceiptTable.MMS_ID} = ${MessageTable.TABLE_NAME}.${MessageTable.ID} 
      WHERE ${MessageTable.TABLE_NAME}.${MessageTable.EXPIRES_IN} <= 0 AND ${MessageTable.TABLE_NAME}.${MessageTable.VIEW_ONCE} <= 0
  """

  @get:JvmStatic
  val attachmentCount: String = """
      SELECT COUNT(*) FROM ${AttachmentTable.TABLE_NAME} 
      INNER JOIN ${MessageTable.TABLE_NAME} ON ${AttachmentTable.TABLE_NAME}.${AttachmentTable.MMS_ID} = ${MessageTable.TABLE_NAME}.${MessageTable.ID} 
      WHERE ${MessageTable.TABLE_NAME}.${MessageTable.EXPIRES_IN} <= 0 AND ${MessageTable.TABLE_NAME}.${MessageTable.VIEW_ONCE} <= 0
  """
}
