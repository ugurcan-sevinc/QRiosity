package com.ugrcaan.qriosity.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ugrcaan.qriosity.model.SavedLink

@Entity(tableName = "saved_links")
data class SavedLinkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val url: String,
    val createdAt: Long = System.currentTimeMillis()
)

fun SavedLinkEntity.toModel(): SavedLink = SavedLink(
    id = id,
    name = name,
    link = url,
    createdAt = createdAt
)
