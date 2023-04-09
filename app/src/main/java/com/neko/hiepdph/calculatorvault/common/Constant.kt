package com.neko.hiepdph.calculatorvault.common


object Constant {
    const val KEY_PATTERN_LOCK = "KEY_PATTERN_LOCK"
    const val KEY_SECRET_PIN = "KEY_SECRET_PIN"
    const val KEY_TACTILE_FEEDBACK = "KEY_TACTILE_FEEDBACK"
    const val KEY_VISIBLE_PATTERN = "KEY_VISIBLE_PATTERN"
    const val KEY_SCREEN_OFF_ACTION = "KEY_SCREEN_OFF_ACTION"
    const val KEY_LOCK_WHEN_LEAVING_APP = "KEY_LOCK_WHEN_LEAVING_APP"
    const val KEY_FINGER_PRINT_DISPLAY = "KEY_FINGER_PRINT_DISPLAY"
    const val KEY_FINGER_PRINT_UNLOCK = "KEY_FINGER_PRINT_UNLOCK"
    const val KEY_LOCK_PRESS_TYPE = "KEY_LOCK_PRESS_TYPE"
    const val KEY_LOCK_TYPE = "KEY_LOCK_TYPE"
    const val KEY_IS_SHOULD_SHOW_HIDDEN = "KEY_IS_SHOULD_SHOW_HIDDEN"
    const val LOCALE = "LOCALE_SUGAR_V2"
    const val KEY_SET_LANG = "KEY_SET_LANG_SUGAR_V2"
    const val KEY_FAQ_CLICKED = "KEY_FAQ_CLICKED_V2"
    const val KEY_FIRST_DATA_INJECT = "KEY_FIRST_DATA_INJECT_SUGAR_V2"
    const val KEY_UNIT = "KEY_UNIT_SUGAR_V2"
    const val KEY_POSITION_INFO = "KEY_POSITION_INFO_SUGAR_V2"
    const val APP_DB = "CALCULATOR_V1"
    const val KEY_PASS_SPLASH = "KEY_PASS_SPLASH_V2"

    const val KEY_USER_RATE = "KEY_USER_RATE_SUGAR_V2"
    const val KEY_INDEX_NOTIFY = "KEY_INDEX_NOTIFY_V2"
    const val KEY_NUM_NOTI = "KEY_NUM_NOTI_SUGAR_V2"
    const val KEY_TIME_RATE = "KEY_TIME_RATE_SUGAR_V2"

    const val NOTI_EVERYDAY = "NOTI_EVERYDAY_MORNING_SUGAR_V2"
    const val NOTI_EVERYDAY_EVENING = "NOTI_EVERYDAY_EVENING_SUGAR_V2"
    const val ALARM_ID = "ALARM_ID_SUGAR_V2"

    const val SUFFIX_REQUEST_CODE = 12121
    const val KEY_NOTIFY_POS = "KEY_NOTIFY_SUGAR_V2"
    const val KEY_FIRST_INIT = "KEY_FIRST_INIT_SUGAR_V2"
    const val CHANNEL_NAME = "CHANNEL_NAME_SUGAR_V2"
    const val CHANNEL_ID = "CHANNEL_ID_SUGAR_V2"
    const val CONTENT_SHARE_APP = "_SUGAR_V2"
    const val KEY_SELECTED_NOTE_LIST = "KEY_SELECTED_NOTE_LIST_SUGAR_V2"
    const val KEY_HISTORY_MODEL = "KEY_HISTORY_MODEL_SUGAR_V2"
    const val KEY_NOTE_LIST = "KEY_NOTE_LIST_SUGAR_V2"
    const val KEY_LANGUAGE = "KEY_LANGUAGE_SUGAR_V2"
    const val MAIL_TO = "nekosoft.feedback.app@gmail.com"
    const val URL_PRIVACY = "https://sites.google.com/view/nksoftpolicy/home"
//    const val URL_APP =
//        "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"


