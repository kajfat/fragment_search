import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

/**
 * Created by Vitaliy Lutchenko on 29.11.2015.
 */
public class Divisions implements Parcelable {
    private static final String DATA = "data";
    private static final String DIVISIONS = "divisions";

    private ArrayList<Division> divisions;

    public Divisions(JsonObject obj) {
        divisions = new ArrayList<>();
        if (obj.has(DATA)) {
            JsonObject data = obj.getAsJsonObject(DATA);
            if (data.has(DIVISIONS)) {
                JsonArray divisionArray = data.getAsJsonArray(DIVISIONS);
                for (JsonElement elem : divisionArray) {
                    try {
                        Division d = new Division(elem.getAsJsonObject());
                        divisions.add(d);
                    } catch (Exception e) {
                       
                    }

                }
            }
        }
    }

    protected Divisions(Parcel in) {
        divisions = in.createTypedArrayList(Division.CREATOR);
    }

    public static final Creator<Divisions> CREATOR = new Creator<Divisions>() {
        @Override
        public Divisions createFromParcel(Parcel in) {
            return new Divisions(in);
        }

        @Override
        public Divisions[] newArray(int size) {
            return new Divisions[size];
        }
    };

    public Divisions(Context context) {
        divisions = new ArrayList<>();
        Uri uri = com.brasnthingsinventory.DB.Tables.Division.CONTENT_URI;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                divisions.add(new Division(context, cursor));
            }
            cursor.close();
        }
    }

    public ArrayList<Division> getDivisions() {
        return divisions;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(divisions);
    }

    public static Divisions getDivisionsFromArguments(Bundle args, String key) {
        if (args != null && args.containsKey(key)) {
            return args.getParcelable(key);
        }
        return null;
    }

    public ArrayList<CodeDescriptionPair> getDivisionsMap() {
        ArrayList<CodeDescriptionPair> list = new ArrayList<>();
        for (Division division : divisions) {
            list.add(new CodeDescriptionPair(division.getDivisionCode(), division.getDivisionDescription()));
        }
        return list;
    }

    public ArrayList<CodeDescriptionPair> getDepartmentsMapByDivisionCode(int divisionCode) {
        ArrayList<CodeDescriptionPair> list = new ArrayList<>();
        for (Division division : divisions) {
            if (division.getDivisionCode() == divisionCode) {
                for (Department department : division.getDepartments()) {
                    list.add(new CodeDescriptionPair(department.getDepartmentCode(), department.getDepartmentDescription()));
                }
                break;
            }
        }
        return list;
    }

    public ArrayList<CodeDescriptionPair> getCategoriesMapByDepartmentCode(int divisionCode, int departmentCode) {
        ArrayList<CodeDescriptionPair> list = new ArrayList<>();
        for (Division division : divisions) {
            if (division.getDivisionCode() == divisionCode) {
                for (Department department : division.getDepartments()) {
                    if (department.getDepartmentCode() == departmentCode) {
                        for (Category category : department.getCategories()) {
                            list.add(new CodeDescriptionPair(category.getCategoryCode(), category.getCategoryDescription()));
                        }
                        break;
                    }
                }
            }
        }
        return list;
    }
}
