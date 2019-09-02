package com.momodupi.piggybank;



import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;


class AccountTypes {

    private int[] type_icon_sets = {
            R.mipmap.clothes, R.mipmap.shoes, R.mipmap.luxury, R.mipmap.accessories,
            R.mipmap.restaurant, R.mipmap.fastfood, R.mipmap.carbide,
            R.mipmap.snacks, R.mipmap.meat, R.mipmap.fruits,
            R.mipmap.vegetables, R.mipmap.drinks, R.mipmap.alcohols,
            R.mipmap.dairy, R.mipmap.cooking,
            R.mipmap.rent, R.mipmap.electricity, R.mipmap.gas,
            R.mipmap.water, R.mipmap.internet, R.mipmap.devices,
            R.mipmap.furniture, R.mipmap.housekeeping, R.mipmap.tools,
            R.mipmap.washing, R.mipmap.personalcare, R.mipmap.hotel,
            R.mipmap.carservice, R.mipmap.fuel, R.mipmap.airplane,
            R.mipmap.bus, R.mipmap.carpool, R.mipmap.train,
            R.mipmap.mobilepayment, R.mipmap.post, R.mipmap.delivery,
            R.mipmap.training, R.mipmap.game, R.mipmap.software,
            R.mipmap.tourism, R.mipmap.movie, R.mipmap.bar,
            R.mipmap.courses, R.mipmap.books, R.mipmap.delivery,
            R.mipmap.party, R.mipmap.gift, R.mipmap.donation,
            R.mipmap.treatment, R.mipmap.pills, R.mipmap.supplements,
            R.mipmap.insurance, R.mipmap.accidents, R.mipmap.tickets,
            R.mipmap.salary, R.mipmap.financial,
    };

    private String[] type_string_sets;
    private String[] type_clothing;
    private String[] type_food;
    private String[] type_housing;
    private String[] type_transportation;
    private String[] type_communication;
    private String[] type_entertainment;
    private String[] type_study;
    private String[] type_social;
    private String[] type_medical;
    private String[] type_misfortune;
    private String[] type_income;



    private String[] general_type_sets = {
            "Clothing", "Food", "Housing",
            "Transportation", "Communication", "Entertainment",
            "Study", "Social", "Medical",
            "Misfortune", "Income",
    };

    private int[] general_type_color_sets = {
            R.color.chartpink300, R.color.chartpurple500, R.color.chartindigo500, R.color.chartblue800,
            R.color.chartlightblue300, R.color.chartcyan800, R.color.chartlightgreen500,
            R.color.chartyellow500, R.color.chartorange800, R.color.chartred800,
    };

    private ArrayList<Typetuple> type_sets;
    //private Context context;

    AccountTypes(Context context) {

        //this.type_string_sets = context.getResources().getStringArray(R.array.type_name);

        this.type_clothing = context.getResources().getStringArray(R.array.type_clothing);
        this.type_food = context.getResources().getStringArray(R.array.type_food);
        this.type_housing = context.getResources().getStringArray(R.array.type_housing);
        this.type_transportation = context.getResources().getStringArray(R.array.type_transportation);
        this.type_communication = context.getResources().getStringArray(R.array.type_communication);
        this.type_entertainment = context.getResources().getStringArray(R.array.type_entertainment);
        this.type_study = context.getResources().getStringArray(R.array.type_study);
        this.type_social = context.getResources().getStringArray(R.array.type_social);
        this.type_medical = context.getResources().getStringArray(R.array.type_medical);
        this.type_misfortune = context.getResources().getStringArray(R.array.type_misfortune);
        this.type_income = context.getResources().getStringArray(R.array.type_income);

        Object[] general_type_sets_obj = {
                this.type_clothing, this.type_food,
                this.type_housing, this.type_transportation,
                this.type_communication, this.type_entertainment,
                this.type_study, this.type_social,
                this.type_medical, this.type_misfortune,
                this.type_income,
        };

        this.type_string_sets = null;
        for (Object i : general_type_sets_obj) {
            this.type_string_sets = this.combineString(this.type_string_sets, (String[]) i);
        }

        this.type_sets = new ArrayList<>();
        for (int i=0; i<this.type_string_sets.length; i++) {
            Typetuple t = new Typetuple();
            t.type_str = this.type_string_sets[i];
            t.type_icon = this.type_icon_sets[i];
            this.type_sets.add(t);
        }
    }


    String[] getTpyeString() {
        return this.type_string_sets;
    }

    String[] getGeneralTypeString() {
        return this.general_type_sets;
    }

    int[] getGeneralTypeColor() {
        /*
        int[] color = new int[this.general_type_color_sets.length];
        for (int cnt=0; cnt<this.general_type_color_sets.length; cnt++) {
            color[cnt] = this.general_type_color_sets[cnt];
        }

         */
        return this.general_type_color_sets;
    }

    String getGeneralType(String type) {
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
        else if (Arrays.asList(this.type_misfortune).contains(type)) {
            return this.general_type_sets[9];
        }
        else if (Arrays.asList(this.type_income).contains(type)) {
            return this.general_type_sets[10];
        }
        else {
            return null;
        }
    }

    int[] getTpyeIcon() {
        return this.type_icon_sets;
    }

    int findIconbySring(String type) {
        for (Typetuple t: this.type_sets) {
            if (t.type_str.equals(type)) {
                return t.type_icon;
            }
        }
        return 0;
    }

    private String[] combineString(String[] a, String[] b) {
        int length;
        if (a == null) {
            length = b.length;
            String[] result = new String[length];
            System.arraycopy(b, 0, result, 0, b.length);
            return result;
        }
        else {
            length = a.length + b.length;
            String[] result = new String[length];
            System.arraycopy(a, 0, result, 0, a.length);
            System.arraycopy(b, 0, result, a.length, b.length);
            return result;
        }
    }

}

class Typetuple {
    String type_str;
    int type_icon;
}