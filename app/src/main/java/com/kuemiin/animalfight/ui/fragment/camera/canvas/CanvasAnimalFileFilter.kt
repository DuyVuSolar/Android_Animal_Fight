package com.kuemiin.animalfight.ui.fragment.camera.canvas

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import com.kuemiin.animalfight.BaseApplication
import com.kuemiin.animalfight.R
import com.kuemiin.animalfight.model.QuizModel
import com.kuemiin.animalfight.ui.fragment.camera.canvas.CanvasTiktokFileFilter.Companion.folderAnimal
import com.kuemiin.animalfight.ui.fragment.camera.canvas.CanvasTiktokFileFilter.Companion.folderRetangle
import com.kuemiin.animalfight.ui.fragment.camera.canvas.CanvasTiktokFileFilter.Companion.getFilterFormatFile
import com.kuemiin.animalfight.ui.fragment.camera.canvas.CanvasTiktokFileFilter.Companion.totalAnimals
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream


@Suppress("DEPRECATION")
@SuppressLint("DefaultLocale")
class CanvasAnimalFileFilter {
//    region canvas prepare filter
    private var mContext : Context? = BaseApplication.instance
    private var mTypeFilter : String = ""
    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    private var textQuestion : String = ""

    private var questionPaint = Paint().apply { isAntiAlias = true }
    private var outlineTextPaint = Paint().apply { isAntiAlias = true }
    private var outlineRectPaint = Paint().apply { isAntiAlias = true }
    private var outlineAnimalPaint = Paint().apply { isAntiAlias = true }

    private var frameBitmapQuestion : Bitmap? = null//this frame will contain mBitmapQuestion
    private val bitmapWidthQuestion = 800
    private val bitmapHeightQuestion = 500

    private val bitmapWidthAnimal = 200
    private val bitmapHeightAnimal = 200

    private val quality = 70
    private val compressConfig = Bitmap.CompressFormat.WEBP
    private val endFile = ".webp"
    private var quizModels = arrayListOf<QuizModel>()
    private var mCanvasDone : () -> Unit = {}

    fun setAnimalAndCanvas(context : Context, quizs : ArrayList<QuizModel>, typeFilter : String, callback : () -> Unit){
        mContext = context
        mTypeFilter = "typeFilter"
        mCanvasDone = callback
        quizModels.clear()
        quizModels.addAll(quizs)
        setText()
    }

    private fun setText(){
        scope.launch {
            withContext(Dispatchers.Default){
                if(frameBitmapQuestion == null) {
                    frameBitmapQuestion = Bitmap.createBitmap(bitmapWidthQuestion, bitmapHeightQuestion, Bitmap.Config.ARGB_8888)
                }


                questionPaint.color = Color.parseColor("#FFFFFF")
                questionPaint.style = Paint.Style.FILL
                questionPaint.textAlign = Paint.Align.CENTER
                questionPaint.typeface = Typeface.DEFAULT_BOLD

                outlineTextPaint.color = Color.BLACK
                outlineTextPaint.style = Paint.Style.STROKE
                outlineTextPaint.strokeWidth = 4f
                outlineTextPaint.typeface = questionPaint.typeface
                outlineTextPaint.textAlign = Paint.Align.CENTER

                outlineRectPaint.color = Color.BLACK
                outlineRectPaint.style = Paint.Style.STROKE
                outlineRectPaint.strokeWidth = 18f
                outlineRectPaint.typeface = questionPaint.typeface
                outlineRectPaint.textAlign = Paint.Align.CENTER

                outlineAnimalPaint.color = Color.YELLOW
                outlineAnimalPaint.style = Paint.Style.STROKE
                outlineAnimalPaint.strokeWidth = 10f
                outlineAnimalPaint.typeface = questionPaint.typeface
                outlineAnimalPaint.textAlign = Paint.Align.CENTER

                for(i in 1..10){
                    createRectangleBitmapAndSaveFile(i)
                }

                val listAnimal = arrayListOf(
                    R.drawable.animal_01,
                    R.drawable.animal_02,
                    R.drawable.animal_03,
                    R.drawable.animal_04,
                    R.drawable.animal_05,
                    R.drawable.animal_06,
                    R.drawable.animal_07,
                    R.drawable.animal_08,
                )
                totalAnimals = listAnimal.size
                listAnimal.forEachIndexed { index, i ->
                    createAnimalBitmapAndSaveFile(index, i)
                }

                mCanvasDone.invoke()
            }
        }
    }

