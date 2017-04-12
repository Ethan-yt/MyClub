package com.ethan.myclub.schedule.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ethan on 2017/3/4.
 */

public class Schedule implements Parcelable {
    @Expose
    @SerializedName("year")
    final private String year;
    @Expose
    @SerializedName("term")
    final private String term;
    @Expose
    @SerializedName("courses")
    final private List<Course> courses;

    private Schedule(Parcel in) {
        year = in.readString();
        term = in.readString();
        courses = new ArrayList<>();
        in.readList(courses, Course.class.getClassLoader());
    }

    public static final Creator<Schedule> CREATOR = new Creator<Schedule>() {
        @Override
        public Schedule createFromParcel(Parcel in) {
            return new Schedule(in);
        }

        @Override
        public Schedule[] newArray(int size) {
            return new Schedule[size];
        }
    };

    private Schedule(Builder builder) {
        year = builder.year;
        term = builder.term;
        courses = builder.courses;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(year);
        parcel.writeString(term);
        parcel.writeList(courses);
    }

    public String getYear() {
        return year;
    }

    public String getTerm() {
        return term;
    }

    public List<Course> getCourses() {
        return courses;
    }

    /**
     * {@code Schedule} builder static inner class.
     */
    public static final class Builder {
        private String year;
        private String term;
        private List<Course> courses;

        public Builder() {
            courses = new ArrayList<>();
        }

        /**
         * Sets the {@code year} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code year} to set
         * @return a reference to this Builder
         */
        public Builder year(String val) {
            year = val;
            return this;
        }

        /**
         * Sets the {@code term} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code term} to set
         * @return a reference to this Builder
         */
        public Builder term(String val) {
            term = val;
            return this;
        }

        /**
         * Sets the {@code courses} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code courses} to set
         * @return a reference to this Builder
         */
        public Builder courses(List<Course> val) {
            courses = val;
            return this;
        }

        /**
         * Returns a {@code Schedule} built from the parameters previously set.
         *
         * @return a {@code Schedule} built with parameters of this {@code Schedule.Builder}
         */
        public Schedule build() {
            return new Schedule(this);
        }
    }
}