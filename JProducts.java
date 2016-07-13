import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Vitaliy Lutchenko on 09.12.2015.
 */
public class JProducts implements Parcelable {
    public int status;
    public int count;
    public int transaction_id;
    public boolean isDataConvertedToDB;

    public JProducts() {
        status = -1;
        count = 0;
        transaction_id = 0;
        isDataConvertedToDB = false;
    }

    protected JProducts(Parcel in) {
        status = in.readInt();
        count = in.readInt();
        transaction_id = in.readInt();
        isDataConvertedToDB = in.readInt() == 1;
    }

    public static final Creator<JProducts> CREATOR = new Creator<JProducts>() {
        @Override
        public JProducts createFromParcel(Parcel in) {
            return new JProducts(in);
        }

        @Override
        public JProducts[] newArray(int size) {
            return new JProducts[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(status);
        dest.writeInt(count);
        dest.writeInt(transaction_id);
        dest.writeInt(isDataConvertedToDB ? 1 : 0);
    }

    public static JProducts getProductsFromArguments(Bundle args, String key) {
        if (args != null && args.containsKey(key)) {
            return args.getParcelable(key);
        }
        return null;
    }

}
