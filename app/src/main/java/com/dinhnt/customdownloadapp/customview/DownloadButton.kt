package com.dinhnt.customdownloadapp.customview

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.core.content.withStyledAttributes
import com.dinhnt.customdownloadapp.R
import com.google.android.material.color.MaterialColors


class DownloadButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var bgColor: Int = 0
    private var textColor: Int = 0
    private lateinit var loadAnimator: ValueAnimator
    private var loadingWidth: Float = 0f
    private val centerPoint: PointF = PointF(0.0f, 0.0f)
    private var bgRect: Rect = Rect(0, 0, 0, 0)
    private var arcRect: RectF = RectF(0f, 0f, 0f, 0f)
    private var arcSweepAngle: Float = 0f
    private var loadingRect: Rect = Rect(0, 0, 0, 0)
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = kotlin.run {
            val t = TextView(context)
            t.textSize = 24f
            return@run t.textSize
        }
        typeface = Typeface.create("", Typeface.BOLD)
    }

    init {
        context.withStyledAttributes(attrs, R.styleable.DownloadButtonColor) {
            bgColor = getColor(R.styleable.DownloadButtonColor_backgroundColor, 0)
            textColor = getColor(R.styleable.DownloadButtonColor_textColor, 0)
        }
        isClickable = true
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        centerPoint.x = width.toFloat() / 2
        centerPoint.y = height.toFloat() / 2
        bgRect = Rect(0, 0, width, height)
        loadingRect = Rect(0, 0, 0, height)
        val arcRadius = (height / 4).toFloat()
        val arcX = width - arcRadius * 2
        val arcY = centerPoint.y
        arcRect = RectF(
            arcX - arcRadius,
            arcY - arcRadius,
            arcX + arcRadius,
            arcY + arcRadius,
        )
    }

    override fun performClick(): Boolean {
        super.performClick()
        invalidate()
        return true
    }

    fun createLoadingAnimation() {
        loadAnimator = ValueAnimator.ofInt(0, (width * 0.6).toInt())
        loadAnimator.interpolator = AccelerateDecelerateInterpolator()
        loadAnimator.duration = 2000
        loadAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                isEnabled = false
            }
        })
        loadAnimator.addUpdateListener { animation ->
            loadingRect.right = animation.animatedValue as Int
            arcSweepAngle = (animation.animatedValue as Int).toFloat() / width * 360
            invalidate()
        }
        loadAnimator.start()
    }

    fun finishLoadingAnimation() {
        loadAnimator.cancel()
        loadAnimator = ValueAnimator.ofInt(loadAnimator.animatedValue as Int, width)
        loadAnimator.duration = 500
        loadAnimator.addUpdateListener { animation ->
            loadingRect.right = animation.animatedValue as Int
            arcSweepAngle = (animation.animatedValue as Int).toFloat() / width * 360
            invalidate()
        }
        loadAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                loadingRect.right = 0
                arcSweepAngle = 0f
                isEnabled = true
                invalidate()
                super.onAnimationEnd(animation)
            }
        })
        loadAnimator.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.color = bgColor
        canvas.drawRect(bgRect, paint)
        paint.color =
            MaterialColors.getColor(this, com.google.android.material.R.attr.colorSecondary)
        canvas.drawRect(loadingRect, paint)
        paint.color = textColor
        canvas.drawText("DOWNLOAD", centerPoint.x - 10, centerPoint.y + 15, paint)
        paint.color = Color.YELLOW
        canvas.drawArc(
            arcRect, 0f, arcSweepAngle, true, paint
        )
    }
}