package net.mready.postit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import net.mready.postit.custom.CenteredTitleToolbar

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: CenteredTitleToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

    }
}