    const val HOUR_MINUTES = 60
    const val DAY_MINUTES = 24 * HOUR_MINUTES
    const val WEEK_MINUTES = DAY_MINUTES * 7
    const val MONTH_MINUTES = DAY_MINUTES * 30
    const val YEAR_MINUTES = DAY_MINUTES * 365

    const val MINUTE_SECONDS = 60
    const val HOUR_SECONDS = HOUR_MINUTES * 60
    const val DAY_SECONDS = DAY_MINUTES * 60
    const val WEEK_SECONDS = WEEK_MINUTES * 60
    const val MONTH_SECONDS = MONTH_MINUTES * 60
    const val YEAR_SECONDS = YEAR_MINUTES * 60
    const val TODAY_BIT = -1
    const val TOMORROW_BIT = -2
    const val OPEN_ALARMS_TAB_INTENT_ID = 9996

    const val KEY_SECURITY_QUESTION = "KEY_SECURITY_QUESTION"
    const val KEY_SECURITY_ANSWER = "KEY_SECURITY_ANSWER"


    const val SECRET_KEY = 9996
    const val DEFAULT_SECRET_PASSWORD = "1234"
    const val DEFAULT_SECRET_PASSWORD_SUFFIX = "%"
    const val KEY_THEME_COLOR = "KEY_THEME_COLOR"

    const val FILES_FOLDER_NAME = "FILE_CALCULATOR_VAULT_${SECRET_KEY}"
    const val PICTURE_FOLDER_NAME = "PICTURE_CALCULATOR_VAULT_${SECRET_KEY}"
    const val AUDIOS_FOLDER_NAME = "AUDIOS_CALCULATOR_VAULT_${SECRET_KEY}"
    const val VIDEOS_FOLDER_NAME = "VIDEOS_CALCULATOR_VAULT_${SECRET_KEY}"

    const val TYPE_PICTURE = "TYPE_PICTURE"
    const val TYPE_AUDIOS = "TYPE_AUDIOS"
    const val TYPE_VIDEOS = "TYPE_VIDEOS"
    const val TYPE_FILE = "TYPE_FILE"
    const val TYPE_ADD_MORE = "TYPE_ADD_MORE"
    val extraAudioMimeTypes = arrayListOf("application/ogg")
    val extraDocumentMimeTypes = arrayListOf(
        "application/pdf",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "application/javascript"
    )

    val archiveMimeTypes = arrayListOf(
        "application/zip",
        "application/octet-stream",
        "application/json",
        "application/x-tar",
        "application/x-rar-compressed",
        "application/x-zip-compressed",
        "application/x-7z-compressed",
        "application/x-compressed",
        "application/x-gzip",
        "application/java-archive",
        "multipart/x-zip"
    )

    const val TYPE_PDF = ".pdf"
    const val TYPE_PPT = ".ppt"
    const val TYPE_PPTX = ".pptx"
    const val TYPE_WORD = ".doc"
    const val TYPE_WORDX = ".docx"
    const val TYPE_EXCEL = ".xlsx"
    const val TYPE_TEXT = ".txt"
    const val TYPE_CSV = ".csv"
    const val TYPE_ZIP = ".zip"
    const val TYPE_OTHER = "TYPE_OTHER"

    val photoExtensions: Array<String>
        get() = arrayOf(
            ".jpg", ".png", ".jpeg", ".bmp", ".webp", ".heic", ".heif", ".apng", ".avif"
        )
    val videoExtensions: Array<String>
        get() = arrayOf(
            ".mp4", ".mkv", ".webm", ".avi", ".3gp", ".mov", ".m4v", ".3gpp"
        )
    val audioExtensions: Array<String>
        get() = arrayOf(
            ".mp3", ".wav", ".wma", ".ogg", ".m4a", ".opus", ".flac", ".aac"
        )
    val rawExtensions: Array<String>
        get() = arrayOf(
            ".dng", ".orf", ".nef", ".arw", ".rw2", ".cr2", ".cr3"
        )


}