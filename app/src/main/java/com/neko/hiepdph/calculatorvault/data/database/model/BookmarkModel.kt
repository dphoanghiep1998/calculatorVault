package com.neko.hiepdph.calculatorvault.data.database.model

import com.neko.hiepdph.calculatorvault.data.database.entity.BookmarkEntity

class BookmarkModel(
    var id: Int = -1, var name: String, var url: String, var imageIcon: String, var persistent: Boolean
) {
    fun toBookmarkEntity(): BookmarkEntity {
        val bookmarkId = if (id == -1) 0 else id
        return BookmarkEntity(
            id = bookmarkId, name, url, imageIcon, persistent

        )
    }

    fun BookmarkEntity.toBookmarkModel(): BookmarkModel {
        return BookmarkModel(
            id = id, name, url, imageIcon, persistent
        )
    }
}