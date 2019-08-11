package com.momodupi.piggybank;


import java.util.ArrayList;


public class AccountTypes {
    private String[] type_string_sets = {
            "Restaurant", "Car Service", "Hotel", "Rent", "Electronics", "Fruits",
            "Clothing", "Personal Care", "Courses", "Party",
            "Fuel", "Software", "Season", "Airplane", "Railway", "Treatment",
            "Supplement", "Water", "Tissue", "Movie", "Network",
            "Game", "Tools", "Exercise", "Drinks", "Cooking", "Shopping", "Books",
            "Accidents", "Mobile Payment", "Milk", "Meat", "Salary"
    };

    private int[] type_icon_sets = {
                R.mipmap.restaurant, R.mipmap.carservice,
                R.mipmap.hotel, R.mipmap.rent,
                R.mipmap.light, R.mipmap.watermelon,
                R.mipmap.clothes, R.mipmap.dispenser,
                R.mipmap.classroom,
                R.mipmap.party, R.mipmap.gas,
                R.mipmap.software, R.mipmap.spice,
                R.mipmap.airplane, R.mipmap.train,
                R.mipmap.treatment, R.mipmap.supplement,
                R.mipmap.water, R.mipmap.tissue,
                R.mipmap.movie, R.mipmap.internethub,
                R.mipmap.gamecontroller, R.mipmap.tools,
                R.mipmap.dumbbell, R.mipmap.cocktail,
                R.mipmap.cooking, R.mipmap.buying,
                R.mipmap.book, R.mipmap.bang,
                R.mipmap.mobilepayment, R.mipmap.milk,
                R.mipmap.steak, R.mipmap.cashinhand
    };

    ArrayList<Typetuple> type_sets = null;


    public AccountTypes() {
        type_sets = new ArrayList<Typetuple>();
        for (int i=0; i<type_string_sets.length; i++) {
            Typetuple t = new Typetuple();
            t.type_str = type_string_sets[i];
            t.type_icon = type_icon_sets[i];
            type_sets.add(t);
        }
    }

    public String[] getTpyeString() {
        return type_string_sets;
    }

    public int[] getTpyeIcon() {
        return type_icon_sets;
    }

    public int findIconbySring(String type) {
        for (Typetuple t: type_sets) {
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