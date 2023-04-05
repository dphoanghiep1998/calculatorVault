package com.neko.hiepdph.calculatorvault.data.services

import com.neko.hiepdph.calculatorvault.data.database.dao.HistoryDao
import javax.inject.Inject

class HistoryLocalService @Inject constructor(val historyDao: HistoryDao)