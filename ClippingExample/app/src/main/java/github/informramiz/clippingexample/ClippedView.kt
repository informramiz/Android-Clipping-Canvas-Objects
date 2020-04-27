package github.informramiz.clippingexample

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

/**
 * Created by Ramiz Raja on 27/04/2020
 */
class ClippedView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attributeSet, defStyle) {
    private val path = Path()
    private val paint = Paint().apply {
        isAntiAlias = true
        textSize = resources.getDimension(R.dimen.textSize)
        strokeWidth = resources.getDimension(R.dimen.strokeWidth)
    }

    private val clipRectRight = resources.getDimension(R.dimen.clipRectRight)
    private val clipRectBottom = resources.getDimension(R.dimen.clipRectBottom)
    private val clipRectTop = resources.getDimension(R.dimen.clipRectTop)
    private val clipRectLeft = resources.getDimension(R.dimen.clipRectLeft)

    private val rectInset = resources.getDimension(R.dimen.rectInset)
    private val smallRectOffset = resources.getDimension(R.dimen.smallRectOffset)

    private val circleRadius = resources.getDimension(R.dimen.circleRadius)
    private val textOffset = resources.getDimension(R.dimen.textOffset)
    private val textSize = resources.getDimension(R.dimen.textSize)

    //the app is divided into 2 columns and 4 shape rows
    //define column coordinates/offsets
    private val columnOne = rectInset
    private val columnTwo = columnOne + rectInset + clipRectRight
    //define row offsets
    private val rowOne = rectInset
    private val rowTwo = rowOne + rectInset + clipRectBottom
    private val rowThree = rowTwo + rectInset + clipRectBottom
    private val rowFour = rowThree + rectInset + clipRectBottom
    private val textRow = rowFour + (1.5f * clipRectBottom)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.GRAY)
        drawClippedRectangle(canvas)
    }

    private fun drawClippedRectangle(canvas: Canvas) {
        //clip the entire frame to this rectangle, this will make area
        //other than this rectangle unavailable for drawing and all drawing commands
        //will actually happen in the boundary of this rectangle only
        canvas.clipRect(clipRectLeft, clipRectTop, clipRectRight, clipRectBottom)
        //set the rectangle color to white
        canvas.drawColor(Color.WHITE)
    }
}