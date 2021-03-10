package app.doggy.la_original

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class Legend(
    @PrimaryKey open var id: String = UUID.randomUUID().toString(),
    open var title: String = "",
    open var iconId: Int = 0,
    open var satisfaction: Int = 0,
    open var chartFormat: Int = 0,
    open var ratio: Float = 0f,
    open var createdAt: Date = Date(System.currentTimeMillis())
): RealmObject()