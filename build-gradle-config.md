### URL Dependancies

defaultConsole {
        buildConfigField "String", "SIGNAL_URL", "\"https://chat.signal.org\""
        buildConfigField "String", "STORAGE_URL", "\"https://storage.signal.org\""
        buildConfigField "String", "SIGNAL_CDN_URL", "\"https://cdn.signal.org\""
        buildConfigField "String", "SIGNAL_CDN2_URL", "\"https://cdn2.signal.org\""
        buildConfigField "String", "SIGNAL_CDSI_URL", "\"https://cdsi.signal.org\""
        buildConfigField "String", "SIGNAL_SERVICE_STATUS_URL", "\"uptime.signal.org\""
        buildConfigField "String", "SIGNAL_KEY_BACKUP_URL", "\"https://api.backup.signal.org\""
        buildConfigField "String", "SIGNAL_SVR2_URL", "\"https://svr2.signal.org\""
        buildConfigField "String", "SIGNAL_SFU_URL", "\"https://sfu.voip.signal.org\""
        buildConfigField "String", "SIGNAL_STAGING_SFU_URL", "\"https://sfu.staging.voip.signal.org\""
        buildConfigField "String[]", "SIGNAL_SFU_INTERNAL_NAMES", "new String[]{\"Test\", \"Staging\", \"Development\"}"
        buildConfigField "String[]", "SIGNAL_SFU_INTERNAL_URLS", "new String[]{\"https://sfu.test.voip.signal.org\", \"https://sfu.staging.voip.signal.org\", \"https://sfu.staging.test.voip.signal.org\"}"
        buildConfigField "String", "CONTENT_PROXY_HOST", "\"contentproxy.signal.org\""

        buildConfigField "String", "BADGE_STATIC_ROOT", "\"https://updates2.signal.org/static/badges/\""
}


        website {
            dimension 'distribution'
            ext.websiteUpdateUrl = "https://updates.signal.org/android"
            buildConfigField "boolean", "PLAY_STORE_DISABLED", "true"
            buildConfigField "String", "NOPLAY_UPDATE_URL", "\"$ext.websiteUpdateUrl\""
            buildConfigField "String", "BUILD_DISTRIBUTION_TYPE", "\"website\""
        }


staging {
            buildConfigField "String", "SIGNAL_URL", "\"https://chat.staging.signal.org\""
            buildConfigField "String", "STORAGE_URL", "\"https://storage-staging.signal.org\""
            buildConfigField "String", "SIGNAL_CDN_URL", "\"https://cdn-staging.signal.org\""
            buildConfigField "String", "SIGNAL_CDN2_URL", "\"https://cdn2-staging.signal.org\""
            buildConfigField "String", "SIGNAL_CDSI_URL", "\"https://cdsi.staging.signal.org\""
            buildConfigField "String", "SIGNAL_KEY_BACKUP_URL", "\"https://api-staging.backup.signal.org\""
            buildConfigField "String", "SIGNAL_SVR2_URL", "\"https://svr2.staging.signal.org\""
}