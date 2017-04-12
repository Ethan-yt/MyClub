package com.ethan.myclub.schedule.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ethan on 2017/3/4.
 */

public class CourseTime implements Parcelable {
    @Expose
    @SerializedName("weekday")
    final private int day;// 星期几
    @Expose
    @SerializedName("week_begin")
    final private int weekBegin;// 第几周
    @Expose
    @SerializedName("week_end")
    final private int weekEnd;// 第几周
    @Expose
    @SerializedName("week_flag")
    final private int weekFlag; // 单双周标记 1为单 2为双 3为不限
    @Expose
    @SerializedName("time_begin")
    final private int timeBegin; // 节数
    @Expose
    @SerializedName("time_end")
    final private int timeEnd; // 节数
    @SerializedName("location")
    @Expose
    final private String location;// 教室 1教1B411

    public int getDay() {
        return day;
    }

    public int getWeekBegin() {
        return weekBegin;
    }

    public int getWeekEnd() {
        return weekEnd;
    }

    public int getTimeBegin() {
        return timeBegin;
    }

    public int getTimeEnd() {
        return timeEnd;
    }

    public int getWeekFlag() {
        return weekFlag;
    }

    public String getLocation() {
        return location;
    }

    private CourseTime(Parcel in) {
        day = in.readInt();
        weekBegin = in.readInt();
        weekEnd = in.readInt();
        weekFlag = in.readInt();
        timeBegin = in.readInt();
        timeEnd = in.readInt();
        location = in.readString();

    }


    public static final Creator<CourseTime> CREATOR = new Creator<CourseTime>() {
        @Override
        public CourseTime createFromParcel(Parcel in) {
            return new CourseTime(in);
        }

        @Override
        public CourseTime[] newArray(int size) {
            return new CourseTime[size];
        }
    };

    private CourseTime(Builder builder) {
        day = builder.day;
        weekBegin = builder.weekBegin;
        weekEnd = builder.weekEnd;
        weekFlag = builder.weekFlag;
        timeBegin = builder.timeBegin;
        timeEnd = builder.timeEnd;
        location = builder.location;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(day);
        parcel.writeInt(weekBegin);
        parcel.writeInt(weekEnd);
        parcel.writeInt(weekFlag);
        parcel.writeInt(timeBegin);
        parcel.writeInt(timeEnd);
        parcel.writeString(location);
    }

    /**
     * {@code CourseTime} builder static inner class.
     */
    public static final class Builder {
        private int day;
        private int weekBegin;
        private int weekEnd;
        private int weekFlag;
        private int timeBegin;
        private int timeEnd;
        private String location;

        public Builder() {
        }

        /**
         * Sets the {@code day} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code day} to set
         * @return a reference to this Builder
         */
        public Builder day(int val) {
            day = val;
            return this;
        }

        /**
         * Sets the {@code weekBegin} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code weekBegin} to set
         * @return a reference to this Builder
         */
        public Builder weekBegin(int val) {
            weekBegin = val;
            return this;
        }

        /**
         * Sets the {@code weekEnd} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code weekEnd} to set
         * @return a reference to this Builder
         */
        public Builder weekEnd(int val) {
            weekEnd = val;
            return this;
        }

        /**
         * Sets the {@code weekFlag} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code weekFlag} to set
         * @return a reference to this Builder
         */
        public Builder weekFlag(int val) {
            weekFlag = val;
            return this;
        }

        /**
         * Sets the {@code timeBegin} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code timeBegin} to set
         * @return a reference to this Builder
         */
        public Builder timeBegin(int val) {
            timeBegin = val;
            return this;
        }

        /**
         * Sets the {@code timeEnd} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code timeEnd} to set
         * @return a reference to this Builder
         */
        public Builder timeEnd(int val) {
            timeEnd = val;
            return this;
        }


        /**
         * Sets the {@code location} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code location} to set
         * @return a reference to this Builder
         */
        public Builder location(String val) {
            location = val;
            return this;
        }

        /**
         * Returns a {@code CourseTime} built from the parameters previously set.
         *
         * @return a {@code CourseTime} built with parameters of this {@code CourseTime.Builder}
         */
        public CourseTime build() {
            return new CourseTime(this);
        }
    }
}