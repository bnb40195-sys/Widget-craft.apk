package com.example.data.model

data class OfflineQuote(
    val arabic: String,
    val english: String,
    val author: String
)

object OfflineDataProvider {
    val quotes = listOf(
        OfflineQuote(
            "لا تحسبن المجد تمراً أنت آكله، لن تبلغ المجد حتى تلعق الصبرا.",
            "Great triumphs require great perseverance.",
            "أبو تمام"
        ),
        OfflineQuote(
            "وما نيل المطالب بالتمني، ولكن تؤخذ الدنيا غلاباً.",
            "Aspirations are achieved by bold effort, not wishing.",
            "أحمد شوقي"
        ),
        OfflineQuote(
            "تفاءل بما تهوى يكن، فالعين ترى ما في القلب.",
            "Optimism shapes the path to reality.",
            "حكمة عربية"
        ),
        OfflineQuote(
            "الوقت كالسيف إن لم تقطعه قطعك.",
            "Time is a sword; master it before it cuts.",
            "مثل عربي"
        ),
        OfflineQuote(
            "من جدّ وجد، ومن زرع حصد، ومن سار على الدرب وصل.",
            "He who strives succeeds, and he who persists arrives.",
            "مثل عربي"
        ),
        OfflineQuote(
            "ابدأ من حيث أنت، واستخدم ما لديك، وافعل ما تستطيع.",
            "Start where you are, use what you have, do what you can.",
            "آرثر آش"
        ),
        OfflineQuote(
            "التركيز سر الإنجاز، ودقيقة انتباه تعادل ساعة تشتت.",
            "Deep focus multiplies your daily output exponentially.",
            "رواد الإنتاجية"
        )
    )

    fun getRandomQuote(): OfflineQuote = quotes.random()

