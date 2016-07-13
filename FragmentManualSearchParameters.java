import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Vitaliy Lutchenko on 02.12.2015.
 */
public class FragmentManualSearchParameters implements Parcelable {

    private boolean isAdvancedMode = false;
    private CodeDescriptionPair division;
    private CodeDescriptionPair department;
    private CodeDescriptionPair category;
    private String keyword;

    public FragmentManualSearchParameters() {
        keyword = "";
    }

    protected FragmentManualSearchParameters(Parcel in) {
        isAdvancedMode = in.readInt() == 1;
        division = in.readParcelable(CodeDescriptionPair.class.getClassLoader());
        department = in.readParcelable(CodeDescriptionPair.class.getClassLoader());
        category = in.readParcelable(CodeDescriptionPair.class.getClassLoader());
        keyword = in.readString();
    }

    public static final Creator<FragmentManualSearchParameters> CREATOR = new Creator<FragmentManualSearchParameters>() {
        @Override
        public FragmentManualSearchParameters createFromParcel(Parcel in) {
            return new FragmentManualSearchParameters(in);
        }

        @Override
        public FragmentManualSearchParameters[] newArray(int size) {
            return new FragmentManualSearchParameters[size];
        }
    };

    public CodeDescriptionPair getDivision() {
        return division;
    }

    public CodeDescriptionPair getDepartment() {
        return department;
    }

    public CodeDescriptionPair getCategory() {
        return category;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setDivision(CodeDescriptionPair division) {
        this.division = division;
    }

    public void setDepartment(CodeDescriptionPair department) {
        this.department = department;
    }

    public void setCategory(CodeDescriptionPair category) {
        this.category = category;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public boolean isAdvancedMode() {
        return isAdvancedMode;
    }

    public void setIsAdvancedMode(boolean isAdvancedMode) {
        this.isAdvancedMode = isAdvancedMode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(isAdvancedMode ? 1 : 0);
        dest.writeParcelable(division, flags);
        dest.writeParcelable(department, flags);
        dest.writeParcelable(category, flags);
        dest.writeString(keyword);
    }
}
