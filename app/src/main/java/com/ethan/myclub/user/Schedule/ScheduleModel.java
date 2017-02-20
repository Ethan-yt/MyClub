package com.ethan.myclub.user.Schedule;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ethan on 2017/1/19.
 */

class ScheduleModel implements Parcelable{
    final private String year;
    final private String term;
    final private List<CourseModel> courses;

    private ScheduleModel(Parcel in) {
        year = in.readString();
        term = in.readString();
        courses = new ArrayList<>();
        in.readList(courses, CourseModel.class.getClassLoader());
    }

    public static final Creator<ScheduleModel> CREATOR = new Creator<ScheduleModel>() {
        @Override
        public ScheduleModel createFromParcel(Parcel in) {
            return new ScheduleModel(in);
        }

        @Override
        public ScheduleModel[] newArray(int size) {
            return new ScheduleModel[size];
        }
    };

    private ScheduleModel(Builder builder) {
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

    String getYear() {
        return year;
    }

    String getTerm() {
        return term;
    }

    List<CourseModel> getCourses() {
        return courses;
    }

    /**
     * {@code ScheduleModel} builder static inner class.
     */
    static final class Builder {
        private String year;
        private String term;
        private List<CourseModel> courses;

        Builder() {
            courses = new ArrayList<>();
        }

        /**
         * Sets the {@code year} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code year} to set
         * @return a reference to this Builder
         */
        Builder year(String val) {
            year = val;
            return this;
        }

        /**
         * Sets the {@code term} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code term} to set
         * @return a reference to this Builder
         */
        Builder term(String val) {
            term = val;
            return this;
        }

        /**
         * Sets the {@code courses} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code courses} to set
         * @return a reference to this Builder
         */
        Builder courses(List<CourseModel> val) {
            courses = val;
            return this;
        }

        /**
         * Returns a {@code ScheduleModel} built from the parameters previously set.
         *
         * @return a {@code ScheduleModel} built with parameters of this {@code ScheduleModel.Builder}
         */
        ScheduleModel build() {
            return new ScheduleModel(this);
        }
    }
}

class CourseModel implements Parcelable {
    final private String name;// 课程名 信号系统
    final private String type;// 属性 专选
    final private String teacher;// 教师 孔军
    final private List<CourseTime> time;//时间

    private CourseModel(Parcel in) {
        name = in.readString();
        type = in.readString();
        teacher = in.readString();
        time = new ArrayList<>();
        in.readList(time, CourseTime.class.getClassLoader());
    }


    public static final Creator<CourseModel> CREATOR = new Creator<CourseModel>() {
        @Override
        public CourseModel createFromParcel(Parcel in) {
            return new CourseModel(in);
        }

        @Override
        public CourseModel[] newArray(int size) {
            return new CourseModel[size];
        }
    };


    private CourseModel(Builder builder) {
        name = builder.name;
        type = builder.type;
        teacher = builder.teacher;
        time = builder.time;
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

    /**
     * {@code CourseModel} builder static inner class.
     */

    static final class Builder {
        private String name;
        private String type;
        private String teacher;
        private List<CourseTime> time;

        Builder() {
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
        Builder type(String val) {
            type = val;
            return this;
        }

        /**
         * Sets the {@code teacher} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code teacher} to set
         * @return a reference to this Builder
         */
        Builder teacher(String val) {
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
         * Returns a {@code CourseModel} built from the parameters previously set.
         *
         * @return a {@code CourseModel} built with parameters of this {@code CourseModel.Builder}
         */
        CourseModel build() {
            return new CourseModel(this);
        }
    }


}

class CourseTime implements Parcelable {

    final private int day;// 星期几
    final private int weekBegin;// 第几周
    final private int weekEnd;// 第几周
    final private int weekFlag; // 单双周标记 0为不限 1为单 2为双
    final private int timeBegin; // 节数
    final private int timeEnd; // 节数
    final private String location;// 教室 1教1B411

    int getDay() {
        return day;
    }

    public int getWeekBegin() {
        return weekBegin;
    }

    public int getWeekEnd() {
        return weekEnd;
    }

    int getTimeBegin() {
        return timeBegin;
    }

    int getTimeEnd() {
        return timeEnd;
    }


    String getLocation() {
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
    static final class Builder {
        private int day;
        private int weekBegin;
        private int weekEnd;
        private int weekFlag;
        private int timeBegin;
        private int timeEnd;
        private String location;

        Builder() {
        }

        /**
         * Sets the {@code day} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code day} to set
         * @return a reference to this Builder
         */
        Builder day(int val) {
            day = val;
            return this;
        }

        /**
         * Sets the {@code weekBegin} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code weekBegin} to set
         * @return a reference to this Builder
         */
        Builder weekBegin(int val) {
            weekBegin = val;
            return this;
        }

        /**
         * Sets the {@code weekEnd} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code weekEnd} to set
         * @return a reference to this Builder
         */
        Builder weekEnd(int val) {
            weekEnd = val;
            return this;
        }

        /**
         * Sets the {@code weekFlag} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code weekFlag} to set
         * @return a reference to this Builder
         */
        Builder weekFlag(int val) {
            weekFlag = val;
            return this;
        }

        /**
         * Sets the {@code timeBegin} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code timeBegin} to set
         * @return a reference to this Builder
         */
        Builder timeBegin(int val) {
            timeBegin = val;
            return this;
        }

        /**
         * Sets the {@code timeEnd} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code timeEnd} to set
         * @return a reference to this Builder
         */
        Builder timeEnd(int val) {
            timeEnd = val;
            return this;
        }


        /**
         * Sets the {@code location} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code location} to set
         * @return a reference to this Builder
         */
        Builder location(String val) {
            location = val;
            return this;
        }

        /**
         * Returns a {@code CourseTime} built from the parameters previously set.
         *
         * @return a {@code CourseTime} built with parameters of this {@code CourseTime.Builder}
         */
        CourseTime build() {
            return new CourseTime(this);
        }
    }
}