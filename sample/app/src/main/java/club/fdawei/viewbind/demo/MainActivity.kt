package club.fdawei.viewbind.demo

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import club.fdawei.viewbind.api.ViewBind
import club.fdawei.viewbind.annotation.BindView
import club.fdawei.viewbind.annotation.OnClick
import club.fdawei.viewbind.annotation.OnLongClick
import club.fdawei.viewbind.annotation.OnTouch
import club.fdawei.viewbind.api.provider.Provider

class MainActivity : AppCompatActivity() {

    @BindView(R.id.tv_hello_1)
    lateinit var mTvHello1: TextView

    @BindView(R.id.tv_hello_2)
    lateinit var mTvHello2: TextView

    @BindView(R.id.tv_hello_3)
    lateinit var mTvHello3: TextView

    @BindView(R.id.fl_container)
    lateinit var mContainer: FrameLayout

    private val viewWrapper: ViewWrapper = ViewWrapper()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ViewBind.bind(this)

        mTvHello1.text = "hello 1"
        mTvHello2.text = "hello 2"
        mTvHello3.text = "hello 3"

        ViewBind.registerProvider(ViewWrapper::class.java, Provider { source, id ->
            if (source is ViewWrapper) {
                return@Provider source.findViewById(id)
            }
            return@Provider null
        })

        viewWrapper.init(mContainer)
        ViewBind.bind(viewWrapper)
    }

    @OnClick(R.id.tv_hello_1)
    fun onHello1Click(view: View) {
        val tv = view as TextView
        Toast.makeText(this, "${tv.text} onClick", Toast.LENGTH_SHORT).show()
    }

    @OnLongClick(R.id.tv_hello_2)
    fun onHello2LongClick(view: View): Boolean {
        val tv = view as TextView
        Toast.makeText(this, "${tv.text} onLongClick", Toast.LENGTH_SHORT).show()
        return true
    }

    @OnTouch(R.id.tv_hello_3)
    fun onHello3Touch(view: View, event: MotionEvent): Boolean {
        val tv = view as TextView
        Toast.makeText(this, "${tv.text} onTouch ${event.action}", Toast.LENGTH_SHORT).show()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        ViewBind.unregisterProvider(ViewWrapper::class.java)
    }

    class ViewWrapper {

        lateinit var context: Context
        lateinit var view: View

        @BindView(R.id.tv_text)
        lateinit var tvText: TextView

        fun init(container: ViewGroup) {
            this.context = container.context
            this.view = LayoutInflater.from(context).inflate(R.layout.layout_container, container)
        }

        @OnClick(R.id.tv_text)
        fun onTextClick(view: View) {
            Toast.makeText(context, "container text", Toast.LENGTH_SHORT).show()
        }

        fun findViewById(id: Int): View? {
            return view.findViewById(id)
        }
    }
}