    val preinstalledTemplates = listOf(
        WidgetConfig(
            id = "preset_cyber_clock",
            name = "ساعة السايبر النيونية",
            tagline = "ساعة رقمية بدقة عالية مع التاريخ وحالة البطارية",
            type = WidgetType.DIGITAL_CLOCK,
            theme = WidgetTheme.CYBER_NEON,
            cornerRadiusDp = 24,
            size = WidgetSize.MEDIUM_4x2,
            showDate = true,
            showBattery = true,
            showSeconds = true,
            source = "PRESET"
        ),
        WidgetConfig(
            id = "preset_daily_memo",
            name = "مفكرة الإنجاز السريعة",
            tagline = "ملاحظاتك ومهمتك الكبرى أمام عينيك دائماً",
            type = WidgetType.QUICK_NOTES,
            theme = WidgetTheme.SUNSET_BLAZE,
            cornerRadiusDp = 20,
            size = WidgetSize.MEDIUM_4x2,
            noteContent = "١. إنهاء مراجعة المشروع 🚀\n٢. ممارسة رياضة خفيفة ٣٠ دقيقة\n٣. قراءة ١٠ صفحات من كتاب اليوم",
            source = "PRESET"
        ),
        WidgetConfig(
            id = "preset_tally_dhikr",
            name = "عداد التسبيح والأذكار",
            tagline = "عداد إلكتروني تفاعلي مع زر زيادة سريع",
            type = WidgetType.HABIT_COUNTER,
            theme = WidgetTheme.EMERALD_MINT,
            cornerRadiusDp = 24,
            size = WidgetSize.MEDIUM_4x2,
            counterValue = 33,
            counterStep = 1,
            counterLabel = "سبحان الله وبحمده",
            source = "PRESET"
        ),
        WidgetConfig(
            id = "preset_battery_radar",
            name = "رادار طاقة البطارية",
            tagline = "متابعة فورية للشحن ودرجة الكفاءة وحالة النظام",
            type = WidgetType.BATTERY_DEVICE,
            theme = WidgetTheme.ROYAL_AMBER,
            cornerRadiusDp = 22,
            size = WidgetSize.WIDE_4x1,
            showBattery = true,
            source = "PRESET"
        ),
        WidgetConfig(
            id = "preset_daily_quote",
            name = "حكمة اليوم",
            tagline = "عبارات ملهمة تتجدد وتمنحك طاقة إيجابية",
            type = WidgetType.DAILY_QUOTE,
            theme = WidgetTheme.COSMIC_VIOLET,
            cornerRadiusDp = 26,
            size = WidgetSize.MEDIUM_4x2,
            quoteArabic = "وما نيل المطالب بالتمني، ولكن تؤخذ الدنيا غلاباً.",
            quoteAuthor = "أحمد شوقي",
            source = "PRESET"
        ),
        WidgetConfig(
            id = "preset_minimal_oled",
            name = "مينيمال OLED الداكن",
            tagline = "تصميم عصري وفائق التوفير لطاقة الشاشة",
            type = WidgetType.MINIMAL_STAT,
            theme = WidgetTheme.OLED_MIDNIGHT,
            cornerRadiusDp = 28,
            size = WidgetSize.SMALL_2x2,
            noteContent = "FOCUS / ركّز",
            source = "PRESET"
        ),
        WidgetConfig(
            id = "preset_frosted_calendar",
            name = "تقويم زجاجي مصغر",
            tagline = "عرض اليوم الحالي وتاريخ الأسبوع بنمط Glassmorphism",
            type = WidgetType.CALENDAR_MINI,
            theme = WidgetTheme.FROSTED_GLASS,
            cornerRadiusDp = 22,
            size = WidgetSize.MEDIUM_4x2,
            source = "PRESET"
        ),
        // 1. Analog Clock (ساعة رقاص / عقارب مثل ما في الصورة)
        WidgetConfig(
            id = "preset_analog_oran",
            name = "ساعة عقارب ALG (الرقاص)",
            tagline = "ساعة كلاسيكية دائرية مع عقارب متحركة وعلامة ALG مثل الصورة",
            type = WidgetType.ANALOG_CLOCK,
            theme = WidgetTheme.OLED_MIDNIGHT,
            cornerRadiusDp = 28,
            size = WidgetSize.SMALL_2x2,
            cityName = "وهران (Oran)",
            analogStyle = "ALG_DARK",
            source = "PRESET"
        ),
        // 2. Prayer Times (مواقيت الصلاة مثل الصورة في الأعلى)
        WidgetConfig(
            id = "preset_prayers_oran",
            name = "مواقيت الصلاة (Oran, Algeria)",
            tagline = "مواقيت الصلاة الدقيقة والعد التنازلي للأذان القادم",
            type = WidgetType.PRAYER_TIMES,
            theme = WidgetTheme.CYBER_NEON,
            cornerRadiusDp = 22,
            size = WidgetSize.MEDIUM_4x2,
            cityName = "وهران (Oran)",
            cityLatitude = 35.6976,
            cityLongitude = -0.6337,
            cityTimeZone = 1.0,
            source = "PRESET"
        ),
        // 3. Islamic Calendar Card (بطاقة التقويم ومواقيت الصلاة مثل أسفل الصورة)
        WidgetConfig(
            id = "preset_islamic_calendar_card",
            name = "بطاقة التقويم الهجري ومواقيت الصلاة",
            tagline = "تصميم إسلامي هندسي يجمع التاريخ الهجري والميلادي وجدول الصلوات",
            type = WidgetType.ISLAMIC_CALENDAR_CARD,
            theme = WidgetTheme.FROSTED_GLASS,
            cornerRadiusDp = 26,
            size = WidgetSize.LARGE_4x4,
            cityName = "وهران (Oran)",
            cityLatitude = 35.6976,
            cityLongitude = -0.6337,
            cityTimeZone = 1.0,
            source = "PRESET"
        ),
        // 4. Weather & Temperature Widget (ويدجت الحرارة والطقس)
        WidgetConfig(
            id = "preset_weather_oran",
            name = "ويدجت الحرارة والطقس",
            tagline = "درجة الحرارة وسرعة الرياح والرطوبة وحالة الطقس",
            type = WidgetType.WEATHER_TEMP,
            theme = WidgetTheme.ROYAL_AMBER,
            cornerRadiusDp = 20,
            size = WidgetSize.WIDE_4x1,
            cityName = "وهران (Oran)",
            temperatureCelsius = 24,
            weatherCondition = "مشمس ومعتدل ☀️",
            source = "PRESET"
        ),
        // 5. Holy Quran Audio Widget (ويدجت القرآن الكريم بصوت)
        WidgetConfig(
            id = "preset_quran_audio",
            name = "ويدجت القرآن الكريم الصوتي",
            tagline = "مشغل تلاوات القرآن الكريم العطرة بصوت كبار القراء",
            type = WidgetType.QURAN_AUDIO,
            theme = WidgetTheme.EMERALD_MINT,
            cornerRadiusDp = 24,
            size = WidgetSize.MEDIUM_4x2,
            quranSurahName = "سورة الفاتحة",
            quranReciterName = "مشاري راشد العفاسي",
            source = "PRESET"
        ),
        // 6. App Shortcuts & Dual Apps Bar (ويدجت إختصارات التطبيقات والمزدوجة)
        WidgetConfig(
            id = "preset_app_shortcuts",
            name = "شريط اختصارات التطبيقات والمزدوجة",
            tagline = "شبكة إطلاق سريعة لأهم التطبيقات بأشكال وألوان مخصصة",
            type = WidgetType.APP_SHORTCUTS,
            theme = WidgetTheme.CYBER_NEON,
            cornerRadiusDp = 24,
            size = WidgetSize.MEDIUM_4x2,
            source = "PRESET"
        ),
        // 7. Daily Quran Ayah (ويدجت آية قرآنية يومية - كل يوم آية)
        WidgetConfig(
            id = "preset_daily_quran_ayah",
            name = "آية قرآنية يومية (كل يوم آية)",
            tagline = "نفحات إيمانية وآيات مباركة متجددة يومياً مع التفسير والترقيم",
            type = WidgetType.DAILY_QURAN_AYAH,
            theme = WidgetTheme.EMERALD_MINT,
            cornerRadiusDp = 24,
            size = WidgetSize.MEDIUM_4x2,
            quranAyahText = "إِنَّ مَعَ الْعُسْرِ يُسْرًا",
            quranAyahSurah = "سورة الشرح",
            quranAyahNumber = 6,
            source = "PRESET"
        ),
        // 8. Today in History (ويدجت حدث اليوم - تاريخ وإسلاميات)
        WidgetConfig(
            id = "preset_today_in_history",
            name = "حدث اليوم (تاريخ وإسلاميات)",
            tagline = "محطات تاريخية وإسلامية بارزة حدثت في مثل هذا اليوم",
            type = WidgetType.TODAY_IN_HISTORY,
            theme = WidgetTheme.ROYAL_AMBER,
            cornerRadiusDp = 24,
            size = WidgetSize.MEDIUM_4x2,
            historyEventTitle = "فتح مكة المكرمة",
            historyEventDate = "20 رمضان 8 هـ",
            historyEventYear = "630 م",
            historyEventDescription = "أعظم انتصارات المسلمين ودخول الناس في دين الله أفواجاً وتطهير الكعبة المشرفة من الأصنام.",
            source = "PRESET"
        )
    )

