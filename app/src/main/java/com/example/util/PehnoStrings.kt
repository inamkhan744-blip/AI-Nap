package com.example.util

object PehnoStrings {

    fun t(key: String, lang: String): String {
        val isUrdu = lang == "ur"
        return when (key) {
            "app_name" -> if (isUrdu) "پہنو" else "Pehno"
            "tagline" -> if (isUrdu) "پہن کے دیکھو" else "Pehen Ke Dekho"
            
            // Profile Setup
            "profile_setup" -> if (isUrdu) "پروفائل بنائیں" else "Profile Setup"
            "profile_setup_sub" -> if (isUrdu) "اپنا قد اور وزن منتخب کریں" else "Set your height & weight"
            "name_label" -> if (isUrdu) "آپ کا نام" else "Your Name"
            "name_placeholder" -> if (isUrdu) "یہاں نام لکھیں..." else "Enter your name..."
            "gender_label" -> if (isUrdu) "جنس منتخب کریں" else "Select Gender"
            "male_btn" -> if (isUrdu) "👨 مرد / Male" else "👨 Male"
            "female_btn" -> if (isUrdu) "👩 عورت / Female" else "👩 Female"
            "weight_label" -> if (isUrdu) "وزن (Wazan)" else "Weight"
            "height_label" -> if (isUrdu) "قد (Qad)" else "Height"
            "language_label" -> if (isUrdu) "زبان / Language" else "Language"
            "start_btn" -> if (isUrdu) "شروع کرو / Start" else "Start Try-On"
            "edit_profile" -> if (isUrdu) "پروفائل تبدیل کریں" else "Edit Profile"
            
            // Main Model Screen
            "front_view" -> if (isUrdu) "سامنے سے / Front" else "Front View"
            "back_view" -> if (isUrdu) "پیچھے سے / Back" else "Back View"
            "rotate_360" -> if (isUrdu) "360° گھماؤ" else "360° Rotate"
            "drag_to_rotate" -> if (isUrdu) "ماڈل کو گھمانے کیلئے سلائیڈر ہلائیں" else "Slide to rotate model"
            "download_look" -> if (isUrdu) "تصویر محفوظ کرو / Save Look" else "Download Look"
            "order_now" -> if (isUrdu) "آرڈر کرو / Order Now" else "Order Now"
            "commission_notice" -> if (isUrdu) "10% کمیشن پہنو ایپ کو جائے گا" else "10% Commission will go to Pehno App"
            "layer_by_layer" -> if (isUrdu) "ماڈل تیار کریں (ایک ایک کر کے چیزیں لگائیں)" else "Layer-by-Layer Dress Up"
            "capture_portrait" -> if (isUrdu) "پورٹریٹ لیں / Capture Portrait" else "Capture Portrait"
            "choose_gallery" -> if (isUrdu) "گیلری سے تصویر / Gallery" else "From Gallery"
            "face_overlay_title" -> if (isUrdu) "ماڈل کے چہرے پر آپ کی تصویر" else "Face Overlay on 3D Model"
            "retake_portrait" -> if (isUrdu) "نیا پورٹریٹ لیں" else "Capture New Portrait"
            "remove_face" -> if (isUrdu) "چہرہ ہٹائیں" else "Remove Face"
            
            // Bottom Tabs
            "tab_tryon" -> if (isUrdu) "ماڈل ٹرائی آن" else "Model Try-On"
            "tab_marketplace" -> if (isUrdu) "مارکیٹ" else "Marketplace"
            "tab_wishlist" -> if (isUrdu) "پسندیدہ" else "Wishlist"
            "tab_sell" -> if (isUrdu) "جوڑا بیچیں" else "Sell Dress"
            "tab_more" -> if (isUrdu) "مزید" else "More"
            
            // Marketplace
            "search_hint" -> if (isUrdu) "یہاں لکھو: بلیک شیروانی، کُرتی، گلا..." else "Search: Black Sherwani, Kurti, Gala..."
            "all_pakistan_marketplace" -> if (isUrdu) "آل پاکستان مارکیٹ پلیس" else "All Pakistan Marketplace"
            "multi_store_sub" -> if (isUrdu) "دراز، کھاڈی، لاہور، کراچی اور کوئٹہ کے شاہی سوٹس" else "Daraz, Khaadi, Lahore & Quetta Designer Suits"
            "try_this" -> if (isUrdu) "پہن کے دیکھو / Try-On" else "Try On Model"
            "sell_floating" -> if (isUrdu) "+ اپنا جوڑا بیچو" else "+ Sell Your Dress"
            
            // Seller Form
            "sell_title" -> if (isUrdu) "اپنا جوڑا بیچیں (پورے پاکستان میں)" else "Sell Your Dress (All Pakistan)"
            "sell_sub" -> if (isUrdu) "اپنی دکان یا پرانا جوڑا آسانی سے لسٹ کریں" else "List your shop or pre-loved dresses"
            "shop_name" -> if (isUrdu) "دکان یا برانڈ کا نام" else "Shop / Brand Name"
            "dress_name" -> if (isUrdu) "جوڑے کا نام" else "Dress Title"
            "city_label" -> if (isUrdu) "شہر منتخب کریں" else "Select City"
            "price_label" -> if (isUrdu) "قیمت (روپے)" else "Price (PKR)"
            "whatsapp_label" -> if (isUrdu) "واٹس ایپ نمبر (خریدار رابطہ کریں گے)" else "WhatsApp Number (For Direct Orders)"
            "front_photo" -> if (isUrdu) "سامنے کی تصویر (Front Photo)" else "Front Photo"
            "back_photo" -> if (isUrdu) "پیچھے کی تصویر (Back Photo)" else "Back Photo"
            "submit_dress" -> if (isUrdu) "مارکیٹ میں شامل کرو / Publish" else "Publish Dress Now"
            "dress_published" -> if (isUrdu) "جوڑا کامیابی سے مارکیٹ میں شامل ہو گیا!" else "Dress successfully published to marketplace!"
            
            // Categories
            "cat_dress" -> if (isUrdu) "جوڑا / Dress" else "Dress"
            "cat_gala" -> if (isUrdu) "گلا / Collar" else "Collar / Gala"
            "cat_cap" -> if (isUrdu) "ٹوپی / Cap" else "Cap / Topi"
            "cat_hair" -> if (isUrdu) "ہیئر اسٹائل" else "Hairstyle"
            "cat_shoes" -> if (isUrdu) "جوتا / Shoes" else "Shoes"
            "cat_gala_design" -> if (isUrdu) "گلا ڈیزائن" else "Gala Design"
            "cat_daman" -> if (isUrdu) "دامن ڈیزائن" else "Daman Design"
            "cat_mehndi" -> if (isUrdu) "مہندی" else "Mehndi"
            "cat_jewellery" -> if (isUrdu) "جیولری" else "Jewellery"

            else -> key
        }
    }
}
