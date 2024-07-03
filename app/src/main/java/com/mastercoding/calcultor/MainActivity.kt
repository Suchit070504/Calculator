package com.mastercoding.calcultor

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mastercoding.calcultor.databinding.ActivityMainBinding
import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var lastNumeric = false
    private var stateError = false
    private var lastDot = false
    private lateinit var expression: Expression

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun onEqualClick(view: View) {
        onEqual()
        binding.textView.text = binding.textView2.text.toString().drop(1)
    }

    fun onDigitClick(view: View) {
        if (stateError) {
            binding.textView.text = (view as Button).text
            stateError = false
        } else {
            binding.textView.append((view as Button).text)
        }
        lastNumeric = true
        onEqual()
    }

    fun onAllClearClick(view: View) {
        binding.textView.text = ""
        binding.textView2.text = ""
        stateError = false
        lastDot = false
        lastNumeric = false
        binding.textView2.visibility = View.GONE
    }

    fun onOperatorClick(view: View) {
        if (!stateError && lastNumeric && !isOperatorAdded(binding.textView.text.toString())) {
            binding.textView.append((view as Button).text)
            lastDot = false
            lastNumeric = false
            onEqual()
        }
    }

    fun onClearClick(view: View) {
        binding.textView.text = ""
        lastNumeric = false
        lastDot = false
        stateError = false
        binding.textView2.text = ""
        binding.textView2.visibility = View.GONE
    }

    fun onBackClick(view: View) {
        val text = binding.textView.text.toString()
        if (text.isNotEmpty()) {
            binding.textView.text = text.dropLast(1)
            if (text.isNotEmpty() && text.last().isDigit()) {
                lastNumeric = true
                onEqual()
            } else if (text.isNotEmpty() && text.last() == '.') {
                lastDot = true
                lastNumeric = false
            } else {
                lastNumeric = false
                lastDot = false
            }
        } else {
            binding.textView2.text = ""
            binding.textView2.visibility = View.GONE
        }
    }

    private fun onEqual() {
        if (lastNumeric && !stateError) {
            val txt = binding.textView.text.toString()
            try {
                expression = ExpressionBuilder(txt).build()
                val result = expression.evaluate()
                binding.textView2.visibility = View.VISIBLE
                binding.textView2.text = "=" + result.toString()
            } catch (ex: Exception) {
                Log.e("evaluate error", ex.toString())
                binding.textView2.text = "Error"
                stateError = true
                lastNumeric = false
            }
        }
    }

    private fun isOperatorAdded(value: String): Boolean {
        return if (value.startsWith("-")) {
            false
        } else {
            value.contains("/") || value.contains("*") || value.contains("+") || value.contains("-")
        }
    }
}
