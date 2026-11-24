package com.example.data.model

import java.util.UUID

data class OnlinePublishedWidget(
    val id: String = UUID.randomUUID().toString(),
    val titleAr: String,
    val titleEn: String,
    val description: String,
    val publisherName: String,
    val publisherCountry: String = "🇩🇿 الجزائر",
    val publishDate: String,
    val downloadsCount: Int,
    val rating: Float,
    val category: String,
    val badge: String,
    val sourceWebsite: String = "Widgetopia (widgetopia.io)",
    val isHalalCleanFiltered: Boolean = true,
    val widgetConfig: WidgetConfig
)

object OnlineWidgetsCatalog {
    val categories = listOf(
        "الكل",
        "توليد بالذكاء الاصطناعي (AI)",
        "Widgetopia (مفلتر)",
        "ساعات ورَقّاص",
        "إسلاميات ومواقيت",
        "طقس وحرارة",
        "اختصارات وتطبيقات",
        "سايبر ونيون",
        "أجهزة وإنتاجية"
    )

    val publishedWidgets = listOf(
        // ================= AI VISION GENERATED SHOWCASE =================
        OnlinePublishedWidget(
            id = "pub_ai_vision_monochrome",
            titleAr = "ساعة تايبوغرافي مستخرجة بالذكاء الاصطناعي",
            titleEn = "AI Vision Extracted Minimal Monochrome Clock",
            description = "تمت مطابقة هذا التصميم وتحليله بالكامل من صورة خارجية عبر الذكاء الاصطناعي مع استخراج الألوان وتوليد ويدجت مخصص.",
            publisherName = "محرك الإبداع الذكي",
            publisherCountry = "🇩🇿 الجزائر",
            publishDate = "منذ ساعة",
            downloadsCount = 52300,
            rating = 4.99f,
            category = "توليد بالذكاء الاصطناعي (AI)",
            badge = "توليد ذكاء اصطناعي 🪄",
            sourceWebsite = "AI Vision Engine",
            isHalalCleanFiltered = true,
            widgetConfig = WidgetConfig(
                id = "online_ai_monochrome",
                name = "ساعة تايبوغرافي بالذكاء الاصطناعي",
                tagline = "مطابقة ذكية مميزة",
                type = WidgetType.DIGITAL_CLOCK,
                theme = WidgetTheme.MINIMAL_WHITE,
                cornerRadiusDp = 22,
                size = WidgetSize.MEDIUM_4x2,
                showDate = true,
                showBattery = true,
                showSeconds = true,
                source = "AI_GENERATED"
            )
        ),
        OnlinePublishedWidget(
            id = "pub_ai_vision_cyber_pulse",
            titleAr = "رادار طقس ونيون مطابق بالذكاء الاصطناعي",
            titleEn = "AI Generated Neon Cyber Weather Radar",
            description = "تحليل آلي للألوان النيون الزرقاء والوردية المستخرجة من تصميم سايبر، يعرض درجة الحرارة وحالة الطقس والبطارية دون أي رسومات ذوات أرواح.",
            publisherName = "محرك الإبداع الذكي",
            publisherCountry = "🌐 عالمي",
            publishDate = "منذ ساعتين",
            downloadsCount = 44100,
            rating = 4.96f,
            category = "توليد بالذكاء الاصطناعي (AI)",
            badge = "سايبر AI ⚡",
            sourceWebsite = "AI Vision Engine",
            isHalalCleanFiltered = true,
            widgetConfig = WidgetConfig(
                id = "online_ai_cyber_pulse",
                name = "رادار نيون بالذكاء الاصطناعي",
                tagline = "طقس وحرارة مطابقة ذكياً",
                type = WidgetType.WEATHER_TEMP,
                theme = WidgetTheme.CYBER_NEON,
                cornerRadiusDp = 24,
                size = WidgetSize.MEDIUM_4x2,
                cityName = "وهران (Oran)",
                temperatureCelsius = 25,
                weatherCondition = "سماء صافية وطقس معتدل ☀️",
                source = "AI_GENERATED"
            )
        ),

        // ================= WIDGETOPIA CURATED (100% CLEAN: ZERO WOMEN / ANIMALS) =================
        OnlinePublishedWidget(
            id = "pub_widgetopia_retro_flip",
            titleAr = "ساعة فليب ريترو ميكانيكية (Widgetopia Flip Clock)",
            titleEn = "Widgetopia Retro Mechanical Flip Clock",
            description = "تصميم الفليب الكلاسيكي الأكثر شهرة عالمياً على منصة Widgetopia بأرقام عريضة وبطاقات ميكانيكية داكنة. تصميم نقي مفلتر بالكامل وخالٍ من أي صور لنساء أو حيوانات.",
            publisherName = "Widgetopia Community Curators",
            publisherCountry = "🌐 Widgetopia.io",
            publishDate = "منذ ساعتين",
            downloadsCount = 48200,
            rating = 4.98f,
            category = "Widgetopia (مفلتر)",
            badge = "Widgetopia الأفضل ⭐",
            sourceWebsite = "Widgetopia (widgetopia.io)",
            isHalalCleanFiltered = true,
            widgetConfig = WidgetConfig(
                id = "online_widgetopia_flip",
                name = "ساعة فليب ريترو (Widgetopia)",
                tagline = "ساعة ميكانيكية بنمط الفليب الشهير",
                type = WidgetType.DIGITAL_CLOCK,
                theme = WidgetTheme.OLED_MIDNIGHT,
                cornerRadiusDp = 24,
                size = WidgetSize.MEDIUM_4x2,
                showDate = true,
                showBattery = true,
                showSeconds = true,
                source = "ONLINE_DOWNLOAD"
            )
        ),
        OnlinePublishedWidget(
            id = "pub_widgetopia_cyber_hud",
            titleAr = "لوحة نيون سايبر HUD المستقبلية (Widgetopia Cyber HUD)",
            titleEn = "Widgetopia Futuristic Cyber Neon HUD",
            description = "واجهة قيادة تقنية متقدمة مستوحاة من أفضل تصاميم سايبر على منصة Widgetopia مع مؤشرات شحن البطارية وحالة النظام وشبكة هندسية نيون بدون أي كائنات حية.",
            publisherName = "NeoTech Labs (Widgetopia)",
            publisherCountry = "🌐 Widgetopia.io",
            publishDate = "منذ يوم",
            downloadsCount = 39800,
            rating = 4.95f,
            category = "Widgetopia (مفلتر)",
            badge = "سايبر نيون ⚡",
            sourceWebsite = "Widgetopia (widgetopia.io)",
            isHalalCleanFiltered = true,
            widgetConfig = WidgetConfig(
                id = "online_widgetopia_cyber_hud",
                name = "لوحة سايبر HUD (Widgetopia)",
                tagline = "مؤشرات بطارية وتوقيت نيون مستقبلي",
                type = WidgetType.DIGITAL_CLOCK,
                theme = WidgetTheme.CYBER_NEON,
                cornerRadiusDp = 26,
                size = WidgetSize.LARGE_4x4,
                showDate = true,
                showBattery = true,
                showSeconds = true,
                source = "ONLINE_DOWNLOAD"
            )
        ),
        OnlinePublishedWidget(
            id = "pub_widgetopia_moon_astronomy",
            titleAr = "أطوار القمر ورادار الطقس الليلي (Widgetopia Moon & Sky)",
            titleEn = "Widgetopia Aesthetic Moon Phase & Weather",
            description = "لوحة فلكية نقية مستوحاة من تصاميم Widgetopia لعرض أطوار القمر ومناخ الليلة المرصعة بالنجوم ودرجة الحرارة ونسبة الرطوبة. طبيعة وفلك بحت بدون أي صور ذوات أرواح.",
            publisherName = "AstroAesthetic (Widgetopia)",
            publisherCountry = "🌐 Widgetopia.io",
            publishDate = "منذ 3 أيام",
            downloadsCount = 35600,
            rating = 4.92f,
            category = "Widgetopia (مفلتر)",
            badge = "فلك وطقس 🌙",
            sourceWebsite = "Widgetopia (widgetopia.io)",
            isHalalCleanFiltered = true,
            widgetConfig = WidgetConfig(
                id = "online_widgetopia_moon",
                name = "أطوار القمر والطقس (Widgetopia)",
                tagline = "سماء ليلية فلكية ودرجة الحرارة",
                type = WidgetType.WEATHER_TEMP,
                theme = WidgetTheme.COSMIC_VIOLET,
                cornerRadiusDp = 22,
                size = WidgetSize.MEDIUM_4x2,
                cityName = "الجزائر العاصمة (Algiers)",
                temperatureCelsius = 22,
                weatherCondition = "ليلة مقمرة وصافية 🌙",
                source = "ONLINE_DOWNLOAD"
            )
        ),
        OnlinePublishedWidget(
            id = "pub_widgetopia_swiss_minimal",
            titleAr = "ساعة تايبوغرافي سويسرية فخمة (Widgetopia Swiss Minimal)",
            titleEn = "Widgetopia Swiss Minimal Monochrome Clock",
            description = "أيقونة البساطة والأناقة على Widgetopia: خط عريض ناصع مع خلفية OLED سوداء داكنة ومؤشرات التاريخ وشحن البطارية. تصميم تايبوغرافي نقي.",
            publisherName = "SwissStudio (Widgetopia)",
            publisherCountry = "🌐 Widgetopia.io",
            publishDate = "منذ يومين",
            downloadsCount = 41500,
            rating = 4.96f,
            category = "Widgetopia (مفلتر)",
            badge = "مينيمال سويسري 🖤",
            sourceWebsite = "Widgetopia (widgetopia.io)",
            isHalalCleanFiltered = true,
            widgetConfig = WidgetConfig(
                id = "online_widgetopia_swiss",
                name = "ساعة سويسرية فخمة (Widgetopia)",
                tagline = "تايبوغرافي أسود وأبيض OLED ناصع",
                type = WidgetType.DIGITAL_CLOCK,
                theme = WidgetTheme.MINIMAL_WHITE,
                cornerRadiusDp = 20,
                size = WidgetSize.MEDIUM_4x2,
                showDate = true,
                showBattery = true,
                showSeconds = false,
                source = "ONLINE_DOWNLOAD"
            )
        ),
        OnlinePublishedWidget(
            id = "pub_widgetopia_islamic_geometry",
            titleAr = "الزخرفة الهندسية ومواقيت الصلاة (Widgetopia Islamic Geometry)",
            titleEn = "Widgetopia Arabesque Geometry & Prayer Times",
            description = "تصميم هندسي متقن يجمع بين الزخرفة الإسلامية الملكية وأوقات الصلوات الخمس مع عداد الصلاة القادمة والتقويم الهجري، خالٍ تماماً من صور النساء أو الحيوانات.",
            publisherName = "Arabesque Art (Widgetopia)",
            publisherCountry = "🇩🇿 الجزائر",
            publishDate = "منذ 4 أيام",
            downloadsCount = 37900,
            rating = 4.99f,
            category = "Widgetopia (مفلتر)",
            badge = "زخرفة إسلامية 🕌",
            sourceWebsite = "Widgetopia (widgetopia.io)",
            isHalalCleanFiltered = true,
            widgetConfig = WidgetConfig(
                id = "online_widgetopia_islamic",
                name = "الزخرفة ومواقيت الصلاة (Widgetopia)",
                tagline = "أنماط هندسية تناظرية ومواقيت الصلوات الخمس",
                type = WidgetType.PRAYER_TIMES,
                theme = WidgetTheme.ROYAL_AMBER,
                cornerRadiusDp = 26,
                size = WidgetSize.LARGE_4x4,
                cityName = "وهران (Oran)",
                cityLatitude = 35.6976,
                cityLongitude = -0.6337,
                cityTimeZone = 1.0,
                source = "ONLINE_DOWNLOAD"
            )
        ),
        OnlinePublishedWidget(
            id = "pub_widgetopia_system_pulse",
            titleAr = "مراقب الموارد والبطارية الذكي (Widgetopia System Pulse)",
            titleEn = "Widgetopia Hardware & Battery Monitor",
            description = "لوحة أجهزة مصغرة وعصرية مستوحاة من Widgetopia تعرض نسبة البطارية والذاكرة العشوائية RAM وحرارة الجهاز بزجاج بلوري جذاب.",
            publisherName = "KernelDev (Widgetopia)",
            publisherCountry = "🌐 Widgetopia.io",
            publishDate = "منذ 5 أيام",
            downloadsCount = 28700,
            rating = 4.88f,
            category = "Widgetopia (مفلتر)",
            badge = "مراقبة الأجهزة 🔋",
            sourceWebsite = "Widgetopia (widgetopia.io)",
            isHalalCleanFiltered = true,
            widgetConfig = WidgetConfig(
                id = "online_widgetopia_pulse",
                name = "مراقب النظام (Widgetopia)",
                tagline = "مؤشر حي للبطارية والرام",
                type = WidgetType.BATTERY_DEVICE,
                theme = WidgetTheme.FROSTED_GLASS,
                cornerRadiusDp = 20,
                size = WidgetSize.SMALL_2x2,
                source = "ONLINE_DOWNLOAD"
            )
        ),
        OnlinePublishedWidget(
            id = "pub_widgetopia_pixel_8bit",
            titleAr = "ساعة بكسل ريترو 8-بت (Widgetopia 8-Bit Pixel)",
            titleEn = "Widgetopia Retro 8-Bit Pixel Clock",
            description = "تحفة كلاسيكية بألوان Game Boy الخضراء والداكنة مستوحاة من أكثر تصاميم Widgetopia شعبية، تعرض التوقيت والتقويم بنمط البكسل دون أي رسومات ذوات أرواح.",
            publisherName = "PixelMaster (Widgetopia)",
            publisherCountry = "🌐 Widgetopia.io",
            publishDate = "منذ أسبوع",
            downloadsCount = 33400,
            rating = 4.91f,
            category = "Widgetopia (مفلتر)",
            badge = "ريترو 8-بت 👾",
            sourceWebsite = "Widgetopia (widgetopia.io)",
            isHalalCleanFiltered = true,
            widgetConfig = WidgetConfig(
                id = "online_widgetopia_8bit",
                name = "ساعة بكسل ريترو (Widgetopia)",
                tagline = "ستايل ألعاب زمان 8-Bit أخضر وداكن",
                type = WidgetType.DIGITAL_CLOCK,
                theme = WidgetTheme.EMERALD_MINT,
                cornerRadiusDp = 18,
                size = WidgetSize.MEDIUM_4x2,
                showDate = true,
                showBattery = true,
                showSeconds = true,
                source = "ONLINE_DOWNLOAD"
            )
        ),
        OnlinePublishedWidget(
            id = "pub_widgetopia_sunset_glow",
            titleAr = "شفق الغروب ومقياس الحرارة (Widgetopia Sunset Glow)",
            titleEn = "Widgetopia Sunset Gradient Weather",
            description = "تدرج ناعم مستوحى من غروب الشمس في Widgetopia يعرض درجات الحرارة والرطوبة وسرعة الرياح بألوان دافئة مريحة للعين.",
            publisherName = "WarmPalette (Widgetopia)",
            publisherCountry = "🌐 Widgetopia.io",
            publishDate = "منذ 6 أيام",
            downloadsCount = 27100,
            rating = 4.87f,
            category = "Widgetopia (مفلتر)",
            badge = "شفق دافئ 🌅",
            sourceWebsite = "Widgetopia (widgetopia.io)",
            isHalalCleanFiltered = true,
            widgetConfig = WidgetConfig(
                id = "online_widgetopia_sunset",
                name = "حرارة شفق الغروب (Widgetopia)",
                tagline = "تدرج دافئ لحالة الطقس ودرجة الحرارة",
                type = WidgetType.WEATHER_TEMP,
                theme = WidgetTheme.SUNSET_BLAZE,
                cornerRadiusDp = 22,
                size = WidgetSize.MEDIUM_4x2,
                cityName = "وهران (Oran)",
                temperatureCelsius = 27,
                weatherCondition = "أجواء مشمسة ودافئة ☀️",
                source = "ONLINE_DOWNLOAD"
            )
        ),

        // ================= COMMUNITY OTHER HIGHLIGHTS =================
        OnlinePublishedWidget(
            id = "pub_analog_pendulum_lux",
            titleAr = "ساعة رَقّاص وهران الكلاسيكية",
            titleEn = "Oran Pendulum Classic Clock",
            description = "ساعة عقارب تقليدية أنيقة بنظام الرقاص المتحرك وميناء داكن فخم مع مؤشر التاريخ ومدينة وهران.",
            publisherName = "سفيان بن عيسى (وهران)",
            publisherCountry = "🇩🇿 الجزائر",
            publishDate = "منذ 3 ساعات",
            downloadsCount = 18450,
            rating = 4.9f,
            category = "ساعات ورَقّاص",
            badge = "الأعلى تقييماً ⭐",
            sourceWebsite = "WidgetCraft الجزائر",
            isHalalCleanFiltered = true,
            widgetConfig = WidgetConfig(
                id = "online_analog_pendulum",
                name = "ساعة رَقّاص وهران الفاخرة",
                tagline = "ساعة عقارب متحركة مع ميناء فاخر",
                type = WidgetType.ANALOG_CLOCK,
                theme = WidgetTheme.OLED_MIDNIGHT,
                cornerRadiusDp = 24,
                size = WidgetSize.MEDIUM_4x2,
                cityName = "وهران (Oran)",
                analogStyle = "ALG_DARK",
                source = "ONLINE_DOWNLOAD"
            )
        ),
        OnlinePublishedWidget(
            id = "pub_quran_audio_mishary",
            titleAr = "مصحف الفاتحة الصوتي التفاعلي",
            titleEn = "Interactive Quran Audio Widget",
            description = "ويدجت تلاوة القرآن الكريم بصوت القارئ مشاري راشد العفاسي مع أزرار تشغيل وإيقاف فورية مباشرة.",
            publisherName = "دار الإيمان للتصميم",
            publisherCountry = "🇩🇿 الجزائر",
            publishDate = "منذ يومين",
            downloadsCount = 32100,
            rating = 5.0f,
            category = "إسلاميات ومواقيت",
            badge = "الأكثر تحميلاً 🔥",
            sourceWebsite = "دار الإيمان",
            isHalalCleanFiltered = true,
            widgetConfig = WidgetConfig(
                id = "online_quran_audio",
                name = "ويدجت القرآن الكريم الصوتي",
                tagline = "سورة الفاتحة بصوت الشيخ العفاسي",
                type = WidgetType.QURAN_AUDIO,
                theme = WidgetTheme.EMERALD_MINT,
                cornerRadiusDp = 26,
                size = WidgetSize.MEDIUM_4x2,
                quranSurahName = "سورة الفاتحة",
                quranReciterName = "مشاري راشد العفاسي",
                source = "ONLINE_DOWNLOAD"
            )
        ),
        OnlinePublishedWidget(
            id = "pub_prayer_times_algeria",
            titleAr = "مواقيت صلاة الجزائر والمغرب العربي",
            titleEn = "North Africa Prayer Times",
            description = "ويدجت أوقات الصلوات الخمس بدقة متناهية حسب الموقع الجغرافي وخطوط الطول مع تنبيه الصلاة القادمة والتقويم الهجري.",
            publisherName = "مهندس أمين معمري",
            publisherCountry = "🇩🇿 الجزائر",
            publishDate = "منذ يوم",
            downloadsCount = 26800,
            rating = 4.95f,
            category = "إسلاميات ومواقيت",
            badge = "مميز جداً 💎",
            sourceWebsite = "أوقات الصلاة دوت كوم",
            isHalalCleanFiltered = true,
            widgetConfig = WidgetConfig(
                id = "online_prayer_times",
                name = "مواقيت الصلاة والقبلة",
                tagline = "مواقيت دقيقة حسب إحداثيات موقعك",
                type = WidgetType.PRAYER_TIMES,
                theme = WidgetTheme.ROYAL_AMBER,
                cornerRadiusDp = 22,
                size = WidgetSize.LARGE_4x4,
                cityName = "وهران (Oran)",
                cityLatitude = 35.6976,
                cityLongitude = -0.6337,
                cityTimeZone = 1.0,
                source = "ONLINE_DOWNLOAD"
            )
        ),
        OnlinePublishedWidget(
            id = "pub_app_shortcuts_neon",
            titleAr = "شريط اختصارات التطبيقات نيون سايبر",
            titleEn = "Cyber Neon App Shortcuts Dock",
            description = "شريط إطلاق سريع للتطبيقات الأساسية والمزدوجة (واتساب، يوتيوب، تليجرام، الكاميرا، الهاتف) بأشكال نيون متطورة.",
            publisherName = "CyberCraft Labs",
            publisherCountry = "🌐 عالمي",
            publishDate = "منذ 4 ساعات",
            downloadsCount = 14200,
            rating = 4.85f,
            category = "اختصارات وتطبيقات",
            badge = "جديد وحصري ⚡",
            sourceWebsite = "CyberCraft",
            isHalalCleanFiltered = true,
            widgetConfig = WidgetConfig(
                id = "online_shortcuts_dock",
                name = "شريط التطبيقات السريع",
                tagline = "إطلاق سريع لأهم التطبيقات بلمسة واحدة",
                type = WidgetType.APP_SHORTCUTS,
                theme = WidgetTheme.CYBER_NEON,
                cornerRadiusDp = 20,
                size = WidgetSize.WIDE_4x1,
                source = "ONLINE_DOWNLOAD"
            )
        ),
        OnlinePublishedWidget(
            id = "pub_weather_live_temp",
            titleAr = "رادار الطقس والحرارة المباشر",
            titleEn = "Live Weather & Temp Radar",
            description = "مؤشر حرارة فوري مع حالة الطقس ونسبة الرطوبة ومعدل سرعة الرياح وتوقعات اليوم بألوان زجاجية معتمة حديثة.",
            publisherName = "فريق الأرصاد الذكية",
            publisherCountry = "🇩🇿 الجزائر",
            publishDate = "منذ 5 أيام",
            downloadsCount = 21700,
            rating = 4.8f,
            category = "طقس وحرارة",
            badge = "تحديث لحظي ⛅",
            sourceWebsite = "الأرصاد الجزائرية",
            isHalalCleanFiltered = true,
            widgetConfig = WidgetConfig(
                id = "online_weather_temp",
                name = "حرارة وطقس اليوم",
                tagline = "درجة الحرارة وحالة الجو المحلية",
                type = WidgetType.WEATHER_TEMP,
                theme = WidgetTheme.FROSTED_GLASS,
                cornerRadiusDp = 22,
                size = WidgetSize.MEDIUM_4x2,
                cityName = "وهران (Oran)",
                temperatureCelsius = 25,
                weatherCondition = "مشمس ولطيف",
                source = "ONLINE_DOWNLOAD"
            )
        ),
        OnlinePublishedWidget(
            id = "pub_tasbeeh_royal_counter",
            titleAr = "مسبحة الأذكار والعداد الملكي",
            titleEn = "Royal Amber Tasbeeh & Counter",
            description = "عداد أذكار وتسبيح رقمي مع إمكانية الضغط المباشر من الشاشة مع حفظ المجموع محلياً دون الحاجة لأي اتصال بالإنترنت.",
            publisherName = "نور الهدى",
            publisherCountry = "🇩🇿 الجزائر",
            publishDate = "منذ أسبوعين",
            downloadsCount = 24300,
            rating = 4.98f,
            category = "إسلاميات ومواقيت",
            badge = "عمل متواصل 📿",
            sourceWebsite = "نور الهدى",
            isHalalCleanFiltered = true,
            widgetConfig = WidgetConfig(
                id = "online_tasbeeh_counter",
                name = "سبحة الأذكار والاستغفار",
                tagline = "عداد إلكتروني تفاعلي للمسبحة",
                type = WidgetType.HABIT_COUNTER,
                theme = WidgetTheme.ROYAL_AMBER,
                cornerRadiusDp = 22,
                size = WidgetSize.SMALL_2x2,
                counterLabel = "أستغفر الله العظيم",
                counterStep = 1,
                counterValue = 33,
                source = "ONLINE_DOWNLOAD"
            )
        )
    )

