package com.pa.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class CommentResult {
	public CommentResult(){}
	
	public String status;
	public ArrayList<Comment> result;

    public static class Comment implements Parcelable {
        public String id, author,comment,date;

        public Comment() {}

        public Comment(Parcel parcel) {
            if (parcel != null) {
                this.id = parcel.readString();
                this.author = parcel.readString();
                this.date = parcel.readString();
                this.comment = parcel.readString();
            }
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(id);
            dest.writeString(author);
            dest.writeString(date);
            dest.writeString(comment);
        }

        // Method to recreate a Question from a Parcel
        public static Creator<Comment> CREATOR = new Creator<Comment>() {

            @Override
            public Comment createFromParcel(Parcel source) {
                return new Comment(source);
            }

            @Override
            public Comment[] newArray(int size) {
                return new Comment[size];
            }

        };
    }
	
	
}
