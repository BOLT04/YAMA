package isel.pt.yama.common

import isel.pt.yama.activity.MainActivity


// name of preferences file
val SP_NAME = MainActivity::class.java.`package`.name + "_Preferences"

const val VIEW_MODEL_KEY = "Login view model key"
const val USER_EXTRA = "LoginActivity.userExtra"
const val ORGANIZATION_EXTRA = "LoginActivity.organizationExtra"
const val TOKEN_EXTRA_KEY = "LoginActivity.tokenExtra"