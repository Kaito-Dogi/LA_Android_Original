package app.doggy.la_original

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class Record(
    @PrimaryKey open var id: String = UUID.randomUUID().toString(),
    open var satisfaction: Int = 0,
    open var amount: Int = 0,
    open var title: String = "",
    open var comment: String = "",
    open var date: Int = 0,
    open var categoryId: String = "",
    open var iconId: Int = 0,
    open var createdAt: Date = Date(System.currentTimeMillis())
): RealmObject()