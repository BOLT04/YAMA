package isel.pt.yama.kotlinx

import androidx.appcompat.app.AppCompatActivity
import isel.pt.yama.YAMAApplication

inline fun AppCompatActivity.getYAMAApplication() = this.application as YAMAApplication