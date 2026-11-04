package com.example.audio

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import com.example.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class QuranSurahItem(
    val name: String,
    val ayahText: String,
    val translation: String,
    val rawResId: Int = R.raw.quran_recitation
)

object AudioPlaybackManager {
    private var mediaPlayer: MediaPlayer? = null
    private var progressJob: Job? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentTrackTitle = MutableStateFlow("سورة الفاتحة")
    val currentTrackTitle: StateFlow<String> = _currentTrackTitle.asStateFlow()

    private val _currentReciter = MutableStateFlow("القارئ: مشاري راشد العفاسي")
    val currentReciter: StateFlow<String> = _currentReciter.asStateFlow()

    private val _currentAyahText = MutableStateFlow("بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ • الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ")
    val currentAyahText: StateFlow<String> = _currentAyahText.asStateFlow()

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress.asStateFlow()

    val availableSurahs = listOf(
        QuranSurahItem(
            name = "سورة الفاتحة",
            ayahText = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ • الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ • الرَّحْمَٰنِ الرَّحِيمِ • مَالِكِ يَوْمِ الدِّينِ",
            translation = "The Opening - Al-Fatihah",
            rawResId = R.raw.quran_recitation
        ),
        QuranSurahItem(
            name = "آية الكرسي (سورة البقرة)",
            ayahText = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ لَّهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ",
            translation = "Ayat Al-Kursi - The Throne Verse",
            rawResId = R.raw.quran_recitation
        ),
        QuranSurahItem(
            name = "سورة الإخلاص",
            ayahText = "قُلْ هُوَ اللَّهُ أَحَدٌ • اللَّهُ الصَّمَدُ • لَمْ يَلِدْ وَلَمْ يُولَدْ • وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ",
            translation = "The Sincerity - Al-Ikhlas",
            rawResId = R.raw.quran_recitation
        ),
        QuranSurahItem(
            name = "سورة الفلق",
            ayahText = "قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ • مِن شَرِّ مَا خَلَقَ • وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ",
            translation = "The Daybreak - Al-Falaq",
            rawResId = R.raw.quran_recitation
        ),
        QuranSurahItem(
            name = "سورة الناس",
            ayahText = "قُلْ أَعُوذُ بِرَبِّ النَّاسِ • مَلِكِ النَّاسِ • إِلَٰهِ النَّاسِ • مِن شَرِّ الْوَسْوَاسِ الْخَنَّاسِ",
            translation = "Mankind - An-Nas",
            rawResId = R.raw.quran_recitation
        ),
        QuranSurahItem(
            name = "سورة الملك (تبارك)",
            ayahText = "تَبَارَكَ الَّذِي بِيَدِهِ الْمُلْكُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ • الَّذِي خَلَقَ الْمَوْتَ وَالْحَيَاةَ لِيَبْلُوَكُمْ أَيُّكُمْ أَحْسَنُ عَمَلًا",
            translation = "The Sovereignty - Al-Mulk",
            rawResId = R.raw.quran_recitation
        )
    )

    val reciters = listOf(
        "مشاري راشد العفاسي",
        "عبد الباسط عبد الصمد",
        "ماهر المعيقلي",
        "سعد الغامدي",
        "ياسر الدوسري",
        "محمود خليل الحصري"
    )

    fun playSurah(context: Context, surah: QuranSurahItem, reciterName: String = reciters[0]) {
        stop()
        try {
            _currentTrackTitle.value = surah.name
            _currentReciter.value = "القارئ: $reciterName"
            _currentAyahText.value = surah.ayahText

            mediaPlayer = MediaPlayer.create(context.applicationContext, surah.rawResId).apply {
                isLooping = true
                setOnCompletionListener {
                    _isPlaying.value = false
                    _progress.value = 0f
                }
                start()
            }
            _isPlaying.value = true
            startProgressTracker()
        } catch (e: Exception) {
            _isPlaying.value = false
        }
    }

    fun playAdhan(context: Context) {
        stop()
        try {
            _currentTrackTitle.value = "صوت الأذان المبارك"
            _currentReciter.value = "أذان الحرم المكي الشريف"
            _currentAyahText.value = "حَانَ الآنَ مَوْعِدُ الصَّلَاةِ • حَيَّ عَلَى الصَّلَاةِ، حَيَّ عَلَى الْفَلَاحِ"

            mediaPlayer = MediaPlayer.create(context.applicationContext, R.raw.adhan_chime).apply {
                setOnCompletionListener {
                    _isPlaying.value = false
                    _progress.value = 0f
                }
                start()
            }
            _isPlaying.value = true
            startProgressTracker()
        } catch (e: Exception) {
            _isPlaying.value = false
        }
    }

    fun playCustomAudioUri(context: Context, uri: Uri, title: String) {
        stop()
        try {
            _currentTrackTitle.value = title
            _currentReciter.value = "تسجيل صوتي خارجي"
            _currentAyahText.value = "تشغيل ملف صوتي من الهاتف بدون نت"

            mediaPlayer = MediaPlayer().apply {
                setDataSource(context.applicationContext, uri)
                prepare()
                setOnCompletionListener {
                    _isPlaying.value = false
                    _progress.value = 0f
                }
                start()
            }
            _isPlaying.value = true
            startProgressTracker()
        } catch (e: Exception) {
            _isPlaying.value = false
        }
    }

    fun togglePlayPause(context: Context) {
        val player = mediaPlayer
        if (player != null) {
            if (player.isPlaying) {
                player.pause()
                _isPlaying.value = false
            } else {
                player.start()
                _isPlaying.value = true
                startProgressTracker()
            }
        } else {
            // Start default surah
            playSurah(context, availableSurahs[0])
        }
    }

    fun pause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                _isPlaying.value = false
            }
        }
    }

    fun stop() {
        progressJob?.cancel()
        progressJob = null
        mediaPlayer?.let {
            try {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            } catch (e: Exception) {
                // ignore
            }
        }
        mediaPlayer = null
        _isPlaying.value = false
        _progress.value = 0f
    }

    private fun startProgressTracker() {
        progressJob?.cancel()
        progressJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive && mediaPlayer != null && _isPlaying.value) {
                val player = mediaPlayer
                if (player != null && player.isPlaying && player.duration > 0) {
                    _progress.value = player.currentPosition.toFloat() / player.duration.toFloat()
                }
                delay(300)
            }
        }
    }
}
