package com.example.data.model

object FashionCatalog {

    val cityWeathers = listOf(
        CityWeather("Quetta", 12, "Thand / Chilly", "Quetta me aaj thand hai (12°C), is look ke sath pashmina shawl ya warm waistcoat add karein."),
        CityWeather("Lahore", 24, "Halka Thand / Pleasant", "Lahore ka mausam khushgawar hai (24°C), cotton lawn aur linen suits ideal hain."),
        CityWeather("Karachi", 29, "Hawaidar / Humid Breeze", "Karachi me halki dhoop aur hawa hai (29°C), light breathable fabrics select karein."),
        CityWeather("Islamabad", 18, "Cool Breeze / Thandi Hawa", "Islamabad me mausam thanda hai (18°C), jacket ya velvet touch stylish lagega."),
        CityWeather("Peshawar", 20, "Dry & Fresh", "Peshawar me crisp mausam hai, traditional peshawari chappal aur khadi kameez perfect hai.")
    )

    fun getWeatherForCity(city: String): CityWeather {
        return cityWeathers.firstOrNull { it.city.equals(city, ignoreCase = true) }
            ?: cityWeathers.first()
    }

    val items: List<FashionItem> = listOf(
        // ==================== 1. DRESS TRY-ON - DAILY (MALE) ====================
        FashionItem(
            id = "m_dress_1",
            category = FashionCategory.DRESS_TRY_ON,
            name = "Classic Black Shalwar Kameez",
            urduName = "کلاسک بلیک شلوار قمیض",
            gender = "Male",
            subType = "Daily",
            description = "Crisp premium blended cotton with structured band collar and cuff styling.",
            heightTip = "Vertical straight cut height ko 2 inch elongated aur slim portrait deta hai.",
            priceRs = 4200,
            rentPerDayRs = 1000,
            colorHex = 0xFF1A1A1A,
            tag = "Pakistani Staple",
            suitabilityScore = 96
        ),
        FashionItem(
            id = "m_dress_2",
            category = FashionCategory.DRESS_TRY_ON,
            name = "Fitted T-Shirt & Dark Blue Jeans",
            urduName = "ٹی شرٹ اور اسٹریٹ جینز",
            gender = "Male",
            subType = "Daily",
            description = "Crewneck breathable cotton tee paired with clean denim.",
            heightTip = "Dark wash denim aur solid tee legs ko balanced proportion dete hain.",
            priceRs = 3200,
            rentPerDayRs = 800,
            colorHex = 0xFF2C3E50,
            tag = "Everyday",
            suitabilityScore = 92
        ),
        FashionItem(
            id = "m_dress_3",
            category = FashionCategory.DRESS_TRY_ON,
            name = "Office Formal Shirt & Chinos",
            urduName = "آفس فارمل شرٹ پینٹ",
            gender = "Male",
            subType = "Daily",
            description = "Light sky oxford shirt tucked into slim flat-front charcoal trousers.",
            heightTip = "High-rise trousers waistline ko elevate karke lower body ko 2 inch lamba show karti hain.",
            priceRs = 4800,
            rentPerDayRs = 1200,
            colorHex = 0xFF34495E,
            tag = "Corporate",
            suitabilityScore = 94
        ),
        FashionItem(
            id = "m_dress_4",
            category = FashionCategory.DRESS_TRY_ON,
            name = "Gym Fit Compression & Joggers",
            urduName = "جم فٹنس سوٹ",
            gender = "Male",
            subType = "Daily",
            description = "High-performance dry-fit sports tee with tapered performance trackpants.",
            heightTip = "Tapered bottom ankle par snug rehti hai jisse legs sporty aur athletic lagti hain.",
            priceRs = 3500,
            rentPerDayRs = 700,
            colorHex = 0xFF1E8449,
            tag = "Athletic",
            suitabilityScore = 90
        ),
        FashionItem(
            id = "m_dress_5",
            category = FashionCategory.DRESS_TRY_ON,
            name = "University Denim Jacket Look",
            urduName = "یونیورسٹی کیژول جیکٹ",
            gender = "Male",
            subType = "Daily",
            description = "Layered trucker denim jacket over white tee with slim black jeans.",
            heightTip = "Cropped denim jacket waist par khatam hoti hai jisse legs automatically lambi dikhti hain.",
            priceRs = 5500,
            rentPerDayRs = 1400,
            colorHex = 0xFF2471A3,
            tag = "College Trend",
            suitabilityScore = 93
        ),

        // ==================== 1. DRESS TRY-ON - DAILY (FEMALE) ====================
        FashionItem(
            id = "f_dress_1",
            category = FashionCategory.DRESS_TRY_ON,
            name = "Straight Cut Lawn Daily Kurti",
            urduName = "اسٹریٹ لان ڈیلی کرتی",
            gender = "Female",
            subType = "Daily",
            description = "Emerald green breathable printed lawn kurti with cigarette trousers.",
            heightTip = "Knee-length se thori lambi kurti continuous vertical illusion banati hai.",
            priceRs = 3800,
            rentPerDayRs = 900,
            colorHex = 0xFF117A65,
            tag = "Comfort Chic",
            suitabilityScore = 97
        ),
        FashionItem(
            id = "f_dress_2",
            category = FashionCategory.DRESS_TRY_ON,
            name = "Modern Front-Open Abaya",
            urduName = "ماڈرن فرنٹ اوپن عبایہ",
            gender = "Female",
            subType = "Daily",
            description = "Flowing Korean Nida fabric with clean lapel cut and matching chiffon hijab.",
            heightTip = "Front vertical opening full height frame ko tall aur graceful look deta hai.",
            priceRs = 6000,
            rentPerDayRs = 1500,
            colorHex = 0xFF212F3D,
            tag = "Modest Grace",
            suitabilityScore = 95
        ),
        FashionItem(
            id = "f_dress_3",
            category = FashionCategory.DRESS_TRY_ON,
            name = "Office Wear Solid Linen 2-Piece",
            urduName = "آفس اسمارٹ لینن سوٹ",
            gender = "Female",
            subType = "Daily",
            description = "Solid lavender linen kurta with straight culottes and neat v-neckline.",
            heightTip = "Monochrome colors bina kisi visual break ke height ko 2-3 inches stretch karti hain.",
            priceRs = 4900,
            rentPerDayRs = 1200,
            colorHex = 0xFF7D3C98,
            tag = "Executive",
            suitabilityScore = 94
        ),

        // ==================== 1. DRESS TRY-ON - WEDDING ====================
        FashionItem(
            id = "m_wed_1",
            category = FashionCategory.DRESS_TRY_ON,
            name = "Royal Ivory Raw Silk Sherwani",
            urduName = "شاہی را سلک شیروانی (دولہا)",
            gender = "Male",
            subType = "Wedding",
            description = "Bespoke zardozi embroidery with matching tilla khussa and maroon silk stole.",
            heightTip = "Padded structured shoulders aur vertical tilla work 5.5ft+ dulha ko majestic presence deta hai.",
            priceRs = 25000,
            rentPerDayRs = 3500,
            colorHex = 0xFFD4AF37,
            tag = "Baraat Dulha",
            suitabilityScore = 98
        ),
        FashionItem(
            id = "m_wed_2",
            category = FashionCategory.DRESS_TRY_ON,
            name = "Designer Prince Coat & Trousers",
            urduName = "پرنس کوٹ سوٹ (جدید اسٹائل)",
            gender = "Male",
            subType = "Wedding",
            description = "Jet black textured jacquard prince coat with bespoke crest buttons.",
            heightTip = "Single-breasted sleek cut torso ko fit aur tall portrait show karta hai.",
            priceRs = 18000,
            rentPerDayRs = 2800,
            colorHex = 0xFF14141E,
            tag = "Walima Special",
            suitabilityScore = 96
        ),
        FashionItem(
            id = "m_wed_3",
            category = FashionCategory.DRESS_TRY_ON,
            name = "Gold Brocade Waistcoat Kurta Pajama",
            urduName = "بنارسی واسکٹ اور کرتا پاجامہ",
            gender = "Male",
            subType = "Wedding",
            description = "Rich banarsi waistcoat over deep crimson raw silk kurta and churidar.",
            heightTip = "Waistcoat torso par focus rakhti hai aur posture ko erect aur masculine banati hai.",
            priceRs = 12000,
            rentPerDayRs = 2000,
            colorHex = 0xFF8A1C14,
            tag = "Mehndi Dulha",
            suitabilityScore = 95
        ),
        FashionItem(
            id = "f_wed_1",
            category = FashionCategory.DRESS_TRY_ON,
            name = "Royal Crimson Velvet Bridal Lehenga",
            urduName = "شاہی ویلوٹ برائیڈل لہنگا (دلہن)",
            gender = "Female",
            subType = "Wedding",
            description = "Deep ruby velvet lehenga with hand dabka, nakshi, and resham jaal + double veil.",
            heightTip = "High-waisted lehenga flare legs ko elongated look deti hai, heels ke sath perfect.",
            priceRs = 65000,
            rentPerDayRs = 8000,
            colorHex = 0xFF9E1B32,
            tag = "Baraat Dulhan",
            suitabilityScore = 99
        ),
        FashionItem(
            id = "f_wed_2",
            category = FashionCategory.DRESS_TRY_ON,
            name = "Emerald Green Floor-Length Maxi",
            urduName = "زمردی گرین فلور لینتھ میکسی",
            gender = "Female",
            subType = "Wedding",
            description = "Shimmer organza flared gown with cutwork bodice and scalloped borders.",
            heightTip = "Floor length flare unbroken vertical line create karti hai, short height ke liye number 1.",
            priceRs = 22000,
            rentPerDayRs = 3500,
            colorHex = 0xFF0E6251,
            tag = "Walima/Mehndi",
            suitabilityScore = 96
        ),
        FashionItem(
            id = "f_wed_3",
            category = FashionCategory.DRESS_TRY_ON,
            name = "Sunshine Yellow Mehndi Gota Gharara",
            urduName = "مہندی گوٹا کناری پیلا غرارہ",
            gender = "Female",
            subType = "Wedding",
            description = "Traditional silk gharara with chatta patti borders and gota work dupatta.",
            heightTip = "Gharara flare knee break se lower body ko balanced flare look deta hai.",
            priceRs = 19000,
            rentPerDayRs = 3000,
            colorHex = 0xFFF1C40F,
            tag = "Mehndi Queen",
            suitabilityScore = 94
        ),

        // ==================== 2. HAIR + BEARD STUDIO ====================
        FashionItem(
            id = "m_hair_1",
            category = FashionCategory.HAIR_BEARD,
            name = "High Skin Fade + Textured Quiff",
            urduName = "ہائ اسکن فیڈ ود قلف",
            gender = "Male",
            description = "Short shaved sides with upward volume on crown.",
            heightTip = "Top hair volume head profile ko 1 inch uncha dikhata hai, short height ke liye best!",
            priceRs = 800,
            colorHex = 0xFF2A2A38,
            tag = "Face Slimming",
            suitabilityScore = 97
        ),
        FashionItem(
            id = "m_hair_2",
            category = FashionCategory.HAIR_BEARD,
            name = "Classic Side Parting",
            urduName = "کلاسک سائیڈ پارٹ",
            gender = "Male",
            description = "Polished gentleman comb-over with sharp side contour.",
            heightTip = "Tall individuals ke liye balanced elegance deta hai bina over-height kiye.",
            priceRs = 600,
            colorHex = 0xFF1C1C26,
            tag = "Formal Class",
            suitabilityScore = 92
        ),
        FashionItem(
            id = "m_hair_3",
            category = FashionCategory.HAIR_BEARD,
            name = "French Beard (Royal Goatee)",
            urduName = "فرینچ کٹ داڑھی",
            gender = "Male",
            description = "Crisply defined circle beard around mouth and chin.",
            heightTip = "Chin ko prominent karke round aur chubby face ko elongated look deta hai.",
            priceRs = 500,
            colorHex = 0xFF181822,
            tag = "Jawline Sharp",
            suitabilityScore = 94
        ),
        FashionItem(
            id = "m_hair_4",
            category = FashionCategory.HAIR_BEARD,
            name = "Full Groomed Boxed Beard",
            urduName = "فل باکسڈ گرومڈ داڑھی",
            gender = "Male",
            description = "Dense, well-shaped square beard for commanding masculine presence.",
            heightTip = "Broad shoulders aur 5.8ft+ height ke saath royal dulha presence banata hai.",
            priceRs = 700,
            colorHex = 0xFF111118,
            tag = "Dulha Presence",
            suitabilityScore = 95
        ),
        FashionItem(
            id = "f_hair_1",
            category = FashionCategory.HAIR_BEARD,
            name = "Royal Kashee's Bridal Bun",
            urduName = "کاشیز شاہی برائیڈل جوڑا",
            gender = "Female",
            description = "Voluminous high updo encircled with fresh motia pearls and gold pins.",
            heightTip = "High bun crown par 2 inch elevation deta hai, bride ko towering look milti hai.",
            priceRs = 4000,
            colorHex = 0xFF4A120E,
            tag = "Bridal Crown",
            suitabilityScore = 98
        ),
        FashionItem(
            id = "f_hair_2",
            category = FashionCategory.HAIR_BEARD,
            name = "Sleek High Ponytail",
            urduName = "ہائی پونی ٹیل (قد لمبا لگے)",
            gender = "Female",
            description = "Tightly pulled high pony giving instant face lift and long neck profile.",
            heightTip = "Neckline expose hone se aap instant 1.5 inch lambi aur athletic lagti hain.",
            priceRs = 1500,
            colorHex = 0xFF3D2314,
            tag = "Instant Taller",
            suitabilityScore = 96
        ),
        FashionItem(
            id = "f_hair_3",
            category = FashionCategory.HAIR_BEARD,
            name = "Soft Open Waves with Curtain Bangs",
            urduName = "اوپن لئیرڈ ویوز",
            gender = "Female",
            description = "Cascading glossy curls framing jawline and shoulders.",
            heightTip = "Natural softness adds romance to both daily kurtis and wedding maxis.",
            priceRs = 2000,
            colorHex = 0xFF2C1E14,
            tag = "Romantic",
            suitabilityScore = 93
        ),

        // ==================== 3. JEWELLERY + WATCH + ACCESSORIES ====================
        FashionItem(
            id = "f_jewel_1",
            category = FashionCategory.JEWELLERY,
            name = "24K Gold Kundan Bridal Choker Set",
            urduName = "شاہی کندن چوکر سیٹ",
            gender = "Female",
            description = "Handcrafted meenakari choker with emerald drops, matching jhumkas & matha patti.",
            heightTip = "Broad neck frame par choker bohot majestic lagta hai.",
            priceRs = 35000,
            rentPerDayRs = 4000,
            colorHex = 0xFFD4AF37,
            tag = "Bridal Royal",
            suitabilityScore = 97
        ),
        FashionItem(
            id = "f_jewel_2",
            category = FashionCategory.JEWELLERY,
            name = "Solitaire Diamond Cut Necklace Set",
            urduName = "ڈائمنڈ کٹ زرقون سیٹ",
            gender = "Female",
            description = "Rhodium-plated sparkling american diamond necklace with drops.",
            heightTip = "Delicate sparkle neckline ko overpower nahi karta, modern reception look.",
            priceRs = 12000,
            rentPerDayRs = 2000,
            colorHex = 0xFFE0E0E0,
            tag = "Modern Glow",
            suitabilityScore = 94
        ),
        FashionItem(
            id = "f_jewel_3",
            category = FashionCategory.JEWELLERY,
            name = "Daily Pearl Studs & Gold Bangles",
            urduName = "ڈیلی پرل ٹاپس اور چوڑیاں",
            gender = "Female",
            description = "Clean minimalist freshwater pearl tops and 2 gold filigree bangles.",
            heightTip = "Minimal accessories short height ko clutter-free aur neat rakhti hain.",
            priceRs = 4500,
            colorHex = 0xFFFFFDD0,
            tag = "Daily Class",
            suitabilityScore = 95
        ),
        FashionItem(
            id = "m_jewel_1",
            category = FashionCategory.JEWELLERY,
            name = "Luxury Emerald Chronograph Steel Watch",
            urduName = "لگژری کرونوگراف اسٹیل گھڑی",
            gender = "Male",
            description = "Sapphire crystal dial with date complication and solid steel bracelet.",
            heightTip = "Wrist presence enhances masculine authority in suits and sherwanis.",
            priceRs = 8500,
            colorHex = 0xFF0E6251,
            tag = "Executive",
            suitabilityScore = 96
        ),
        FashionItem(
            id = "m_jewel_2",
            category = FashionCategory.JEWELLERY,
            name = "Sleek Silver Chain & Onyx Ring",
            urduName = "سلور چین اور عقیق انگوٹھی",
            gender = "Male",
            description = "2mm Italian silver box chain and sterling silver black onyx statement ring.",
            heightTip = "Subtle collar accents without cluttering the band collar.",
            priceRs = 3800,
            colorHex = 0xFFBDC3C7,
            tag = "Subtle Luxe",
            suitabilityScore = 92
        ),

        // ==================== 4. SHOES & KHUSSA STUDIO ====================
        FashionItem(
            id = "f_shoe_1",
            category = FashionCategory.SHOES,
            name = "Pointed Stiletto Gold Heels (+3.0\" Boost)",
            urduName = "گولڈ ہائی ہیلز (+3 انچ بوسٹ)",
            gender = "Female",
            description = "Metallic gold pointed heels giving posture transformation and 3-inch elevation.",
            heightBoostInches = 3.0f,
            heightTip = "Aap 5.5ft se 5.75ft lagengi! Maximum height transformation.",
            priceRs = 5500,
            rentPerDayRs = 1000,
            colorHex = 0xFFD4AF37,
            tag = "Max Height (+3\")",
            suitabilityScore = 99
        ),
        FashionItem(
            id = "f_shoe_2",
            category = FashionCategory.SHOES,
            name = "Handmade Kundan Tilla Khussa",
            urduName = "کندن کڑھائی ویلوٹ کھسہ",
            gender = "Female",
            description = "Pure velvet khussa with genuine dabka embroidery and soft double-cushioned sole.",
            heightBoostInches = 0.5f,
            heightTip = "Mehndi aur Eid ke liye authentic traditional footwear.",
            priceRs = 2800,
            colorHex = 0xFF8A1C14,
            tag = "Ethnic Khussa",
            suitabilityScore = 95
        ),
        FashionItem(
            id = "f_shoe_3",
            category = FashionCategory.SHOES,
            name = "Comfort Block Heel Sandals (+2.0\" Boost)",
            urduName = "بلاک ہیلز (+2 انچ بوسٹ)",
            gender = "Female",
            description = "Nude patent block heels for steady walking all day at work or weddings.",
            heightBoostInches = 2.0f,
            heightTip = "Steady 2 inch height lift with zero foot pain.",
            priceRs = 4200,
            colorHex = 0xFFD8A878,
            tag = "Daily Lift (+2\")",
            suitabilityScore = 96
        ),
        FashionItem(
            id = "m_shoe_1",
            category = FashionCategory.SHOES,
            name = "Royal Black & Gold Tilla Khussa",
            urduName = "شاہی کالا اور سنہری کھسہ",
            gender = "Male",
            description = "Hand-stitched leather khussa with intricate golden threadwork.",
            heightBoostInches = 0.8f,
            heightTip = "Shalwar kameez aur sherwani ke sath must-have royal Pakistani look.",
            priceRs = 3200,
            rentPerDayRs = 600,
            colorHex = 0xFFD4AF37,
            tag = "Sherwani Match",
            suitabilityScore = 97
        ),
        FashionItem(
            id = "m_shoe_2",
            category = FashionCategory.SHOES,
            name = "Hidden Lift Oxford Shoes (+1.8\" Boost)",
            urduName = "فارمل آکسفورڈ شوز (+1.8 انچ بوسٹ)",
            gender = "Male",
            description = "Premium calfskin leather with discreet built-in 1.8-inch elevation insole.",
            heightBoostInches = 1.8f,
            heightTip = "Suit ya pants ke sath natural height boost, koi pehchan nahi sakta.",
            priceRs = 6500,
            rentPerDayRs = 1200,
            colorHex = 0xFF14141E,
            tag = "Height Booster",
            suitabilityScore = 98
        ),
        FashionItem(
            id = "m_shoe_3",
            category = FashionCategory.SHOES,
            name = "Urban Cushion Joggers (+1.2\" Boost)",
            urduName = "اسپورٹس جاگرز (+1.2 انچ)",
            gender = "Male",
            description = "Breathable mesh lightweight running trainers.",
            heightBoostInches = 1.2f,
            heightTip = "University aur daily walk ke liye maximum comfort.",
            priceRs = 3800,
            colorHex = 0xFF2C3E50,
            tag = "Casual Run",
            suitabilityScore = 91
        ),

        // ==================== 5. MEHNDI DESIGNER ====================
        FashionItem(
            id = "f_mehndi_1",
            category = FashionCategory.MEHNDI,
            name = "Kashee's Peacock Royal Bridal Arms",
            urduName = "کاشیز شاہی مور برائیڈل مہندی",
            gender = "Female",
            description = "Intricate peacock motifs, dulha portrait, and micro-grid fill up to elbows.",
            heightTip = "Grand statement for baraat bride.",
            priceRs = 8000,
            colorHex = 0xFF511812,
            tag = "Bridal Kashee",
            suitabilityScore = 99
        ),
        FashionItem(
            id = "f_mehndi_2",
            category = FashionCategory.MEHNDI,
            name = "Khafif Arabic Floral Trail (Back Hand)",
            urduName = "خفیف عربی بیل ڈیزائن",
            gender = "Female",
            description = "Diagonal flowing floral creeper from index finger to wrist.",
            heightTip = "Diagonal lines haath ko slim aur lamba dikhati hain.",
            priceRs = 2500,
            colorHex = 0xFF641E16,
            tag = "Arabic Chic",
            suitabilityScore = 95
        ),
        FashionItem(
            id = "f_mehndi_3",
            category = FashionCategory.MEHNDI,
            name = "Minimalist Delicate Center Mandala",
            urduName = "نازک سینٹر منڈالہ",
            gender = "Female",
            description = "Circular lace mandala on palm with decorated fingertips.",
            heightTip = "Daily routine aur college/office ke liye neat and quick.",
            priceRs = 1500,
            colorHex = 0xFF78281F,
            tag = "Minimal Daily",
            suitabilityScore = 94
        )
    )

