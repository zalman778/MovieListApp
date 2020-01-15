package com.hwx.kt_moxy_coroutines_test.activity

import android.graphics.Point
import android.os.Bundle
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beardedhen.androidbootstrap.BootstrapButton
import com.bumptech.glide.Glide
import com.hwx.Configuration
import com.hwx.kt_moxy_coroutines_test.R
import com.hwx.kt_moxy_coroutines_test.carousel.FilmsCarouselRecyclerAdapter
import com.hwx.kt_moxy_coroutines_test.carousel.ItemSnapHelper
import com.hwx.kt_moxy_coroutines_test.carousel.OnViewScrollChangedListener
import com.hwx.kt_moxy_coroutines_test.convertDpToPixel
import com.hwx.kt_moxy_coroutines_test.convertPixelsToDp
import com.hwx.kt_moxy_coroutines_test.model.FilmCredit
import com.hwx.kt_moxy_coroutines_test.model.FilmSimple
import com.hwx.kt_moxy_coroutines_test.presenter.MainPresenter
import com.hwx.kt_moxy_coroutines_test.view.MainView
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.carousel_item_layout.view.*
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.round


class MainActivity : MvpAppCompatActivity(), MainView {

    private lateinit var recyclerView: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var filmsCarouselRecyclerAdapter: FilmsCarouselRecyclerAdapter

    @Inject
    @InjectPresenter
    lateinit var presenter: MainPresenter

