package club.fdawei.viewbind.demo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import club.fdawei.viewbind.api.ViewBind
import club.fdawei.viewbind.annotation.BindView
import club.fdawei.viewbind.annotation.OnClick
import club.fdawei.viewbinddemo.R

class MainActivity : AppCompatActivity() {

    @BindView(id = R.id.tv_hello_1)
    lateinit var mTvHello1: TextView

    @BindView(id = R.id.tv_hello_2)
    lateinit var mTvHello2: TextView

    @BindView(id = R.id.tv_hello_3)
    lateinit var mTvHello3: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ViewBind.bind(this)

        mTvHello1.setText("hello 1")
        mTvHello2.setText("hello 2")
        mTvHello3.setText("hello 3")
    }

    @OnClick(id = R.id.tv_hello_1)
    fun onHello1Click() {
        Toast.makeText(this, "hello 1", Toast.LENGTH_SHORT).show()
    }

    @OnClick(id = R.id.tv_hello_2)
    fun onHello2Click() {
        Toast.makeText(this, "hello 2", Toast.LENGTH_SHORT).show()
    }


    @OnClick(id = R.id.tv_hello_3)
    fun onHello3Click() {
        Toast.makeText(this, "hello 3", Toast.LENGTH_SHORT).show()
    }
}
