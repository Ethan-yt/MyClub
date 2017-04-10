package com.ethan.myclub.user.schedule.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ethan on 2017/3/4.
 */


public class Course implements Parcelable {
    @SerializedName("name")
    @Expose
    final private String name;// 课程名 信号系统
    @SerializedName("type")
    @Expose
    final private String type;// 属性 专选
    @SerializedName("teacher")
    @Expose
    final private String teacher;// 教师 孔军
    @Expose
    @SerializedName("courses_time")
    final private List<CourseTime> time;//时间

    final private int color;

    private Course(Parcel in) {
        name = in.readString();
        type = in.readString();
        teacher = in.readString();
        time = new ArrayList<>();
        in.readList(time, CourseTime.class.getClassLoader());
        color = in.readInt();
    }


    public static final Creator<Course> CREATOR = new Creator<Course>() {
        @Override
        public Course createFromParcel(Parcel in) {
            return new Course(in);
        }

        @Override
        public Course[] newArray(int size) {
            return new Course[size];
        }
    };


    private Course(Builder builder) {
        name = builder.name;
        type = builder.type;
        teacher = builder.teacher;
        time = builder.time;
        color = builder.color;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(type);
        parcel.writeString(teacher);
        parcel.writeList(time);
        parcel.writeInt(color);
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getTeacher() {
        return teacher;
    }

    public List<CourseTime> getTime() {
        return time;
    }

    public int getColor() {
        return color;
    }

    /**
     * {@code Course} builder static inner class.
     */

    public static final class Builder {
        private String name;
        private String type;
        private String teacher;
        private int color;
        private List<CourseTime> time;

        public Builder() {
            time = new ArrayList<>();
        }

        /**
         * Sets the {@code name} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code name} to set
         * @return a reference to this Builder
         */
        public Builder name(String val) {
            name = val;
            return this;
        }

        /**
         * Sets the {@code type} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code type} to set
         * @return a reference to this Builder
         */
        public Builder type(String val) {
            type = val;
            return this;
        }

        /**
         * Sets the {@code teacher} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code teacher} to set
         * @return a reference to this Builder
         */
        public Builder teacher(String val) {
            teacher = val;
            return this;
        }

        /**
         * Sets the {@code time} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code time} to set
         * @return a reference to this Builder
         */
        public Builder time(List<CourseTime> val) {
            time = val;
            return this;
        }

        /**
         * Sets the {@code color} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code color} to set
         * @return a reference to this Builder
         */
        public Builder color(int val) {
            color = val;
            return this;
        }

        /**
         * Returns a {@code Course} built from the parameters previously set.
         *
         * @return a {@code Course} built with parameters of this {@code Course.Builder}
         */
        public Course build() {
            return new Course(this);
        }
    }


}