    val nearShops = listOf(
        NearShop("s1", "Jamil Hair & Beard Studio", "Barber", "500m away", 4.9f, "Rs 400 - Fade & Styling", "Main Jinnah Road"),
        NearShop("s2", "Zahid Master Tailor & Suits", "Tailor", "700m away", 4.8f, "Rs 1,800 - 24hr Stitched", "Liaquat Bazar"),
        NearShop("s3", "Bolan Gents & Bridal Salon", "Barber", "1.1km away", 4.7f, "Rs 800 - Groom Package", "Cantt Area"),
        NearShop("s4", "Royal Dulha & Bridal Sherwani Rent", "Rent Shop", "900m away", 4.9f, "Sherwani on Rent 1 Day: Rs 3,000", "Zarghoon Plaza"),
        NearShop("s5", "Kashee's Replica Bridal Rent Shop", "Rent Shop", "1.4km away", 4.8f, "Lehenga Rent 1 Day: Rs 7,500", "Model Town"),
        NearShop("s6", "Daraz Express Pickup Hub", "Daraz Outlet", "300m away", 4.6f, "Free delivery on orders > Rs 1,500", "Circular Road")
    )

    fun getItemsForCategory(category: FashionCategory, gender: String): List<FashionItem> {
        return items.filter { item ->
            item.category == category && (item.gender == gender || item.gender == "Both" || item.category == FashionCategory.MEHNDI)
        }
    }
}
