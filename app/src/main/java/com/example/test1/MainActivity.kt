package com.example.test1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    //https://github.com/public-apis/public-apis

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recycler = findViewById<RecyclerView>(R.id.recycler)
        val adapter = CustomAdapter()
        recycler.adapter = adapter

        val request = Request.Builder().url("https://api.publicapis.org/entries").build()

        OkHttpClient().newCall(request).enqueue(
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.e("TEST", "Error, " + e.localizedMessage)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        //Añadir breakpoint para ver como se puede ver en el debugger
//                        Log.e("TEST", response.body.toString())
//Es bastante util crear el string para probar con un breakpoint si no casca el parseado
                        val string = response.body?.string()
                        /// Por si acaso la respuesta no es la que esperabamos...
                        val result = Gson().fromJson<Wrapper>(string, Wrapper::class.java)
                        Log.e("TEST", result.toString())
                        runOnUiThread{
                            adapter.updateData(result.entries.filter { !it.HTTPS })
                        }
//                        result.entries.filter { !it.HTTPS }
                    }

                }
        )
    }
}

class CustomAdapter : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {
    private val items = ArrayList<APIInfo>()

    fun updateData(newData: List<APIInfo>) {
        items.apply {
            clear()
            addAll(newData)
            notifyDataSetChanged()
        }
    }

    //Los metodos de aqui en adelante hay que definirlos siempre (es obligatorio cuando extendemos del RecyclerView adapter
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            //No cogemos el context de ningun lado, EL CONTEXT NO SE TOCA NI SE MUEVE!!! (salvo que sea imprescindible)
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_layout, null)) {
                Log.d("TEST", items[it].API)
            }
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    class ViewHolder(itemView: View, clickListener: ((Int) -> Unit)): RecyclerView.ViewHolder(itemView) {
        private val title = itemView.findViewById<AppCompatTextView>(R.id.title)
        private val description = itemView.findViewById<AppCompatTextView>(R.id.description)

        init {
             itemView.setOnClickListener{
                 clickListener.invoke(adapterPosition)
             }
        }

        fun bind(item: APIInfo){
            title.text = item.API
            description.text = item.Description
        }
    }


}