package com.example.afinal

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.lifecycle.LifecycleCoroutineScope
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CityAutocompleteAdapter(
    context: Context,
    private val placesClient: PlacesClient,
    private val lifecycleScope: LifecycleCoroutineScope
) : ArrayAdapter<AutocompletePrediction>(context, android.R.layout.simple_dropdown_item_1line), Filterable {

    private var autocompletePredictions: List<AutocompletePrediction> = emptyList()

    override fun getCount(): Int = autocompletePredictions.size

    override fun getItem(position: Int): AutocompletePrediction = autocompletePredictions[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(android.R.layout.simple_dropdown_item_1line, parent, false)
        val item = getItem(position)
        view.findViewById<TextView>(android.R.id.text1).text = item.getPrimaryText(null).toString()
        return view
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint.isNullOrBlank()) {
                    filterResults.values = emptyList<AutocompletePrediction>()
                    filterResults.count = 0
                } else {
                    lifecycleScope.launch {
                        filterResults.values = getAutocomplete(constraint)
                        filterResults.count = autocompletePredictions.size
                        publishResults(constraint, filterResults)
                    }
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                autocompletePredictions = if (results.values is List<*>) {
                    results.values as List<AutocompletePrediction>
                } else {
                    emptyList()
                }
                if (results.count > 0) {
                    notifyDataSetChanged()
                } else {
                    notifyDataSetInvalidated()
                }
            }
        }
    }

    private suspend fun getAutocomplete(constraint: CharSequence): List<AutocompletePrediction> {
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(constraint.toString())
            .build()

        val response = placesClient.findAutocompletePredictions(request).await()
        return response.autocompletePredictions.also {
            autocompletePredictions = it
        }
    }
}

