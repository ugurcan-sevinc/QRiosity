package com.ugrcaan.qriosity.data

import com.ugrcaan.qriosity.data.local.SavedLinkDao
import com.ugrcaan.qriosity.data.local.SavedLinkEntity
import com.ugrcaan.qriosity.data.local.toModel
import com.ugrcaan.qriosity.model.SavedLink
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SavedLinkRepository(private val savedLinkDao: SavedLinkDao) {

    val savedLinks: Flow<List<SavedLink>> =
        savedLinkDao.observeSavedLinks().map { entities ->
            entities.map { it.toModel() }
        }

    suspend fun saveLink(name: String, url: String) {
        val entity = SavedLinkEntity(name = name, url = url)
        savedLinkDao.insertLink(entity)
    }

    suspend fun deleteLink(id: Long) {
        savedLinkDao.deleteLink(id)
    }
}
