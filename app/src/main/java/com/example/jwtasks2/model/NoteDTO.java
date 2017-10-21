package com.example.jwtasks2.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Date;

public class NoteDTO implements Parcelable, Comparable<NoteDTO> {
    private String description = "";
    private Date date = new Date();
    private String type = "";
    private long id = -1;

    public NoteDTO(String description, Date date, String type, long id) {
        this.description = description;
        this.date = date;
        this.type = type;
        this.id = id;
    }

    public NoteDTO() {
    }

    private NoteDTO(Parcel in) {
        description = in.readString();
        date = new Date(in.readLong());
        type = in.readString();
        id = in.readLong();
    }

    @Override
    public int compareTo(@NonNull NoteDTO another) {
        return date.compareTo(another.getDate());
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "NoteDTO{" +
                "description='" + description + '\'' +
                ", date='" + date + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(description);
        parcel.writeLong(date.getTime());
        parcel.writeString(type);
        parcel.writeLong(id);
    }


    public static final Creator<NoteDTO> CREATOR = new Creator<NoteDTO>() {
        @Override
        public NoteDTO createFromParcel(Parcel in) {
            return new NoteDTO(in);
        }

        @Override
        public NoteDTO[] newArray(int size) {
            return new NoteDTO[size];
        }
    };
}