    @ProvidePresenter
    fun providePresenter(): MainPresenter = presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.films_carousel_recycler)
        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = linearLayoutManager

        //for snap
        val snapHelper = ItemSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)

        presenter.loadData()

    }

    override fun onUpdateFilmsCarouselData(filmsList: Array<FilmSimple>) {
        val itemsAmount = filmsList.size
        val scrollLinearLayout = findViewById<LinearLayout>(R.id.inner_linear_layout)
        val screenWidthPx = getScreenWidth()
        val screenWidthDp = convertPixelsToDp(screenWidthPx.toFloat(), this).toInt()


        val originalItemWidthDp = 200
        //val originalItemWidthPx = convertDpToPixel(originalItemWidthDp.toFloat(), this)

        //scale of current item:
        val targetSizeMultiplier = 0.3F

        //diff of current view center when to start or finish scaling
        val startDiffScaleDp = 50
        val finishDiffScaleDp = 150
        val rootViewDefaultHeightDp = 550
        val rootViewDefaultHeightPx =  convertDpToPixel(rootViewDefaultHeightDp.toFloat(), this)

        //Total recycler scroll summ in px:
        var recyclerViewTotalScrolledX = 0

        recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                recyclerViewTotalScrolledX += dx
            }
        })

        val cornerRadiusPx = convertDpToPixel(40.toFloat(), this).toInt()
        //block width = 300dp
        val backgroundOffsetToScreenSizeCoef = screenWidthDp / 300.0f //1.37 //size of block on main activity are screen_width / scaleCoef size

        filmsCarouselRecyclerAdapter = FilmsCarouselRecyclerAdapter(
              filmsList
            , this
            , cornerRadiusPx
            , object: OnViewScrollChangedListener{
                override fun onViewScrollChangedListener(itemView: View, adapterPosition: Int) {

                    val itemMidPx = (itemView.left + itemView.right) / 2
                    val itemMidDp = convertPixelsToDp(itemMidPx.toFloat(), this@MainActivity)

                    //determining difference from central position
                    val diffToCentralDp = abs(itemMidDp - screenWidthDp / 2)

                    //in these distance we should scale item (x 1.3): 50 - 1.0x; 300 - targetSizeMultiplier x
                    if (diffToCentralDp > startDiffScaleDp ) {

                        //0.. 0.3 (targetSizeMultiplier)
                        //0 for other, 0.3 for primary
                        val scaleDiff = (targetSizeMultiplier - targetSizeMultiplier * (diffToCentralDp - startDiffScaleDp ) / finishDiffScaleDp)

                        val targetLayoutAlpha = 0.7F + scaleDiff //0.7-1.0
                        itemView.shortFilmWrapper.alpha = targetLayoutAlpha

                        //height of others:
                        val p = itemView.shortFilmWrapper.layoutParams
                        val targetHeightCoef = 0.9F + scaleDiff * 0.3 //0.9 - 1.0
                        p.height = (rootViewDefaultHeightPx * targetHeightCoef).toInt()
                        itemView.requestLayout()
                    } else {
                        filmsCarouselRecyclerAdapter.currentPosition = adapterPosition
                    }

                    //background swap:
                    val fullImagesWidth = screenWidthPx * itemsAmount
                    val horizontalOffset = recyclerView.computeHorizontalScrollOffset()
                    val scrollX = fullImagesWidth - horizontalOffset * backgroundOffsetToScreenSizeCoef
                    scrollLinearLayout.scrollX = scrollX.toInt()

                }
            }
        )

        recyclerView.adapter = filmsCarouselRecyclerAdapter


        loadBackgroundImages(filmsList, scrollLinearLayout)


        //locking horizontal scroll_view movement event
        val scrollView = findViewById<HorizontalScrollView>(R.id.horizontal_scroll_view)
        scrollView.setOnTouchListener { _, _ -> false }


        //attaching event to button
        val btnOpen = findViewById<BootstrapButton>(R.id.btnOpen)
        btnOpen.setOnClickListener {
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(filmsCarouselRecyclerAdapter.currentPosition)!!
            presenter.onFilmClick(
                  filmsList[filmsCarouselRecyclerAdapter.currentPosition]
                , viewHolder.itemView.shortFilmWrapper
                , viewHolder.itemView.shortFilmImage
                , viewHolder.itemView.tvFilmCaption
                , viewHolder.itemView.shortFilmGenresWrapper
                , viewHolder.itemView.ratingText
                , viewHolder.itemView.ratingBar
            )
        }

    }

    private fun loadBackgroundImages(
        filmsList: Array<FilmSimple>,
        scrollLinearLayout: LinearLayout
    ) {
        var listCopy = filmsList.copyOf()
       // listCopy = listCopy.first()
        //listCopy += listCopy.first()
        listCopy = arrayOf(listCopy.last()) + listCopy + arrayOf(listCopy.first())//?
        listCopy.reverse()
        listCopy.forEachIndexed  { index, _ ->

            val imageView = ImageView(this)
            val layoutParams = LinearLayout.LayoutParams(getScreenWidth(), LinearLayout.LayoutParams.MATCH_PARENT)
            imageView.layoutParams = layoutParams
            scrollLinearLayout.addView(imageView)

            val imageUrl = Configuration.getImageFullUrl(listCopy[index].posterPath)
            Glide.with(this)
                .load(imageUrl)
                .centerCrop()
                .into(imageView)

        }
    }

    private fun getScreenWidth(): Int {
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        return size.x
    }


    override fun goToFilmDescription(
        filmSimple: FilmSimple,
        filmCredit: FilmCredit,
        viewWrapper: View,
        viewImage: View,
        viewCaption: View,
        viewGenres: View,
        viewRatingText: View,
        viewRatingBar: View
    ) {

        val p1 = androidx.core.util.Pair(viewWrapper, "wrapper")
        val p2 = androidx.core.util.Pair(viewImage, "filmImage")
        val p3 = androidx.core.util.Pair(viewCaption, "filmCaption")
        val p4 = androidx.core.util.Pair(viewCaption, "filmGenres")
        val p5 = androidx.core.util.Pair(viewRatingText, "ratingText")
        val p6 = androidx.core.util.Pair(viewRatingBar, "ratingBar")

        val activityOptionsCompat =
            ActivityOptionsCompat.makeSceneTransitionAnimation(this, p1, p2, p3, p4, p5, p6)

        val intent = FilmDescriptionActivity.getIntent(this, filmSimple, filmCredit)

        startActivity(intent, activityOptionsCompat.toBundle())
    }

}

