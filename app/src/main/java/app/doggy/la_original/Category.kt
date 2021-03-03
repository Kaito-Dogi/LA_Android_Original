package app.doggy.la_original

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class Category(
    @PrimaryKey open var id: String = UUID.randomUUID().toString(),
    open var name: String = "",
    open var iconId: Int = 0,
    open var createdAt: Date = Date(System.currentTimeMillis())
): RealmObject()