    private fun createRectangleBitmapAndSaveFile(indexQuiz : Int) {
        frameBitmapQuestion ?: return
        val folderSingWords = File(getFolderFilter(), "/${folderRetangle}")
        if(!folderSingWords.exists()) folderSingWords.mkdirs()
        val file = File(folderSingWords, "Rect_${indexQuiz}${endFile}")
//        if(file.exists() && file.length() > 0) return

        frameBitmapQuestion?.eraseColor( Color.GRAY)
        val canvasQuestion = Canvas(frameBitmapQuestion!!)
        canvasQuestion.drawRect(Rect(0,0,frameBitmapQuestion!!.width, frameBitmapQuestion!!.height), outlineRectPaint)
        questionPaint.textSize = frameBitmapQuestion!!.height * 0.15f
        outlineTextPaint.textSize = questionPaint.textSize

        var yText = frameBitmapQuestion!!.height * 1/6
        canvasQuestion.drawText("$indexQuiz/10", frameBitmapQuestion!!.width / 2f , yText.toFloat(), questionPaint)
        canvasQuestion.drawText("$indexQuiz/10", frameBitmapQuestion!!.width / 2f , yText.toFloat(), outlineTextPaint)

        // Save to file
        FileOutputStream(file).use { out ->
            frameBitmapQuestion?.compress(compressConfig, quality, out)
        }
    }

    private fun createAnimalBitmapAndSaveFile(indexQuiz: Int, res: Int) {
        val source = BitmapFactory.decodeResource(mContext?.resources, res)
        val mBitmapAnimal = Bitmap.createScaledBitmap(source, bitmapWidthAnimal, bitmapHeightAnimal, true)

        val canvasQuestion = Canvas(mBitmapAnimal)

        val folderSingWords = File(getFolderFilter(), "/${folderAnimal}")
        if(!folderSingWords.exists()) folderSingWords.mkdirs()
        val file = File(folderSingWords, "Animal_${indexQuiz}${endFile}")
        val fileSelect = File(folderSingWords, "Animal_${indexQuiz + totalAnimals}${endFile}")
        if(file.exists() && file.length() > 0 && fileSelect.exists() && fileSelect.length() > 0) return

        outlineAnimalPaint.color = Color.BLACK
        canvasQuestion.drawRect(Rect(0,0,mBitmapAnimal.width, mBitmapAnimal.height), outlineAnimalPaint)
        FileOutputStream(file).use { out ->
            mBitmapAnimal.compress(compressConfig, quality, out)
        }

        outlineAnimalPaint.color = Color.YELLOW
        canvasQuestion.drawRect(Rect(0,0,mBitmapAnimal.width, mBitmapAnimal.height), outlineAnimalPaint)
        FileOutputStream(fileSelect).use { out ->
            mBitmapAnimal.compress(compressConfig, quality, out)
        }
    }

    private fun getFolderFilter(): File {
        val folder = File(mContext?.filesDir, getFilterFormatFile(mTypeFilter))
        if(!folder.exists()) folder.mkdirs()
        return folder
    }


    private fun getFolderSize(folder: File): Long {
        var totalSize: Long = 0
        folder.listFiles()?.forEach { file ->
            totalSize += if (file.isFile) {
                file.length() // Size of the file
            } else {
                getFolderSize(file) // Recursively calculate size for subdirectories
            }
        }
        return totalSize
    }
    //endregion
}