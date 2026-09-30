package com.example.data.model

import com.example.R

object FashionCatalog {

    val cityWeathers = listOf(
        CityWeather("Lahore", 24, "Pleasant", "Lahore ka mausam khushgawar hai (24°C), cotton lawn aur linen suits ideal hain."),
        CityWeather("Karachi", 29, "Humid Breeze", "Karachi me halki dhoop aur hawa hai (29°C), light breathable fabrics select karein."),
        CityWeather("Islamabad", 18, "Cool Breeze", "Islamabad me mausam thanda hai (18°C), jacket ya velvet touch stylish lagega."),
        CityWeather("Quetta", 12, "Chilly", "Quetta me aaj thand hai (12°C), is look ke sath pashmina shawl ya warm waistcoat add karein."),
        CityWeather("Peshawar", 20, "Dry & Fresh", "Peshawar me crisp mausam hai, peshawari chappal aur khadi kameez perfect hai.")
    )

    fun getWeatherForCity(city: String): CityWeather {
        return cityWeathers.firstOrNull { it.city.equals(city, ignoreCase = true) }
            ?: cityWeathers.first()
    }

    val nearShops: List<NearShop> = listOf(
        NearShop("s1", "Ustad Shaukat Master Tailor", "Tailor", "0.8 km", 4.9f, "Rs 1,200 Stitching", "Anarkali, Lahore"),
        NearShop("s2", "Royal Bridal & Sherwani Rent", "Rent Shop", "1.2 km", 4.8f, "From Rs 2,500/day", "Mall Road, Lahore"),
        NearShop("s3", "Toni&Guy Executive Grooming", "Barber", "2.1 km", 4.9f, "Rs 800 Fade & Beard", "Gulberg III, Lahore"),
        NearShop("s4", "Daraz Express Pickup Hub", "Daraz Hub", "0.5 km", 4.7f, "Free Parcel Pickup", "Main Market, Lahore")
    )

