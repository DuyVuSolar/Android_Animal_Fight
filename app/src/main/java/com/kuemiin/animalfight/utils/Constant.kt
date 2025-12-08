package com.kuemiin.animalfight.utils

import android.content.Context
import android.content.res.Resources
import cn.ezandroid.lib.ezfilter.extra.sticker.Bitmap2025QuizFilter.EFFECT_ANIMAL_EFFECT
import cn.ezandroid.lib.ezfilter.extra.sticker.Bitmap2025QuizFilter.EFFECT_SING_A_SONG
import cn.ezandroid.lib.ezfilter.extra.sticker.Bitmap2025QuizFilter.EFFECT_TIKTOK_VOICE
import com.kuemiin.animalfight.R
import com.kuemiin.animalfight.model.EffectModel
import com.kuemiin.animalfight.model.QuizModel
import com.kuemiin.animalfight.model.buildQuiz
import com.kuemiin.animalfight.utils.LocaleHelper.dLocale
import com.kuemiin.animalfight.utils.extension.getApplication

object Constant {

    const val MAX_TIME_RECORD_15s = 15
    const val MAX_TIME_RECORD_60s = 60
    const val MAX_TIME_RECORD_10P = 600

    var timeRecord = MAX_TIME_RECORD_15s

    //key extra
    const val KEY_EXTRA_ID = "KEY_EXTRA_ID"
    const val KEY_EXTRA_DATA = "KEY_EXTRA_DATA"
    const val KEY_EXTRA_LANGUAGE = "KEY_EXTRA_LANGUAGE"

    const val KEY_EXTRA_BOOLEAN = "KEY_EXTRA_BOOLEAN"
    const val MY_FOLDER_EXTERNAL = "PDFViewer"

    const val REQUEST_CODE_OPEN_SETTINGS_SYSTEM = 3001

    const val MAIN_TYPE_HOME = 0
    const val MAIN_TYPE_RECORDINGS = 1
    const val MAIN_TYPE_SETTING = 2

    const val TODAY = "Today"
    const val YESTERDAY = "Yesterday"


