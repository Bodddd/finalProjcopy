package com.example.afinal

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CityAutocompleteAdapter(
    context: Context,
    private val placesClient: PlacesClient,
    private val coroutineScope: CoroutineScope
) : ArrayAdapter<AutocompletePrediction>(context, android.R.layout.simple_dropdown_item_1line),
    Filterable {

    private val predictions = mutableListOf<AutocompletePrediction>()

    override fun getCount(): Int = predictions.size

    override fun getItem(position: Int): AutocompletePrediction = predictions[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(android.R.layout.simple_dropdown_item_1line, parent, false)
        val item = getItem(position)
        (view.findViewById(android.R.id.text1) as TextView).text =
            item.getPrimaryText(null).toString()
        return view
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (!constraint.isNullOrBlank()) {
                    coroutineScope.launch {
                        val request = FindAutocompletePredictionsRequest.builder()
                            .setQuery(constraint.toString())
                            .build()

                        val response = placesClient.findAutocompletePredictions(request).await()
                        predictions.clear()
                        predictions.addAll(response.autocompletePredictions)
                        filterResults.values = predictions
                        filterResults.count = predictions.size
                        publishResults(constraint, filterResults)
                    }
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged()
                } else {
                    notifyDataSetInvalidated()
                }
            }
        }
    }
}



