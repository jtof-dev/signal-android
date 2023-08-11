package org.mycrimes.insecuretests.database.model

import org.signal.core.util.StringSerializer
import org.mycrimes.insecuretests.database.model.databaseprotos.BodyRangeList
import org.mycrimes.insecuretests.util.Base64

object BodyRangeListSerializer : StringSerializer<BodyRangeList> {
  override fun serialize(data: BodyRangeList): String = Base64.encodeBytes(data.toByteArray())
  override fun deserialize(data: String): BodyRangeList = BodyRangeList.parseFrom(Base64.decode(data))
}

fun BodyRangeList.serialize(): String {
  return BodyRangeListSerializer.serialize(this)
}
