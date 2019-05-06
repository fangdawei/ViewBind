package club.fdawei.viewbind.plugin

import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Modifier

/**
 * Create by david on 2019/05/01.
 */
class R2Builder {

    private val clzName = "R2"
    private val typeMap = mutableMapOf<String, MutableMap<String, String>>()

    fun add(type: String, key: String, value: String) {
        var keyMap = typeMap[type]
        if (keyMap == null) {
            keyMap = mutableMapOf()
            typeMap[type] = keyMap
        }
        keyMap[key] = value
    }

    fun build(): TypeSpec {
        val r2Builder = TypeSpec.classBuilder(clzName)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
        typeMap.forEach { (t, u) ->
            val typeBuilder = TypeSpec.classBuilder(t)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
            u.forEach { (k, v) ->
                val fieldBuilder = FieldSpec.builder(TypeName.INT, k)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer(v)
                typeBuilder.addField(fieldBuilder.build())
            }
            r2Builder.addType(typeBuilder.build())
        }
        return r2Builder.build()
    }
}