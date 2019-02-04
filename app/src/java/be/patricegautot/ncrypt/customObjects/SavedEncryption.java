package be.patricegautot.ncrypt.customObjects;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Entity
public class SavedEncryption implements Parcelable {
    @Ignore
    public static final int TYPE_TEXT = 1;
    @Ignore
    public static final int TYPE_IMAGE = 2;

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String text;
    private String title;
    private String hint;
    private int type; // 1 : Text, 2 : Image (path is stored in text)
    private long timeInMillis;

    @Ignore
    public SavedEncryption(String text, String title, String hint, int type){
        this.text = text;
        this.title = title;
        this.hint = hint;
        this.type = type;
        timeInMillis = Calendar.getInstance().getTimeInMillis();
    }

    @Ignore
    public SavedEncryption(String text, String title, String hint, int type, long timeInMillis){
        this.text = text;
        this.title = title;
        this.hint = hint;
        this.type = type;
        this.timeInMillis = timeInMillis;
    }

    public SavedEncryption(int id, String text, String title, String hint, int type, long timeInMillis){
        this.id = id;
        this.text = text;
        this.title = title;
        this.hint = hint;
        this.type = type;
        if(timeInMillis < 0) this.timeInMillis = Calendar.getInstance().getTimeInMillis();
        else this.timeInMillis = timeInMillis;
    }

    ////////////////////////GETTERS/////////////////////////////
    public int getId(){return id;}
    public String getText(){return text;}
    public String getTitle(){return title;}
    public String getHint() {return hint;}
    public int getType(){return type;}
    public long getTimeInMillis() {return timeInMillis;}

    ////////////////////////SETTERS/////////////////////////////
    public void setId(int id){this.id = id;}
    public void setText(String text){this.text = text;}
    public void setTitle(String title){this.title = title;}
    public void setHint(String hint){this.hint = hint;}
    public void setType(int type){this.type = type;}
    public void setTimeInMillis(long time){this.timeInMillis = time;}

    ////////////////////////HELPERS/////////////////////////////
    public String getDate(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        Date date = new Date(timeInMillis);
        return dateFormat.format(date);
    }

    @Ignore
    protected SavedEncryption(Parcel in) {
        text = in.readString();
        title = in.readString();
        hint = in.readString();
        type = in.readInt();
        timeInMillis = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeString(title);
        dest.writeString(hint);
        dest.writeInt(type);
        dest.writeLong(timeInMillis);
    }

    @Ignore
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<SavedEncryption> CREATOR = new Parcelable.Creator<SavedEncryption>() {
        @Override
        public SavedEncryption createFromParcel(Parcel in) {
            return new SavedEncryption(in);
        }

        @Override
        public SavedEncryption[] newArray(int size) {
            return new SavedEncryption[size];
        }
    };
}