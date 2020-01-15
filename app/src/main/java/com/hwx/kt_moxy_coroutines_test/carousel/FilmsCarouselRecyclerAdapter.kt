package com.hwx.kt_moxy_coroutines_test.carousel

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.beardedhen.androidbootstrap.BootstrapButton
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.hwx.Configuration
import com.hwx.kt_moxy_coroutines_test.R
import com.hwx.kt_moxy_coroutines_test.ValuesHolder
import com.hwx.kt_moxy_coroutines_test.model.FilmSimple
import kotlinx.android.synthetic.main.carousel_item_layout.view.*
import me.zhanghai.android.materialratingbar.MaterialRatingBar


class FilmsCarouselRecyclerAdapter (
    var filmsList: Array<FilmSimple>,
    var context: Context,
    var cornerRadiusPx: Int,
    var onViewScrollChangedListener: OnViewScrollChangedListener? = null
):  RecyclerView.Adapter<FilmsCarouselRecyclerAdapter.FilmsCarouselViewHolder>() {

    open var currentPosition: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmsCarouselViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.carousel_item_layout, parent, false)
        return FilmsCarouselViewHolder(view)
    }

    override fun getItemCount(): Int {
        return filmsList.size
    }

    override fun onBindViewHolder(holder: FilmsCarouselViewHolder, position: Int) {
        holder.tvFilmCaption.text = filmsList[position].title
        val imageUrl = Configuration.getImageFullUrl(filmsList[position].backdropPath)
        var roundRequestOptions = RequestOptions()
        roundRequestOptions = roundRequestOptions.transforms(CenterCrop(), RoundedCorners(cornerRadiusPx))

        Glide.with(context)
            .load(imageUrl)
            .apply(roundRequestOptions)
            .into(holder.ivPrimaryImage)

        holder.itemView.viewTreeObserver.addOnScrollChangedListener {
            onViewScrollChangedListener?.onViewScrollChangedListener(holder.itemView, position)
        }

        //clearing buttons wrapper view:
        holder.itemView.shortFilmGenresWrapper.removeAllViews()

        filmsList[position].genreArray.forEach {
            val lp = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
            val mGenreBtn = BootstrapButton(context)
            mGenreBtn.layoutParams = lp
            mGenreBtn.text = ValuesHolder.cachedGenres[it]
            mGenreBtn.isShowOutline = true
            mGenreBtn.isRounded = true
            mGenreBtn.bootstrapBrand = DefaultBootstrapBrand.PRIMARY
            mGenreBtn.isEnabled = false
            mGenreBtn.bootstrapSize = 0.7f
            holder.itemView.shortFilmGenresWrapper.addView(mGenreBtn)
        }

        holder.rvFilmRating.rating = filmsList[position].voteAverage.toFloat() / 2
        holder.tvFilmRating.text = "%.1f".format(filmsList[position].voteAverage / 2)

    }


    inner class FilmsCarouselViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var ivPrimaryImage: ImageView = itemView.findViewById(R.id.shortFilmImage)
        internal var tvFilmCaption: TextView = itemView.findViewById(R.id.tvFilmCaption)
        internal var tvFilmRating: TextView = itemView.findViewById(R.id.ratingText)
        internal var rvFilmRating: MaterialRatingBar = itemView.findViewById(R.id.ratingBar)
    }
}

