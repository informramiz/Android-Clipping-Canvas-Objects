package github.informramiz.clippingexample

import android.content.Context
import android.graphics.*
import android.os.Build
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

    private var roundRectF = RectF(
        rectInset,
        rectInset,
        clipRectRight - rectInset,
        clipRectBottom - rectInset
    )

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.GRAY)
        drawOriginalShape(canvas)
        drawDifferenceClippingShape(canvas)
        drawCircularClippingShape(canvas)
        drawIntersectionClippingShape(canvas)
        drawCombinedClippingShape(canvas)
        drawRoundedRectangleClippingShape(canvas)
        drawRectangleClippingShape(canvas)
        drawTranslatedTextShape(canvas)
        drawSkewedTextShape(canvas)
    }

    private fun drawClippedRectangle(canvas: Canvas) {
        //clip the entire frame to this rectangle, this will make area
        //other than this rectangle unavailable for drawing and all drawing commands
        //will actually happen in the boundary of this rectangle only
        canvas.clipRect(clipRectLeft, clipRectTop, clipRectRight, clipRectBottom)
        //set the rectangle color to white
        canvas.drawColor(Color.WHITE)

        //draw a line inside the clipped rectangle, color it red
        paint.color = Color.RED
        canvas.drawLine(clipRectLeft, clipRectTop, clipRectBottom, clipRectRight, paint)

        //draw a circle inside the clipped rectangle, color it green
        paint.color = Color.GREEN
        canvas.drawCircle(circleRadius, clipRectBottom - circleRadius, circleRadius, paint)

        //draw text, color it blue
        paint.color = Color.BLUE
        paint.textAlign = Paint.Align.RIGHT
        paint.textSize = textSize
        canvas.drawText(resources.getString(R.string.clipping), clipRectRight, textOffset, paint)
    }

    private fun drawOriginalShape(canvas: Canvas) {
        //save the current state of canvas (to save the original origin of the canvas)
        canvas.save()
        //translate the origin of the canvas to our desired position
        canvas.translate(columnOne, rowOne)
        //now draw the original rectangle with respect to this origin
        drawClippedRectangle(canvas)
        //restore the original origin of the canvas
        canvas.restore()
    }

    private fun drawDifferenceClippingShape(canvas: Canvas) {
        canvas.save()
        canvas.translate(columnTwo, rowOne)

        //we will achieve this effect by clipping (making unavailable) 2 rectangles (1 inside the other) and
        //only their difference (region between rectangles) will be available for drawing when we
        //draw the original shape

        //draw the first rectangle
        canvas.clipRect(2 * rectInset, 2 * rectInset, clipRectRight - rectInset * 2, clipRectBottom - rectInset * 2)

        //draw the 2nd rectangle for clipping by telling the OS that only their difference is
        //available for drawing
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            canvas.clipOutRect(
                rectInset * 4,
                rectInset * 4,
                clipRectRight - rectInset * 4,
                clipRectBottom - rectInset * 4
            )
        } else {
            canvas.clipRect(rectInset * 4,
                rectInset * 4,
                clipRectRight - rectInset * 4,
                clipRectBottom - rectInset * 4,
                Region.Op.DIFFERENCE
            )
        }

        //after above clipping, only the difference (space) between 2 rectangles is available
        //for drawing so drawing the original shape now will draw on that space
        drawClippedRectangle(canvas)
        canvas.restore()
    }

    /**
     * Draws the a circle to make this area unavailable for clipping
     */
    private fun drawCircularClippingShape(canvas: Canvas) {
        canvas.save()
        canvas.translate(columnOne, rowTwo)

        // Clears any lines and curves from the path but unlike reset(),
        // keeps the internal data structure for faster reuse.
        path.rewind()
        path.addCircle(circleRadius, clipRectBottom - circleRadius, circleRadius, Path.Direction.CCW)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            canvas.clipOutPath(path)
        } else {
            canvas.clipPath(path, Region.Op.DIFFERENCE)
        }

        drawClippedRectangle(canvas)
        canvas.restore()
    }

    private fun drawIntersectionClippingShape(canvas: Canvas) {
        canvas.save()
        canvas.translate(columnTwo, rowTwo)

        //we will achieve this effect by clipping (making unavailable) 2 rectangles (1 inside the other) and
        //only their intersection (region common between rectangles) will be available for drawing when we
        //draw the original shape

        //draw the first rectangle
        canvas.clipRect(2 * rectInset, 2 * rectInset, clipRectRight - rectInset * 2, clipRectBottom - rectInset * 2)

        //draw the 2nd rectangle for clipping by telling the OS that only their intersection is
        //available for drawing
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            canvas.clipRect(
                rectInset * 4,
                rectInset * 4,
                clipRectRight - rectInset * 4,
                clipRectBottom - rectInset * 4
            )
        } else {
            canvas.clipRect(rectInset * 4,
                rectInset * 4,
                clipRectRight - rectInset * 4,
                clipRectBottom - rectInset * 4,
                Region.Op.INTERSECT
            )
        }

        //after above clipping, only the intersection area between 2 rectangles is available
        //for drawing so drawing the original shape now will draw on that space
        drawClippedRectangle(canvas)
        canvas.restore()
    }

    private fun drawCombinedClippingShape(canvas: Canvas) {
        canvas.save()
        canvas.translate(columnOne, rowThree)
        path.rewind()
        path.addCircle(
            clipRectLeft + rectInset + circleRadius,
            clipRectTop + circleRadius + rectInset,
            circleRadius,Path.Direction.CCW
        )
        path.addRect(
            clipRectRight / 2 - circleRadius,
            clipRectTop + circleRadius + rectInset,
            clipRectRight / 2 + circleRadius,
            clipRectBottom - rectInset,Path.Direction.CCW
        )
        //only the area defined by the shapes (circle+rectangle) inside the path is available for drawing
        canvas.clipPath(path)
        drawClippedRectangle(canvas)
        canvas.restore()
    }

    /**
     * Draw a rounded rectangle to make edges and sides clipped (unavailable) for drawing
     */
    private fun drawRoundedRectangleClippingShape(canvas: Canvas) {
        canvas.save()
        canvas.translate(columnTwo, rowThree)
        //clear shapes in path
        path.rewind()
        path.addRoundRect(roundRectF, clipRectRight/4, clipRectRight/4, Path.Direction.CCW)
        //now clip this path, making area covered by shapes in the path unavailable for drawing
        canvas.clipPath(path)

        drawClippedRectangle(canvas)
        canvas.restore()
    }

    /**
     * Draw a rectangle to make edges and sides clipped (unavailable) for drawing
     */
    private fun drawRectangleClippingShape(canvas: Canvas) {
        canvas.save()
        canvas.translate(columnOne, rowFour)
        //clear shapes in path
        path.rewind()
        path.addRect(roundRectF, Path.Direction.CCW)
        //now clip this path, making area covered by shapes in the path unavailable for drawing
        canvas.clipPath(path)

        drawClippedRectangle(canvas)
        canvas.restore()
    }

    private fun drawTranslatedTextShape(canvas: Canvas) {
        canvas.save()
        canvas.translate(columnTwo, textRow)
        paint.color = Color.GREEN
        // Align the START side of the text with the origin
        paint.textAlign = Paint.Align.LEFT
        canvas.drawText(resources.getString(R.string.translated), clipRectLeft, clipRectTop, paint)
        canvas.restore()
    }

    private fun drawSkewedTextShape(canvas: Canvas) {
        canvas.save()
        canvas.translate(columnTwo, textRow)
        canvas.skew(0.2f, 0.3f)
        paint.color = Color.YELLOW
        // Align the END side of the text with the origin
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText(resources.getString(R.string.skewed), clipRectLeft, clipRectTop, paint)
        canvas.restore()
    }
}