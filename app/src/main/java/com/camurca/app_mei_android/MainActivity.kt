package com.camurca.app_mei_android

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import android.widget.NumberPicker
import android.widget.RadioGroup
import android.widget.TextView
import java.util.Calendar

const val PREFS_FILE = "app_prefs"

class SharedPrefsUtil(private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences(PREFS_FILE, MODE_PRIVATE)

    fun addValue(year: Int, value: Float) {
        val currentBalance = getBalance(year)

        val newBalance = currentBalance + value
        sharedPreferences.edit().putFloat("$year", newBalance).apply()
    }

    fun removeValue(year: Int, value: Float) {
        val currentBalance = getBalance(year)

        val newBalance = currentBalance - value
        sharedPreferences.edit().putFloat("$year", newBalance).apply()
    }

    fun getBalance(year: Int): Float {
        return sharedPreferences.getFloat("$year", 0.0f)
    }
}

class MainActivity : AppCompatActivity() {
    private lateinit var mNumberPicker: NumberPicker
    private lateinit var mAcaoRadioGroup: RadioGroup
    private lateinit var mValorEditText: EditText
    private lateinit var mSaldoTextView: TextView
    private lateinit var mConfirmarButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mNumberPicker = findViewById<NumberPicker>(R.id.ano)
        mAcaoRadioGroup = findViewById<RadioGroup>(R.id.acao)
        mValorEditText = findViewById<EditText>(R.id.value)
        mSaldoTextView = findViewById<TextView>(R.id.value_txt)
        mConfirmarButton = findViewById<Button>(R.id.add_button)

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        mNumberPicker.minValue = currentYear - 30
        mNumberPicker.maxValue = currentYear + 30
        mNumberPicker.value = currentYear

        mNumberPicker.setOnValueChangedListener {picker, oldVal, newVal ->
            exibirSaldo(newVal)
        }

        mConfirmarButton.setOnClickListener {
            val valorText = mValorEditText.text.toString()
            if (valorText.isNotEmpty()) {
                val valor = valorText.toFloat()
                val ano = mNumberPicker.value

                when (mAcaoRadioGroup.checkedRadioButtonId) {
                    R.id.acao_adicionar -> adicionarValor(ano, valor)
                    R.id.acao_remover -> excluirValor(ano, valor)
                }

                exibirSaldo(ano)
            }
        }

        exibirSaldo(mNumberPicker.value)
    }

    private fun adicionarValor(ano: Int, valor: Float) {
        val sharedPrefsUtil = SharedPrefsUtil(this)
        sharedPrefsUtil.addValue(ano, valor)

        exibirSaldo(ano)
    }
    private fun excluirValor(ano: Int, valor: Float) {
        val sharedPrefsUtil = SharedPrefsUtil(this)
        sharedPrefsUtil.removeValue(ano, valor)

        exibirSaldo(ano)
    }
    private fun exibirSaldo(ano: Int) {
        val sharedPrefsUtil = SharedPrefsUtil(this)
        val saldo = sharedPrefsUtil.getBalance(ano)

        mSaldoTextView.text = saldo.toString()
    }
}