    val dailyAyahs = listOf(
        OfflineAyah(
            "إِنَّ مَعَ الْعُسْرِ يُسْرًا",
            "سورة الشرح",
            6,
            "بشرى وتطمين بأن مع كل ضيق فرجاً قريباً"
        ),
        OfflineAyah(
            "وَقُل رَّبِّ زِدْنِي عِلْمًا",
            "سورة طه",
            114,
            "دعاء مبارك لطلب العلم والتوفيق"
        ),
        OfflineAyah(
            "أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ",
            "سورة الرعد",
            28,
            "سكينة النفس وراحة الفؤاد بذكر الله"
        ),
        OfflineAyah(
            "وَمَن يَتَوَكَّلْ عَلَى اللَّهِ فَهُوَ حَسْبُهُ",
            "سورة الطلاق",
            3,
            "كفاية الله لمن وكل أمره إليه بصدق"
        ),
        OfflineAyah(
            "وَاصْبِرْ لِحُكْمِ رَبِّكَ فَإِنَّكَ بِأَعْيُنِنَا",
            "سورة الطور",
            48,
            "معية الله وحفظه ورعايته للصابرين"
        ),
        OfflineAyah(
            "فَاذْكُرُونِي أَذْكُرْكُمْ وَاشْكُرُوا لِي وَلَا تَكْفُرُونِ",
            "سورة البقرة",
            152,
            "شرف عظيم أن يذكرك الله تعالى في ملأ خير منهم"
        ),
        OfflineAyah(
            "وَإِذَا سَأَلَكَ عِبَادِي عَنِّي فَإِنِّي قَرِيبٌ ۖ أُجِيبُ دَعْوَةَ الدَّاعِ إِذَا دَعَانِ",
            "سورة البقرة",
            186,
            "قرب الله وإجابته لدعاء عباده"
        )
    )

