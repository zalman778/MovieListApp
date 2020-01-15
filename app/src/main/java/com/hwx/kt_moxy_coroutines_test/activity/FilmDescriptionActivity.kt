package com.hwx.kt_moxy_coroutines_test.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.beardedhen.androidbootstrap.BootstrapButton
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.hwx.Configuration
import com.hwx.kt_moxy_coroutines_test.R
import com.hwx.kt_moxy_coroutines_test.ValuesHolder
import com.hwx.kt_moxy_coroutines_test.convertDpToPixel
import com.hwx.kt_moxy_coroutines_test.model.FilmCast
import com.hwx.kt_moxy_coroutines_test.model.FilmCredit
import com.hwx.kt_moxy_coroutines_test.model.FilmSimple
import com.hwx.kt_moxy_coroutines_test.view.FilmDescriptionView
import com.nex3z.flowlayout.FlowLayout
import dagger.android.AndroidInjection
import me.zhanghai.android.materialratingbar.MaterialRatingBar
import moxy.MvpAppCompatActivity


class FilmDescriptionActivity : MvpAppCompatActivity(), FilmDescriptionView {

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_film_description)

        if (intent.hasExtra(EXTRA_FILM_SIMPLE)) {
            val filmSimple = intent.getSerializableExtra(EXTRA_FILM_SIMPLE) as FilmSimple
            val filmCredit = intent.getSerializableExtra(EXTRA_FILM_CREDIT) as FilmCredit

            val tvCaption = findViewById<TextView>(R.id.tvFilmCaption)
            tvCaption.text = filmSimple.title

            val shortFilmGenresWrapper = findViewById<FlowLayout>(R.id.shortFilmGenresWrapper)
            fillGenresWrapper(shortFilmGenresWrapper, filmSimple.genreArray)

            val tvDirector = findViewById<TextView>(R.id.tvFilmDirector)
            tvDirector.text = "Director / " + filmCredit.crew.first { it.job == "Director" }.name

            val tvRating = findViewById<TextView>(R.id.ratingText)
            tvRating.text = "%.1f".format(filmSimple.voteAverage / 2)

            val rvRating = findViewById<MaterialRatingBar>(R.id.ratingBar)
            rvRating.rating = (filmSimple.voteAverage / 2).toFloat()

            //film Image
            val ivFilmImage = findViewById<ImageView>(R.id.shortFilmImage)
            val imageUrl = Configuration.getImageFullUrl(filmSimple.backdropPath)
            val primaryImageRequestOptions = RequestOptions().transforms(CenterCrop(), RoundedCorners(60))
            Glide.with(this)
                .load(imageUrl)
                .apply(primaryImageRequestOptions)
                .into(ivFilmImage)

            //cast list
            val castLinearLayout = findViewById<LinearLayout>(R.id.castWrapperInner)
            fillCastLinearLayoutWithValues(castLinearLayout, filmCredit.casts)

            animateTextOverview(filmSimple.overview)

        }

    }

    override fun onBackPressed() {
        supportFinishAfterTransition()
    }

    private fun animateTextOverview(value: String) {
        val tvOverview = findViewById<TextView>(R.id.tvFilmOverview)
        tvOverview.text = value

        val startTranslationYPx = convertDpToPixel(1000.toFloat(), this)
        val endTranslationYPx = convertDpToPixel(150.toFloat(), this)
        val translationAnimator = ValueAnimator.ofFloat(startTranslationYPx, endTranslationYPx)
        val transparencyAnimator = ValueAnimator.ofFloat(0.0f, 1.0f)

        translationAnimator.addUpdateListener { animation ->
            val ty = animation.animatedValue as Float
            tvOverview.translationY = ty
        }

        transparencyAnimator.addUpdateListener { animation ->
            val alpha = animation.animatedValue as Float
            tvOverview.alpha = alpha
        }

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(translationAnimator, transparencyAnimator)
        animatorSet.duration = 500
        animatorSet.startDelay = 500
        animatorSet.interpolator = AccelerateInterpolator()

        animatorSet.start()
    }

    private fun fillGenresWrapper(shortFilmGenresWrapper: FlowLayout, genreArray: List<Int>) {
        shortFilmGenresWrapper.removeAllViews()

        genreArray.forEach {
            val lp = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
            val mGenreBtn = BootstrapButton(this)
            mGenreBtn.layoutParams = lp
            mGenreBtn.text = ValuesHolder.cachedGenres[it]
            mGenreBtn.isShowOutline = true
            mGenreBtn.isRounded = true
            mGenreBtn.bootstrapBrand = DefaultBootstrapBrand.PRIMARY
            mGenreBtn.isEnabled = false
            mGenreBtn.bootstrapSize = 0.7f
            shortFilmGenresWrapper.addView(mGenreBtn)
        }
    }

    /**
     * Fills linear layout with images and texts...
     */
    private fun fillCastLinearLayoutWithValues(
        castLinearLayout: LinearLayout,
        filmCastList: List<FilmCast>
    ) {
        val imageSideSizePx = convertDpToPixel(120.0f, this).toInt()
        val castImageRequestOptions = RequestOptions().transforms(CenterCrop(), RoundedCorners(20))

        val textHeightPx = convertDpToPixel(20.0f, this).toInt()
        val targetInnerLayoutHeightPx = convertDpToPixel(140.0f, this).toInt()
        var initialInnerLayoutHeightPx: Int
        var initialInnerLayoutAlpha: Float


        var itemPositionCounter = 0 //счетчик номера аниматоров

        filmCastList
           // .take(15)
            .filter{ !it.profilePath.isNullOrEmpty() }
            .forEach{
                if (itemPositionCounter < AMOUNT_OF_CAST_POSITIONS_ANIMATED) {
                    initialInnerLayoutHeightPx = (targetInnerLayoutHeightPx * 1.5f).toInt()
                    initialInnerLayoutAlpha = 0.0f
                } else {
                    initialInnerLayoutHeightPx = targetInnerLayoutHeightPx
                    initialInnerLayoutAlpha = 1.0f
                }

                val innerVerticalLayout = LinearLayout(this)
                val innerLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, initialInnerLayoutHeightPx)
                innerLayoutParams.marginStart = 15
                innerLayoutParams.marginEnd = 15
                innerLayoutParams.gravity = Gravity.TOP
                innerVerticalLayout.orientation = LinearLayout.VERTICAL
                innerVerticalLayout.layoutParams = innerLayoutParams
                innerVerticalLayout.gravity = Gravity.BOTTOM
                innerVerticalLayout.alpha = initialInnerLayoutAlpha
                castLinearLayout.addView(innerVerticalLayout)

                val castImageUrl = Configuration.getImageFullUrl(it.profilePath)
                val ivCastImage = ImageView(this)
                val ivLayoutParams = LinearLayout.LayoutParams(imageSideSizePx, imageSideSizePx)
                ivCastImage.layoutParams = ivLayoutParams
                ivCastImage.background = getDrawable(R.drawable.asset_shadow)
                innerVerticalLayout.addView(ivCastImage)

                val tvCastTitle = TextView(this)
                val tvLayoutParams = LinearLayout.LayoutParams(imageSideSizePx, textHeightPx)
                tvCastTitle.text = it.name
                tvCastTitle.width = LinearLayout.LayoutParams.MATCH_PARENT
                tvCastTitle.gravity = Gravity.CENTER
                tvCastTitle.layoutParams = tvLayoutParams
                innerVerticalLayout.addView(tvCastTitle)

                Glide.with(this)
                    .load(castImageUrl)
                    .apply(castImageRequestOptions)
                    .into(ivCastImage)

                //start animating layout height:
                if (itemPositionCounter < AMOUNT_OF_CAST_POSITIONS_ANIMATED) {
                    animateCastAppearance(itemPositionCounter, initialInnerLayoutHeightPx, targetInnerLayoutHeightPx, innerVerticalLayout)
                }
                itemPositionCounter++
            }
    }

    private fun animateCastAppearance(
        itemPositionCounter: Int,
        initialHeightPx: Int,
        targetInnerLayoutHeightPx: Int,
        innerVerticalLayout: LinearLayout
    ) {
        val heightAnimator = ValueAnimator.ofInt(initialHeightPx, targetInnerLayoutHeightPx)
        val transparencyAnimator = ValueAnimator.ofFloat(0.0f, 1.0f)

        // Set the layer type to hardware
        innerVerticalLayout.setLayerType(View.LAYER_TYPE_HARDWARE, null)

        heightAnimator.addUpdateListener { animation ->
            val height = animation.animatedValue as Int
            val newLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height)
            newLayoutParams.marginStart = 15
            newLayoutParams.marginEnd = 15
            newLayoutParams.gravity = Gravity.TOP
            innerVerticalLayout.layoutParams = newLayoutParams
        }
        transparencyAnimator.addUpdateListener { animation ->
            val alpha = animation.animatedValue as Float
            innerVerticalLayout.alpha = alpha
        }

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(heightAnimator, transparencyAnimator)
        animatorSet.duration = 500
        animatorSet.startDelay = (250 * itemPositionCounter).toLong()
        animatorSet.interpolator = AccelerateInterpolator()

        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                innerVerticalLayout.setLayerType(View.LAYER_TYPE_NONE, null)
            }
        })
        animatorSet.start()
    }


    companion object {
        const val EXTRA_FILM_SIMPLE = "EXTRA_FILM_SIMPLE"
        const val EXTRA_FILM_CREDIT = "EXTRA_FILM_CREDIT"
        const val AMOUNT_OF_CAST_POSITIONS_ANIMATED = 3

        fun getIntent(
            context: Context,
            filmSimple: FilmSimple,
            filmCredit: FilmCredit
        ): Intent {
            val intent = Intent(context, FilmDescriptionActivity::class.java)
            intent.putExtra(EXTRA_FILM_SIMPLE, filmSimple)
            intent.putExtra(EXTRA_FILM_CREDIT, filmCredit)
            return intent
        }
    }

}