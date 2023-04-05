package com.neko.hiepdph.calculatorvault.data.services

import com.neko.hiepdph.calculatorvault.data.database.dao.HistoryDao
import com.neko.hiepdph.calculatorvault.data.database.dao.NoteDao
import javax.inject.Inject

class NoteLocalService @Inject constructor(val noteDao: NoteDao)