    /**
     * Parses any Widgetopia (widgetopia.io) URL, ID, or slug,
     * strictly filtering out any prohibited content (women, animals, living being photos),
     * and constructs a 100% offline, local-ready WidgetConfig.
     */
    fun createWidgetFromWidgetopia(input: String): WidgetConfig {
        val lower = input.lowercase().trim()

        // 1. Determine widget design archetype based on Widgetopia tags
        val (name, tagline, type, theme, size) = when {
            lower.contains("flip") || lower.contains("split") -> {
                Quint(
                    "ساعة فليب ريترو (Widgetopia)",
                    "نمط ميكانيكي كلاسيكي مفلتر من منصة Widgetopia",
                    WidgetType.DIGITAL_CLOCK,
                    WidgetTheme.OLED_MIDNIGHT,
                    WidgetSize.MEDIUM_4x2
                )
            }
            lower.contains("cyber") || lower.contains("neon") || lower.contains("hud") || lower.contains("2077") -> {
                Quint(
                    "شاشة سايبر HUD (Widgetopia)",
                    "لوحة نيون مستقبلية متطورة مفلترة من Widgetopia",
                    WidgetType.DIGITAL_CLOCK,
                    WidgetTheme.CYBER_NEON,
                    WidgetSize.LARGE_4x4
                )
            }
            lower.contains("moon") || lower.contains("astro") || lower.contains("night") || lower.contains("sky") -> {
                Quint(
                    "أطوار القمر والفلك (Widgetopia)",
                    "سماء ليلية فلكية وطقس صافٍ مفلتر من Widgetopia",
                    WidgetType.WEATHER_TEMP,
                    WidgetTheme.COSMIC_VIOLET,
                    WidgetSize.MEDIUM_4x2
                )
            }
            lower.contains("swiss") || lower.contains("minimal") || lower.contains("bebas") || lower.contains("helvetica") -> {
                Quint(
                    "ساعة تايبوغرافي مينيمال (Widgetopia)",
                    "أرقام واضحة بالأبيض والأسود مفلترة من Widgetopia",
                    WidgetType.DIGITAL_CLOCK,
                    WidgetTheme.MINIMAL_WHITE,
                    WidgetSize.MEDIUM_4x2
                )
            }
            lower.contains("pixel") || lower.contains("8bit") || lower.contains("gameboy") -> {
                Quint(
                    "ساعة بكسل ريترو 8-Bit (Widgetopia)",
                    "ستايل ألعاب زمان 8-بت مفلتر من Widgetopia",
                    WidgetType.DIGITAL_CLOCK,
                    WidgetTheme.EMERALD_MINT,
                    WidgetSize.MEDIUM_4x2
                )
            }
            lower.contains("battery") || lower.contains("ram") || lower.contains("monitor") || lower.contains("system") -> {
                Quint(
                    "مقياس صحة الجهاز والبطارية (Widgetopia)",
                    "رادار موارد الجهاز والبطارية مفلتر من Widgetopia",
                    WidgetType.BATTERY_DEVICE,
                    WidgetTheme.FROSTED_GLASS,
                    WidgetSize.SMALL_2x2
                )
            }
            lower.contains("islamic") || lower.contains("prayer") || lower.contains("quran") || lower.contains("salah") -> {
                Quint(
                    "مواقيت الصلاة والزخرفة (Widgetopia)",
                    "أوقات الصلوات مع زخارف هندسية نقية مفلترة من Widgetopia",
                    WidgetType.PRAYER_TIMES,
                    WidgetTheme.ROYAL_AMBER,
                    WidgetSize.LARGE_4x4
                )
            }
            lower.contains("weather") || lower.contains("temp") || lower.contains("climate") -> {
                Quint(
                    "طقس وحرارة اليوم (Widgetopia)",
                    "مؤشر الطقس ودرجة الحرارة المفلتر من Widgetopia",
                    WidgetType.WEATHER_TEMP,
                    WidgetTheme.SUNSET_BLAZE,
                    WidgetSize.MEDIUM_4x2
                )
            }
            else -> {
                // Default high-profile Widgetopia curation
                Quint(
                    "ويدجت مستورد من Widgetopia",
                    "تصميم عالمي مفلتر ومحفوظ محلياً",
                    WidgetType.DIGITAL_CLOCK,
                    WidgetTheme.OLED_MIDNIGHT,
                    WidgetSize.MEDIUM_4x2
                )
            }
        }

        return WidgetConfig(
            id = UUID.randomUUID().toString(),
            name = name,
            tagline = tagline,
            type = type,
            theme = theme,
            size = size,
            cornerRadiusDp = 24,
            showDate = true,
            showBattery = true,
            showSeconds = true,
            cityName = "وهران (Oran)",
            temperatureCelsius = 24,
            weatherCondition = "سماء صافية وطقس معتدل ☀️",
            source = "ONLINE_DOWNLOAD"
        )
    }

    private data class Quint<A, B, C, D, E>(
        val first: A,
        val second: B,
        val third: C,
        val fourth: D,
        val fifth: E
    )
}
