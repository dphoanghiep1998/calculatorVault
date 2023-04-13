package com.neko.hiepdph.calculatorvault.data.services

import com.neko.hiepdph.calculatorvault.data.database.dao.BookmarkDao
import javax.inject.Inject

class BookmarkService @Inject constructor(val bookmarkDao: BookmarkDao)