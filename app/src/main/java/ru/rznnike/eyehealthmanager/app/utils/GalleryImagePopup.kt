package ru.rznnike.eyehealthmanager.app.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.content.ContextWrapper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.alexvasilkov.gestures.views.GestureImageView
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.utils.extensions.default
import ru.rznnike.eyehealthmanager.app.utils.extensions.load
import ru.rznnike.eyehealthmanager.domain.model.RemoteFile

@SuppressLint("InflateParams")
class GalleryImagePopup(
    context: Context,
    parentView: View,
    images: List<RemoteFile>,
    currentIndex: Int = 0
) : PopupWindow(
    (context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater)
        .inflate(R.layout.dialog_gallery, null),
    ViewGroup.LayoutParams.MATCH_PARENT,
    ViewGroup.LayoutParams.MATCH_PARENT
) {
    init {
        elevation = 5.0f
        isOutsideTouchable = true
        isFocusable = true

        // Using custom zoom-aware pager
        val photoPager = contentView.findViewById<PhotoGalleryViewPager>(R.id.pager)
        photoPager.adapter = photosAdapter(
            context = context,
            images = images
        ) { dismiss() }
        photoPager.currentItem = currentIndex

        showAtLocation(parentView, Gravity.CENTER, 0, 0)
        getActivity(context)?.window?.statusBarColor = ContextCompat.getColor(context, R.color.colorGalleryPopupBackground)
        setOnDismissListener {
            getActivity(context)?.window?.statusBarColor = ContextCompat.getColor(context, R.color.colorTransparent)
        }
    }

    private fun getActivity(context: Context): Activity? {
        var outerContext = context
        while (outerContext is ContextWrapper) {
            if (outerContext is Activity) {
                return outerContext
            }
            outerContext = outerContext.baseContext
        }
        return null
    }

    private fun photosAdapter(
        context: Context,
        images: List<RemoteFile>,
        onImageClick: View.OnClickListener
    ) = object : PagerAdapter() {
        override fun getCount(): Int {
            return images.size
        }

        override fun isViewFromObject(view: View, item: Any): Boolean {
            return view === item
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val pager = container as ViewPager
            val imageView = GestureImageView(context)
            imageView.controller.enableScrollInViewPager(pager)

            imageView.load(
                remoteFile = images[position]
            ) {
                default(imageView, R.drawable.icon)
            }
            imageView.setOnClickListener(onImageClick)

            pager.addView(imageView, 0)
            return imageView
        }

        override fun destroyItem(container: ViewGroup, position: Int, item: Any) {
            container.removeView(item as View)
        }
    }
}