    fun getConfigurationContext(): Context {
        val configuration: android.content.res.Configuration = getApplication().resources.configuration
        configuration.setLocale(dLocale)
        configuration.setLayoutDirection(dLocale)
        // Create a new context with this configuration
        val resources: Resources = getApplication().resources
        resources.updateConfiguration(configuration, resources.displayMetrics)
        return getApplication().createConfigurationContext(configuration)
    }

//    us
    private var singWithWordsEnglish = arrayListOf(
        buildQuiz("Love"),
        buildQuiz("Baby"),
        buildQuiz("Red"),
        buildQuiz("Me"),
        buildQuiz("Heart"),
        buildQuiz("Party"),
        buildQuiz("Time"),
        buildQuiz("Rain"),
        buildQuiz("Yesterday"),
        buildQuiz("Tomorrow"),
        buildQuiz("Sorry"),
        buildQuiz("Blue"),
        buildQuiz("Purple"),
        buildQuiz("Money"),
        buildQuiz("Life"),
        buildQuiz("World"),
        buildQuiz("Friend"),
        buildQuiz("Day"),
        buildQuiz("Night"),
        buildQuiz("Dream"),
        buildQuiz("Cry"),
        buildQuiz("Forever"),
        buildQuiz("Smile"),
        buildQuiz("Happy"),
        buildQuiz("Kiss"),
        buildQuiz("Dance"),
        buildQuiz("Girl"),
        buildQuiz("Sun"),
        buildQuiz("Boy"),
    )

//    vn
    private var singWithWordsVN = arrayListOf(
        buildQuiz("yêu"),
        buildQuiz("tim"),
        buildQuiz("đêm"),
        buildQuiz("nắng"),
        buildQuiz("mơ"),
        buildQuiz("cưới"),
        buildQuiz("đêm"),
        buildQuiz("chia tay"),
        buildQuiz("ngày mai"),
        buildQuiz("niềm tin"),
        buildQuiz("ánh sáng"),
        buildQuiz("đau"),
        buildQuiz("chờ"),
        buildQuiz("say"),
        buildQuiz("quên"),
        buildQuiz("nhớ"),
        buildQuiz("hôn"),
        buildQuiz("mong"),
        buildQuiz("gần"),
        buildQuiz("xa"),
        buildQuiz("khóc"),
        buildQuiz("cười"),
        buildQuiz("vui"),
        buildQuiz("mưa"),
        buildQuiz("trời"),
        buildQuiz("thương"),
        buildQuiz("người"),
        buildQuiz("trong"),
        buildQuiz("nhớ"),
        buildQuiz("buồn"),
    )

//    th
    private var singWithWordsThai = arrayListOf(
        buildQuiz("รัก"),
        buildQuiz("ใจ"),
        buildQuiz("ฝัน"),
        buildQuiz("คืน"),
        buildQuiz("ไฟ"),
        buildQuiz("แสง"),
        buildQuiz("ฝน"),
        buildQuiz("ฟ้า"),
        buildQuiz("เธอ"),
        buildQuiz("เต้น"),
        buildQuiz("หวาน"),
        buildQuiz("ตลอดไป"),
        buildQuiz("เศร้า"),
        buildQuiz("ยิ้ม"),
        buildQuiz("หวัง"),
        buildQuiz("คิดถึง"),
        buildQuiz("บ้าน"),
        buildQuiz("ดาว"),
        buildQuiz("เหงา"),
        buildQuiz("เทวดา"),
        buildQuiz("เวลา"),
        buildQuiz("อยู่"),
        buildQuiz("จูบ"),
        buildQuiz("ล้ม"),
        buildQuiz("วิ่ง"),
        buildQuiz("สวรรค์"),
        buildQuiz("บ้า"),
        buildQuiz("เสียง"),
        buildQuiz("ความจริง"),
        buildQuiz("คืนนี้"),
    )

//    es
    private var singWithWordsSpanish = arrayListOf(
        buildQuiz("amor"),
        buildQuiz("corazón"),
        buildQuiz("sueño"),
        buildQuiz("noche"),
        buildQuiz("fuego"),
        buildQuiz("luz"),
        buildQuiz("lluvia"),
        buildQuiz("cielo"),
        buildQuiz("bebé"),
        buildQuiz("bailar"),
        buildQuiz("magia"),
        buildQuiz("siempre"),
        buildQuiz("romper"),
        buildQuiz("llorar"),
        buildQuiz("sonrisa"),
        buildQuiz("esperanza"),
        buildQuiz("perdido"),
        buildQuiz("casa"),
        buildQuiz("salvaje"),
        buildQuiz("estrellas"),
        buildQuiz("solo/sola"),
        buildQuiz("dulce"),
        buildQuiz("ángel"),
        buildQuiz("tiempo"),
        buildQuiz("quedar"),
        buildQuiz("beso"),
        buildQuiz("caer"),
        buildQuiz("correr"),
        buildQuiz("cielo"),
        buildQuiz("loco/loca"),
    )

//    fr
    private var singWithWordsFr = arrayListOf(
        buildQuiz("amour"),
        buildQuiz("cœur"),
        buildQuiz("rêve"),
        buildQuiz("nuit"),
        buildQuiz("feu"),
        buildQuiz("lumière"),
        buildQuiz("pluie"),
        buildQuiz("ciel"),
        buildQuiz("bébé / chéri(e)"),
        buildQuiz("danser"),
        buildQuiz("magie"),
        buildQuiz("toujours"),
        buildQuiz("casser"),
        buildQuiz("pleurer"),
        buildQuiz("sourire"),
        buildQuiz("espoir"),
        buildQuiz("perdu(e)"),
        buildQuiz("maison"),
        buildQuiz("sauvage"),
        buildQuiz("étoiles"),
        buildQuiz("seul(e)"),
        buildQuiz("doux/douce"),
        buildQuiz("ange"),
        buildQuiz("temps"),
        buildQuiz("rester"),
        buildQuiz("baiser"),
        buildQuiz("tomber"),
        buildQuiz("courir"),
        buildQuiz("paradis"),
        buildQuiz("fou/folle"),
    )

//    hi
    private var singWithWordsFrHindi = arrayListOf(
        buildQuiz("प्यार"),
        buildQuiz("दिल"),
        buildQuiz("सपना"),
        buildQuiz("रात"),
        buildQuiz("आग"),
        buildQuiz("रोशनी"),
        buildQuiz("बारिश"),
        buildQuiz("आसमान"),
        buildQuiz("जानू"),
        buildQuiz("नाच"),
        buildQuiz("जादू"),
        buildQuiz("हमेशा"),
        buildQuiz("टूटना"),
        buildQuiz("रोना"),
        buildQuiz("मुस्कान"),
        buildQuiz("उम्मीद"),
        buildQuiz("खोया"),
        buildQuiz("घर"),
        buildQuiz("जंगली"),
        buildQuiz("सितारे"),
        buildQuiz("अकेला/अकेली"),
        buildQuiz("मीठा/मीठी"),
        buildQuiz("फ़रिश्ता"),
        buildQuiz("समय"),
        buildQuiz("रहना"),
        buildQuiz("चुम्बन"),
        buildQuiz("गिरना"),
        buildQuiz("भागना"),
        buildQuiz("स्वर्ग"),
        buildQuiz("पागल"),
    )

//    cn
    private var singWithWordsFrChina = arrayListOf(
        buildQuiz("爱"),
        buildQuiz("心"),
        buildQuiz("梦"),
        buildQuiz("火"),
        buildQuiz("光"),
        buildQuiz("雨"),
        buildQuiz("天空"),
        buildQuiz("宝贝"),
        buildQuiz("跳舞"),
        buildQuiz("魔法"),
        buildQuiz("永远"),
        buildQuiz("破碎"),
        buildQuiz("哭"),
        buildQuiz("笑"),
        buildQuiz("希望"),
        buildQuiz("失去"),
        buildQuiz("回家"),
        buildQuiz("夜"),
        buildQuiz("狂野"),
        buildQuiz("星星"),
        buildQuiz("孤独"),
        buildQuiz("甜蜜"),
        buildQuiz("天使"),
        buildQuiz("时间"),
        buildQuiz("留下"),
        buildQuiz("亲吻"),
        buildQuiz("追"),
        buildQuiz("奔跑"),
        buildQuiz("天堂"),
        buildQuiz("分手"),
    )

