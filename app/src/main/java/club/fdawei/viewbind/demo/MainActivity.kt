package club.fdawei.viewbind.demo

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import club.fdawei.viewbind.api.ViewBind
import club.fdawei.viewbind.annotation.BindView
import club.fdawei.viewbind.annotation.OnClick
import club.fdawei.viewbind.api.provider.Provider
import club.fdawei.viewbinddemo.R

class MainActivity : AppCompatActivity() {

    @BindView(id = R.id.tv_hello_1)
    lateinit var mTvHello1: TextView

    @BindView(id = R.id.tv_hello_2)
    lateinit var mTvHello2: TextView

    @BindView(id = R.id.tv_hello_3)
    lateinit var mTvHello3: TextView

    @BindView(id = R.id.fl_container)
    lateinit var mContainer: FrameLayout

    val viewWrapper: ViewWrapper = ViewWrapper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ViewBind.bind(this)

        mTvHello1.setText("hello 1")
        mTvHello2.setText("hello 2")
        mTvHello3.setText("hello 3")

        ViewBind.registerProvider(ViewWrapper::class.java, Provider { source, id ->
            if (source is ViewWrapper) {
                return@Provider source.findViewById(id)
            }
            return@Provider null
        })

        viewWrapper.init(mContainer)
        ViewBind.bind(viewWrapper)
    }

    @OnClick(id = [R.id.tv_hello_1])
    fun onHello1Click(view: View) {
        Toast.makeText(this, "hello 1", Toast.LENGTH_SHORT).show()
    }

    @OnClick(id = [R.id.tv_hello_2])
    fun onHello2Click(view: View) {
        Toast.makeText(this, "hello 2", Toast.LENGTH_SHORT).show()
    }


    @OnClick(id = [R.id.tv_hello_3])
    fun onHello3Click(view: View) {
        Toast.makeText(this, "hello 3", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        ViewBind.unregisterProvider(ViewWrapper::class.java)
    }

    class ViewWrapper {

        lateinit var context: Context
        lateinit var view: View

        @BindView(id = R.id.tv_text)
        lateinit var tvText: TextView

        fun init(container: ViewGroup) {
            this.context = container.context
            this.view = LayoutInflater.from(context).inflate(R.layout.layout_container, container)
        }

        @OnClick(id = [R.id.tv_text])
        fun onTextClick(view: View) {
            Toast.makeText(context, "container text", Toast.LENGTH_SHORT).show()
        }

        fun findViewById(id: Int): View? {
            return view.findViewById(id)
        }
    }
}
