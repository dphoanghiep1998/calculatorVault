package com.neko.hiepdph.calculatorvault.data.services

import com.neko.hiepdph.calculatorvault.data.database.dao.FileVaultItemDao
import javax.inject.Inject

class FileVaultService @Inject constructor(val fileVaultItemDao: FileVaultItemDao) {
}