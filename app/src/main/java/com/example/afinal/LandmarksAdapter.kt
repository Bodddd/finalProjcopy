package com.example.afinal

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class LandmarksAdapter(private var landmarks: List<Landmark>) :
    RecyclerView.Adapter<LandmarksAdapter.LandmarkViewHolder>() {

    class LandmarkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(landmark: Landmark) {
            itemView.findViewById<TextView>(R.id.landmarkName).text = landmark.name
            Picasso.get().load(landmark.imageUrl)
                .into(itemView.findViewById<ImageView>(R.id.landmarkImage))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LandmarkViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.landmark_item, parent, false)
        return LandmarkViewHolder(view)
    }

    override fun onBindViewHolder(holder: LandmarkViewHolder, position: Int) {
        holder.bind(landmarks[position])
    }

    override fun getItemCount() = landmarks.size

    fun updateData(newLandmarks: List<Landmark>) {
        Log.d("LandmarksAdapter", "Updating data with ${newLandmarks.size} landmarks")
        this.landmarks = newLandmarks
        notifyDataSetChanged()
    }
}