    val historicalEvents = listOf(
        OfflineHistoryEvent(
            title = "فتح مكة المكرمة",
            date = "20 رمضان 8 هـ",
            year = "630 م",
            description = "أعظم انتصارات المسلمين بقيادة رسول الله ﷺ، وتطهير البيت الحرام وإعلان العفو العام: اذهبوا فأنتم الطلقاء.",
            category = "غزوات وبطولات إسلامية"
        ),
        OfflineHistoryEvent(
            title = "معركة حطين وتحرير القدس",
            date = "26 ربيع الثاني 583 هـ",
            year = "1187 م",
            description = "انتصار القائد العظيم صلاح الدين الأيوبي في معركة حطين الخالدة وتحرير بيت المقدس والمسجد الأقصى المبارك.",
            category = "تاريخ إسلامي مجيد"
        ),
        OfflineHistoryEvent(
            title = "معركة عين جالوت",
            date = "25 رمضان 658 هـ",
            year = "1260 م",
            description = "انتصار جيش المسلمين بقيادة سيف الدين قطز والظاهر بيبرس على التتار وإنقاذ العالم الإسلامي والحضارة الإنسانية.",
            category = "انتصارات حاسمة"
        ),
        OfflineHistoryEvent(
            title = "فتح القسطنطينية",
            date = "20 جمادى الأولى 857 هـ",
            year = "1453 م",
            description = "تحقق بشارة النبي ﷺ بفتح القسطنطينية على يد السلطان العثماني محمد الفاتح رحمه الله.",
            category = "فتوحات كبرى"
        ),
        OfflineHistoryEvent(
            title = "تأسيس جامع الأزهر الشريف",
            date = "7 رمضان 361 هـ",
            year = "972 م",
            description = "افتتاح الجامع الأزهر في القاهرة ليصبح منارة للعلم الشرعي واللغة العربية وحصناً للأمة الإسلامية عبر القرون.",
            category = "حضارة وعمارة إسلامية"
        ),
        OfflineHistoryEvent(
            title = "معركة اليرموك الخالدة",
            date = "5 رجب 15 هـ",
            year = "636 م",
            description = "انتصار خالد لجيش المسلمين بقيادة سيف الله المسلول خالد بن الوليد على الروم وفتح بلاد الشام المباركة.",
            category = "فتوحات إسلامية"
        )
    )

    fun getRandomAyah(): OfflineAyah = dailyAyahs.random()
    fun getRandomHistoryEvent(): OfflineHistoryEvent = historicalEvents.random()
}

data class OfflineAyah(
    val ayahText: String,
    val surahName: String,
    val ayahNumber: Int,
    val themeNote: String
)

data class OfflineHistoryEvent(
    val title: String,
    val date: String,
    val year: String,
    val description: String,
    val category: String
)

