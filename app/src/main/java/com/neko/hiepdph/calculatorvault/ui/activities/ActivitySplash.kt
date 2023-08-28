package com.neko.hiepdph.calculatorvault.ui.activities

import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.neko.hiepdph.calculatorvault.CustomApplication
import com.neko.hiepdph.calculatorvault.common.extensions.config
import com.neko.hiepdph.calculatorvault.config.TemporaryTimeDeletion
import com.neko.hiepdph.calculatorvault.viewmodel.AppViewModel

class ActivitySplash : AppCompatActivity() {
    private val viewModel by viewModels<AppViewModel>()
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
         if (CustomApplication.app.firstTimeOpen && config.temporaryFileDeletionTime == TemporaryTimeDeletion.EXIT_APP) {
            viewModel.deleteMultipleFolder(mutableListOf(config.decryptFolder.path) , onResult = { listOfFileDeletedSuccess, listOfFileDeletedFailed ->  }, onProgress = {})
        }
    }
}