    private var tiktokVoices = arrayListOf(
        buildQuiz("1+1"),
    )

    fun getListTikTokVoiceClone() : ArrayList<QuizModel>{
        val res = arrayListOf<QuizModel>()
        tiktokVoices.forEach {
            res.add(it.copy())
        }
        res.shuffled()
        return res
    }

    fun getAllListMath(alpha: String): ArrayList<EffectModel> {
        val res = arrayListOf<EffectModel>()
        res.add(EffectModel(EFFECT_SING_A_SONG, R.drawable.bg_sing_a_song_with_word, singWithWordsEnglish.size, getListSingAlongFromAlpha(alpha)))
        res.add(EffectModel(EFFECT_TIKTOK_VOICE, R.drawable.bg_tiktok_voice_full, tiktokVoices.size, getListTikTokVoiceClone()))
        res.add(EffectModel(EFFECT_ANIMAL_EFFECT, R.drawable.bg_tiktok_voice_full, tiktokVoices.size, getListTikTokVoiceClone()))
        return res
    }

    fun getListSingAlongFromAlpha(alpha : String): ArrayList<QuizModel> {
        return if(alpha == "us") singWithWordsEnglish
        else if(alpha == "vn") singWithWordsVN
        else if(alpha == "th") singWithWordsThai
        else if(alpha == "fr") singWithWordsFr
        else if(alpha == "hi") singWithWordsFrHindi
        else if(alpha == "es") singWithWordsSpanish
        else singWithWordsFrChina
    }
}
