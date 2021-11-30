package org.hector.numeros

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.AdapterView.OnItemLongClickListener
import org.hector.numeros.databinding.ActivityMainBinding
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    var numeros = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val language = arrayOf(getString(R.string.ascending), getString(R.string.descending))

        setupListeners()

        binding.add.setOnClickListener{
            validate()
        }

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            //se reproduce si no se seleccionó nada
            override fun onNothingSelected(parent: AdapterView<*>?) {
                showDialog(getString(R.string.no_select_title),getString(R.string.no_select_msg))
                Toast.makeText(applicationContext, getString(R.string.no_selected) , Toast.LENGTH_LONG).show()
            }
            // se reproduce si se seleccionó un elemento
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Toast.makeText(applicationContext, language[position], Toast.LENGTH_LONG).show()
                sortData(position)
            }
        }

        val languageAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, language)
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = languageAdapter

        val itemsAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, numeros)
        binding.listView.adapter = itemsAdapter

        binding.listView.onItemLongClickListener = OnItemLongClickListener { _, _, i, _ ->
            Toast.makeText(applicationContext, getString(R.string.delete, numeros[i]), Toast.LENGTH_LONG).show()
            numeros.removeAt(i)
            false
        }

        binding.listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            Toast.makeText(applicationContext, getString(R.string.delete, numeros[position]), Toast.LENGTH_SHORT).show()
        }
    }

    private fun sortData(position: Int) {

        val auxNum = mutableListOf<Int>()

        numeros.forEach{
            auxNum.add(it.toInt())
        }

        when (position){
            0 -> {auxNum.sort()}
            1 -> {auxNum.sortDescending()}
        }

        numeros = mutableListOf()
        auxNum.forEach{
            numeros.add(it.toString())
        }

        binding.listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, numeros)
    }

    private fun validate(){
        if (validateUserNum()) {
            numeros.add(binding.userNum.text.toString())
            binding.userNum.onEditorAction(EditorInfo.IME_ACTION_DONE)
            Toast.makeText(applicationContext, getString(R.string.add_new_number, binding.userNum.text.toString()), Toast.LENGTH_LONG).show()
        }
    }

    private fun setupListeners() {
        binding.userNum.addTextChangedListener(TextFieldValidation(binding.userNum))
    }

    //private fun isValidate(): Boolean = validateUserNum()

    private fun validateUserNum(): Boolean {
        when {
            binding.userNum.text.toString().trim().isEmpty() -> {
                binding.userNumTil.error = getString(R.string.required_field)
                binding.userNum.requestFocus()
                return false
            }
            else -> {
                binding.userNumTil.isErrorEnabled = false
            }
        }
        return true
    }

    inner class TextFieldValidation(private val view: View) : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            when (view.id) {
                R.id.user_num -> {
                    validateUserNum()
                }
            }
        }
    }


    private fun showDialog(title:String,message:String){
        val builder = AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(getString(R.string.ok)){_, _->  }

        val alertDialog = builder.create()
        alertDialog.show()
    }

}