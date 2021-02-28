package app.doggy.la_original

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class Record(
    @PrimaryKey open var id: String = UUID.randomUUID().toString(),
    open var actualValue: Int = 0,
    open var perceivedValue: Int = 0,
    open var comment: String = "",
    open var createdAt: Date = Date(System.currentTimeMillis())
): RealmObject()