package com.momodupi.piggybank;



import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class AccountTypes {
    /*
    private String[] type_string_sets = {
            "Clothes", "Shoes", "Luxury", "Accessories",
            "Restaurant", "Fast Food", "Carbide", "Snacks", "Meats", "Fruits",
            "Vegetables", "Drinks", "Alcohols", "Milk", "Cooking",
            "Rent", "Electricity", "Gas", "Water", "Internet", "Devices", "Furniture", "House Keeping",
            "Tools", "Washing", "Personal Care", "Hotel",
            "Car Service", "Fuel", "Airplane", "Bus", "Car Pooling", "Train", "Car Insurance",
            "Mobile Payments", "Post", "Delivery",
            "Training", "Game", "Software", "Tourism", "Movies", "Bar",
            "Courses", "Books", "Stationery",
            "Party", "Gift", "Donation",
            "Treatment", "Pills", "Supplements",
            "Accidents", "Tickets"
    };
    */

    private int[] type_icon_sets = {
            R.mipmap.clothes, R.mipmap.shoes, R.mipmap.luxury, R.mipmap.accessories,
            R.mipmap.restaurant, R.mipmap.fastfood, R.mipmap.carbide,
            R.mipmap.snacks, R.mipmap.meat, R.mipmap.fruits,
            R.mipmap.vegetables, R.mipmap.drinks, R.mipmap.alcohols,
            R.mipmap.milk, R.mipmap.cooking,
            R.mipmap.rent, R.mipmap.electricity, R.mipmap.gas,
            R.mipmap.water, R.mipmap.internet, R.mipmap.devices,
            R.mipmap.furniture, R.mipmap.housekeeping, R.mipmap.tools,
            R.mipmap.washing, R.mipmap.personalcare, R.mipmap.hotel,
            R.mipmap.carservice, R.mipmap.fuel, R.mipmap.airplane,
            R.mipmap.bus, R.mipmap.carpool, R.mipmap.train, R.mipmap.carinsurance,
            R.mipmap.mobilepayment, R.mipmap.post, R.mipmap.delivery,
            R.mipmap.training, R.mipmap.game, R.mipmap.software,
            R.mipmap.tourism, R.mipmap.movie, R.mipmap.bar,
            R.mipmap.courses, R.mipmap.books, R.mipmap.delivery,
            R.mipmap.party, R.mipmap.gift, R.mipmap.donation,
            R.mipmap.treatment, R.mipmap.pills, R.mipmap.supplements,
            R.mipmap.accidents, R.mipmap.tickets
    };

    private String[] type_string_sets;
    //private int[] type_icon_sets;


    private String[] type_clothing = {
            "Clothes", "Shoes", "Luxury", "Accessories"
    };
    private int[] type_clothing_icon = {
            R.mipmap.clothes, R.mipmap.shoes, R.mipmap.luxury, R.mipmap.accessories
    };

    private String[] type_food = {
            "Restaurant", "Fast Food", "Carbide", "Snacks", "Meats", "Fruits",
            "Vegetables", "Drinks", "Alcohols", "Milk", "Cooking"
    };
    private int[] type_food_icon = {
            R.mipmap.restaurant, R.mipmap.fastfood, R.mipmap.carbide,
            R.mipmap.snacks, R.mipmap.meat, R.mipmap.fruits,
            R.mipmap.vegetables, R.mipmap.drinks, R.mipmap.alcohols,
            R.mipmap.milk, R.mipmap.cooking
    };

    private String[] type_housing = {
            "Rent", "Electricity", "Gas", "Water", "Internet", "Devices", "Furniture", "House Keeping",
            "Tools", "Washing", "Personal Care", "Hotel"
    };
    private int[] type_housing_icon = {
            R.mipmap.rent, R.mipmap.electricity, R.mipmap.gas,
            R.mipmap.water, R.mipmap.internet, R.mipmap.devices,
            R.mipmap.furniture, R.mipmap.housekeeping, R.mipmap.tools,
            R.mipmap.washing, R.mipmap.personalcare, R.mipmap.hotel
    };

    private String[] type_transportation = {
            "Car Service", "Fuel", "Airplane", "Bus", "Car Pool", "Train", "Car Insurance"
    };
    private int[] type_transportation_icon = {
            R.mipmap.carservice, R.mipmap.fuel, R.mipmap.airplane,
            R.mipmap.bus, R.mipmap.carpool, R.mipmap.train, R.mipmap.carinsurance
    };

    private String[] type_communication = {
            "Mobile Payments", "Post", "Delivery"
    };
    private int[] type_communication_icon = {
            R.mipmap.mobilepayment, R.mipmap.post, R.mipmap.delivery
    };

    private String[] type_entertainment = {
            "Training", "Game", "Software", "Tourism", "Movie", "Bar",
    };
    private int[] type_entertainment_icon = {
            R.mipmap.training, R.mipmap.game, R.mipmap.software,
            R.mipmap.tourism, R.mipmap.movie, R.mipmap.bar,
    };

    private String[] type_study = {
            "Courses", "Books", "Stationery"
    };
    private int[] type_study_icon = {
            R.mipmap.courses, R.mipmap.books, R.mipmap.delivery
    };

    private String[] type_social = {
            "Party", "Gift", "Donation",
    };
    private int[] type_social_icon = {
            R.mipmap.party, R.mipmap.gift, R.mipmap.donation
    };

    private String[] type_medical = {
            "Treatment", "Pills", "Supplements",
    };
    private int[] type_medical_icon = {
            R.mipmap.treatment, R.mipmap.pills, R.mipmap.supplements
    };

    private String[] type_misfortune = {
            "Accidents", "Tickets"
    };
    private int[] type_misfortune_icon = {
            R.mipmap.accidents, R.mipmap.tickets
    };


    private String[] general_type_sets = {
            "Clothing", "Food", "Housing", "Transportation",
            "Communication", "Entertainment", "Study",
            "Social", "Medical", "Misfortune"
    };
    private int[] general_type_color_sets = {
            R.color.chartlightgreen800, R.color.chartorange800, R.color.chartblue800,
            R.color.chartyellow500, R.color.chartpurple800, R.color.chartlightblue500,
            R.color.chartlime800, R.color.chartcyan800, R.color.chartred800
    };


    private ArrayList<Typetuple> type_sets = null;
    private Context context;


    public AccountTypes(Context context) {

        this.context = context;

        this.type_string_sets = context.getResources().getStringArray(R.array.type_name);

        this.type_sets = new ArrayList<Typetuple>();
        for (int i=0; i<this.type_string_sets.length; i++) {
            Typetuple t = new Typetuple();
            t.type_str = this.type_string_sets[i];
            t.type_icon = this.type_icon_sets[i];
            this.type_sets.add(t);
        }
    }


    public String[] getTpyeString() {
        return this.type_string_sets;
    }

    public String[] getGeneralTypeString() {
        return this.general_type_sets;
    }
    public int[] getGeneralTypeColor() {
        int color[] = new int[this.general_type_color_sets.length];
        for (int cnt=0; cnt<this.general_type_color_sets.length; cnt++) {
            color[cnt] = this.general_type_color_sets[cnt];
        }
        return this.general_type_color_sets;
    }

    public String getGeneralType(String type) {
        if (Arrays.asList(this.type_clothing).contains(type)) {
            return this.general_type_sets[0];
        }
        else if (Arrays.asList(this.type_food).contains(type)) {
            return this.general_type_sets[1];
        }
        else if (Arrays.asList(this.type_housing).contains(type)) {
            return this.general_type_sets[2];
        }
        else if (Arrays.asList(this.type_transportation).contains(type)) {
            return this.general_type_sets[3];
        }
        else if (Arrays.asList(this.type_communication).contains(type)) {
            return this.general_type_sets[4];
        }
        else if (Arrays.asList(this.type_entertainment).contains(type)) {
            return this.general_type_sets[5];
        }
        else if (Arrays.asList(this.type_study).contains(type)) {
            return this.general_type_sets[6];
        }
        else if (Arrays.asList(this.type_social).contains(type)) {
            return this.general_type_sets[7];
        }
        else if (Arrays.asList(this.type_medical).contains(type)) {
            return this.general_type_sets[8];
        }
        else {
            return this.general_type_sets[9];
        }
    }

    public int[] getTpyeIcon() {
        return this.type_icon_sets;
    }

    public int findIconbySring(String type) {
        for (Typetuple t: this.type_sets) {
            if (t.type_str.equals(type)) {
                return t.type_icon;
            }
        }
        return 0;
    }

}

class Typetuple {
    public String type_str;
    public int type_icon;
}