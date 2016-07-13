import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Vitaliy Lutchenko on 02.12.2015.
 */
public class CodeDescriptionPair implements Parcelable {
    private int code;
    private String description;

    public CodeDescriptionPair(int c, String d) {
        this.code = c;
        this.description = d;
    }

    protected CodeDescriptionPair(Parcel in) {
        code = in.readInt();
        description = in.readString();
    }

    public static final Creator<CodeDescriptionPair> CREATOR = new Creator<CodeDescriptionPair>() {
        @Override
        public CodeDescriptionPair createFromParcel(Parcel in) {
            return new CodeDescriptionPair(in);
        }

        @Override
        public CodeDescriptionPair[] newArray(int size) {
            return new CodeDescriptionPair[size];
        }
    };

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(code);
        dest.writeString(description);
    }
}
