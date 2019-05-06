package club.fdawei.viewbind.demo.sub

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import club.fdawei.viewbind.annotation.BindView
import club.fdawei.viewbind.api.ViewBind

class SubActivity : AppCompatActivity() {

    @BindView(R2.id.tv_sub_hello)
    lateinit var tvHello: TextView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub)

        ViewBind.bind(this)

        tvHello.text = "sub hello"
    }
}