    val items: List<FashionItem> = listOf(
        // =========================================================================
        // MALE DRESSES (10 Real Options with Multi-Store Sources)
        // =========================================================================
        FashionItem(
            id = "m_dress_1",
            category = FashionCategory.DRESS_TRY_ON,
            pehnoCategory = PehnoCategory.DRESS,
            name = "Classic Black Shalwar Kameez",
            urduName = "کلاسک بلیک شلوار قمیض",
            gender = "Male",
            subType = "Daily",
            description = "Tailored rich jet black Pakistani cotton suit with royal ban collar placket.",
            heightTip = "Straight vertical drape aur monochrome black color height ko 2 inch lamba dikhata hai.",
            priceRs = 4500,
            darazLink = "https://www.daraz.pk/catalog/?q=black+shalwar+kameez+men&aff=pehno10",
            frontImageUrl = "https://images.unsplash.com/photo-1617137984095-74e4e5e3613f?auto=format&fit=crop&w=800&q=80",
            backImageUrl = "https://images.unsplash.com/photo-1507679799987-c73779587ccf?auto=format&fit=crop&w=800&q=80",
            storeSource = "Source: Daraz.pk - Rs. 4,500",
            tag = "Top Seller",
            suitabilityScore = 98,
            drawableResId = R.drawable.model_black_shalwar_male
        ),
        FashionItem(
            id = "m_dress_2",
            category = FashionCategory.DRESS_TRY_ON,
            pehnoCategory = PehnoCategory.DRESS,
            name = "Royal Navy Blue Sherwani",
            urduName = "شاہی نیوی بلیو شیروانی",
            gender = "Male",
            subType = "Wedding",
            description = "Exquisite jamawar fabric with gold metallic embroidery on collar and cuffs.",
            heightTip = "Structured shoulder pads aur slim knee-length cut masculine aur royal posture banata hai.",
            priceRs = 18500,
            darazLink = "https://www.khaadi.com/men/formal?aff=pehno10",
            frontImageUrl = "https://images.unsplash.com/photo-1594938298603-c8148c4dae35?auto=format&fit=crop&w=800&q=80",
            backImageUrl = "https://images.unsplash.com/photo-1594938298603-c8148c4dae35?auto=format&fit=crop&w=800&q=80",
            storeSource = "Source: Khaadi Online - Rs. 18,500",
            tag = "Wedding Formal",
            suitabilityScore = 96,
            drawableResId = R.drawable.model_sherwani_male
        ),
        FashionItem(
            id = "m_dress_3",
            category = FashionCategory.DRESS_TRY_ON,
            pehnoCategory = PehnoCategory.DRESS,
            name = "White Cotton Kurta Pajama",
            urduName = "خالص سفید کُرتا پاجامہ",
            gender = "Male",
            subType = "Daily",
            description = "Breathable pure Egyptian cotton kurta with delicate self-thread threadwork.",
            heightTip = "Clean white lines create a crisp, dignified, and tall silhouette.",
            priceRs = 3800,
            darazLink = "https://www.junaidjamshed.com/men?aff=pehno10",
            frontImageUrl = "https://images.unsplash.com/photo-1597983073493-88cd35cf93b0?auto=format&fit=crop&w=800&q=80",
            backImageUrl = "https://images.unsplash.com/photo-1597983073493-88cd35cf93b0?auto=format&fit=crop&w=800&q=80",
            storeSource = "Source: J. Junaid Jamshed - Rs. 3,800",
            tag = "Jumma Special",
            suitabilityScore = 94,
            drawableResId = R.drawable.model_black_shalwar_male
        ),
        FashionItem(
            id = "m_dress_4",
            category = FashionCategory.DRESS_TRY_ON,
            pehnoCategory = PehnoCategory.DRESS,
            name = "Emerald Green Silk Kurta Suit",
            urduName = "زمردی ریشمی کُرتا سوٹ",
            gender = "Male",
            subType = "Wedding",
            description = "Raw silk festive ensemble with antique copper piping and mother-of-pearl buttons.",
            heightTip = "Rich emerald hue flatters medium wheatish Pakistani skin tones perfectly.",
            priceRs = 6200,
            darazLink = "https://gulahmedshop.com/men?aff=pehno10",
            frontImageUrl = "https://images.unsplash.com/photo-1563245372-f21724e3856d?auto=format&fit=crop&w=800&q=80",
            backImageUrl = "https://images.unsplash.com/photo-1563245372-f21724e3856d?auto=format&fit=crop&w=800&q=80",
            storeSource = "Source: Sana Collection Lahore - Rs. 6,200",
            tag = "Mehndi Night",
            suitabilityScore = 95,
            drawableResId = R.drawable.model_black_shalwar_male
        ),
        FashionItem(
            id = "m_dress_5",
            category = FashionCategory.DRESS_TRY_ON,
            pehnoCategory = PehnoCategory.DRESS,
            name = "Maroon Velvet Prince Coat & Shalwar",
            urduName = "میرون مخمل پرنس کوٹ",
            gender = "Male",
            subType = "Wedding",
            description = "Heavy velvet tailored prince coat with mandarin collar and engraved brass buttons.",
            heightTip = "Short structured coat elongates lower limbs giving prominent height.",
            priceRs = 14000,
            darazLink = "https://instagram.com/bridal_quetta?aff=pehno10",
            frontImageUrl = "https://images.unsplash.com/photo-1507679799987-c73779587ccf?auto=format&fit=crop&w=800&q=80",
            backImageUrl = "https://images.unsplash.com/photo-1507679799987-c73779587ccf?auto=format&fit=crop&w=800&q=80",
            storeSource = "Source: @bridal_quetta Instagram - Rs. 14,000",
            tag = "Baraat Luxury",
            suitabilityScore = 97,
            drawableResId = R.drawable.model_sherwani_male
        ),
        FashionItem(
            id = "m_dress_6",
            category = FashionCategory.DRESS_TRY_ON,
            pehnoCategory = PehnoCategory.DRESS,
            name = "Charcoal Linen Designer Kurta",
            urduName = "چارکول لیلن ڈیزائنر کُرتا",
            gender = "Male",
            subType = "Daily",
            description = "Premium washed Irish linen with casual roll-up sleeves and wooden buttons.",
            heightTip = "Charcoal shade hides midsection bulk and slims chest line.",
            priceRs = 5200,
            darazLink = "https://bagallery.com/men?aff=pehno10",
            frontImageUrl = "https://images.unsplash.com/photo-1617137984095-74e4e5e3613f?auto=format&fit=crop&w=800&q=80",
            backImageUrl = "https://images.unsplash.com/photo-1617137984095-74e4e5e3613f?auto=format&fit=crop&w=800&q=80",
            storeSource = "Source: Bagallery Official - Rs. 5,200",
            tag = "Summer Breeze",
            suitabilityScore = 91,
            drawableResId = R.drawable.model_black_shalwar_male
        ),
        FashionItem(
            id = "m_dress_7",
            category = FashionCategory.DRESS_TRY_ON,
            pehnoCategory = PehnoCategory.DRESS,
            name = "Ivory Raw Silk Angrakha Kurta",
            urduName = "عاجی را سلک انگرکھا",
            gender = "Male",
            subType = "Wedding",
            description = "Traditional crossover Mughal angrakha cut with side tassels and embroidery.",
            heightTip = "Diagonal wrap draws the eyes upward, enhancing overall tallness.",
            priceRs = 9400,
            darazLink = "https://gulahmedshop.com?aff=pehno10",
            frontImageUrl = "https://images.unsplash.com/photo-1594938298603-c8148c4dae35?auto=format&fit=crop&w=800&q=80",
            backImageUrl = "https://images.unsplash.com/photo-1594938298603-c8148c4dae35?auto=format&fit=crop&w=800&q=80",
            storeSource = "Source: Gul Ahmed Festive - Rs. 9,400",
            tag = "Royal Heritage",
            suitabilityScore = 93,
            drawableResId = R.drawable.model_sherwani_male
        ),
        FashionItem(
            id = "m_dress_8",
            category = FashionCategory.DRESS_TRY_ON,
            pehnoCategory = PehnoCategory.DRESS,
            name = "Mustard Festive Cotton Kurta",
            urduName = "سرسوں کاٹن کُرتا شلوار",
            gender = "Male",
            subType = "Daily",
            description = "Vibrant mustard tone designed for Mayun and Haldi functions.",
            heightTip = "Bright festive tone stands out in group wedding photography.",
            priceRs = 4200,
            darazLink = "https://alkaramstudio.com?aff=pehno10",
            frontImageUrl = "https://images.unsplash.com/photo-1597983073493-88cd35cf93b0?auto=format&fit=crop&w=800&q=80",
            backImageUrl = "https://images.unsplash.com/photo-1597983073493-88cd35cf93b0?auto=format&fit=crop&w=800&q=80",
            storeSource = "Source: Alkaram Studio - Rs. 4,200",
            tag = "Mayun Must",
            suitabilityScore = 90,
            drawableResId = R.drawable.model_black_shalwar_male
        ),
        FashionItem(
            id = "m_dress_9",
            category = FashionCategory.DRESS_TRY_ON,
            pehnoCategory = PehnoCategory.DRESS,
            name = "Sky Blue Lawn Kurta Pajama",
            urduName = "آسمانی لان کُرتا پاجامہ",
            gender = "Male",
            subType = "Daily",
            description = "Featherlight cooling summer lawn with subtle chest pocket embroidery.",
            heightTip = "Soft pastel tone keeps daytime outdoor casual posture calm and sharp.",
            priceRs = 3500,
            darazLink = "https://generation.com.pk?aff=pehno10",
            frontImageUrl = "https://images.unsplash.com/photo-1563245372-f21724e3856d?auto=format&fit=crop&w=800&q=80",
            backImageUrl = "https://images.unsplash.com/photo-1563245372-f21724e3856d?auto=format&fit=crop&w=800&q=80",
            storeSource = "Source: Generation PK - Rs. 3,500",
            tag = "Casual Cool",
            suitabilityScore = 89,
            drawableResId = R.drawable.model_black_shalwar_male
        ),
        FashionItem(
            id = "m_dress_10",
            category = FashionCategory.DRESS_TRY_ON,
            pehnoCategory = PehnoCategory.DRESS,
            name = "Gold Brocade Wedding Sherwani",
            urduName = "سنہری بروکید شیروانی",
            gender = "Male",
            subType = "Wedding",
            description = "Heirloom gold metallic weave with zardozi crest on collar.",
            heightTip = "Regal gold texture catches ceremony lights and frames the groom.",
            priceRs = 22000,
            darazLink = "https://anarkali.pk?aff=pehno10",
            frontImageUrl = "https://images.unsplash.com/photo-1594938298603-c8148c4dae35?auto=format&fit=crop&w=800&q=80",
            backImageUrl = "https://images.unsplash.com/photo-1594938298603-c8148c4dae35?auto=format&fit=crop&w=800&q=80",
            storeSource = "Source: Anarkali Bazaar Lahore - Rs. 22,000",
            tag = "Groom Grandeur",
            suitabilityScore = 99,
            drawableResId = R.drawable.model_sherwani_male
        ),

        // =========================================================================
        // MALE GALA / COLLAR (10 Real Options)
        // =========================================================================
        FashionItem(id = "m_gala_1", pehnoCategory = PehnoCategory.GALA_COLLAR, name = "Structured Ban Collar", urduName = "اسٹرکچرڈ بین کالر", gender = "Male", description = "Crisp upright stand collar that lengthens neck profile.", heightTip = "Adds 1 inch visual height to head-neck alignment.", priceRs = 450, frontImageUrl = "https://images.unsplash.com/photo-1594938298603-c8148c4dae35?auto=format&fit=crop&w=600&q=80", storeSource = "Tailor Master Cut"),
        FashionItem(id = "m_gala_2", pehnoCategory = PehnoCategory.GALA_COLLAR, name = "Sherwani High Neck", urduName = "شیروانی ہائی نیک", gender = "Male", description = "Regal military high collar with velvet inner cushion.", heightTip = "Majestic vertical neckline posture.", priceRs = 650, frontImageUrl = "https://images.unsplash.com/photo-1594938298603-c8148c4dae35?auto=format&fit=crop&w=600&q=80", storeSource = "Daraz Tailoring"),
        FashionItem(id = "m_gala_3", pehnoCategory = PehnoCategory.GALA_COLLAR, name = "Zari Embroidered Placket", urduName = "زری کڑھائی پٹی", gender = "Male", description = "Fine golden thread embroidery down the front chest placket.", heightTip = "Center placket pulls eye line upwards.", priceRs = 850, frontImageUrl = "https://images.unsplash.com/photo-1617137984095-74e4e5e3613f?auto=format&fit=crop&w=600&q=80", storeSource = "Khaadi Artisans"),
        FashionItem(id = "m_gala_4", pehnoCategory = PehnoCategory.GALA_COLLAR, name = "Hidden Button Minimalist", urduName = "پوشیدہ بٹن گلا", gender = "Male", description = "Sleek concealed placket for clean modern look.", heightTip = "Unbroken line enhances slimming effect.", priceRs = 400, frontImageUrl = "https://images.unsplash.com/photo-1507679799987-c73779587ccf?auto=format&fit=crop&w=600&q=80", storeSource = "J. Modern"),
        FashionItem(id = "m_gala_5", pehnoCategory = PehnoCategory.GALA_COLLAR, name = "Kurta Patti with Tassels", urduName = "کُرتا پٹی اور ٹیسلز", gender = "Male", description = "Traditional festive cord detailing with handmade tassels.", heightTip = "Festive ethnic elegance.", priceRs = 550, frontImageUrl = "https://images.unsplash.com/photo-1597983073493-88cd35cf93b0?auto=format&fit=crop&w=600&q=80", storeSource = "Sana Lahore"),
        FashionItem(id = "m_gala_6", pehnoCategory = PehnoCategory.GALA_COLLAR, name = "Velvet Trim Stand Collar", urduName = "مخمل بین کالر", gender = "Male", description = "Soft micro-velvet lining along inner collar edge.", heightTip = "Comfortable upright luxury frame.", priceRs = 700, frontImageUrl = "https://images.unsplash.com/photo-1507679799987-c73779587ccf?auto=format&fit=crop&w=600&q=80", storeSource = "Gul Ahmed"),
        FashionItem(id = "m_gala_7", pehnoCategory = PehnoCategory.GALA_COLLAR, name = "Angrakha Overlap Gala", urduName = "انگرکھا اوورلیپ گلا", gender = "Male", description = "Mughal asymmetric closure with side dori knot.", heightTip = "Broadens chest while keeping waist tapered.", priceRs = 900, frontImageUrl = "https://images.unsplash.com/photo-1594938298603-c8148c4dae35?auto=format&fit=crop&w=600&q=80", storeSource = "Royal Couture"),
        FashionItem(id = "m_gala_8", pehnoCategory = PehnoCategory.GALA_COLLAR, name = "Double Stitch Formal Collar", urduName = "ڈبل سلائی فارمل کالر", gender = "Male", description = "Sharp edge precision stitching for office and Jumma.", heightTip = "Subtle professional contour.", priceRs = 450, frontImageUrl = "https://images.unsplash.com/photo-1617137984095-74e4e5e3613f?auto=format&fit=crop&w=600&q=80", storeSource = "Daraz"),
        FashionItem(id = "m_gala_9", pehnoCategory = PehnoCategory.GALA_COLLAR, name = "Loop Button Traditional Neck", urduName = "لوپ بٹن روایتی گلا", gender = "Male", description = "Silk fabric loops with metallic shank buttons.", heightTip = "Traditional bespoke aesthetic.", priceRs = 500, frontImageUrl = "https://images.unsplash.com/photo-1597983073493-88cd35cf93b0?auto=format&fit=crop&w=600&q=80", storeSource = "Alkaram"),
        FashionItem(id = "m_gala_10", pehnoCategory = PehnoCategory.GALA_COLLAR, name = "Contrast Piping Placket", urduName = "کنٹراسٹ پائپنگ پٹی", gender = "Male", description = "Subtle gold or silver border piping along front collar.", heightTip = "Sharp contrast definition.", priceRs = 600, frontImageUrl = "https://images.unsplash.com/photo-1563245372-f21724e3856d?auto=format&fit=crop&w=600&q=80", storeSource = "Anarkali Tailors"),

        // =========================================================================
        // MALE TOPI / CAP (10 Real Options)
        // =========================================================================
        FashionItem(id = "m_topi_1", pehnoCategory = PehnoCategory.TOPI_CAP, name = "Sindhi Handcrafted Topi", urduName = "سندھی ہینڈ کرافٹڈ ٹوپی", gender = "Male", description = "Geometrical mirror work with deep crown cutaway.", heightTip = "Iconic cultural stature.", priceRs = 1200, frontImageUrl = "https://images.unsplash.com/photo-1579783902614-a3fb3927b675?auto=format&fit=crop&w=600&q=80", storeSource = "Sindh Crafts"),
        FashionItem(id = "m_topi_2", pehnoCategory = PehnoCategory.TOPI_CAP, name = "Peshawari Karakul Cap", urduName = "پشاوری قراقلی ٹوپی", gender = "Male", description = "Genuine Persian astrakhan wool Jinnah cap.", heightTip = "Adds 2 inches vertical height to stature.", priceRs = 3500, frontImageUrl = "https://images.unsplash.com/photo-1507679799987-c73779587ccf?auto=format&fit=crop&w=600&q=80", storeSource = "Karakul House Peshawar"),
        FashionItem(id = "m_topi_3", pehnoCategory = PehnoCategory.TOPI_CAP, name = "Royal Groom Turban (Pagri)", urduName = "شاہی دلہا پگڑی", gender = "Male", description = "Pleated raw silk safa with gold zardozi kalgi.", heightTip = "Gives commanding groom presence.", priceRs = 4500, frontImageUrl = "https://images.unsplash.com/photo-1594938298603-c8148c4dae35?auto=format&fit=crop&w=600&q=80", storeSource = "Dulha Pagri Lahore"),
        FashionItem(id = "m_topi_4", pehnoCategory = PehnoCategory.TOPI_CAP, name = "Chitrali Pakol Cap", urduName = "چترالی پکول ٹوپی", gender = "Male", description = "Pure northern sheep wool roll-up mountain hat.", heightTip = "Rugged winter comfort.", priceRs = 900, frontImageUrl = "https://images.unsplash.com/photo-1563245372-f21724e3856d?auto=format&fit=crop&w=600&q=80", storeSource = "Swat Valley Traders"),
        FashionItem(id = "m_topi_5", pehnoCategory = PehnoCategory.TOPI_CAP, name = "Balochi Shishe-Wali Topi", urduName = "بلوچی شیشہ والی ٹوپی", gender = "Male", description = "Ornate threadwork with sparkling mirror highlights.", heightTip = "Traditional Quetta design.", priceRs = 1500, frontImageUrl = "https://images.unsplash.com/photo-1579783902614-a3fb3927b675?auto=format&fit=crop&w=600&q=80", storeSource = "Quetta Bazaar"),
        FashionItem(id = "m_topi_6", pehnoCategory = PehnoCategory.TOPI_CAP, name = "Black Velvet Kulla", urduName = "بلیک ویلوٹ کلا", gender = "Male", description = "Embroidered skullcap base for formal gatherings.", heightTip = "Crisp and classic.", priceRs = 800, frontImageUrl = "https://images.unsplash.com/photo-1617137984095-74e4e5e3613f?auto=format&fit=crop&w=600&q=80", storeSource = "Daraz"),
        FashionItem(id = "m_topi_7", pehnoCategory = PehnoCategory.TOPI_CAP, name = "White Prayer Kufi Cap", urduName = "سفید جالی دار کوفی", gender = "Male", description = "Fine openwork cotton cap for daily namaz.", heightTip = "Clean spiritual simplicity.", priceRs = 350, frontImageUrl = "https://images.unsplash.com/photo-1597983073493-88cd35cf93b0?auto=format&fit=crop&w=600&q=80", storeSource = "Madinah Collection"),
        FashionItem(id = "m_topi_8", pehnoCategory = PehnoCategory.TOPI_CAP, name = "Golden Sehra Festive Topi", urduName = "سنہری سہرہ ٹوپی", gender = "Male", description = "Baraat special headpiece with floral hanging threads.", heightTip = "Bespoke ceremony look.", priceRs = 2200, frontImageUrl = "https://images.unsplash.com/photo-1594938298603-c8148c4dae35?auto=format&fit=crop&w=600&q=80", storeSource = "Anarkali"),
        FashionItem(id = "m_topi_9", pehnoCategory = PehnoCategory.TOPI_CAP, name = "Embroidered Kashmiri Wool Topi", urduName = "کشمیری اونی ٹوپی", gender = "Male", description = "Fine needlework floral embroidery on pure pashmina base.", heightTip = "Warm heirloom texture.", priceRs = 1800, frontImageUrl = "https://images.unsplash.com/photo-1507679799987-c73779587ccf?auto=format&fit=crop&w=600&q=80", storeSource = "Kashmir Shawls"),
        FashionItem(id = "m_topi_10", pehnoCategory = PehnoCategory.TOPI_CAP, name = "Classic Jinnah Fur Cap", urduName = "قائدِ اعظم والی ٹوپی", gender = "Male", description = "Historic folding fur cap symbolizing national pride.", heightTip = "Distinguished dignitary stance.", priceRs = 2800, frontImageUrl = "https://images.unsplash.com/photo-1507679799987-c73779587ccf?auto=format&fit=crop&w=600&q=80", storeSource = "Jinnah House"),

        // =========================================================================
        // MALE HAIRSTYLE (10 Real Options)
        // =========================================================================
        FashionItem(id = "m_hair_1", category = FashionCategory.HAIR_BEARD, pehnoCategory = PehnoCategory.HAIRSTYLE, name = "Side Parting Taper Fade", urduName = "سائیڈ پارٹنگ ٹیپر فیڈ", gender = "Male", description = "Sharp gentleman side partition with smooth skin taper.", heightTip = "Elongates head profile by 1 inch.", priceRs = 600, frontImageUrl = "https://images.unsplash.com/photo-1622286342621-4bd786c2447c?auto=format&fit=crop&w=600&q=80", storeSource = "Toni&Guy Lahore"),
        FashionItem(id = "m_hair_2", category = FashionCategory.HAIR_BEARD, pehnoCategory = PehnoCategory.HAIRSTYLE, name = "Classic Slick Back", urduName = "کلاسک سلک بیک", gender = "Male", description = "Pomade combed back with medium density volume.", heightTip = "Gives polished formal authority.", priceRs = 700, frontImageUrl = "https://images.unsplash.com/photo-1617137984095-74e4e5e3613f?auto=format&fit=crop&w=600&q=80", storeSource = "Grooming Lounge"),
        FashionItem(id = "m_hair_3", category = FashionCategory.HAIR_BEARD, pehnoCategory = PehnoCategory.HAIRSTYLE, name = "Royal Pompadour", urduName = "شاہی پومپاڈور ہیئر", gender = "Male", description = "High-volume lifted crown with neat scissor cut sides.", heightTip = "Adds 1.5 inches direct height boost.", priceRs = 800, frontImageUrl = "https://images.unsplash.com/photo-1507679799987-c73779587ccf?auto=format&fit=crop&w=600&q=80", storeSource = "Royale Salon"),
        FashionItem(id = "m_hair_4", category = FashionCategory.HAIR_BEARD, pehnoCategory = PehnoCategory.HAIRSTYLE, name = "Textured Modern Quiff", urduName = "ٹیکسچرڈ کوئف", gender = "Male", description = "Matte clay finger-styled top with low fade.", heightTip = "Modern youthful framing.", priceRs = 650, frontImageUrl = "https://images.unsplash.com/photo-1594938298603-c8148c4dae35?auto=format&fit=crop&w=600&q=80", storeSource = "Karachi Barbers"),
        FashionItem(id = "m_hair_5", category = FashionCategory.HAIR_BEARD, pehnoCategory = PehnoCategory.HAIRSTYLE, name = "Clean Fade with Trimmed Beard", urduName = "فیڈ کٹ اور سیٹ داڑھی", gender = "Male", description = "Sharp beard cheek line with mid drop fade.", heightTip = "Chisels jawline and slims face.", priceRs = 900, frontImageUrl = "https://images.unsplash.com/photo-1597983073493-88cd35cf93b0?auto=format&fit=crop&w=600&q=80", storeSource = "Men's Cave"),
        FashionItem(id = "m_hair_6", category = FashionCategory.HAIR_BEARD, pehnoCategory = PehnoCategory.HAIRSTYLE, name = "Natural Wave Medium Crop", urduName = "قدرتی ویوی اسٹائل", gender = "Male", description = "Effortless casual texture with neat ear line.", heightTip = "Natural softness for daily wear.", priceRs = 500, frontImageUrl = "https://images.unsplash.com/photo-1563245372-f21724e3856d?auto=format&fit=crop&w=600&q=80", storeSource = "Local Ustads"),
        FashionItem(id = "m_hair_7", category = FashionCategory.HAIR_BEARD, pehnoCategory = PehnoCategory.HAIRSTYLE, name = "Short Buzz Military Cut", urduName = "ملٹری بز کٹ", gender = "Male", description = "Low maintenance high & tight clipper cut.", heightTip = "Highlights facial bone structure.", priceRs = 400, frontImageUrl = "https://images.unsplash.com/photo-1622286342621-4bd786c2447c?auto=format&fit=crop&w=600&q=80", storeSource = "Army Salon"),
        FashionItem(id = "m_hair_8", category = FashionCategory.HAIR_BEARD, pehnoCategory = PehnoCategory.HAIRSTYLE, name = "Groom Royal Beard & Hair Combo", urduName = "دلہا شاہی داڑھی و بال", gender = "Male", description = "Sculpted beard with temple fade and shine serum.", heightTip = "Wedding day master grooming.", priceRs = 1500, frontImageUrl = "https://images.unsplash.com/photo-1507679799987-c73779587ccf?auto=format&fit=crop&w=600&q=80", storeSource = "Bridal Salon"),
        FashionItem(id = "m_hair_9", category = FashionCategory.HAIR_BEARD, pehnoCategory = PehnoCategory.HAIRSTYLE, name = "Clean Shaven Gentleman", urduName = "کلین شیو جنٹلمین", gender = "Male", description = "Smooth hot towel razor finish with parted hair.", heightTip = "Timeless traditional simplicity.", priceRs = 450, frontImageUrl = "https://images.unsplash.com/photo-1617137984095-74e4e5e3613f?auto=format&fit=crop&w=600&q=80", storeSource = "Classic Barber"),
        FashionItem(id = "m_hair_10", category = FashionCategory.HAIR_BEARD, pehnoCategory = PehnoCategory.HAIRSTYLE, name = "Long Flowing Waves with Headband", urduName = "لمبے گھنگھریالے بال", gender = "Male", description = "Shoulder length artistic waves with side tuck.", heightTip = "Boho cultural flair.", priceRs = 800, frontImageUrl = "https://images.unsplash.com/photo-1563245372-f21724e3856d?auto=format&fit=crop&w=600&q=80", storeSource = "Urban Stylist"),

        // =========================================================================
        // MALE SHOES / JOOTA (10 Real Options with Height Boost)
        // =========================================================================
        FashionItem(id = "m_shoes_1", category = FashionCategory.SHOES, pehnoCategory = PehnoCategory.SHOES, name = "Handcrafted Black Velvet Khussa", urduName = "ہاتھ کا بنا سیاہ مخمل کھسہ", gender = "Male", heightBoostInches = 1.5f, description = "Genuine buffalo leather sole with plush black velvet and gold tilla work.", heightTip = "+1.5 inch natural sole lift keeps posture upright.", priceRs = 2600, frontImageUrl = "https://images.unsplash.com/photo-1549298916-b41d501d3772?auto=format&fit=crop&w=600&q=80", storeSource = "Source: Daraz.pk - Rs. 2,600"),
        FashionItem(id = "m_shoes_2", category = FashionCategory.SHOES, pehnoCategory = PehnoCategory.SHOES, name = "Kaptaan Peshawari Chappal", urduName = "کپتان پشاوری چپل", gender = "Male", heightBoostInches = 2.0f, description = "Double leather tyre sole with semi-matte mustard finish.", heightTip = "+2.0 inch authentic sturdy tyre sole boost.", priceRs = 3400, frontImageUrl = "https://images.unsplash.com/photo-1560769629-975ec94e6a86?auto=format&fit=crop&w=600&q=80", storeSource = "Source: Namak Mandi Peshawar - Rs. 3,400"),
        FashionItem(id = "m_shoes_3", category = FashionCategory.SHOES, pehnoCategory = PehnoCategory.SHOES, name = "Golden Zari Wedding Khussa", urduName = "سنہری زری دولہا کھسہ", gender = "Male", heightBoostInches = 1.8f, description = "Intricate zardozi embroidery on champagne silk base.", heightTip = "+1.8 inch cushioned insole for day-long comfort.", priceRs = 3800, frontImageUrl = "https://images.unsplash.com/photo-1549298916-b41d501d3772?auto=format&fit=crop&w=600&q=80", storeSource = "Source: Anarkali Bazaar - Rs. 3,800"),
        FashionItem(id = "m_shoes_4", category = FashionCategory.SHOES, pehnoCategory = PehnoCategory.SHOES, name = "Tan Brown Leather Oxford", urduName = "براؤن لیدر آکسفورڈ شوز", gender = "Male", heightBoostInches = 1.6f, description = "Full grain calfskin leather with polished cap toe.", heightTip = "+1.6 inch stacked heel gives executive height.", priceRs = 6500, frontImageUrl = "https://images.unsplash.com/photo-1614252235316-8c857d38b5f4?auto=format&fit=crop&w=600&q=80", storeSource = "Source: Bata Executive - Rs. 6,500"),
        FashionItem(id = "m_shoes_5", category = FashionCategory.SHOES, pehnoCategory = PehnoCategory.SHOES, name = "Traditional Kolhapuri Chappal", urduName = "روایتی کولہا پوری چپل", gender = "Male", heightBoostInches = 1.2f, description = "Hand-braided leather straps with vegetable tanned sole.", heightTip = "+1.2 inch flat ethnic stability.", priceRs = 2200, frontImageUrl = "https://images.unsplash.com/photo-1560769629-975ec94e6a86?auto=format&fit=crop&w=600&q=80", storeSource = "Source: Multan Crafts - Rs. 2,200"),
        FashionItem(id = "m_shoes_6", category = FashionCategory.SHOES, pehnoCategory = PehnoCategory.SHOES, name = "Dark Chocolate Formal Loafer", urduName = "ڈارک چاکلیٹ لوفڑ", gender = "Male", heightBoostInches = 1.5f, description = "Slip-on penny loafer with cushioned memory foam.", heightTip = "+1.5 inch stealth comfort lift.", priceRs = 4800, frontImageUrl = "https://images.unsplash.com/photo-1614252235316-8c857d38b5f4?auto=format&fit=crop&w=600&q=80", storeSource = "Source: Service Shoes - Rs. 4,800"),
        FashionItem(id = "m_shoes_7", category = FashionCategory.SHOES, pehnoCategory = PehnoCategory.SHOES, name = "White Formal Moccasins", urduName = "سفید موکاسینز", gender = "Male", heightBoostInches = 1.4f, description = "Summer breathable leather with flexible driving sole.", heightTip = "+1.4 inch subtle lift for light linen outfits.", priceRs = 3200, frontImageUrl = "https://images.unsplash.com/photo-1549298916-b41d501d3772?auto=format&fit=crop&w=600&q=80", storeSource = "Source: Bagallery - Rs. 3,200"),
        FashionItem(id = "m_shoes_8", category = FashionCategory.SHOES, pehnoCategory = PehnoCategory.SHOES, name = "Balochi Chawat Chappal", urduName = "بلوچی چاوت چپل", gender = "Male", heightBoostInches = 1.8f, description = "Broad toe traditional Balochi leather sandal.", heightTip = "+1.8 inch thick leather foundation.", priceRs = 2900, frontImageUrl = "https://images.unsplash.com/photo-1560769629-975ec94e6a86?auto=format&fit=crop&w=600&q=80", storeSource = "Source: Quetta Chawat - Rs. 2,900"),
        FashionItem(id = "m_shoes_9", category = FashionCategory.SHOES, pehnoCategory = PehnoCategory.SHOES, name = "Maroon Velvet Groom Khussa", urduName = "میرون ویلوٹ دلہا کھسہ", gender = "Male", heightBoostInches = 2.0f, description = "Curled toe Saleem Shahi khussa with heavy dabka work.", heightTip = "+2.0 inch cushioned wedding insole lift.", priceRs = 4200, frontImageUrl = "https://images.unsplash.com/photo-1549298916-b41d501d3772?auto=format&fit=crop&w=600&q=80", storeSource = "Source: Lahore Shahi Khussa - Rs. 4,200"),
        FashionItem(id = "m_shoes_10", category = FashionCategory.SHOES, pehnoCategory = PehnoCategory.SHOES, name = "Black Suede Formal Slip-on", urduName = "سیاہ سابر سلپ آن", gender = "Male", heightBoostInches = 1.6f, description = "Plush black suede with subtle brass horsebit clasp.", heightTip = "+1.6 inch polished evening stance.", priceRs = 5400, frontImageUrl = "https://images.unsplash.com/photo-1614252235316-8c857d38b5f4?auto=format&fit=crop&w=600&q=80", storeSource = "Source: Borjan PK - Rs. 5,400"),

        // =========================================================================
        // FEMALE DRESSES (10 Real Options with Multi-Store Sources)
        // =========================================================================
        FashionItem(
            id = "f_dress_1",
            category = FashionCategory.DRESS_TRY_ON,
            pehnoCategory = PehnoCategory.DRESS,
            name = "Emerald Green Embroidered Kurti",
            urduName = "زمردی کڑھائی والی کُرتی",
            gender = "Female",
            subType = "Daily",
            description = "Pure lawn kurti with detailed tilla neck embroidery and cutwork sleeves.",
            heightTip = "Vertical embroidered center panel elongates petite female frames.",
            priceRs = 4800,
            darazLink = "https://www.daraz.pk/catalog/?q=emerald+green+kurti&aff=pehno10",
            frontImageUrl = "https://images.unsplash.com/photo-1583391733956-3750e0ff4e8b?auto=format&fit=crop&w=800&q=80",
            backImageUrl = "https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=800&q=80",
            storeSource = "Source: Daraz.pk - Rs. 4,800",
            tag = "Bestseller",
            suitabilityScore = 97,
            drawableResId = R.drawable.model_emerald_kurti_female
        ),
        FashionItem(
            id = "f_dress_2",
            category = FashionCategory.DRESS_TRY_ON,
            pehnoCategory = PehnoCategory.DRESS,
            name = "Ruby Red Bridal Lehenga Choli",
            urduName = "سرخ دلہن لہنگا چولی",
            gender = "Female",
            subType = "Wedding",
            description = "Heavily encrusted velvet lehenga with handcrafted dabka, zari, and pearl work.",
            heightTip = "High-waisted flared skirt creates iconic hourglass proportion and tall grace.",
            priceRs = 65000,
            darazLink = "https://www.mariab.pk/bridal?aff=pehno10",
            frontImageUrl = "https://images.unsplash.com/photo-1595777457583-95e059d581b8?auto=format&fit=crop&w=800&q=80",
            backImageUrl = "https://images.unsplash.com/photo-1595777457583-95e059d581b8?auto=format&fit=crop&w=800&q=80",
            storeSource = "Source: Maria.B Bridal - Rs. 65,000",
            tag = "Bridal Couture",
            suitabilityScore = 99,
            drawableResId = R.drawable.model_bridal_lehenga_female
        ),
        FashionItem(
            id = "f_dress_3",
            category = FashionCategory.DRESS_TRY_ON,
            pehnoCategory = PehnoCategory.DRESS,
            name = "Classic Black Velvet 3-Piece Suit",
            urduName = "سیاہ مخمل تھری پیس سوٹ",
            gender = "Female",
            subType = "Wedding",
            description = "Rich velvet shirt with gold scalloped daman and organza dupatta.",
            heightTip = "Deep black velvet with straight pants slims waist and highlights jewelry.",
            priceRs = 12500,
            darazLink = "https://khaadi.com/women?aff=pehno10",
            frontImageUrl = "https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=800&q=80",
            backImageUrl = "https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=800&q=80",
            storeSource = "Source: Khaadi Luxury - Rs. 12,500",
            tag = "Winter Glam",
            suitabilityScore = 96,
            drawableResId = R.drawable.model_emerald_kurti_female
        ),
        FashionItem(
            id = "f_dress_4",
            category = FashionCategory.DRESS_TRY_ON,
            pehnoCategory = PehnoCategory.DRESS,
            name = "Pastel Pink Organza Anarkali Frock",
            urduName = "پیسٹل گلابی انارکلی فراک",
            gender = "Female",
            subType = "Wedding",
            description = "Floor length sheer organza with silver sequins and matching churidar.",
            heightTip = "Floor length flare creates majestic tall movement and elegance.",
            priceRs = 14500,
            darazLink = "https://sanasafinaz.com?aff=pehno10",
            frontImageUrl = "https://images.unsplash.com/photo-1583391733956-3750e0ff4e8b?auto=format&fit=crop&w=800&q=80",
            backImageUrl = "https://images.unsplash.com/photo-1583391733956-3750e0ff4e8b?auto=format&fit=crop&w=800&q=80",
            storeSource = "Source: Sana Safinaz - Rs. 14,500",
            tag = "Walima Royalty",
            suitabilityScore = 94,
            drawableResId = R.drawable.model_bridal_lehenga_female
        ),
        FashionItem(
            id = "f_dress_5",
            category = FashionCategory.DRESS_TRY_ON,
            pehnoCategory = PehnoCategory.DRESS,
            name = "Mustard Yellow Mayun Gharara Set",
            urduName = "سرسوں مایوں غرارہ سوٹ",
            gender = "Female",
            subType = "Wedding",
            description = "Two-tiered flared gharara with gotta kinari border and green piping.",
            heightTip = "Fitted knee band followed by full flare elongates leg line.",
            priceRs = 9800,
            darazLink = "https://instagram.com/bridal_quetta?aff=pehno10",
            frontImageUrl = "https://images.unsplash.com/photo-1595777457583-95e059d581b8?auto=format&fit=crop&w=800&q=80",
            backImageUrl = "https://images.unsplash.com/photo-1595777457583-95e059d581b8?auto=format&fit=crop&w=800&q=80",
            storeSource = "Source: @bridal_quetta Instagram - Rs. 9,800",
            tag = "Mayun Special",
            suitabilityScore = 93,
            drawableResId = R.drawable.model_emerald_kurti_female
        ),
        FashionItem(
            id = "f_dress_6",
            category = FashionCategory.DRESS_TRY_ON,
            pehnoCategory = PehnoCategory.DRESS,
            name = "White Chiffon Chikankari Suit",
            urduName = "سفید شفون چکن کاری",
            gender = "Female",
            subType = "Daily",
            description = "Handcrafted Lakhnavi threadwork with schiffli lace borders.",
            heightTip = "Serene white gives pure angelic freshness.",
            priceRs = 6800,
            darazLink = "https://bagallery.com/women?aff=pehno10",
            frontImageUrl = "https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=800&q=80",
            backImageUrl = "https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=800&q=80",
            storeSource = "Source: Bagallery Official - Rs. 6,800",
            tag = "Eid Festive",
            suitabilityScore = 92,
            drawableResId = R.drawable.model_emerald_kurti_female
        ),
        FashionItem(
            id = "f_dress_7",
            category = FashionCategory.DRESS_TRY_ON,
            pehnoCategory = PehnoCategory.DRESS,
            name = "Royal Blue Pure Silk Saree",
            urduName = "شاہی نیلی ریشمی ساڑھی",
            gender = "Female",
            subType = "Wedding",
            description = "Banarasi silk pallu with woven antique gold floral motifs.",
            heightTip = "Vertical saree pleats add 2 to 3 inches visual height instantly.",
            priceRs = 16000,
            darazLink = "https://sana-collection.pk?aff=pehno10",
            frontImageUrl = "https://images.unsplash.com/photo-1583391733956-3750e0ff4e8b?auto=format&fit=crop&w=800&q=80",
            backImageUrl = "https://images.unsplash.com/photo-1583391733956-3750e0ff4e8b?auto=format&fit=crop&w=800&q=80",
            storeSource = "Source: Sana Collection Lahore - Rs. 16,000",
            tag = "Saree Diva",
            suitabilityScore = 98,
            drawableResId = R.drawable.model_bridal_lehenga_female
        ),
        FashionItem(
            id = "f_dress_8",
            category = FashionCategory.DRESS_TRY_ON,
            pehnoCategory = PehnoCategory.DRESS,
            name = "Maroon Formal Sharara with Peplum",
            urduName = "میرون فارمل شرارہ اور پیپلم",
            gender = "Female",
            subType = "Wedding",
            description = "Fitted embroidered peplum top paired with flared layered sharara.",
            heightTip = "Peplum accentuates the waistline beautifully.",
            priceRs = 11200,
            darazLink = "https://gulahmedshop.com?aff=pehno10",
            frontImageUrl = "https://images.unsplash.com/photo-1595777457583-95e059d581b8?auto=format&fit=crop&w=800&q=80",
            backImageUrl = "https://images.unsplash.com/photo-1595777457583-95e059d581b8?auto=format&fit=crop&w=800&q=80",
            storeSource = "Source: Gul Ahmed Festive - Rs. 11,200",
            tag = "Nikkah Look",
            suitabilityScore = 95,
            drawableResId = R.drawable.model_bridal_lehenga_female
        ),
        FashionItem(
            id = "f_dress_9",
            category = FashionCategory.DRESS_TRY_ON,
            pehnoCategory = PehnoCategory.DRESS,
            name = "Plum Raw Silk Straight Kurta",
            urduName = "جامنی را سلک اسٹریٹ کُرتا",
            gender = "Female",
            subType = "Daily",
            description = "Minimalist raw silk with loop button placket and cigarette trousers.",
            heightTip = "Monochrome straight cut creates long clean vertical lines.",
            priceRs = 5600,
            darazLink = "https://generation.com.pk?aff=pehno10",
            frontImageUrl = "https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=800&q=80",
            backImageUrl = "https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=800&q=80",
            storeSource = "Source: Generation PK - Rs. 5,600",
            tag = "Executive Chic",
            suitabilityScore = 91,
            drawableResId = R.drawable.model_emerald_kurti_female
        ),
        FashionItem(
            id = "f_dress_10",
            category = FashionCategory.DRESS_TRY_ON,
            pehnoCategory = PehnoCategory.DRESS,
            name = "Peach Embroidered Net Maxi Dress",
            urduName = "پیچ کڑھائی نیٹ میکسی ڈریس",
            gender = "Female",
            subType = "Wedding",
            description = "Floor sweeping flowy net with pearl sprays and satin underlay.",
            heightTip = "Flared bottom sweep adds height and elegance in walking motion.",
            priceRs = 13800,
            darazLink = "https://anarkali.pk?aff=pehno10",
            frontImageUrl = "https://images.unsplash.com/photo-1583391733956-3750e0ff4e8b?auto=format&fit=crop&w=800&q=80",
            backImageUrl = "https://images.unsplash.com/photo-1583391733956-3750e0ff4e8b?auto=format&fit=crop&w=800&q=80",
            storeSource = "Source: Anarkali Bazaar Lahore - Rs. 13,800",
            tag = "Party Perfection",
            suitabilityScore = 96,
            drawableResId = R.drawable.model_bridal_lehenga_female
        ),

        // =========================================================================
        // FEMALE GALA DESIGN (10 Real Options)
        // =========================================================================
        FashionItem(id = "f_gala_1", pehnoCategory = PehnoCategory.GALA_DESIGN, name = "Angrakha Crossover Gala", urduName = "انگرکھا گلا ڈیزائن", gender = "Female", description = "Diagonal wrap neckline with handcrafted fabric potli buttons.", heightTip = "Draws eye along an angle making torso look slim.", priceRs = 550, frontImageUrl = "https://images.unsplash.com/photo-1583391733956-3750e0ff4e8b?auto=format&fit=crop&w=600&q=80", storeSource = "Maria.B Cut"),
        FashionItem(id = "f_gala_2", pehnoCategory = PehnoCategory.GALA_DESIGN, name = "Keyhole Button Embroidered Neck", urduName = "کی ہول بٹن گلا", gender = "Female", description = "Teardrop opening with antique pearl drop button.", heightTip = "Delicate framing of collarbones.", priceRs = 450, frontImageUrl = "https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=600&q=80", storeSource = "Khaadi Artisans"),
        FashionItem(id = "f_gala_3", pehnoCategory = PehnoCategory.GALA_DESIGN, name = "Sweetheart Zardozi Neckline", urduName = "سوئٹ ہارٹ زردوزی گلا", gender = "Female", description = "Royal curved sweetheart neckline embellished with gold wires.", heightTip = "Classic bridal elegance.", priceRs = 850, frontImageUrl = "https://images.unsplash.com/photo-1595777457583-95e059d581b8?auto=format&fit=crop&w=600&q=80", storeSource = "Sana Collection"),
        FashionItem(id = "f_gala_4", pehnoCategory = PehnoCategory.GALA_DESIGN, name = "Boat Neck with Gotta Patti", urduName = "بوٹ نیک گوٹہ پٹی", gender = "Female", description = "Wide horizontal curve highlighting neck and shoulders.", heightTip = "Balances broader hips with shoulder width.", priceRs = 500, frontImageUrl = "https://images.unsplash.com/photo-1583391733956-3750e0ff4e8b?auto=format&fit=crop&w=600&q=80", storeSource = "Gul Ahmed"),
        FashionItem(id = "f_gala_5", pehnoCategory = PehnoCategory.GALA_DESIGN, name = "Ban Collar with Dori Tassels", urduName = "بین کالر اور ڈوری ٹیسلز", gender = "Female", description = "Stand collar opening into a subtle V-slit with hanging doris.", heightTip = "Lengthens neck profile.", priceRs = 600, frontImageUrl = "https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=600&q=80", storeSource = "J. Women"),
        FashionItem(id = "f_gala_6", pehnoCategory = PehnoCategory.GALA_DESIGN, name = "Scalloped Embroidered Border Neck", urduName = "کنگری کڑھائی گلا", gender = "Female", description = "Laser-cut scallop border with fine self-threadwork.", heightTip = "Feminine curved softness.", priceRs = 650, frontImageUrl = "https://images.unsplash.com/photo-1583391733956-3750e0ff4e8b?auto=format&fit=crop&w=600&q=80", storeSource = "Generation"),
        FashionItem(id = "f_gala_7", pehnoCategory = PehnoCategory.GALA_DESIGN, name = "V-Placket Mirror Work Neck", urduName = "وی پلیکیٹ شیشہ گلا", gender = "Female", description = "Traditional reflective mirror glass sewn into colorful stitches.", heightTip = "Vertical V accentuates height.", priceRs = 750, frontImageUrl = "https://images.unsplash.com/photo-1595777457583-95e059d581b8?auto=format&fit=crop&w=600&q=80", storeSource = "Quetta Mirror"),
        FashionItem(id = "f_gala_8", pehnoCategory = PehnoCategory.GALA_DESIGN, name = "Round Dori Tie Neckline", urduName = "گول ڈوری ناٹ گلا", gender = "Female", description = "Classic deep round neck with braided silk ties.", heightTip = "Easy traditional comfort.", priceRs = 400, frontImageUrl = "https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=600&q=80", storeSource = "Daraz"),
        FashionItem(id = "f_gala_9", pehnoCategory = PehnoCategory.GALA_DESIGN, name = "Organza Illusion Cutout Neck", urduName = "آرگنزا کٹ آؤٹ گلا", gender = "Female", description = "Sheer organza insert creating modern luxury contrast.", heightTip = "Contemporary runway styling.", priceRs = 800, frontImageUrl = "https://images.unsplash.com/photo-1583391733956-3750e0ff4e8b?auto=format&fit=crop&w=600&q=80", storeSource = "Sana Safinaz"),
        FashionItem(id = "f_gala_10", pehnoCategory = PehnoCategory.GALA_DESIGN, name = "Lace Applique High Neck", urduName = "لیس ہائی نیک ڈیزائن", gender = "Female", description = "Victorian lace trim layered onto Eastern kameez placket.", heightTip = "High collar elongates posture.", priceRs = 700, frontImageUrl = "https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=600&q=80", storeSource = "Alkaram"),

        // =========================================================================
        // FEMALE DAMAN DESIGN (10 Real Options)
        // =========================================================================
        FashionItem(id = "f_daman_1", pehnoCategory = PehnoCategory.DAMAN_DESIGN, name = "Cutwork Lace Embroidered Daman", urduName = "کٹ ورک لیس دامن", gender = "Female", description = "Intricate floral cutouts along shirt hemline.", heightTip = "Sheer hemline prevents visual heaviness at knees.", priceRs = 600, frontImageUrl = "https://images.unsplash.com/photo-1583391733956-3750e0ff4e8b?auto=format&fit=crop&w=600&q=80", storeSource = "Maria.B Cutwork"),
        FashionItem(id = "f_daman_2", pehnoCategory = PehnoCategory.DAMAN_DESIGN, name = "Pearl & Tassel Hanging Daman", urduName = "موتی اور ٹیسلز دامن", gender = "Female", description = "Hanging crystal beads and silky tassels dancing in movement.", heightTip = "Creates rhythmic movement when walking.", priceRs = 750, frontImageUrl = "https://images.unsplash.com/photo-1595777457583-95e059d581b8?auto=format&fit=crop&w=600&q=80", storeSource = "Khaadi Lace"),
        FashionItem(id = "f_daman_3", pehnoCategory = PehnoCategory.DAMAN_DESIGN, name = "Scallop Embroidered Gheera", urduName = "کنگری دامن بارڈر", gender = "Female", description = "Curved scallop hem with thick gold tilla framing.", heightTip = "Classic royal finish.", priceRs = 500, frontImageUrl = "https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=600&q=80", storeSource = "Sana Safinaz"),
        FashionItem(id = "f_daman_4", pehnoCategory = PehnoCategory.DAMAN_DESIGN, name = "Organza Tissue Border Patch", urduName = "آرگنزا ٹشو دامن بارڈر", gender = "Female", description = "Translucent tissue hem with pleated pintucks.", heightTip = "Adds modern airy lightness to silhouette.", priceRs = 650, frontImageUrl = "https://images.unsplash.com/photo-1583391733956-3750e0ff4e8b?auto=format&fit=crop&w=600&q=80", storeSource = "Generation"),
        FashionItem(id = "f_daman_5", pehnoCategory = PehnoCategory.DAMAN_DESIGN, name = "Heavy Zari Bridal Daman", urduName = "بھاری زری دلہن دامن", gender = "Female", description = "4-inch wide gold metallic embroidery band for festive formals.", heightTip = "Grounds formal shirts with richness.", priceRs = 1200, frontImageUrl = "https://images.unsplash.com/photo-1595777457583-95e059d581b8?auto=format&fit=crop&w=600&q=80", storeSource = "Bridal Quetta"),
        FashionItem(id = "f_daman_6", pehnoCategory = PehnoCategory.DAMAN_DESIGN, name = "Zigzag Gota Patti Border", urduName = "گوٹہ پٹی زگ زیگ دامن", gender = "Female", description = "Reflective golden gota laid out in geometric chevrons.", heightTip = "Festive Mehndi vibrance.", priceRs = 450, frontImageUrl = "https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=600&q=80", storeSource = "Anarkali Gota"),
        FashionItem(id = "f_daman_7", pehnoCategory = PehnoCategory.DAMAN_DESIGN, name = "Pleated Silk Frill Daman", urduName = "سلک فرل دامن", gender = "Female", description = "Accordion micro pleats along front and back hem.", heightTip = "Feminine bounce in motion.", priceRs = 550, frontImageUrl = "https://images.unsplash.com/photo-1583391733956-3750e0ff4e8b?auto=format&fit=crop&w=600&q=80", storeSource = "Gul Ahmed"),
        FashionItem(id = "f_daman_8", pehnoCategory = PehnoCategory.DAMAN_DESIGN, name = "Triangle Samosa Lace Trim", urduName = "سموسہ لیس دامن", gender = "Female", description = "Traditional folded fabric triangles framing edges.", heightTip = "Timeless Pakistani tailor detail.", priceRs = 350, frontImageUrl = "https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=600&q=80", storeSource = "Daraz"),
        FashionItem(id = "f_daman_9", pehnoCategory = PehnoCategory.DAMAN_DESIGN, name = "Velvet Applique Border Daman", urduName = "مخمل پیچ دaman", gender = "Female", description = "Contrast colored velvet patch with ari work.", heightTip = "Rich winter weight drape.", priceRs = 800, frontImageUrl = "https://images.unsplash.com/photo-1595777457583-95e059d581b8?auto=format&fit=crop&w=600&q=80", storeSource = "Lahore Velvet"),
        FashionItem(id = "f_daman_10", pehnoCategory = PehnoCategory.DAMAN_DESIGN, name = "Crystal Droplet Fringe Daman", urduName = "کرسٹل جھالر دامن", gender = "Female", description = "Shimmering glass crystals catching evening lights.", heightTip = "Walima reception glamour.", priceRs = 950, frontImageUrl = "https://images.unsplash.com/photo-1583391733956-3750e0ff4e8b?auto=format&fit=crop&w=600&q=80", storeSource = "Couture Gems"),

        // =========================================================================
        // FEMALE MEHNDI (10 Real Options)
        // =========================================================================
        FashionItem(id = "f_mehndi_1", category = FashionCategory.MEHNDI, pehnoCategory = PehnoCategory.MEHNDI, name = "Kashee's Bridal Peacock Mehndi", urduName = "کاشیز برائیڈل مور مہندی", gender = "Female", description = "Elaborate dancing peacock motifs extending from palms to elbow.", heightTip = "Full bridal royal coverage.", priceRs = 3500, frontImageUrl = "https://images.unsplash.com/photo-1560066984-138dadb4c035?auto=format&fit=crop&w=600&q=80", storeSource = "Kashee's Salon Karachi"),
        FashionItem(id = "f_mehndi_2", category = FashionCategory.MEHNDI, pehnoCategory = PehnoCategory.MEHNDI, name = "Arabic Floral Shaded Mehndi", urduName = "عربی پھول دار شیڈڈ مہندی", gender = "Female", description = "Bold flowing vine trails with shaded rose petals.", heightTip = "Stunning contrast on hands.", priceRs = 1800, frontImageUrl = "https://images.unsplash.com/photo-1560066984-138dadb4c035?auto=format&fit=crop&w=600&q=80", storeSource = "Arabian Henna"),
        FashionItem(id = "f_mehndi_3", category = FashionCategory.MEHNDI, pehnoCategory = PehnoCategory.MEHNDI, name = "Minimalist Delicate Finger Bel", urduName = "باریک فنگر بیل مہندی", gender = "Female", description = "Geometric single finger trail with wrist cuff band.", heightTip = "Modern aesthetic chic.", priceRs = 1000, frontImageUrl = "https://images.unsplash.com/photo-1560066984-138dadb4c035?auto=format&fit=crop&w=600&q=80", storeSource = "Henna Studio"),
        FashionItem(id = "f_mehndi_4", category = FashionCategory.MEHNDI, pehnoCategory = PehnoCategory.MEHNDI, name = "Traditional Mandala Palm Mehndi", urduName = "روایتی گول ٹکی منڈالا", gender = "Female", description = "Intricate circular sacred mandala center with fingertip caps.", heightTip = "Classic Pakistani Eid favourite.", priceRs = 1200, frontImageUrl = "https://images.unsplash.com/photo-1560066984-138dadb4c035?auto=format&fit=crop&w=600&q=80", storeSource = "Eid Henna Artists"),
        FashionItem(id = "f_mehndi_5", category = FashionCategory.MEHNDI, pehnoCategory = PehnoCategory.MEHNDI, name = "Full Arm Jaal Net Mehndi", urduName = "جال نیٹ مہندی بازو", gender = "Female", description = "Delicate criss-cross grid with tiny floral buds in each square.", heightTip = "Dense regal wedding texture.", priceRs = 4000, frontImageUrl = "https://images.unsplash.com/photo-1560066984-138dadb4c035?auto=format&fit=crop&w=600&q=80", storeSource = "Royal Bridal Mehndi"),
        FashionItem(id = "f_mehndi_6", category = FashionCategory.MEHNDI, pehnoCategory = PehnoCategory.MEHNDI, name = "Backhand Floral Vine Trail", urduName = "پچھلے ہاتھ کی بیل مہندی", gender = "Female", description = "Graceful diagonal sweep from index finger to wrist bone.", heightTip = "Accentuates delicate ring fingers.", priceRs = 1400, frontImageUrl = "https://images.unsplash.com/photo-1560066984-138dadb4c035?auto=format&fit=crop&w=600&q=80", storeSource = "Lahore Henna"),
        FashionItem(id = "f_mehndi_7", category = FashionCategory.MEHNDI, pehnoCategory = PehnoCategory.MEHNDI, name = "Hathphool Jewelry Style Mehndi", urduName = "ہتھ پھول جیولری مہندی", gender = "Female", description = "Chain-link floral pattern imitating gold jewelry ornament.", heightTip = "Doubles as visual jewelry.", priceRs = 1600, frontImageUrl = "https://images.unsplash.com/photo-1560066984-138dadb4c035?auto=format&fit=crop&w=600&q=80", storeSource = "Islamabad Henna"),
        FashionItem(id = "f_mehndi_8", category = FashionCategory.MEHNDI, pehnoCategory = PehnoCategory.MEHNDI, name = "Gulf Style Bold Negative Space", urduName = "گلف بولڈ نیگیٹو سپیس", gender = "Female", description = "Deep dark filled contours with bright skin contrast.", heightTip = "High-fashion photographic contrast.", priceRs = 2200, frontImageUrl = "https://images.unsplash.com/photo-1560066984-138dadb4c035?auto=format&fit=crop&w=600&q=80", storeSource = "Dubai Mehndi"),
        FashionItem(id = "f_mehndi_9", category = FashionCategory.MEHNDI, pehnoCategory = PehnoCategory.MEHNDI, name = "Feet Payal Bridal Henna", urduName = "پاؤں پائل برائیڈل مہندی", gender = "Female", description = "Anklet style motif along foot curve and toe tips.", heightTip = "Completes full wedding bridal look.", priceRs = 2500, frontImageUrl = "https://images.unsplash.com/photo-1560066984-138dadb4c035?auto=format&fit=crop&w=600&q=80", storeSource = "Pehno Artists"),
        FashionItem(id = "f_mehndi_10", category = FashionCategory.MEHNDI, pehnoCategory = PehnoCategory.MEHNDI, name = "Geometric Modern Moroccan Henna", urduName = "مراکشی جیومیٹرک مہندی", gender = "Female", description = "Sharp linear chevrons and tribal diamond dots.", heightTip = "Edgy contemporary festive look.", priceRs = 1800, frontImageUrl = "https://images.unsplash.com/photo-1560066984-138dadb4c035?auto=format&fit=crop&w=600&q=80", storeSource = "Moroccan Vibe"),

        // =========================================================================
        // FEMALE HAIRSTYLE (10 Real Options)
        // =========================================================================
        FashionItem(id = "f_hair_1", category = FashionCategory.HAIR_BEARD, pehnoCategory = PehnoCategory.HAIRSTYLE, name = "Traditional Low Bun with Fresh Gajra", urduName = "روایتی لو جوڑا اور تازہ گجرا", gender = "Female", description = "Polished neat bridal chignon ringed with aromatic white jasmine motia buds.", heightTip = "Shows off neckline and heavy earrings.", priceRs = 1500, frontImageUrl = "https://images.unsplash.com/photo-1560066984-138dadb4c035?auto=format&fit=crop&w=600&q=80", storeSource = "Depilex Karachi"),
        FashionItem(id = "f_hair_2", category = FashionCategory.HAIR_BEARD, pehnoCategory = PehnoCategory.HAIRSTYLE, name = "Soft Voluminous Bridal Curls", urduName = "نرم برائیڈل کھلے گھنگھرالے بال", gender = "Female", description = "Cascading glossy barrel curls framed over shoulders.", heightTip = "Adds glamorous volume and softness.", priceRs = 1800, frontImageUrl = "https://images.unsplash.com/photo-1595777457583-95e059d581b8?auto=format&fit=crop&w=600&q=80", storeSource = "Nabila's Salon"),
        FashionItem(id = "f_hair_3", category = FashionCategory.HAIR_BEARD, pehnoCategory = PehnoCategory.HAIRSTYLE, name = "Half-Up Twisted Crown Braid", urduName = "ہاف اپ ٹوئسٹڈ کراؤن چوٹی", gender = "Female", description = "Boho romantic side twists pinned with tiny baby's breath flowers.", heightTip = "Lifts crown height by 1 inch.", priceRs = 1400, frontImageUrl = "https://images.unsplash.com/photo-1583391733956-3750e0ff4e8b?auto=format&fit=crop&w=600&q=80", storeSource = "Kashee's"),
        FashionItem(id = "f_hair_4", category = FashionCategory.HAIR_BEARD, pehnoCategory = PehnoCategory.HAIRSTYLE, name = "Sleek High Ponytail", urduName = "اونچی پونی ٹیل", gender = "Female", description = "Ultra smooth lifted ponytail with hair-wrapped base.", heightTip = "Lifts facial features and elongates neck.", priceRs = 1000, frontImageUrl = "https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=600&q=80", storeSource = "Tony & Guy"),
        FashionItem(id = "f_hair_5", category = FashionCategory.HAIR_BEARD, pehnoCategory = PehnoCategory.HAIRSTYLE, name = "Side-Swept Hollywood Waves", urduName = "سائیڈ سوئپٹ گلیمرس ویوز", gender = "Female", description = "Deep side partition with structured glossy red carpet waves.", heightTip = "Fabulous vintage Walima glamour.", priceRs = 2000, frontImageUrl = "https://images.unsplash.com/photo-1595777457583-95e059d581b8?auto=format&fit=crop&w=600&q=80", storeSource = "Sabs Salon"),
        FashionItem(id = "f_hair_6", category = FashionCategory.HAIR_BEARD, pehnoCategory = PehnoCategory.HAIRSTYLE, name = "Fishtail Floral Braid", urduName = "فش ٹیل پھولدار چوٹی", gender = "Female", description = "Thick textured fishtail braid decorated with real rose petals.", heightTip = "Perfect traditional Mehndi back detail.", priceRs = 1600, frontImageUrl = "https://images.unsplash.com/photo-1583391733956-3750e0ff4e8b?auto=format&fit=crop&w=600&q=80", storeSource = "Lahore Stylists"),
        FashionItem(id = "f_hair_7", category = FashionCategory.HAIR_BEARD, pehnoCategory = PehnoCategory.HAIRSTYLE, name = "Center-Parted Royal Braid with Teeka", urduName = "مانگ ٹیکا سینٹر پارٹڈ چوٹی", gender = "Female", description = "Classic center part tailored to anchor heavy bridal matha patti.", heightTip = "Symmetrical royal face framing.", priceRs = 1700, frontImageUrl = "https://images.unsplash.com/photo-1560066984-138dadb4c035?auto=format&fit=crop&w=600&q=80", storeSource = "Bridal Lounge"),
        FashionItem(id = "f_hair_8", category = FashionCategory.HAIR_BEARD, pehnoCategory = PehnoCategory.HAIRSTYLE, name = "Textured Messy Updo", urduName = "میسی اپ ڈو جوڑا", gender = "Female", description = "Relaxed curled updo with face-framing tendrils.", heightTip = "Romantic contemporary reception look.", priceRs = 1500, frontImageUrl = "https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=600&q=80", storeSource = "Islamabad Hair"),
        FashionItem(id = "f_hair_9", category = FashionCategory.HAIR_BEARD, pehnoCategory = PehnoCategory.HAIRSTYLE, name = "Silky Straight Blowout", urduName = "سلکی اسٹریٹ بلو آؤٹ", gender = "Female", description = "Mirror shine straight hair with curved interior ends.", heightTip = "Clean minimalist elegance.", priceRs = 900, frontImageUrl = "https://images.unsplash.com/photo-1583391733956-3750e0ff4e8b?auto=format&fit=crop&w=600&q=80", storeSource = "Daily Glam"),
        FashionItem(id = "f_hair_10", category = FashionCategory.HAIR_BEARD, pehnoCategory = PehnoCategory.HAIRSTYLE, name = "Retro Victory Rolls Festive Hair", urduName = "ریٹرو ہیر اسٹائل", gender = "Female", description = "Vintage sculpted rolls on crown with cascading curls.", heightTip = "Dramatic distinctive height.", priceRs = 1800, frontImageUrl = "https://images.unsplash.com/photo-1595777457583-95e059d581b8?auto=format&fit=crop&w=600&q=80", storeSource = "Vintage Parlour"),

        // =========================================================================
        // FEMALE JEWELLERY (10 Real Options)
        // =========================================================================
        FashionItem(id = "f_jewel_1", category = FashionCategory.JEWELLERY, pehnoCategory = PehnoCategory.JEWELLERY, name = "24K Gold Plated Kundan Choker Set", urduName = "کندن چوکر اور جھمکے سیٹ", gender = "Female", description = "Hand-set kundan stones with pearl drops and matching earrings.", heightTip = "Focal point accentuates neck gracefully.", priceRs = 5800, frontImageUrl = "https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?auto=format&fit=crop&w=600&q=80", storeSource = "Source: Daraz.pk - Rs. 5,800"),
        FashionItem(id = "f_jewel_2", category = FashionCategory.JEWELLERY, pehnoCategory = PehnoCategory.JEWELLERY, name = "Emerald Green Meenakari Jhumkas", urduName = "زمردی میناکاری جھمکے", gender = "Female", description = "Traditional domed jhumki earrings with hanging seed pearls.", heightTip = "Draws attention upwards to cheekbones.", priceRs = 2800, frontImageUrl = "https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?auto=format&fit=crop&w=600&q=80", storeSource = "Source: Sana Collection - Rs. 2,800"),
        FashionItem(id = "f_jewel_3", category = FashionCategory.JEWELLERY, pehnoCategory = PehnoCategory.JEWELLERY, name = "Bridal Matha Patti & Teeka", urduName = "ماتھا پٹی اور جھومر ٹیکہ", gender = "Female", description = "Delicate gold chain headpiece framing the hairline.", heightTip = "Regal bridal symmetry.", priceRs = 4200, frontImageUrl = "https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?auto=format&fit=crop&w=600&q=80", storeSource = "Source: @bridal_quetta - Rs. 4,200"),
        FashionItem(id = "f_jewel_4", category = FashionCategory.JEWELLERY, pehnoCategory = PehnoCategory.JEWELLERY, name = "Polki Diamond Rani Haar", urduName = "پولکی رانی ہار طویل ہار", gender = "Female", description = "Multi-strand long necklace cascading down to chest.", heightTip = "Vertical long haar lengthens the torso.", priceRs = 8500, frontImageUrl = "https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?auto=format&fit=crop&w=600&q=80", storeSource = "Source: Khaadi Gems - Rs. 8,500"),
        FashionItem(id = "f_jewel_5", category = FashionCategory.JEWELLERY, pehnoCategory = PehnoCategory.JEWELLERY, name = "Traditional Pearl Guluband", urduName = "موتیوں کا گلوبند", gender = "Female", description = "Snug choker necklace with 5 rows of real freshwater pearls.", heightTip = "Dignified high-society look.", priceRs = 6400, frontImageUrl = "https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?auto=format&fit=crop&w=600&q=80", storeSource = "Source: Bagallery - Rs. 6,400"),
        FashionItem(id = "f_jewel_6", category = FashionCategory.JEWELLERY, pehnoCategory = PehnoCategory.JEWELLERY, name = "Traditional Bridal Nath with Chain", urduName = "دلہن والی نتھ اور سنہری زنجیر", gender = "Female", description = "Gold hoop nose ring with pearl chain securing to hair.", heightTip = "Iconic Pakistani bridal ornament.", priceRs = 3200, frontImageUrl = "https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?auto=format&fit=crop&w=600&q=80", storeSource = "Source: Anarkali Jewelers - Rs. 3,200"),
        FashionItem(id = "f_jewel_7", category = FashionCategory.JEWELLERY, pehnoCategory = PehnoCategory.JEWELLERY, name = "Velvet Red Glass Chooriyan & Kangan", urduName = "سرخ شیشے کی چوڑیاں اور کنگن", gender = "Female", description = "Set of 48 musical glass bangles with antique gold kangans.", heightTip = "Traditional wrist melody.", priceRs = 1800, frontImageUrl = "https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?auto=format&fit=crop&w=600&q=80", storeSource = "Source: Hyderabad Bangles - Rs. 1,800"),
        FashionItem(id = "f_jewel_8", category = FashionCategory.JEWELLERY, pehnoCategory = PehnoCategory.JEWELLERY, name = "Ruby Red Choker & Earring Combo", urduName = "یاقوت چوکر سیٹ", gender = "Female", description = "Sparkling synthetic ruby stones set in micro-plated silver.", heightTip = "Bold color contrast against white or black dresses.", priceRs = 5200, frontImageUrl = "https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?auto=format&fit=crop&w=600&q=80", storeSource = "Source: Maria.B Jewels - Rs. 5,200"),
        FashionItem(id = "f_jewel_9", category = FashionCategory.JEWELLERY, pehnoCategory = PehnoCategory.JEWELLERY, name = "Temple Gold Antique Chandbali", urduName = "چاند بالی کان کی بالیاں", gender = "Female", description = "Crescent moon shaped earrings with dangling ghungroos.", heightTip = "Feminine movement and charm.", priceRs = 2600, frontImageUrl = "https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?auto=format&fit=crop&w=600&q=80", storeSource = "Source: Daraz - Rs. 2,600"),
        FashionItem(id = "f_jewel_10", category = FashionCategory.JEWELLERY, pehnoCategory = PehnoCategory.JEWELLERY, name = "Minimalist Zircon Solitaire Pendant", urduName = "سولیٹیئر زرقون لاکٹ", gender = "Female", description = "Dainty gold chain with radiant cut cubic zirconia drop.", heightTip = "Everyday subtle shine.", priceRs = 2100, frontImageUrl = "https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?auto=format&fit=crop&w=600&q=80", storeSource = "Source: J. Jewels - Rs. 2,100"),

        // =========================================================================
        // FEMALE SHOES (10 Real Options with Height Boost)
        // =========================================================================
        FashionItem(id = "f_shoes_1", category = FashionCategory.SHOES, pehnoCategory = PehnoCategory.SHOES, name = "Handcrafted Raw Silk Golden Khussa", urduName = "را سلک سنہری کھسہ", gender = "Female", heightBoostInches = 1.5f, description = "Comfort cushioned sole with golden tilla and pearl work.", heightTip = "+1.5 inch natural sole lift.", priceRs = 2400, frontImageUrl = "https://images.unsplash.com/photo-1543163521-1bf539c55dd2?auto=format&fit=crop&w=600&q=80", storeSource = "Source: Daraz.pk - Rs. 2,400"),
        FashionItem(id = "f_shoes_2", category = FashionCategory.SHOES, pehnoCategory = PehnoCategory.SHOES, name = "Metallic Gold Block Heels", urduName = "سنہری بلاک ہیلز", gender = "Female", heightBoostInches = 3.0f, description = "Stable 3-inch chunky block heel with shimmering cross strap.", heightTip = "+3.0 inch direct height boost with high dance comfort.", priceRs = 4500, frontImageUrl = "https://images.unsplash.com/photo-1543163521-1bf539c55dd2?auto=format&fit=crop&w=600&q=80", storeSource = "Source: Stylo Shoes - Rs. 4,500"),
        FashionItem(id = "f_shoes_3", category = FashionCategory.SHOES, pehnoCategory = PehnoCategory.SHOES, name = "Embroidered Kolhapuri Flats", urduName = "کڑھائی والی کولہا پوری فلیٹس", gender = "Female", heightBoostInches = 1.2f, description = "Braided toe ring with mirror work straps.", heightTip = "+1.2 inch flat ethnic comfort for Mehndi.", priceRs = 2100, frontImageUrl = "https://images.unsplash.com/photo-1560769629-975ec94e6a86?auto=format&fit=crop&w=600&q=80", storeSource = "Source: Sana Collection - Rs. 2,100"),
        FashionItem(id = "f_shoes_4", category = FashionCategory.SHOES, pehnoCategory = PehnoCategory.SHOES, name = "Bridal Pearl Stiletto Pumps", urduName = "دلہن اسٹائلٹو پمپس", gender = "Female", heightBoostInches = 3.5f, description = "4-inch champagne stiletto heel with encrusted pearl ankle strap.", heightTip = "+3.5 inch dramatic height lift for lehengas.", priceRs = 6800, frontImageUrl = "https://images.unsplash.com/photo-1543163521-1bf539c55dd2?auto=format&fit=crop&w=600&q=80", storeSource = "Source: Borjan Luxury - Rs. 6,800"),
        FashionItem(id = "f_shoes_5", category = FashionCategory.SHOES, pehnoCategory = PehnoCategory.SHOES, name = "Velvet Tilla Black Khussa", urduName = "سیاہ مخمل تلہ کھسہ", gender = "Female", heightBoostInches = 1.5f, description = "Jet black velvet embellished with floral gold threadwork.", heightTip = "+1.5 inch cushioned comfort.", priceRs = 2800, frontImageUrl = "https://images.unsplash.com/photo-1549298916-b41d501d3772?auto=format&fit=crop&w=600&q=80", storeSource = "Source: Dhaani Shoes - Rs. 2,800"),
        FashionItem(id = "f_shoes_6", category = FashionCategory.SHOES, pehnoCategory = PehnoCategory.SHOES, name = "Nude Ankle Strap Minimal Heels", urduName = "نیوڈ اینکل ہیلز", gender = "Female", heightBoostInches = 2.5f, description = "Leg-lengthening skin tone strap with comfortable mid heel.", heightTip = "+2.5 inch optical illusion elongates legs.", priceRs = 3900, frontImageUrl = "https://images.unsplash.com/photo-1543163521-1bf539c55dd2?auto=format&fit=crop&w=600&q=80", storeSource = "Source: ECS Shoes - Rs. 3,900"),
        FashionItem(id = "f_shoes_7", category = FashionCategory.SHOES, pehnoCategory = PehnoCategory.SHOES, name = "Mirror Work Mojari Shoes", urduName = "شیشہ ورک موجڑی", gender = "Female", heightBoostInches = 1.4f, description = "Rajasthan inspired mirror embroidery on festive mustard base.", heightTip = "+1.4 inch cultural statement.", priceRs = 2500, frontImageUrl = "https://images.unsplash.com/photo-1549298916-b41d501d3772?auto=format&fit=crop&w=600&q=80", storeSource = "Source: Bagallery - Rs. 2,500"),
        FashionItem(id = "f_shoes_8", category = FashionCategory.SHOES, pehnoCategory = PehnoCategory.SHOES, name = "Rose Gold Shimmer Wedges", urduName = "روز گولڈ ویجز", gender = "Female", heightBoostInches = 2.8f, description = "Solid wedge heel platform offering zero wobble stability.", heightTip = "+2.8 inch height with sneaker-like balance.", priceRs = 4200, frontImageUrl = "https://images.unsplash.com/photo-1543163521-1bf539c55dd2?auto=format&fit=crop&w=600&q=80", storeSource = "Source: Metro Shoes - Rs. 4,200"),
        FashionItem(id = "f_shoes_9", category = FashionCategory.SHOES, pehnoCategory = PehnoCategory.SHOES, name = "Casual Camel Leather Khussa", urduName = "کیمل لیدر روزمرہ کھسہ", gender = "Female", heightBoostInches = 1.3f, description = "Plain tan leather that molds to foot shape effortlessly.", heightTip = "+1.3 inch everyday university & office staple.", priceRs = 1900, frontImageUrl = "https://images.unsplash.com/photo-1549298916-b41d501d3772?auto=format&fit=crop&w=600&q=80", storeSource = "Source: Rawalpindi Crafts - Rs. 1,900"),
        FashionItem(id = "f_shoes_10", category = FashionCategory.SHOES, pehnoCategory = PehnoCategory.SHOES, name = "Crystal Embellished Bridal Sandal", urduName = "کرسٹل سینڈل", gender = "Female", heightBoostInches = 3.2f, description = "Clear vinyl straps decorated with Swarovski crystals.", heightTip = "+3.2 inch princess sparkle.", priceRs = 5800, frontImageUrl = "https://images.unsplash.com/photo-1543163521-1bf539c55dd2?auto=format&fit=crop&w=600&q=80", storeSource = "Source: Insignia - Rs. 5,800")
    )

    fun getItemsForCategory(category: FashionCategory, gender: String): List<FashionItem> {
        return items.filter { item ->
            item.category == category && (item.gender.equals("Both", ignoreCase = true) || item.gender.equals(gender, ignoreCase = true))
        }
    }

    fun getItemsForPehnoCategory(category: PehnoCategory, gender: String): List<FashionItem> {
        return items.filter { item ->
            item.pehnoCategory == category && (item.gender.equals("Both", ignoreCase = true) || item.gender.equals(gender, ignoreCase = true))
        }
    }

    fun searchMultiStoreItems(query: String, gender: String? = null): List<FashionItem> {
        val q = query.trim().lowercase()
        return items.filter { item ->
            val matchGender = gender == null || item.gender.equals("Both", ignoreCase = true) || item.gender.equals(gender, ignoreCase = true)
            val matchQuery = q.isEmpty() ||
                item.name.lowercase().contains(q) ||
                item.urduName.lowercase().contains(q) ||
                item.description.lowercase().contains(q) ||
                item.storeSource.lowercase().contains(q) ||
                item.pehnoCategory.name.lowercase().contains(q)
            matchGender && matchQuery
        }
    }
}
